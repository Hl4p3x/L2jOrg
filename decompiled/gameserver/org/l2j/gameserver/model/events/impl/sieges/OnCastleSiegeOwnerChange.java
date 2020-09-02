// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.sieges;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCastleSiegeOwnerChange implements IBaseEvent
{
    private final Siege _siege;
    
    public OnCastleSiegeOwnerChange(final Siege siege) {
        this._siege = siege;
    }
    
    public Siege getSiege() {
        return this._siege;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CASTLE_SIEGE_OWNER_CHANGE;
    }
}
