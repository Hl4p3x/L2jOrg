// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMentorStatus implements IBaseEvent
{
    private final Player _mentor;
    private final boolean _isOnline;
    
    public OnPlayerMentorStatus(final Player mentor, final boolean isOnline) {
        this._mentor = mentor;
        this._isOnline = isOnline;
    }
    
    public Player getMentor() {
        return this._mentor;
    }
    
    public boolean isMentorOnline() {
        return this._isOnline;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MENTOR_STATUS;
    }
}
