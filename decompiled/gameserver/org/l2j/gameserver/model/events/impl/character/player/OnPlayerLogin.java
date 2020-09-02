// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerLogin implements IBaseEvent
{
    private final Player player;
    
    public OnPlayerLogin(final Player activeChar) {
        this.player = activeChar;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_LOGIN;
    }
}
