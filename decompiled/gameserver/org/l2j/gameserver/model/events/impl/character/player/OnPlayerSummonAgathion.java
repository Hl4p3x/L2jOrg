// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerSummonAgathion implements IBaseEvent
{
    private final Player _player;
    private final int _agathionId;
    
    public OnPlayerSummonAgathion(final Player player, final int agathionId) {
        this._player = player;
        this._agathionId = agathionId;
    }
    
    public Player getPlayer() {
        return this._player;
    }
    
    public int getAgathionId() {
        return this._agathionId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SUMMON_AGATHION;
    }
}
