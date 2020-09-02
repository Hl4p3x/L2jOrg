// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

public class BypassHandler implements IHandler<IBypassHandler, String>
{
    private final Map<String, IBypassHandler> _datatable;
    
    private BypassHandler() {
        this._datatable = new HashMap<String, IBypassHandler>();
    }
    
    @Override
    public void registerHandler(final IBypassHandler handler) {
        for (final String element : handler.getBypassList()) {
            this._datatable.put(element.toLowerCase(), handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IBypassHandler handler) {
        for (final String element : handler.getBypassList()) {
            this._datatable.remove(element.toLowerCase());
        }
    }
    
    @Override
    public IBypassHandler getHandler(String command) {
        if (command.contains(" ")) {
            command = command.substring(0, command.indexOf(" "));
        }
        return this._datatable.get(command.toLowerCase());
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static BypassHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final BypassHandler INSTANCE;
        
        static {
            INSTANCE = new BypassHandler();
        }
    }
}
