// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMenteeRemove implements IBaseEvent
{
    private final Player _mentor;
    private final Mentee _mentee;
    
    public OnPlayerMenteeRemove(final Player mentor, final Mentee mentee) {
        this._mentor = mentor;
        this._mentee = mentee;
    }
    
    public Player getMentor() {
        return this._mentor;
    }
    
    public Mentee getMentee() {
        return this._mentee;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MENTEE_REMOVE;
    }
}
