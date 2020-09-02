// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerChat implements IBaseEvent
{
    private final Player _activeChar;
    private final String _target;
    private final String _text;
    private final ChatType _type;
    
    public OnPlayerChat(final Player activeChar, final String target, final String text, final ChatType type) {
        this._activeChar = activeChar;
        this._target = target;
        this._text = text;
        this._type = type;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public String getTarget() {
        return this._target;
    }
    
    public String getText() {
        return this._text;
    }
    
    public ChatType getChatType() {
        return this._type;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CHAT;
    }
}
