// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerFishing implements IBaseEvent
{
    private final Player _player;
    private final ExFishingEnd.FishingEndReason _reason;
    
    public OnPlayerFishing(final Player player, final ExFishingEnd.FishingEndReason reason) {
        this._player = player;
        this._reason = reason;
    }
    
    public Player getActiveChar() {
        return this._player;
    }
    
    public ExFishingEnd.FishingEndReason getReason() {
        return this._reason;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_FISHING;
    }
}
