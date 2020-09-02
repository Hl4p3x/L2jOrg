// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

import java.util.HashMap;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractExecutionContext<T extends IScriptingEngine> implements IExecutionContext
{
    private final T _engine;
    private final Map<String, String> _properties;
    private volatile Path _currentExecutingScipt;
    
    protected AbstractExecutionContext(final T engine) {
        if (engine == null) {
            throw new IllegalArgumentException();
        }
        this._engine = engine;
        this._properties = new HashMap<String, String>();
    }
    
    @Override
    public final String setProperty(final String key, final String value) {
        return this._properties.put(key, value);
    }
    
    @Override
    public final String getProperty(final String key) {
        if (!this._properties.containsKey(key)) {
            return this._engine.getProperty(key);
        }
        return this._properties.get(key);
    }
    
    @Override
    public final Path getCurrentExecutingScript() {
        return this._currentExecutingScipt;
    }
    
    protected final void setCurrentExecutingScript(final Path currentExecutingScript) {
        this._currentExecutingScipt = currentExecutingScript;
    }
    
    @Override
    public final T getScriptingEngine() {
        return this._engine;
    }
}
