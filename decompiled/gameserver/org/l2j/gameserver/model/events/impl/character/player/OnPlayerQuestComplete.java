// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerQuestComplete implements IBaseEvent
{
    private final Player _activeChar;
    private final int _questId;
    private final QuestType _questType;
    
    public OnPlayerQuestComplete(final Player activeChar, final int questId, final QuestType questType) {
        this._activeChar = activeChar;
        this._questId = questId;
        this._questType = questType;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getQuestId() {
        return this._questId;
    }
    
    public QuestType getQuestType() {
        return this._questType;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_QUEST_COMPLETE;
    }
}
