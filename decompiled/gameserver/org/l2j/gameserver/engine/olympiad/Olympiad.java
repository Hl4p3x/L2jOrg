// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import java.time.Year;
import java.time.YearMonth;
import org.l2j.commons.util.Rnd;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchMakingResult;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import java.util.concurrent.TimeUnit;
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
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.OlympiadDAO;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.w3c.dom.Node;
import org.l2j.gameserver.util.GameXmlReader;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import org.l2j.gameserver.data.database.data.OlympiadData;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class Olympiad extends AbstractEventManager<OlympiadMatch>
{
    private static final Logger LOGGER;
    private final Set<Player> matchMaking;
    private final Set<OlympiadMatch> matches;
    private ConsumerEventListener onPlayerLoginListener;
    private OlympiadData data;
    private OlympiadState state;
    private LocalDate startDate;
    private boolean forceStartDate;
    private int minParticipant;
    
    private Olympiad() {
        this.matchMaking = (Set<Player>)ConcurrentHashMap.newKeySet(20);
        this.matches = (Set<OlympiadMatch>)ConcurrentHashMap.newKeySet(10);
        this.data = new OlympiadData();
        this.state = OlympiadState.SCHEDULED;
    }
    
    @Override
    public void config(final GameXmlReader reader, final Node configNode) {
        final Node olympiadConfig = configNode.getFirstChild();
        if (Objects.nonNull(olympiadConfig) && olympiadConfig.getNodeName().equals("olympiad-config")) {
            final NamedNodeMap attr = olympiadConfig.getAttributes();
            this.minParticipant = reader.parseInt(attr, "min-participant");
            this.forceStartDate = reader.parseBoolean(attr, "force-start-date");
            final String strDate = reader.parseString(attr, "start-date");
            this.startDate = (Objects.isNull(strDate) ? LocalDate.now() : LocalDate.parse(strDate));
        }
    }
    
    @Override
    public void onInitialized() {
        this.data = ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).findData();
        if (Objects.isNull(this.data)) {
            (this.data = new OlympiadData()).setNextSeasonDate(this.startDate);
            this.data.setId(1);
            ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).save((Object)this.data);
        }
        else if (this.forceStartDate) {
            this.data.setNextSeasonDate(this.startDate);
        }
        if (this.data.getNextSeasonDate().isAfter(LocalDate.now())) {
            Olympiad.LOGGER.info("World Olympiad season start scheduled to {}", (Object)this.data.getNextSeasonDate());
        }
        else if (this.data.getSeason() > 0) {
            Olympiad.LOGGER.info("World Olympiad {} season has been started", (Object)this.data.getSeason());
        }
        AntiFeedManager.getInstance().registerEvent(1);
    }
    
    public void onStartMatch() {
        this.state = OlympiadState.STARTED;
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.show(OlympiadRuleType.CLASSLESS, 300));
        final ListenersContainer listeners = Listeners.players();
        listeners.addListener(this.onPlayerLoginListener = new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, e -> this.onPlayerLogin(e.getPlayer()), this));
        Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
    }
    
    private void onPlayerLogin(final Player player) {
        if (this.state.matchesInProgress()) {
            final EventScheduler scheduler = this.getScheduler("stop-match");
            if (Objects.nonNull(scheduler)) {
                player.sendPacket(ExOlympiadInfo.show(OlympiadRuleType.CLASSLESS, (int)scheduler.getRemainingTime(TimeUnit.SECONDS)));
            }
            else {
                Olympiad.LOGGER.warn("Can't find stop-match scheduler");
            }
        }
    }
    
    public void onStopMatch() {
        this.state = OlympiadState.SCHEDULED;
        if (Objects.nonNull(this.onPlayerLoginListener)) {
            Listeners.players().removeListener(this.onPlayerLoginListener);
        }
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.hide(OlympiadRuleType.CLASSLESS));
        Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED));
    }
    
    public void onNewSeason() {
        if (LocalDate.now().compareTo((ChronoLocalDate)this.data.getNextSeasonDate()) >= 0) {
            this.data.increaseSeason();
            ((OlympiadDAO)DatabaseAccess.getDAO((Class)OlympiadDAO.class)).save((Object)this.data);
            Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED)).addInt(this.data.getSeason()));
        }
    }
    
    public boolean isMatchesInProgress() {
        return this.state.matchesInProgress();
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
    
    public void registerPlayer(final Player player, final OlympiadRuleType ruleType) {
        if (!this.state.matchesInProgress()) {
            return;
        }
        if (this.matchMaking.contains(player)) {
            player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH)).addPcName(player));
            return;
        }
        this.matchMaking.add(player);
        player.sendPacket(SystemMessageId.YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE);
        player.sendPacket(new ExOlympiadMatchMakingResult(true, ruleType));
        if (this.state != OlympiadState.MATCH_MAKING && this.matchMaking.size() >= this.minParticipant) {
            this.state = OlympiadState.MATCH_MAKING;
            ThreadPool.schedule(this::distributeAndStartMatches, 1L, TimeUnit.MINUTES);
        }
    }
    
    private void distributeAndStartMatches() {
        final List<Player> participants;
        synchronized (this.matchMaking) {
            participants = new ArrayList<Player>(this.matchMaking);
            this.matchMaking.clear();
        }
        while (participants.size() >= OlympiadRuleType.CLASSLESS.participantCount()) {
            this.newMatch(participants);
        }
        if (!participants.isEmpty()) {
            this.matchMaking.addAll(participants);
        }
        if (this.state == OlympiadState.MATCH_MAKING) {
            this.state = OlympiadState.STARTED;
        }
    }
    
    private void newMatch(final List<Player> participants) {
        final OlympiadMatch match = OlympiadMatch.of(OlympiadRuleType.CLASSLESS);
        for (int i = 0; i < OlympiadRuleType.CLASSLESS.participantCount(); ++i) {
            match.addParticipant(participants.remove(Rnd.get(participants.size())));
        }
        this.matches.add(match);
        ThreadPool.schedule((Runnable)match, 1L, TimeUnit.MINUTES);
    }
    
    public void unregisterPlayer(final Player player, final OlympiadRuleType ruleType) {
        if (this.matchMaking.remove(player)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            player.sendPacket(new ExOlympiadMatchMakingResult(false, ruleType));
        }
    }
    
    public boolean isRegistered(final Player player) {
        return this.matchMaking.contains(player);
    }
    
    @Override
    protected void onPlayerLogout(final Player player) {
        this.unregisterPlayer(player, OlympiadRuleType.CLASSLESS);
    }
    
    public int getSeasonMonth() {
        return YearMonth.now().getMonthValue();
    }
    
    public int getSeasonYear() {
        return Year.now().getValue();
    }
    
    public static Olympiad getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Olympiad.class);
    }
    
    private static class Singleton
    {
        private static final Olympiad INSTANCE;
        
        static {
            INSTANCE = new Olympiad();
        }
    }
}
