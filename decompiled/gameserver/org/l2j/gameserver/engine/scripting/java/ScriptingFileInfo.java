// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting.java;

import javax.tools.JavaFileManager;
import java.nio.file.Path;

final class ScriptingFileInfo
{
    private final Path _sourcePath;
    private final String _javaName;
    private final String moduleName;
    private final JavaFileManager.Location location;
    
    public ScriptingFileInfo(final Path scriptPath, final String javaName, final String moduleName, final JavaFileManager.Location location) {
        this._sourcePath = scriptPath;
        this._javaName = javaName;
        this.moduleName = moduleName;
        this.location = location;
    }
    
    public Path getSourcePath() {
        return this._sourcePath;
    }
    
    public String getJavaName() {
        return this._javaName;
    }
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public JavaFileManager.Location getLocation() {
        return this.location;
    }
}
