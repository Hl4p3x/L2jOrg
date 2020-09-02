// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.model.punishment.PunishmentType;

public class PunishmentHandler implements IHandler<IPunishmentHandler, PunishmentType>
{
    private final Map<PunishmentType, IPunishmentHandler> _handlers;
    
    private PunishmentHandler() {
        this._handlers = new HashMap<PunishmentType, IPunishmentHandler>();
    }
    
    @Override
    public void registerHandler(final IPunishmentHandler handler) {
        this._handlers.put(handler.getType(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IPunishmentHandler handler) {
        this._handlers.remove(handler.getType());
    }
    
    @Override
    public IPunishmentHandler getHandler(final PunishmentType val) {
        return this._handlers.get(val);
    }
    
    @Override
    public int size() {
        return this._handlers.size();
    }
    
    public static PunishmentHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final PunishmentHandler INSTANCE;
        
        static {
            INSTANCE = new PunishmentHandler();
        }
    }
}
