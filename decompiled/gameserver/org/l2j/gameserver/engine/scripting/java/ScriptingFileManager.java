// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting.java;

import java.util.Iterator;
import java.io.File;
import java.util.Objects;
import javax.tools.StandardLocation;
import java.util.Collections;
import java.util.Collection;
import java.io.IOException;
import org.l2j.commons.util.Util;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileManager;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.nio.file.Path;
import java.util.Map;
import javax.tools.StandardJavaFileManager;
import javax.tools.ForwardingJavaFileManager;

final class ScriptingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
{
    private final Map<Path, ScriptingFileInfo> scriptsFileInfo;
    private final Set<String> moduleNames;
    
    ScriptingFileManager(final StandardJavaFileManager fileManager) {
        super(fileManager);
        this.scriptsFileInfo = new HashMap<Path, ScriptingFileInfo>();
        this.moduleNames = new HashSet<String>();
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(final JavaFileManager.Location location, String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
        final JavaFileObject javaFileObject = super.getJavaFileForOutput(location, className, kind, sibling);
        if (kind == JavaFileObject.Kind.CLASS) {
            if (className.contains("/")) {
                className = className.replace('/', '.');
            }
            final Path scriptPath = Path.of(sibling.getName(), new String[0]);
            final String moduleName = this.inferModuleName(location);
            final ScriptingFileInfo scriptFileInfo = new ScriptingFileInfo(scriptPath, className, moduleName, location);
            this.scriptsFileInfo.put(scriptPath, scriptFileInfo);
            if (!Util.isNullOrEmpty((CharSequence)moduleName)) {
                this.moduleNames.add(moduleName);
            }
        }
        return javaFileObject;
    }
    
    Set<String> getModuleNames() {
        return this.moduleNames;
    }
    
    ScriptingFileInfo getScriptInfo(final Path scriptPath) {
        return this.scriptsFileInfo.get(scriptPath);
    }
    
    Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(final Collection<Path> paths) {
        return (Iterable<? extends JavaFileObject>)((StandardJavaFileManager)this.fileManager).getJavaFileObjectsFromPaths((Collection)paths);
    }
    
    boolean beAwareOfObjectFile(final Path path, final Path compiled) throws IOException {
        final Iterable<? extends JavaFileObject> filesObject = this.getJavaFileObjectsFromPaths(Collections.singletonList(compiled));
        final Iterator<? extends JavaFileObject> it = filesObject.iterator();
        if (!it.hasNext()) {
            return false;
        }
        final JavaFileObject javaFileObject = (JavaFileObject)it.next();
        final JavaFileManager.Location classLocation = this.getLocationForModule(StandardLocation.CLASS_OUTPUT, javaFileObject);
        String module;
        Path parentPath;
        for (module = this.inferModuleName(classLocation), parentPath = compiled.getParent(); Objects.nonNull(parentPath) && !parentPath.getFileName().toString().equals(module); parentPath = parentPath.getParent()) {}
        if (Objects.isNull(parentPath)) {
            return false;
        }
        final String className = parentPath.relativize(compiled).toString().replace(".class", "").replace(File.separator, ".");
        this.scriptsFileInfo.putIfAbsent(path, new ScriptingFileInfo(path, className, module, classLocation));
        return true;
    }
}
