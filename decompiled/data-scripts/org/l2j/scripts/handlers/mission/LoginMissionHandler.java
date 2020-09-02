// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import java.util.Calendar;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class LoginMissionHandler extends AbstractMissionHandler
{
    private byte days;
    
    private LoginMissionHandler(final MissionDataHolder holder) {
        super(holder);
        this.days = 0;
        final String days = holder.getParams().getString("days", "");
        for (final String day : days.split(" ")) {
            if (Util.isInteger(day)) {
                this.days |= (byte)(1 << Integer.parseInt(day));
            }
        }
    }
    
    public boolean isAvailable(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, false);
        return entry != null && MissionStatus.AVAILABLE == entry.getStatus();
    }
    
    public void init() {
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_PLAYER_LOGIN, (Consumer)this::onPlayerLogin, (Object)this));
    }
    
    private void onPlayerLogin(final OnPlayerLogin event) {
        final MissionPlayerData entry = this.getPlayerEntry(event.getPlayer(), true);
        if (MissionStatus.COMPLETED.equals((Object)entry.getStatus())) {
            return;
        }
        final int currentDay = Calendar.getInstance().get(7);
        if (this.days != 0 && (this.days & 1 << currentDay) == 0x0) {
            entry.setProgress(0);
            entry.setStatus(MissionStatus.NOT_AVAILABLE);
        }
        else {
            entry.setProgress(1);
            entry.setStatus(MissionStatus.AVAILABLE);
            this.notifyAvailablesReward(event.getPlayer());
        }
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new LoginMissionHandler(data);
        }
        
        public String handlerName() {
            return "login";
        }
    }
}
