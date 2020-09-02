// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.model.skills.targets.TargetType;

public class TargetHandler implements IHandler<ITargetTypeHandler, Enum<TargetType>>
{
    private final Map<Enum<TargetType>, ITargetTypeHandler> _datatable;
    
    private TargetHandler() {
        this._datatable = new HashMap<Enum<TargetType>, ITargetTypeHandler>();
    }
    
    @Override
    public void registerHandler(final ITargetTypeHandler handler) {
        this._datatable.put(handler.getTargetType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final ITargetTypeHandler handler) {
        this._datatable.remove(handler.getTargetType());
    }
    
    @Override
    public ITargetTypeHandler getHandler(final Enum<TargetType> targetType) {
        return this._datatable.get(targetType);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static TargetHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        protected static final TargetHandler INSTANCE;
        
        static {
            INSTANCE = new TargetHandler();
        }
    }
}
