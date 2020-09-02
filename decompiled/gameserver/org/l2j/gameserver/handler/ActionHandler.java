// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.enums.InstanceType;

public class ActionHandler implements IHandler<IActionHandler, InstanceType>
{
    private final Map<InstanceType, IActionHandler> _actions;
    
    private ActionHandler() {
        this._actions = new HashMap<InstanceType, IActionHandler>();
    }
    
    @Override
    public void registerHandler(final IActionHandler handler) {
        this._actions.put(handler.getInstanceType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IActionHandler handler) {
        this._actions.remove(handler.getInstanceType());
    }
    
    @Override
    public IActionHandler getHandler(final InstanceType iType) {
        IActionHandler result = null;
        for (InstanceType t = iType; t != null; t = t.getParent()) {
            result = this._actions.get(t);
            if (result != null) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public int size() {
        return this._actions.size();
    }
    
    public static ActionHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ActionHandler INSTANCE;
        
        static {
            INSTANCE = new ActionHandler();
        }
    }
}
