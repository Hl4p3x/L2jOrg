// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMenteeAdd implements IBaseEvent
{
    private final Player _mentor;
    private final Player _mentee;
    
    public OnPlayerMenteeAdd(final Player mentor, final Player mentee) {
        this._mentor = mentor;
        this._mentee = mentee;
    }
    
    public Player getMentor() {
        return this._mentor;
    }
    
    public Player getMentee() {
        return this._mentee;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MENTEE_ADD;
    }
}
