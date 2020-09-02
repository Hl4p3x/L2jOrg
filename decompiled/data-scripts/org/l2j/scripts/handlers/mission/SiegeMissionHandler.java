// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
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

public class SiegeMissionHandler extends AbstractMissionHandler
{
    private SiegeMissionHandler(final MissionDataHolder holder) {
        super(holder);
    }
    
    public void init() {
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_CASTLE_SIEGE_START, (Consumer)this::onSiegeStart, (Object)this));
    }
    
    public boolean isAvailable(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, false);
        return Objects.nonNull(entry) && MissionStatus.AVAILABLE == entry.getStatus();
    }
    
    private void onSiegeStart(final OnCastleSiegeStart event) {
        event.getSiege().getAttackerClans().values().forEach(this::processSiegeClan);
        event.getSiege().getDefenderClans().values().forEach(this::processSiegeClan);
    }
    
    private void processSiegeClan(final SiegeClanData siegeClan) {
        final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
        if (clan != null) {
            final MissionPlayerData entry;
            clan.getOnlineMembers(0).forEach(player -> {
                entry = this.getPlayerEntry(player, true);
                entry.setStatus(MissionStatus.AVAILABLE);
                this.notifyAvailablesReward(player);
                this.storePlayerEntry(entry);
            });
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new SiegeMissionHandler(data);
        }
        
        public String handlerName() {
            return "siege";
        }
    }
}
