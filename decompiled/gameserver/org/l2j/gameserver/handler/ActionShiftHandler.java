// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.enums.InstanceType;

public class ActionShiftHandler implements IHandler<IActionShiftHandler, InstanceType>
{
    private final Map<InstanceType, IActionShiftHandler> _actionsShift;
    
    private ActionShiftHandler() {
        this._actionsShift = new HashMap<InstanceType, IActionShiftHandler>();
    }
    
    @Override
    public void registerHandler(final IActionShiftHandler handler) {
        this._actionsShift.put(handler.getInstanceType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IActionShiftHandler handler) {
        this._actionsShift.remove(handler.getInstanceType());
    }
    
    @Override
    public IActionShiftHandler getHandler(final InstanceType iType) {
        IActionShiftHandler result = null;
        for (InstanceType t = iType; t != null; t = t.getParent()) {
            result = this._actionsShift.get(t);
            if (result != null) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public int size() {
        return this._actionsShift.size();
    }
    
    public static ActionShiftHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ActionShiftHandler INSTANCE;
        
        static {
            INSTANCE = new ActionShiftHandler();
        }
    }
}
