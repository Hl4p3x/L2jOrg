// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.model.skills.targets.AffectScope;

public class AffectScopeHandler implements IHandler<IAffectScopeHandler, Enum<AffectScope>>
{
    private final Map<Enum<AffectScope>, IAffectScopeHandler> _datatable;
    
    private AffectScopeHandler() {
        this._datatable = new HashMap<Enum<AffectScope>, IAffectScopeHandler>();
    }
    
    @Override
    public void registerHandler(final IAffectScopeHandler handler) {
        this._datatable.put(handler.getAffectScopeType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IAffectScopeHandler handler) {
        this._datatable.remove(handler.getAffectScopeType());
    }
    
    @Override
    public IAffectScopeHandler getHandler(final Enum<AffectScope> affectScope) {
        return this._datatable.get(affectScope);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static AffectScopeHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        protected static final AffectScopeHandler INSTANCE;
        
        static {
            INSTANCE = new AffectScopeHandler();
        }
    }
}
