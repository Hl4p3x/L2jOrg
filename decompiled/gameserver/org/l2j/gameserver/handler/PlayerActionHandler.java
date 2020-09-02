// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

public class PlayerActionHandler implements IHandler<IPlayerActionHandler, String>
{
    private final Map<String, IPlayerActionHandler> _actions;
    
    private PlayerActionHandler() {
        this._actions = new HashMap<String, IPlayerActionHandler>();
    }
    
    @Override
    public void registerHandler(final IPlayerActionHandler handler) {
        this._actions.put(handler.getClass().getSimpleName(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IPlayerActionHandler handler) {
        this._actions.remove(handler.getClass().getSimpleName());
    }
    
    @Override
    public IPlayerActionHandler getHandler(final String name) {
        return this._actions.get(name);
    }
    
    @Override
    public int size() {
        return this._actions.size();
    }
    
    public static PlayerActionHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final PlayerActionHandler INSTANCE;
        
        static {
            INSTANCE = new PlayerActionHandler();
        }
    }
}
