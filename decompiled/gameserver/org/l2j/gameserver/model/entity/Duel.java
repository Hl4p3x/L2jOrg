// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import java.util.Set;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.ExDuelEnd;
import org.l2j.gameserver.enums.DuelResult;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import java.util.List;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.network.serverpackets.ExDuelStart;
import org.l2j.gameserver.network.serverpackets.ExDuelReady;
import org.l2j.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Map;
import java.util.Calendar;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.slf4j.Logger;

public class Duel
{
    public static final int DUELSTATE_NODUEL = 0;
    public static final int DUELSTATE_DUELLING = 1;
    public static final int DUELSTATE_DEAD = 2;
    public static final int DUELSTATE_WINNER = 3;
    public static final int DUELSTATE_INTERRUPTED = 4;
    protected static final Logger LOGGER;
    private static final PlaySound B04_S01;
    private static final int PARTY_DUEL_DURATION = 300;
    private static final int PLAYER_DUEL_DURATION = 120;
    private final int _duelId;
    private final boolean _partyDuel;
    private final Calendar _duelEndTime;
    private final Map<Integer, PlayerCondition> _playerConditions;
    Instance _duelInstance;
    private Player _playerA;
    private Player _playerB;
    private int _surrenderRequest;
    private int _countdown;
    private boolean _finished;
    
    public Duel(final Player playerA, final Player playerB, final int partyDuel, final int duelId) {
        this._playerConditions = new ConcurrentHashMap<Integer, PlayerCondition>();
        this._surrenderRequest = 0;
        this._countdown = 5;
        this._finished = false;
        this._duelId = duelId;
        this._playerA = playerA;
        this._playerB = playerB;
        this._partyDuel = (partyDuel == 1);
        if (this._partyDuel) {
            for (final Player member : this._playerA.getParty().getMembers()) {
                member.setStartingDuel();
            }
            for (final Player member : this._playerB.getParty().getMembers()) {
                member.setStartingDuel();
            }
        }
        else {
            this._playerA.setStartingDuel();
            this._playerB.setStartingDuel();
        }
        (this._duelEndTime = Calendar.getInstance()).add(13, this._partyDuel ? 300 : 120);
        this.setFinished(false);
        if (this._partyDuel) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE);
            this.broadcastToTeam1(sm);
            this.broadcastToTeam2(sm);
        }
        ThreadPool.schedule((Runnable)new ScheduleStartDuelTask(this), 3000L);
    }
    
    public Instance getDueldInstance() {
        return this._duelInstance;
    }
    
    private void stopFighting() {
        final ActionFailed af = ActionFailed.STATIC_PACKET;
        if (this._partyDuel) {
            for (final Player temp : this._playerA.getParty().getMembers()) {
                temp.abortCast();
                temp.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                temp.setTarget(null);
                temp.sendPacket(af);
                temp.getServitorsAndPets().forEach(s -> {
                    s.abortCast();
                    s.abortAttack();
                    s.setTarget(null);
                    s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    return;
                });
            }
            for (final Player temp : this._playerB.getParty().getMembers()) {
                temp.abortCast();
                temp.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                temp.setTarget(null);
                temp.sendPacket(af);
                temp.getServitorsAndPets().forEach(s -> {
                    s.abortCast();
                    s.abortAttack();
                    s.setTarget(null);
                    s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                });
            }
        }
        else {
            this._playerA.abortCast();
            this._playerB.abortCast();
            this._playerA.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            this._playerA.setTarget(null);
            this._playerB.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            this._playerB.setTarget(null);
            this._playerA.sendPacket(af);
            this._playerB.sendPacket(af);
            this._playerA.getServitorsAndPets().forEach(s -> {
                s.abortCast();
                s.abortAttack();
                s.setTarget(null);
                s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            });
            this._playerB.getServitorsAndPets().forEach(s -> {
                s.abortCast();
                s.abortAttack();
                s.setTarget(null);
                s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            });
        }
    }
    
    public boolean isDuelistInPvp(final boolean sendMessage) {
        if (this._partyDuel) {
            return false;
        }
        if (this._playerA.getPvpFlag() != 0 || this._playerB.getPvpFlag() != 0) {
            if (sendMessage) {
                final String engagedInPvP = "The duel was canceled because a duelist engaged in PvP combat.";
                this._playerA.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
                this._playerB.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
            }
            return true;
        }
        return false;
    }
    
    public void startDuel() {
        if (this._playerA == null || this._playerB == null || this._playerA.isInDuel() || this._playerB.isInDuel()) {
            this._playerConditions.clear();
            DuelManager.getInstance().removeDuel(this);
            return;
        }
        if (this._partyDuel) {
            for (final Player temp : this._playerA.getParty().getMembers()) {
                temp.cancelActiveTrade();
                temp.setIsInDuel(this._duelId);
                temp.setTeam(Team.BLUE);
                temp.broadcastUserInfo();
                this.broadcastToTeam2(new ExDuelUpdateUserInfo(temp));
            }
            for (final Player temp : this._playerB.getParty().getMembers()) {
                temp.cancelActiveTrade();
                temp.setIsInDuel(this._duelId);
                temp.setTeam(Team.RED);
                temp.broadcastUserInfo();
                this.broadcastToTeam1(new ExDuelUpdateUserInfo(temp));
            }
            this.broadcastToTeam1(ExDuelReady.PARTY_DUEL);
            this.broadcastToTeam2(ExDuelReady.PARTY_DUEL);
            this.broadcastToTeam1(ExDuelStart.PARTY_DUEL);
            this.broadcastToTeam2(ExDuelStart.PARTY_DUEL);
            for (final Door door : this._duelInstance.getDoors()) {
                if (door != null && !door.isOpen()) {
                    door.openMe();
                }
            }
        }
        else {
            this._playerA.setIsInDuel(this._duelId);
            this._playerA.setTeam(Team.BLUE);
            this._playerB.setIsInDuel(this._duelId);
            this._playerB.setTeam(Team.RED);
            this.broadcastToTeam1(ExDuelReady.PLAYER_DUEL);
            this.broadcastToTeam2(ExDuelReady.PLAYER_DUEL);
            this.broadcastToTeam1(ExDuelStart.PLAYER_DUEL);
            this.broadcastToTeam2(ExDuelStart.PLAYER_DUEL);
            this.broadcastToTeam1(new ExDuelUpdateUserInfo(this._playerB));
            this.broadcastToTeam2(new ExDuelUpdateUserInfo(this._playerA));
            this._playerA.broadcastUserInfo();
            this._playerB.broadcastUserInfo();
        }
        this.broadcastToTeam1(Duel.B04_S01);
        this.broadcastToTeam2(Duel.B04_S01);
        ThreadPool.schedule((Runnable)new ScheduleDuelTask(this), 1000L);
    }
    
    public void savePlayerConditions() {
        if (this._partyDuel) {
            for (final Player player : this._playerA.getParty().getMembers()) {
                this._playerConditions.put(player.getObjectId(), new PlayerCondition(player, this._partyDuel));
            }
            for (final Player player : this._playerB.getParty().getMembers()) {
                this._playerConditions.put(player.getObjectId(), new PlayerCondition(player, this._partyDuel));
            }
        }
        else {
            this._playerConditions.put(this._playerA.getObjectId(), new PlayerCondition(this._playerA, this._partyDuel));
            this._playerConditions.put(this._playerB.getObjectId(), new PlayerCondition(this._playerB, this._partyDuel));
        }
    }
    
    public void restorePlayerConditions(final boolean abnormalDuelEnd) {
        if (this._partyDuel) {
            for (final Player temp : this._playerA.getParty().getMembers()) {
                temp.setIsInDuel(0);
                temp.setTeam(Team.NONE);
                temp.broadcastUserInfo();
            }
            for (final Player temp : this._playerB.getParty().getMembers()) {
                temp.setIsInDuel(0);
                temp.setTeam(Team.NONE);
                temp.broadcastUserInfo();
            }
        }
        else {
            this._playerA.setIsInDuel(0);
            this._playerA.setTeam(Team.NONE);
            this._playerA.broadcastUserInfo();
            this._playerB.setIsInDuel(0);
            this._playerB.setTeam(Team.NONE);
            this._playerB.broadcastUserInfo();
        }
        if (abnormalDuelEnd) {
            return;
        }
        this._playerConditions.values().forEach(c -> c.restoreCondition());
    }
    
    public int getId() {
        return this._duelId;
    }
    
    public int getRemainingTime() {
        return (int)(this._duelEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
    }
    
    public Player getPlayerA() {
        return this._playerA;
    }
    
    public Player getPlayerB() {
        return this._playerB;
    }
    
    public boolean isPartyDuel() {
        return this._partyDuel;
    }
    
    public boolean getFinished() {
        return this._finished;
    }
    
    public void setFinished(final boolean mode) {
        this._finished = mode;
    }
    
    public void teleportPlayers() {
        if (!this._partyDuel) {
            return;
        }
        final int instanceId = DuelManager.getInstance().getDuelArena();
        final OlympiadStadiumZone zone = ZoneManager.getInstance().getAllZones(OlympiadStadiumZone.class).stream().filter(z -> z.getInstanceTemplateId() == instanceId).findFirst().orElse(null);
        if (zone == null) {
            throw new RuntimeException("Unable to find a party duel arena!");
        }
        final List<Location> spawns = zone.getSpawns();
        this._duelInstance = InstanceManager.getInstance().createInstance(InstanceManager.getInstance().getInstanceTemplate(instanceId), null);
        final Location spawn1 = spawns.get(Rnd.get(spawns.size() / 2));
        for (final Player temp : this._playerA.getParty().getMembers()) {
            temp.teleToLocation(spawn1.getX(), spawn1.getY(), spawn1.getZ(), 0, 0, this._duelInstance);
        }
        final Location spawn2 = spawns.get(Rnd.get(spawns.size() / 2, spawns.size()));
        for (final Player temp2 : this._playerB.getParty().getMembers()) {
            temp2.teleToLocation(spawn2.getX(), spawn2.getY(), spawn2.getZ(), 0, 0, this._duelInstance);
        }
    }
    
    public void broadcastToTeam1(final ServerPacket packet) {
        if (this._playerA == null) {
            return;
        }
        if (this._partyDuel && this._playerA.getParty() != null) {
            for (final Player temp : this._playerA.getParty().getMembers()) {
                temp.sendPacket(packet);
            }
        }
        else {
            this._playerA.sendPacket(packet);
        }
    }
    
    public void broadcastToTeam2(final ServerPacket packet) {
        if (this._playerB == null) {
            return;
        }
        if (this._partyDuel && this._playerB.getParty() != null) {
            for (final Player temp : this._playerB.getParty().getMembers()) {
                temp.sendPacket(packet);
            }
        }
        else {
            this._playerB.sendPacket(packet);
        }
    }
    
    public Player getWinner() {
        if (!this._finished || this._playerA == null || this._playerB == null) {
            return null;
        }
        if (this._playerA.getDuelState() == 3) {
            return this._playerA;
        }
        if (this._playerB.getDuelState() == 3) {
            return this._playerB;
        }
        return null;
    }
    
    public Player getLooser() {
        if (!this._finished || this._playerA == null || this._playerB == null) {
            return null;
        }
        if (this._playerA.getDuelState() == 3) {
            return this._playerB;
        }
        if (this._playerB.getDuelState() == 3) {
            return this._playerA;
        }
        return null;
    }
    
    public void playKneelAnimation() {
        final Player looser = this.getLooser();
        if (looser == null) {
            return;
        }
        if (this._partyDuel && looser.getParty() != null) {
            for (final Player temp : looser.getParty().getMembers()) {
                temp.broadcastPacket(new SocialAction(temp.getObjectId(), 7));
            }
        }
        else {
            looser.broadcastPacket(new SocialAction(looser.getObjectId(), 7));
        }
    }
    
    public int countdown() {
        --this._countdown;
        if (this._countdown > 3) {
            return this._countdown;
        }
        SystemMessage sm = null;
        if (this._countdown > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_DUEL_WILL_BEGIN_IN_S1_SECOND_S);
            sm.addInt(this._countdown);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.LET_THE_DUEL_BEGIN);
        }
        this.broadcastToTeam1(sm);
        this.broadcastToTeam2(sm);
        return this._countdown;
    }
    
    public void endDuel(final DuelResult result) {
        if (this._playerA == null || this._playerB == null) {
            this._playerConditions.clear();
            DuelManager.getInstance().removeDuel(this);
            return;
        }
        SystemMessage sm = null;
        switch (result) {
            case TEAM_1_WIN:
            case TEAM_2_SURRENDER: {
                this.restorePlayerConditions(false);
                sm = (this._partyDuel ? SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_WON_THE_DUEL) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_WON_THE_DUEL));
                sm.addString(this._playerA.getName());
                this.broadcastToTeam1(sm);
                this.broadcastToTeam2(sm);
                break;
            }
            case TEAM_1_SURRENDER:
            case TEAM_2_WIN: {
                this.restorePlayerConditions(false);
                sm = (this._partyDuel ? SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_WON_THE_DUEL) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_WON_THE_DUEL));
                sm.addString(this._playerB.getName());
                this.broadcastToTeam1(sm);
                this.broadcastToTeam2(sm);
                break;
            }
            case CANCELED: {
                this.stopFighting();
                this.restorePlayerConditions(true);
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
                this.broadcastToTeam1(sm);
                this.broadcastToTeam2(sm);
                break;
            }
            case TIMEOUT: {
                this.stopFighting();
                this.restorePlayerConditions(false);
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
                this.broadcastToTeam1(sm);
                this.broadcastToTeam2(sm);
                break;
            }
        }
        final ExDuelEnd duelEnd = this._partyDuel ? ExDuelEnd.PARTY_DUEL : ExDuelEnd.PLAYER_DUEL;
        this.broadcastToTeam1(duelEnd);
        this.broadcastToTeam2(duelEnd);
        this._playerConditions.clear();
        DuelManager.getInstance().removeDuel(this);
    }
    
    public DuelResult checkEndDuelCondition() {
        if (this._playerA == null || this._playerB == null) {
            return DuelResult.CANCELED;
        }
        if (this._surrenderRequest != 0) {
            return (this._surrenderRequest == 1) ? DuelResult.TEAM_1_SURRENDER : DuelResult.TEAM_2_SURRENDER;
        }
        if (this.getRemainingTime() <= 0) {
            return DuelResult.TIMEOUT;
        }
        if (this._playerA.getDuelState() == 3) {
            this.stopFighting();
            return DuelResult.TEAM_1_WIN;
        }
        if (this._playerB.getDuelState() == 3) {
            this.stopFighting();
            return DuelResult.TEAM_2_WIN;
        }
        if (!this._partyDuel) {
            if (this._playerA.getDuelState() == 4 || this._playerB.getDuelState() == 4) {
                return DuelResult.CANCELED;
            }
            if (!MathUtil.isInsideRadius2D(this._playerA, this._playerB, 1600)) {
                return DuelResult.CANCELED;
            }
            if (this.isDuelistInPvp(true)) {
                return DuelResult.CANCELED;
            }
            if (this._playerA.isInsideZone(ZoneType.PEACE) || this._playerB.isInsideZone(ZoneType.PEACE) || this._playerA.isInsideZone(ZoneType.SIEGE) || this._playerB.isInsideZone(ZoneType.SIEGE) || this._playerA.isInsideZone(ZoneType.PVP) || this._playerB.isInsideZone(ZoneType.PVP)) {
                return DuelResult.CANCELED;
            }
        }
        return DuelResult.CONTINUE;
    }
    
    public void doSurrender(final Player player) {
        if (this._surrenderRequest != 0) {
            return;
        }
        this.stopFighting();
        if (this._partyDuel) {
            if (this._playerA.getParty().getMembers().contains(player)) {
                this._surrenderRequest = 1;
                for (final Player temp : this._playerA.getParty().getMembers()) {
                    temp.setDuelState(2);
                }
                for (final Player temp : this._playerB.getParty().getMembers()) {
                    temp.setDuelState(3);
                }
            }
            else if (this._playerB.getParty().getMembers().contains(player)) {
                this._surrenderRequest = 2;
                for (final Player temp : this._playerB.getParty().getMembers()) {
                    temp.setDuelState(2);
                }
                for (final Player temp : this._playerA.getParty().getMembers()) {
                    temp.setDuelState(3);
                }
            }
        }
        else if (player == this._playerA) {
            this._surrenderRequest = 1;
            this._playerA.setDuelState(2);
            this._playerB.setDuelState(3);
        }
        else if (player == this._playerB) {
            this._surrenderRequest = 2;
            this._playerB.setDuelState(2);
            this._playerA.setDuelState(3);
        }
    }
    
    public void onPlayerDefeat(final Player player) {
        player.setDuelState(2);
        if (this._partyDuel) {
            final boolean teamdefeated = player.getParty().getMembers().stream().anyMatch(member -> member.getDuelState() == 1);
            if (teamdefeated) {
                final Player winner = this._playerA.getParty().getMembers().contains(player) ? this._playerB : this._playerA;
                for (final Player temp : winner.getParty().getMembers()) {
                    temp.setDuelState(3);
                }
            }
        }
        else {
            if (player != this._playerA && player != this._playerB) {
                Duel.LOGGER.warn("Error in onPlayerDefeat(): player is not part of this 1vs1 duel");
            }
            if (this._playerA == player) {
                this._playerB.setDuelState(3);
            }
            else {
                this._playerA.setDuelState(3);
            }
        }
    }
    
    public void onRemoveFromParty(final Player player) {
        if (!this._partyDuel) {
            return;
        }
        if (player == this._playerA || player == this._playerB) {
            for (final PlayerCondition cond : this._playerConditions.values()) {
                cond.teleportBack();
                cond.getPlayer().setIsInDuel(0);
            }
            this._playerA = null;
            this._playerB = null;
        }
        else {
            final PlayerCondition cond2 = this._playerConditions.remove(player.getObjectId());
            if (cond2 != null) {
                cond2.teleportBack();
            }
            player.setIsInDuel(0);
        }
    }
    
    public void onBuff(final Player player, final Skill debuff) {
        final PlayerCondition cond = this._playerConditions.get(player.getObjectId());
        if (cond != null) {
            cond.registerDebuff(debuff);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Duel.class);
        B04_S01 = new PlaySound(1, "B04_S01", 0, 0, 0, 0, 0);
    }
    
    public static class PlayerCondition
    {
        private Player _player;
        private double _hp;
        private double _mp;
        private double _cp;
        private boolean _paDuel;
        private int _x;
        private int _y;
        private int _z;
        private Set<Skill> _debuffs;
        
        public PlayerCondition(final Player player, final boolean partyDuel) {
            if (player == null) {
                return;
            }
            this._player = player;
            this._hp = this._player.getCurrentHp();
            this._mp = this._player.getCurrentMp();
            this._cp = this._player.getCurrentCp();
            this._paDuel = partyDuel;
            if (this._paDuel) {
                this._x = this._player.getX();
                this._y = this._player.getY();
                this._z = this._player.getZ();
            }
        }
        
        public void restoreCondition() {
            if (this._player == null) {
                return;
            }
            this._player.setCurrentHp(this._hp);
            this._player.setCurrentMp(this._mp);
            this._player.setCurrentCp(this._cp);
            if (this._paDuel) {
                this.teleportBack();
            }
            if (this._debuffs != null) {
                for (final Skill skill : this._debuffs) {
                    if (skill != null) {
                        this._player.stopSkillEffects(true, skill.getId());
                    }
                }
            }
        }
        
        public void registerDebuff(final Skill debuff) {
            if (this._debuffs == null) {
                this._debuffs = (Set<Skill>)ConcurrentHashMap.newKeySet();
            }
            this._debuffs.add(debuff);
        }
        
        public void teleportBack() {
            if (this._paDuel) {
                this._player.teleToLocation(this._x, this._y, this._z);
            }
        }
        
        public Player getPlayer() {
            return this._player;
        }
    }
    
    public static class ScheduleStartDuelTask implements Runnable
    {
        private final Duel _duel;
        
        public ScheduleStartDuelTask(final Duel duel) {
            this._duel = duel;
        }
        
        @Override
        public void run() {
            try {
                final int count = this._duel.countdown();
                if (count == 4) {
                    this._duel.savePlayerConditions();
                    this._duel.teleportPlayers();
                    ThreadPool.schedule((Runnable)this, 20000L);
                }
                else if (count > 0) {
                    ThreadPool.schedule((Runnable)this, 1000L);
                }
                else {
                    this._duel.startDuel();
                }
            }
            catch (Exception e) {
                Duel.LOGGER.error("There has been a problem while runing a duel start task!", (Throwable)e);
            }
        }
    }
    
    public static class ScheduleEndDuelTask implements Runnable
    {
        private final Duel _duel;
        private final DuelResult _result;
        
        public ScheduleEndDuelTask(final Duel duel, final DuelResult result) {
            this._duel = duel;
            this._result = result;
        }
        
        @Override
        public void run() {
            try {
                this._duel.endDuel(this._result);
            }
            catch (Exception e) {
                Duel.LOGGER.error("There has been a problem while runing a duel end task!", (Throwable)e);
            }
        }
    }
    
    public class ScheduleDuelTask implements Runnable
    {
        private final Duel _duel;
        
        public ScheduleDuelTask(final Duel duel) {
            this._duel = duel;
        }
        
        @Override
        public void run() {
            try {
                switch (this._duel.checkEndDuelCondition()) {
                    case CANCELED: {
                        Duel.this.setFinished(true);
                        this._duel.endDuel(DuelResult.CANCELED);
                        break;
                    }
                    case CONTINUE: {
                        ThreadPool.schedule((Runnable)this, 1000L);
                        break;
                    }
                    default: {
                        Duel.this.setFinished(true);
                        Duel.this.playKneelAnimation();
                        ThreadPool.schedule((Runnable)new ScheduleEndDuelTask(this._duel, this._duel.checkEndDuelCondition()), 5000L);
                        if (Duel.this._duelInstance != null) {
                            Duel.this._duelInstance.destroy();
                            break;
                        }
                        break;
                    }
                }
            }
            catch (Exception e) {
                Duel.LOGGER.error("There has been a problem while runing a duel task!", (Throwable)e);
            }
        }
    }
}
