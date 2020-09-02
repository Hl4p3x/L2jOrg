// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting.java;

import javax.tools.Diagnostic;
import org.slf4j.LoggerFactory;
import java.nio.file.attribute.BasicFileAttributes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import org.l2j.gameserver.engine.scripting.annotations.Disabled;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.file.LinkOption;
import org.l2j.commons.util.FilterUtil;
import java.util.function.Predicate;
import java.io.Writer;
import java.io.StringWriter;
import org.l2j.commons.util.Util;
import java.lang.module.Configuration;
import java.util.Set;
import java.lang.module.ModuleFinder;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.FileVisitOption;
import java.io.IOException;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import javax.tools.JavaFileManager;
import java.util.Collections;
import javax.tools.StandardLocation;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.l2j.gameserver.engine.scripting.AbstractExecutionContext;

public final class JavaExecutionContext extends AbstractExecutionContext<JavaScriptingEngine>
{
    private static final Logger LOGGER;
    private final Path destination;
    private final DiagnosticListener<JavaFileObject> listener;
    private final Path sourcePath;
    private final boolean forceCompile;
    private ScriptingFileManager scriptingFileManager;
    private ModuleLayer layer;
    
    JavaExecutionContext(final JavaScriptingEngine engine) {
        super(engine);
        this.listener = new DefaultDiagnosticListener();
        this.sourcePath = ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve(Objects.requireNonNullElse(this.getProperty("source.path"), "data/scripts"));
        this.destination = Path.of(Objects.requireNonNullElse(this.getProperty("compiled.path"), "compiledScripts"), new String[0]);
        this.forceCompile = Boolean.parseBoolean(Objects.requireNonNullElse(this.getProperty("force.compile"), "true"));
        try {
            this.compileModuleInfo();
            this.compile(this.sourcePath);
        }
        catch (Exception e) {
            JavaExecutionContext.LOGGER.error("Could not compile Java Scripts", (Throwable)e);
        }
    }
    
    private void compileModuleInfo() throws Exception {
        Files.createDirectories(this.destination, (FileAttribute<?>[])new FileAttribute[0]);
        this.initializeScriptingFileManager();
        final ArrayList<String> options = new ArrayList<String>(this.compileOptions());
        options.add("--module-source-path");
        options.add(this.sourcePath.toString());
        this.compile(this.findModuleInfo(), options);
    }
    
    private void initializeScriptingFileManager() throws IOException {
        final StandardJavaFileManager fileManager = this.getScriptingEngine().getCompiler().getStandardFileManager(this.listener, null, StandardCharsets.UTF_8);
        fileManager.setLocation(StandardLocation.CLASS_PATH, (Iterable<? extends File>)Collections.emptyList());
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, Collections.singletonList(this.destination));
        this.scriptingFileManager = new ScriptingFileManager(fileManager);
    }
    
    private List<Path> findModuleInfo() throws IOException {
        return Files.find(this.sourcePath, Integer.MAX_VALUE, (path, attributes) -> "module-info.java".equals(path.getFileName().toString()), new FileVisitOption[0]).collect((Collector<? super Path, ?, List<Path>>)Collectors.toList());
    }
    
    private void tryConfigureModuleLayer() {
        final Set<String> moduleNames = this.scriptingFileManager.getModuleNames();
        if (moduleNames.isEmpty()) {
            return;
        }
        try {
            final Configuration configuration = ModuleLayer.boot().configuration().resolve(ModuleFinder.of(this.destination), ModuleFinder.of(new Path[0]), moduleNames);
            this.layer = ModuleLayer.boot().defineModulesWithOneLoader(configuration, ClassLoader.getSystemClassLoader());
        }
        catch (Exception e) {
            JavaExecutionContext.LOGGER.warn("Couldn't configure module layer of modules {} : {}", (Object)moduleNames, (Object)e.getMessage());
        }
    }
    
    private void compile(final List<Path> sources, final List<String> options) throws JavaCompilerException {
        if (!Util.isNullOrEmpty((Collection)sources)) {
            final StringWriter writer = new StringWriter();
            final boolean compilationSuccess = this.getScriptingEngine().getCompiler().getTask(writer, this.scriptingFileManager, this.listener, options, null, this.scriptingFileManager.getJavaFileObjectsFromPaths(sources)).call();
            if (!compilationSuccess) {
                throw new JavaCompilerException(writer.toString());
            }
            this.tryConfigureModuleLayer();
        }
    }
    
    private void compile(final Path sourcePath) throws JavaCompilerException, IOException {
        final List<Path> paths = Files.walk(sourcePath, new FileVisitOption[0]).filter(this::needCompile).collect((Collector<? super Path, ?, List<Path>>)Collectors.toList());
        if (!Util.isNullOrEmpty((Collection)paths)) {
            this.compile(paths, this.compileOptions());
        }
    }
    
    private boolean needCompile(final Path path) {
        if (!FilterUtil.javaFile(path)) {
            return false;
        }
        if (this.forceCompile) {
            return true;
        }
        try {
            final Path compiled = this.destination.resolve(Path.of(this.sourcePath.relativize(path).toString().replace(".java", ".class"), new String[0]));
            return Files.notExists(compiled, new LinkOption[0]) || Files.getLastModifiedTime(compiled, new LinkOption[0]).compareTo(Files.getLastModifiedTime(path, new LinkOption[0])) < 0 || !this.scriptingFileManager.beAwareOfObjectFile(path, compiled);
        }
        catch (IOException e) {
            JavaExecutionContext.LOGGER.warn(e.getMessage(), (Throwable)e);
            return true;
        }
    }
    
    private List<String> compileOptions() {
        final String javaVersion = System.getProperty("java.specification.version");
        return List.of("--enable-preview", "--module-path", System.getProperty("jdk.module.path"), "-target", javaVersion, "--source", javaVersion, "-implicit:class");
    }
    
    @Override
    public Map<Path, Throwable> executeScripts(final Iterable<Path> sourcePaths) {
        final Map<Path, Throwable> executionFailures = new LinkedHashMap<Path, Throwable>();
        for (final Path sourcePath : sourcePaths) {
            ScriptingFileInfo scriptFileInfo = this.scriptingFileManager.getScriptInfo(sourcePath);
            if (Objects.isNull(scriptFileInfo)) {
                try {
                    this.compile(sourcePath);
                    scriptFileInfo = this.scriptingFileManager.getScriptInfo(sourcePath);
                }
                catch (JavaCompilerException | IOException ex2) {
                    final Exception ex;
                    final Exception e = ex;
                    JavaExecutionContext.LOGGER.error(e.getMessage(), (Throwable)e);
                    continue;
                }
            }
            if (Objects.isNull(scriptFileInfo)) {
                JavaExecutionContext.LOGGER.error("Compilation successful, but class corresponding to {} not found!", (Object)sourcePath.toString());
            }
            else {
                final String javaName = scriptFileInfo.getJavaName();
                if (javaName.contains("$") || javaName.equals("module-info")) {
                    continue;
                }
                if (javaName.equals("package-info")) {
                    continue;
                }
                this.setCurrentExecutingScript(sourcePath);
                try {
                    this.executeMain(scriptFileInfo);
                }
                catch (NoSuchMethodException e3) {
                    JavaExecutionContext.LOGGER.warn("There is no main method on script {}", (Object)sourcePath);
                }
                catch (Exception e2) {
                    executionFailures.put(sourcePath, e2);
                }
                finally {
                    this.setCurrentExecutingScript(null);
                }
            }
        }
        return executionFailures;
    }
    
    private void executeMain(final ScriptingFileInfo scriptFileInfo) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final ClassLoader classLoader = this.getClassLoaderOfScript(scriptFileInfo);
        final Class<?> javaClass = classLoader.loadClass(scriptFileInfo.getJavaName());
        final Method mainMethod = javaClass.getMethod("main", String[].class);
        if (Modifier.isStatic(mainMethod.getModifiers()) && !javaClass.isAnnotationPresent(Disabled.class)) {
            mainMethod.invoke(null, new String[] { scriptFileInfo.getSourcePath().toString() });
        }
    }
    
    private ClassLoader getClassLoaderOfScript(final ScriptingFileInfo scriptFileInfo) {
        if (Objects.nonNull(this.layer)) {
            try {
                return this.layer.findLoader(scriptFileInfo.getModuleName());
            }
            catch (Exception e) {
                JavaExecutionContext.LOGGER.warn("Could not find class loader of module", (Throwable)e);
            }
        }
        return this.scriptingFileManager.getClassLoader(scriptFileInfo.getLocation());
    }
    
    @Override
    public Map.Entry<Path, Throwable> executeScript(final Path sourcePath) {
        final Map<Path, Throwable> executionFailures = this.executeScripts(Collections.singletonList(sourcePath));
        if (!executionFailures.isEmpty()) {
            return executionFailures.entrySet().iterator().next();
        }
        return null;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)JavaExecutionContext.class);
    }
    
    private static class DefaultDiagnosticListener implements DiagnosticListener<JavaFileObject>
    {
        @Override
        public void report(final Diagnostic<? extends JavaFileObject> diagnostic) {
            if (Objects.nonNull(diagnostic.getSource())) {
                JavaExecutionContext.LOGGER.warn("{} {}:{} - {}", new Object[] { ((JavaFileObject)diagnostic.getSource()).getName(), diagnostic.getLineNumber(), diagnostic.getColumnNumber(), diagnostic.getMessage(Locale.getDefault()) });
            }
            else {
                JavaExecutionContext.LOGGER.warn(diagnostic.getMessage(Locale.getDefault()), (Object)diagnostic.getSource());
            }
        }
    }
}
