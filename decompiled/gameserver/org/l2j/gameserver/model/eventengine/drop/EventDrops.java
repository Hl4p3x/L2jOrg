// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.drop;

import java.util.function.Supplier;

public enum EventDrops
{
    GROUPED((Supplier<IEventDrop>)GroupedDrop::new), 
    NORMAL((Supplier<IEventDrop>)NormalDrop::new);
    
    private final Supplier<? extends IEventDrop> _supplier;
    
    private EventDrops(final Supplier<IEventDrop> supplier) {
        this._supplier = supplier;
    }
    
    public <T extends IEventDrop> T newInstance() {
        return (T)this._supplier.get();
    }
}
