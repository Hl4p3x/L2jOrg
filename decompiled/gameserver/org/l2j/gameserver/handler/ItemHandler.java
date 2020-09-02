// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.model.item.EtcItem;

public class ItemHandler implements IHandler<IItemHandler, EtcItem>
{
    private final Map<String, IItemHandler> _datatable;
    
    private ItemHandler() {
        this._datatable = new HashMap<String, IItemHandler>();
    }
    
    @Override
    public void registerHandler(final IItemHandler handler) {
        this._datatable.put(handler.getClass().getSimpleName(), handler);
    }
    
    @Override
    public synchronized void removeHandler(final IItemHandler handler) {
        this._datatable.remove(handler.getClass().getSimpleName());
    }
    
    @Override
    public IItemHandler getHandler(final EtcItem item) {
        if (item == null || item.getHandlerName() == null) {
            return null;
        }
        return this._datatable.get(item.getHandlerName());
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static ItemHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ItemHandler INSTANCE;
        
        static {
            INSTANCE = new ItemHandler();
        }
    }
}
