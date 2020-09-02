// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMenteeLeft implements IBaseEvent
{
    private final Mentee _mentor;
    private final Player _mentee;
    
    public OnPlayerMenteeLeft(final Mentee mentor, final Player mentee) {
        this._mentor = mentor;
        this._mentee = mentee;
    }
    
    public Mentee getMentor() {
        return this._mentor;
    }
    
    public Player getMentee() {
        return this._mentee;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MENTEE_LEFT;
    }
}
