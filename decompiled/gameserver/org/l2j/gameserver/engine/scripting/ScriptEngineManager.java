// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.nio.file.FileVisitor;
import java.io.IOException;
import java.util.LinkedList;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import org.slf4j.Logger;

public final class ScriptEngineManager
{
    private static final Logger LOGGER;
    public static final Path SCRIPT_FOLDER;
    private final Map<String, IExecutionContext> extEngines;
    private IExecutionContext currentExecutionContext;
    
    private ScriptEngineManager() {
        this.extEngines = new HashMap<String, IExecutionContext>();
        this.currentExecutionContext = null;
    }
    
    public static void init() {
        final ScriptEngineManager instance = getInstance();
        instance.loadEngines();
        try {
            instance.executeScriptInit();
        }
        catch (Exception e) {
            ScriptEngineManager.LOGGER.error("Could not execute Scripts Init", (Throwable)e);
        }
    }
    
    private void loadEngines() {
        final Properties props = this.loadProperties();
        ServiceLoader.load(IScriptingEngine.class).forEach(engine -> this.registerEngine(engine, props));
    }
    
    private Properties loadProperties() {
        final Properties props = new Properties();
        try {
            final FileInputStream fis = new FileInputStream("config/ScriptEngine.properties");
            try {
                props.load(fis);
                fis.close();
            }
            catch (Throwable t) {
                try {
                    fis.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception e) {
            ScriptEngineManager.LOGGER.warn("Couldn't load ScriptEngine.properties", (Throwable)e);
        }
        return props;
    }
    
    private void registerEngine(final IScriptingEngine engine, final Properties props) {
        this.maybeSetProperties(engine.getLanguageName().toLowerCase(), props, engine);
        ScriptEngineManager.LOGGER.info("{} {} ({} {})", new Object[] { engine.getEngineName(), engine.getEngineVersion(), engine.getLanguageName(), engine.getLanguageVersion() });
        final IExecutionContext context = engine.createExecutionContext();
        for (final String commonExtension : engine.getCommonFileExtensions()) {
            this.extEngines.put(commonExtension, context);
        }
    }
    
    private void maybeSetProperties(final String language, final Properties props, final IScriptingEngine engine) {
        for (final Map.Entry<Object, Object> prop : props.entrySet()) {
            String key = prop.getKey();
            String value = prop.getValue();
            if (key.startsWith(language)) {
                key = key.substring(language.length() + 1);
                if (value.startsWith("%") && value.endsWith("%")) {
                    value = System.getProperty(value.substring(1, value.length() - 1));
                }
                engine.setProperty(key, value);
            }
        }
    }
    
    private IExecutionContext getEngineByExtension(final String ext) {
        return this.extEngines.get(ext);
    }
    
    private String getFileExtension(final Path p) {
        final String name = p.getFileName().toString();
        final int lastDotIdx = name.lastIndexOf(46);
        if (lastDotIdx == -1) {
            return null;
        }
        final String extension = name.substring(lastDotIdx + 1);
        if (extension.isEmpty()) {
            return null;
        }
        return extension;
    }
    
    private void checkExistingFile(final Path filePath) throws Exception {
        if (!Files.exists(filePath, new LinkOption[0])) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/nio/file/Path;)Ljava/lang/String;, filePath));
        }
        if (!Files.isRegularFile(filePath, new LinkOption[0])) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/nio/file/Path;)Ljava/lang/String;, filePath));
        }
    }
    
    public void executeScriptInit() throws Exception {
        this.executeScripts("Init");
    }
    
    private void executeScripts(final String scriptsName) throws Exception {
        for (final Map.Entry<IExecutionContext, List<Path>> entry : this.parseScriptDirectory(scriptsName).entrySet()) {
            this.currentExecutionContext = entry.getKey();
            try {
                final Map<Path, Throwable> invokationErrors = this.currentExecutionContext.executeScripts(entry.getValue());
                for (final Map.Entry<Path, Throwable> entry2 : invokationErrors.entrySet()) {
                    ScriptEngineManager.LOGGER.warn("{} failed execution!", (Object)entry2.getKey(), (Object)entry2.getValue());
                }
            }
            finally {
                this.currentExecutionContext = null;
            }
        }
    }
    
    public void executeScriptLoader() throws Exception {
        this.executeScripts("Loader");
    }
    
    private Map<IExecutionContext, List<Path>> parseScriptDirectory(final String names) throws IOException {
        final Map<IExecutionContext, List<Path>> files = new HashMap<IExecutionContext, List<Path>>();
        Files.walkFileTree(ScriptEngineManager.SCRIPT_FOLDER, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                final String fileName = file.getFileName().toString();
                if (attrs.isRegularFile() && fileName.startsWith(names)) {
                    final String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (ext.equals(fileName)) {
                        ScriptEngineManager.LOGGER.warn("ScriptFile: {} does not have an extension to determine the script engine!", (Object)file);
                        return FileVisitResult.CONTINUE;
                    }
                    final IExecutionContext engine = ScriptEngineManager.this.getEngineByExtension(ext);
                    if (engine == null) {
                        return FileVisitResult.CONTINUE;
                    }
                    files.computeIfAbsent(engine, k -> new LinkedList()).add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }
    
    public void executeScript(Path sourceFile) throws Exception {
        Objects.requireNonNull(sourceFile);
        if (sourceFile.isAbsolute()) {
            sourceFile = ScriptEngineManager.SCRIPT_FOLDER.toAbsolutePath().relativize(sourceFile);
        }
        this.checkExistingFile(sourceFile);
        final String ext = this.getFileExtension(sourceFile);
        Objects.requireNonNull(sourceFile, invokedynamic(makeConcatWithConstants:(Ljava/nio/file/Path;)Ljava/lang/String;, sourceFile));
        final IExecutionContext engine = this.getEngineByExtension(ext);
        Objects.requireNonNull(engine, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ext));
        this.currentExecutionContext = engine;
        try {
            final Map.Entry<Path, Throwable> error = engine.executeScript(sourceFile);
            if (error != null) {
                throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, error.getKey()), (Throwable)error.getValue());
            }
        }
        finally {
            this.currentExecutionContext = null;
        }
    }
    
    public Path getCurrentLoadingScript() {
        return (this.currentExecutionContext != null) ? this.currentExecutionContext.getCurrentExecutingScript() : null;
    }
    
    public static ScriptEngineManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ScriptEngineManager.class);
        SCRIPT_FOLDER = ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/scripts");
    }
    
    private static class Singleton
    {
        protected static final ScriptEngineManager INSTANCE;
        
        static {
            INSTANCE = new ScriptEngineManager();
        }
    }
}
