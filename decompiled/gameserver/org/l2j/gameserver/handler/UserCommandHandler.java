// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

public class UserCommandHandler implements IHandler<IUserCommandHandler, Integer>
{
    private final Map<Integer, IUserCommandHandler> _datatable;
    
    private UserCommandHandler() {
        this._datatable = new HashMap<Integer, IUserCommandHandler>();
    }
    
    @Override
    public void registerHandler(final IUserCommandHandler handler) {
        for (final int id : handler.getUserCommandList()) {
            this._datatable.put(id, handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IUserCommandHandler handler) {
        for (final int id : handler.getUserCommandList()) {
            this._datatable.remove(id);
        }
    }
    
    @Override
    public IUserCommandHandler getHandler(final Integer userCommand) {
        return this._datatable.get(userCommand);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static UserCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final UserCommandHandler INSTANCE;
        
        static {
            INSTANCE = new UserCommandHandler();
        }
    }
}
