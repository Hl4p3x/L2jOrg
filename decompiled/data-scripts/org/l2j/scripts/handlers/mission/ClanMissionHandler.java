// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class ClanMissionHandler extends AbstractMissionHandler
{
    private ClanMissionHandler(final MissionDataHolder holder) {
        super(holder);
    }
    
    public boolean isAvailable(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, false);
        return Objects.nonNull(entry) && MissionStatus.AVAILABLE == entry.getStatus();
    }
    
    public void init() {
        if (MissionKind.JOIN == this.getHolder().getParams().getEnum("kind", (Class)MissionKind.class, (Enum)MissionKind.JOIN)) {
            Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_PLAYER_CLAN_JOIN, (Consumer)this::onPlayerJoinClan, (Object)this));
        }
    }
    
    private void onPlayerJoinClan(final OnPlayerClanJoin event) {
        final MissionPlayerData entry = this.getPlayerEntry(event.getActiveChar().getPlayerInstance(), true);
        if (MissionStatus.COMPLETED.equals((Object)entry.getStatus())) {
            return;
        }
        entry.setProgress(1);
        entry.setStatus(MissionStatus.AVAILABLE);
        this.storePlayerEntry(entry);
        this.notifyAvailablesReward(event.getActiveChar().getPlayerInstance());
    }
    
    enum MissionKind
    {
        JOIN, 
        ARENA;
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new ClanMissionHandler(data);
        }
        
        public String handlerName() {
            return "clan";
        }
    }
}
