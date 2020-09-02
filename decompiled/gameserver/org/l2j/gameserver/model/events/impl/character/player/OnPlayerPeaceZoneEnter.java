// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.world.zone.type.PeaceZone;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPeaceZoneEnter implements IBaseEvent
{
    private final Player player;
    private final PeaceZone zone;
    
    public OnPlayerPeaceZoneEnter(final Player player, final PeaceZone peaceZone) {
        this.player = player;
        this.zone = peaceZone;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public PeaceZone getZone() {
        return this.zone;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PEACE_ZONE_ENTER;
    }
}
