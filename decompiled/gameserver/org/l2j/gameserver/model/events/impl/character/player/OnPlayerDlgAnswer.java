// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerDlgAnswer implements IBaseEvent
{
    private final Player _activeChar;
    private final int _messageId;
    private final int _answer;
    private final int _requesterId;
    
    public OnPlayerDlgAnswer(final Player activeChar, final int messageId, final int answer, final int requesterId) {
        this._activeChar = activeChar;
        this._messageId = messageId;
        this._answer = answer;
        this._requesterId = requesterId;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getMessageId() {
        return this._messageId;
    }
    
    public int getAnswer() {
        return this._answer;
    }
    
    public int getRequesterId() {
        return this._requesterId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_DLG_ANSWER;
    }
}
