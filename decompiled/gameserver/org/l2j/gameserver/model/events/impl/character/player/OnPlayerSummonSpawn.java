// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerSummonSpawn implements IBaseEvent
{
    private final Summon _summon;
    
    public OnPlayerSummonSpawn(final Summon summon) {
        this._summon = summon;
    }
    
    public Summon getSummon() {
        return this._summon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SUMMON_SPAWN;
    }
}
