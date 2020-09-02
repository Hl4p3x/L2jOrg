// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerReputationChanged implements IBaseEvent
{
    private final Player _activeChar;
    private final int _oldReputation;
    private final int _newReputation;
    
    public OnPlayerReputationChanged(final Player activeChar, final int oldReputation, final int newReputation) {
        this._activeChar = activeChar;
        this._oldReputation = oldReputation;
        this._newReputation = newReputation;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getOldReputation() {
        return this._oldReputation;
    }
    
    public int getNewReputation() {
        return this._newReputation;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_REPUTATION_CHANGED;
    }
}
