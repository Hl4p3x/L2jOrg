// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import org.slf4j.Logger;

public abstract class ManagedScript
{
    private static final Logger LOGGER;
    private final Path _scriptFile;
    private long _lastLoadTime;
    private boolean _isActive;
    
    public ManagedScript() {
        this._scriptFile = this.getScriptPath();
        this.setLastLoadTime(System.currentTimeMillis());
    }
    
    public abstract Path getScriptPath();
    
    public boolean reload() {
        try {
            ScriptEngineManager.getInstance().executeScript(this.getScriptFile());
            return true;
        }
        catch (Exception e) {
            ManagedScript.LOGGER.warn("Failed to reload script!", (Throwable)e);
            return false;
        }
    }
    
    public abstract boolean unload();
    
    public boolean isActive() {
        return this._isActive;
    }
    
    public void setActive(final boolean status) {
        this._isActive = status;
    }
    
    public Path getScriptFile() {
        return this._scriptFile;
    }
    
    protected long getLastLoadTime() {
        return this._lastLoadTime;
    }
    
    protected void setLastLoadTime(final long lastLoadTime) {
        this._lastLoadTime = lastLoadTime;
    }
    
    public abstract String getScriptName();
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ManagedScript.class);
    }
}
