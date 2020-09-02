// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.model.skills.targets.AffectObject;

public class AffectObjectHandler implements IHandler<IAffectObjectHandler, Enum<AffectObject>>
{
    private final Map<Enum<AffectObject>, IAffectObjectHandler> _datatable;
    
    private AffectObjectHandler() {
        this._datatable = new HashMap<Enum<AffectObject>, IAffectObjectHandler>();
    }
    
    @Override
    public void registerHandler(final IAffectObjectHandler handler) {
        this._datatable.put(handler.getAffectObjectType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IAffectObjectHandler handler) {
        this._datatable.remove(handler.getAffectObjectType());
    }
    
    @Override
    public IAffectObjectHandler getHandler(final Enum<AffectObject> targetType) {
        return this._datatable.get(targetType);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static AffectObjectHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final AffectObjectHandler INSTANCE;
        
        static {
            INSTANCE = new AffectObjectHandler();
        }
    }
}
