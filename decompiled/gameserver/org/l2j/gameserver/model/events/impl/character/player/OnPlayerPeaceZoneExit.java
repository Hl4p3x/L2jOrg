// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPeaceZoneExit implements IBaseEvent
{
    private final Player player;
    
    public OnPlayerPeaceZoneExit(final Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PEACE_ZONE_EXIT;
    }
}
