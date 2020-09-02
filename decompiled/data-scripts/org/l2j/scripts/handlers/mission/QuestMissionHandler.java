// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestComplete;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class QuestMissionHandler extends AbstractMissionHandler
{
    private QuestMissionHandler(final MissionDataHolder holder) {
        super(holder);
    }
    
    public void init() {
        Listeners.players().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_PLAYER_QUEST_COMPLETE, event -> this.onQuestComplete(event), (Object)this));
    }
    
    private void onQuestComplete(final OnPlayerQuestComplete event) {
        final Player player = event.getActiveChar();
        if (event.getQuestType() == QuestType.DAILY) {
            final MissionPlayerData entry = this.getPlayerEntry(player, true);
            if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
                if (entry.increaseProgress() >= this.getRequiredCompletion()) {
                    entry.setStatus(MissionStatus.AVAILABLE);
                    this.notifyAvailablesReward(player);
                }
                this.storePlayerEntry(entry);
            }
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new QuestMissionHandler(data);
        }
        
        public String handlerName() {
            return "quest";
        }
    }
}
