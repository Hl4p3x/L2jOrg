// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;
import java.time.LocalDate;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.OlympiadDAO;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.data.database.data.OlympiadData;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class OlympiadEngine extends AbstractEventManager<AbstractEvent<?>>
{
    private static final Logger LOGGER;
    private OlympiadData data;
    private boolean matchInProgress;
    private ConsumerEventListener onPlayerLoginListener;
    
    private OlympiadEngine() {
        this.data = new OlympiadData();
    }
    
    @Override
    public void onInitialized() {
        this.data = ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).findData();
        final LocalDate startDate = LocalDate.parse(this.getVariables().getString("start-date", "2020-06-22"));
        if (Objects.isNull(this.data)) {
            (this.data = new OlympiadData()).setNextSeasonDate(startDate);
            ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).save((Object)this.data);
        }
        else if (this.getVariables().getBoolean("force-start-date", false)) {
            this.data.setNextSeasonDate(startDate);
        }
        if (this.data.getNextSeasonDate().isAfter(LocalDate.now())) {
            OlympiadEngine.LOGGER.info("World Olympiad season start scheduled to {}", (Object)this.data.getNextSeasonDate());
        }
        else if (this.data.getSeason() > 0) {
            OlympiadEngine.LOGGER.info("World Olympiad {} season has been started", (Object)this.data.getSeason());
        }
        AntiFeedManager.getInstance().registerEvent(1);
    }
    
    public void onStartMatch() {
        this.matchInProgress = true;
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.show(OlympiadRuleType.MAX, 300));
        final ListenersContainer listeners = Listeners.players();
        listeners.addListener(this.onPlayerLoginListener = new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, e -> this.onPlayerLogin(e.getPlayer()), this));
        Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
    }
    
    private void onPlayerLogin(final Player player) {
        if (this.matchInProgress) {
            final EventScheduler scheduler = this.getScheduler("stop-match");
            if (Objects.nonNull(scheduler)) {
                player.sendPacket(ExOlympiadInfo.show(OlympiadRuleType.MAX, (int)scheduler.getRemainingTime(TimeUnit.SECONDS)));
            }
            else {
                OlympiadEngine.LOGGER.warn("Can't find stop-match scheduler");
            }
        }
    }
    
    public void onStopMatch() {
        this.matchInProgress = false;
        if (Objects.nonNull(this.onPlayerLoginListener)) {
            Listeners.players().removeListener(this.onPlayerLoginListener);
        }
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.hide(OlympiadRuleType.MAX));
        Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED));
    }
    
    public void onNewSeason() {
        if (LocalDate.now().compareTo((ChronoLocalDate)this.data.getNextSeasonDate()) >= 0) {
            this.data.increaseSeason();
            ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).save((Object)this.data);
            Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED)).addInt(this.data.getSeason()));
        }
    }
    
    public boolean isMatchInProgress() {
        return this.matchInProgress;
    }
    
    public int getCurrentSeason() {
        return this.data.getSeason();
    }
    
    public int getPeriod() {
        return this.data.getPeriod();
    }
    
    public void saveOlympiadStatus() {
        ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).save((Object)this.data);
    }
    
    public int getOlympiadPoints(final Player player) {
        return 0;
    }
    
    public int getRemainingDailyMatches(final Player player) {
        return 5;
    }
    
    public static OlympiadEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)OlympiadEngine.class);
    }
    
    private static class Singleton
    {
        private static final OlympiadEngine INSTANCE;
        
        static {
            INSTANCE = new OlympiadEngine();
        }
    }
}
