// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class OlympiadMissionHandler extends AbstractMissionHandler
{
    private final boolean winnerOnly;
    
    private OlympiadMissionHandler(final MissionDataHolder holder) {
        super(holder);
        this.winnerOnly = holder.getParams().getBoolean("win", false);
    }
    
    public void init() {
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_OLYMPIAD_MATCH_RESULT, (Consumer)this::onOlympiadMatchResult, (Object)this));
    }
    
    private void onOlympiadMatchResult(final OnOlympiadMatchResult event) {
        if (Objects.nonNull(event.getWinner())) {
            final MissionPlayerData winnerEntry = this.getPlayerEntry(event.getWinner().getPlayer(), true);
            this.increaseProgress(winnerEntry, event.getWinner().getPlayer());
        }
        if (Objects.nonNull(event.getLoser()) && !this.winnerOnly) {
            final MissionPlayerData loseEntry = this.getPlayerEntry(event.getLoser().getPlayer(), true);
            this.increaseProgress(loseEntry, event.getLoser().getPlayer());
        }
    }
    
    private void increaseProgress(final MissionPlayerData entry, final Player player) {
        if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
            if (entry.increaseProgress() >= this.getRequiredCompletion()) {
                entry.setStatus(MissionStatus.AVAILABLE);
                this.notifyAvailablesReward(player);
            }
            this.storePlayerEntry(entry);
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new OlympiadMissionHandler(data);
        }
        
        public String handlerName() {
            return "olympiad";
        }
    }
}
