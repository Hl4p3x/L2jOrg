// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMenteeStatus implements IBaseEvent
{
    private final Player _mentee;
    private final boolean _isOnline;
    
    public OnPlayerMenteeStatus(final Player mentee, final boolean isOnline) {
        this._mentee = mentee;
        this._isOnline = isOnline;
    }
    
    public Player getMentee() {
        return this._mentee;
    }
    
    public boolean isMenteeOnline() {
        return this._isOnline;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MENTEE_STATUS;
    }
}
