// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class LevelMissionHandler extends AbstractMissionHandler
{
    private LevelMissionHandler(final MissionDataHolder holder) {
        super(holder);
    }
    
    public void init() {
        Listeners.players().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_PLAYER_LEVEL_CHANGED, (Consumer)this::onPlayerLevelChanged, (Object)this));
    }
    
    public int getProgress(final Player player) {
        return player.getLevel();
    }
    
    public int getStatus(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, true);
        return Objects.nonNull(entry) ? entry.getStatus().getClientId() : MissionStatus.NOT_AVAILABLE.getClientId();
    }
    
    private void onPlayerLevelChanged(final OnPlayerLevelChanged event) {
        final Player player = event.getActiveChar();
        if (player.getLevel() >= this.getRequiredCompletion()) {
            final MissionPlayerData entry = this.getPlayerEntry(player, true);
            if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
                entry.setStatus(MissionStatus.AVAILABLE);
                this.storePlayerEntry(entry);
                this.notifyAvailablesReward(player);
            }
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new LevelMissionHandler(data);
        }
        
        public String handlerName() {
            return "level";
        }
    }
}
