// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureZoneEnter implements IBaseEvent
{
    private final Creature _creature;
    private final Zone _zone;
    
    public OnCreatureZoneEnter(final Creature creature, final Zone zone) {
        this._creature = creature;
        this._zone = zone;
    }
    
    public Creature getCreature() {
        return this._creature;
    }
    
    public Zone getZone() {
        return this._zone;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_ZONE_ENTER;
    }
}
