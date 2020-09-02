// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerQuestAbort implements IBaseEvent
{
    private final Player _activeChar;
    private final int _questId;
    
    public OnPlayerQuestAbort(final Player activeChar, final int questId) {
        this._activeChar = activeChar;
        this._questId = questId;
    }
    
    public final Player getActiveChar() {
        return this._activeChar;
    }
    
    public final int getQuestId() {
        return this._questId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_QUEST_ABORT;
    }
}
