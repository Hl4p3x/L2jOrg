// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnPlayerTutorialEvent implements IBaseEvent
{
    private final Player player;
    private final int event;
    
    public OnPlayerTutorialEvent(final Player activeChar, final int event) {
        this.player = activeChar;
        this.event = event;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getEventId() {
        return this.event;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_TUTORIAL_EVENT;
    }
}
