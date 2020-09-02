// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerBypass implements IBaseEvent
{
    private final Player player;
    private final String _command;
    
    public OnPlayerBypass(final Player activeChar, final String command) {
        this.player = activeChar;
        this._command = command;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getCommand() {
        return this._command;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_BYPASS;
    }
}
