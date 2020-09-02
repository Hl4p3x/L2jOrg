// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.Summon;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.time.temporal.Temporal;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.serverpackets.SiegeInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import java.util.stream.Stream;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import java.util.Comparator;
import org.l2j.gameserver.util.MathUtil;
import java.util.Set;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.TowerSpawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.world.zone.type.SiegeZone;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.enums.SiegeTeleportWhoType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import java.util.Iterator;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.time.temporal.TemporalAdjusters;
import org.l2j.gameserver.model.SiegeScheduleDate;
import org.l2j.gameserver.data.xml.impl.SiegeScheduleData;
import org.l2j.gameserver.instancemanager.CastleManager;
import java.time.chrono.ChronoLocalDateTime;
import java.time.LocalDateTime;
import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import java.util.ArrayList;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.ScheduledFuture;
import java.time.Instant;
import org.l2j.gameserver.model.actor.instance.FlameTower;
import org.l2j.gameserver.model.actor.instance.ControlTower;
import java.util.List;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class Siege implements Siegable
{
    protected static final Logger LOGGER;
    private final IntMap<SiegeClanData> attackers;
    private final IntMap<SiegeClanData> defenders;
    private final IntMap<SiegeClanData> defendersWaiting;
    private final List<ControlTower> controlTowers;
    private final List<FlameTower> flameTowers;
    private final Castle castle;
    private Instant endTime;
    private int controlTowerCount;
    protected boolean isRegistrationOver;
    protected ScheduledFuture<?> scheduledStartSiegeTask;
    protected int firstOwnerClanId;
    boolean isInProgress;
    
    public Siege(final Castle castle) {
        this.attackers = (IntMap<SiegeClanData>)new CHashIntMap();
        this.defenders = (IntMap<SiegeClanData>)new CHashIntMap();
        this.defendersWaiting = (IntMap<SiegeClanData>)new CHashIntMap();
        this.controlTowers = new ArrayList<ControlTower>();
        this.flameTowers = new ArrayList<FlameTower>();
        this.isRegistrationOver = false;
        this.scheduledStartSiegeTask = null;
        this.firstOwnerClanId = -1;
        this.isInProgress = false;
        this.castle = castle;
        this.startAutoTask();
    }
    
    private void startAutoTask() {
        this.correctSiegeDateTime();
        Siege.LOGGER.info("Siege of {} : {}", (Object)this.castle, (Object)this.castle.getSiegeDate());
        this.loadSiegeClan();
        if (Objects.nonNull(this.scheduledStartSiegeTask)) {
            this.scheduledStartSiegeTask.cancel(false);
        }
        this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this.castle), 1000L);
    }
    
    private void correctSiegeDateTime() {
        if (Objects.isNull(this.castle.getSiegeDate()) || LocalDateTime.now().isAfter(this.castle.getSiegeDate())) {
            this.setNextSiegeDate();
            this.saveSiegeDate();
        }
    }
    
    private void saveSiegeDate() {
        if (Objects.nonNull(this.scheduledStartSiegeTask)) {
            this.scheduledStartSiegeTask.cancel(true);
            this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this.castle), 1000L);
        }
        this.castle.updateSiegeDate();
    }
    
    private void setNextSiegeDate() {
        LocalDateTime siegeDate = LocalDateTime.now().plusWeeks(2L).withMinute(0).withSecond(0);
        final CastleManager castleManager = CastleManager.getInstance();
        for (final SiegeScheduleDate holder : SiegeScheduleData.getInstance().getScheduleDates()) {
            siegeDate = siegeDate.with(TemporalAdjusters.next(holder.getDay())).withHour(holder.getHour());
            if (castleManager.getSiegesOnDate(siegeDate) < holder.getMaxConcurrent()) {
                castleManager.registerSiegeDate(this.castle, siegeDate);
                break;
            }
        }
        Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME)).addCastleId(this.castle.getId()));
        this.isRegistrationOver = false;
    }
    
    private void loadSiegeClan() {
        this.attackers.clear();
        this.defenders.clear();
        this.defendersWaiting.clear();
        if (this.castle.getOwnerId() > 0) {
            this.addOwnerDefender(this.castle.getOwnerId());
        }
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).findSiegeClansByCastle(this.castle.getId()).forEach(siegeClan -> {
            switch (siegeClan.getType()) {
                case DEFENDER: {
                    this.addDefender(siegeClan);
                    break;
                }
                case ATTACKER: {
                    this.addAttacker(siegeClan);
                    break;
                }
                case DEFENDER_PENDING: {
                    this.addDefenderWaiting(siegeClan);
                    break;
                }
            }
        });
    }
    
    private void addOwnerDefender(final int clanId) {
        this.defenders.put(clanId, (Object)new SiegeClanData(clanId, SiegeClanType.OWNER, this.castle.getId()));
    }
    
    private void addAttacker(final SiegeClanData siegeClan) {
        siegeClan.setType(SiegeClanType.ATTACKER);
        this.getAttackerClans().put(siegeClan.getClanId(), (Object)siegeClan);
    }
    
    private void addDefender(final SiegeClanData siegeClan) {
        this.defenders.put(siegeClan.getClanId(), (Object)siegeClan);
    }
    
    private void addDefenderWaiting(final SiegeClanData siegeClan) {
        this.defendersWaiting.put(siegeClan.getClanId(), (Object)siegeClan);
    }
    
    @Override
    public void startSiege() {
        if (!this.isInProgress) {
            this.firstOwnerClanId = this.castle.getOwnerId();
            if (this.attackers.isEmpty()) {
                SystemMessage sm;
                if (this.firstOwnerClanId <= 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                }
                else {
                    ClanTable.getInstance().getClan(this.firstOwnerClanId).increaseBloodAllianceCount();
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED);
                }
                sm.addCastleId(this.castle.getId());
                Broadcast.toAllOnlinePlayers(sm);
                this.saveCastleSiege();
                return;
            }
            this.isInProgress = true;
            this.loadSiegeClan();
            this.updatePlayerSiegeStateFlags(false);
            this.teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN);
            this.controlTowerCount = 0;
            this.spawnControlTower();
            this.spawnFlameTower();
            this.castle.spawnDoor();
            this.spawnSiegeGuard();
            SiegeGuardManager.getInstance().deleteTickets(this.getCastle().getId());
            final SiegeZone zone = this.castle.getZone();
            zone.setSiegeInstance(this);
            zone.setIsActive(true);
            zone.updateZoneStatusForCharactersInside();
            Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_STARTED)).addCastleId(this.castle.getId()), new PlaySound("systemmsg_eu.17"));
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeStart(this), this.getCastle());
            this.endTime = Instant.now().plus((long)SiegeManager.getInstance().getSiegeLength(), (TemporalUnit)ChronoUnit.MINUTES);
            ThreadPool.schedule((Runnable)new ScheduleEndSiegeTask(), 1000L);
        }
    }
    
    @Override
    public final IntMap<SiegeClanData> getAttackerClans() {
        return this.attackers;
    }
    
    private void saveCastleSiege() {
        this.setNextSiegeDate();
        this.castle.setSiegeTimeRegistrationEnd(LocalDateTime.now().plusDays(1L));
        this.saveSiegeDate();
        this.startAutoTask();
    }
    
    private void updatePlayerSiegeStateFlags(final boolean clear) {
        this.attackers.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> this.updateClanMemberSiegeState(clear, clan, (byte)1));
        this.attackers.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).forEach(clan -> this.updateClanMemberSiegeState(clear, clan, (byte)2));
    }
    
    private void updateClanMemberSiegeState(final boolean clear, final Clan clan, final byte state) {
        clan.forEachOnlineMember(member -> {
            if (clear) {
                member.setSiegeState((byte)0);
                member.setSiegeSide(0);
                member.setIsInSiege(false);
                member.stopFameTask();
            }
            else {
                member.setSiegeState(state);
                member.setSiegeSide(this.castle.getId());
                if (this.checkIfInZone(member)) {
                    member.setIsInSiege(true);
                    member.startFameTask(Config.CASTLE_ZONE_FAME_TASK_FREQUENCY * 1000, Config.CASTLE_ZONE_FAME_AQUIRE_POINTS);
                }
            }
            this.broadcastMemberInfo(member);
        });
    }
    
    private void broadcastMemberInfo(final Player member) {
        member.sendPacket(new UserInfo(member));
        int relation;
        Integer oldRelation;
        RelationChanged rc;
        World.getInstance().forEachVisibleObject(member, Player.class, player -> {
            if (!(!member.isVisibleFor(player))) {
                relation = member.getRelation(player);
                oldRelation = member.getKnownRelations().get(player.getObjectId());
                if (Objects.isNull(oldRelation) || oldRelation != relation) {
                    rc = new RelationChanged();
                    rc.addRelation(member, relation, member.isAutoAttackable(player));
                    if (member.hasSummon()) {
                        Util.doIfNonNull((Object)member.getPet(), pet -> rc.addRelation(pet, relation, member.isAutoAttackable(player)));
                        if (member.hasServitors()) {
                            member.getServitors().values().forEach(s -> rc.addRelation(s, relation, member.isAutoAttackable(player)));
                        }
                    }
                    player.sendPacket(rc);
                    member.getKnownRelations().put(player.getObjectId(), relation);
                }
            }
        });
    }
    
    private void teleportPlayer(final SiegeTeleportWhoType teleportWho, final TeleportWhereType teleportWhere) {
        switch (teleportWho) {
            case Owner: {
                this.teleportOnwersInZone(teleportWhere);
                break;
            }
            case NotOwner: {
                this.teleportNotOwnerInZone(teleportWhere);
                break;
            }
            case Attacker: {
                this.teleportAttackersInZone(teleportWhere);
                break;
            }
            case Spectator: {
                this.teleportSpectatorsInZone(teleportWhere);
                break;
            }
        }
    }
    
    private void spawnControlTower() {
        try {
            for (final TowerSpawn ts : SiegeManager.getInstance().getControlTowers(this.castle.getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                this.controlTowers.add((ControlTower)spawn.doSpawn());
            }
        }
        catch (Exception e) {
            Siege.LOGGER.warn("Cannot spawn control tower!", (Throwable)e);
        }
        this.controlTowerCount = this.controlTowers.size();
    }
    
    private void spawnFlameTower() {
        try {
            for (final TowerSpawn ts : SiegeManager.getInstance().getFlameTowers(this.castle.getId())) {
                final Spawn spawn = new Spawn(ts.getId());
                spawn.setLocation(ts.getLocation());
                final FlameTower tower = (FlameTower)spawn.doSpawn();
                tower.setUpgradeLevel(ts.getUpgradeLevel());
                tower.setZoneList(ts.getZoneList());
                this.flameTowers.add(tower);
            }
        }
        catch (Exception e) {
            Siege.LOGGER.warn("Cannot spawn flame tower!", (Throwable)e);
        }
    }
    
    private void spawnSiegeGuard() {
        final SiegeGuardManager siegeGuardManager = SiegeGuardManager.getInstance();
        siegeGuardManager.spawnSiegeGuard(this.castle);
        final Set<Spawn> spawns = siegeGuardManager.getSpawnedGuards(this.castle.getId());
        if (!spawns.isEmpty()) {
            this.registerToClosestTower(spawns);
        }
    }
    
    private void registerToClosestTower(final Set<Spawn> spawns) {
        for (final Spawn spawn : spawns) {
            Util.doIfNonNull((Object)this.controlTowers.stream().min(Comparator.comparingDouble(ct -> MathUtil.calculateDistanceSq3D(ct, spawn))).orElse(null), ct -> ct.registerGuard(spawn));
        }
    }
    
    public void midVictory() {
        if (this.isInProgress) {
            if (this.castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(this.castle);
            }
            if (this.defenders.isEmpty() && this.attackers.size() == 1) {
                Util.doIfNonNull((Object)this.getAttackerClan(this.castle.getOwnerId()), newOwner -> {
                    this.removeAttacker(newOwner);
                    this.addDefender(newOwner, SiegeClanType.OWNER);
                    return;
                });
                this.endSiege();
                return;
            }
            if (this.castle.getOwnerId() > 0) {
                final int allyId = ClanTable.getInstance().getClan(this.castle.getOwnerId()).getAllyId();
                if (this.defenders.isEmpty() && allyId != 0) {
                    final boolean allInSameAlliance = this.attackers.values().stream().map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).allMatch(clan -> clan.getAllyId() == allyId);
                    if (allInSameAlliance) {
                        Util.doIfNonNull((Object)this.getAttackerClan(this.castle.getOwnerId()), newOwner -> {
                            this.removeAttacker(newOwner);
                            this.addDefender(newOwner, SiegeClanType.OWNER);
                            return;
                        });
                        this.endSiege();
                        return;
                    }
                }
                final Iterator<SiegeClanData> iterator = this.defenders.values().iterator();
                while (iterator.hasNext()) {
                    this.addAttacker(iterator.next());
                    iterator.remove();
                }
                Util.doIfNonNull((Object)this.getAttackerClan(this.castle.getOwnerId()), newOwner -> {
                    this.removeAttacker(newOwner);
                    this.addDefender(newOwner, SiegeClanType.OWNER);
                    return;
                });
                for (final Clan clan2 : ClanTable.getInstance().getClanAllies(allyId)) {
                    Util.doIfNonNull((Object)this.getAttackerClan(clan2.getId()), siegeClan -> {
                        this.removeAttacker(siegeClan);
                        this.addDefender(siegeClan, SiegeClanType.DEFENDER);
                        return;
                    });
                }
                this.teleportPlayer(SiegeTeleportWhoType.Attacker, TeleportWhereType.SIEGEFLAG);
                this.teleportPlayer(SiegeTeleportWhoType.Spectator, TeleportWhereType.TOWN);
                this.removeDefenderFlags();
                this.castle.removeUpgrade();
                this.castle.spawnDoor(true);
                this.removeTowers();
                this.controlTowerCount = 0;
                this.spawnControlTower();
                this.spawnFlameTower();
                this.updatePlayerSiegeStateFlags(false);
                EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeOwnerChange(this), this.getCastle());
            }
        }
    }
    
    private void removeAttacker(final SiegeClanData sc) {
        this.attackers.remove(sc.getClanId());
    }
    
    private void addDefender(final SiegeClanData sc, final SiegeClanType type) {
        sc.setType(type);
        this.defenders.put(sc.getClanId(), (Object)sc);
    }
    
    @Override
    public void endSiege() {
        if (this.isInProgress) {
            Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_FINISHED)).addCastleId(this.castle.getId()), new PlaySound("systemmsg_eu.18"));
            if (this.castle.getOwnerId() > 0) {
                final Clan clan2 = ClanTable.getInstance().getClan(this.castle.getOwnerId());
                Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE).addString(clan2.getName())).addCastleId(this.castle.getId()));
                if (clan2.getId() == this.firstOwnerClanId) {
                    clan2.increaseBloodAllianceCount();
                }
                else {
                    this.castle.setTicketBuyCount(0);
                    clan2.forEachOnlineMember(noble -> Hero.getInstance().setCastleTaken(noble.getObjectId(), this.castle.getId()), Player::isNoble);
                }
            }
            else {
                Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addCastleId(this.castle.getId()));
            }
            Stream.concat(this.attackers.values().stream(), this.defenders.values().stream()).forEach(siegeClan -> Util.doIfNonNull((Object)ClanTable.getInstance().getClan(siegeClan.getClanId()), clan -> {
                clan.forEachOnlineMember(Player::checkItemRestriction);
                clan.clearSiegeKills();
                clan.clearSiegeDeaths();
            }));
            this.castle.updateClansReputation();
            this.removeFlags();
            this.teleportPlayer(SiegeTeleportWhoType.NotOwner, TeleportWhereType.TOWN);
            this.isInProgress = false;
            this.updatePlayerSiegeStateFlags(true);
            this.saveCastleSiege();
            this.clearSiegeClan();
            this.removeTowers();
            SiegeGuardManager.getInstance().unspawnSiegeGuard(this.getCastle());
            if (this.castle.getOwnerId() > 0) {
                SiegeGuardManager.getInstance().removeSiegeGuards(this.getCastle());
            }
            this.castle.spawnDoor();
            final SiegeZone zone = this.castle.getZone();
            zone.setIsActive(false);
            zone.updateZoneStatusForCharactersInside();
            zone.setSiegeInstance(null);
            EventDispatcher.getInstance().notifyEventAsync(new OnCastleSiegeFinish(this), this.getCastle());
        }
    }
    
    public void approveSiegeDefenderClan(final int clanId) {
        if (clanId <= 0) {
            return;
        }
        final SiegeClanData siegeClan = (SiegeClanData)this.defendersWaiting.remove(clanId);
        siegeClan.setType(SiegeClanType.DEFENDER);
        this.addDefender(siegeClan);
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).save(siegeClan);
    }
    
    public boolean checkIfInZone(final ILocational loc) {
        return this.isInProgress && this.castle.checkIfInZone(loc);
    }
    
    @Override
    public boolean checkIsAttacker(final Clan clan) {
        return Objects.nonNull(this.getAttackerClan(clan));
    }
    
    @Override
    public boolean checkIsDefender(final Clan clan) {
        return Objects.nonNull(this.getDefenderClan(clan));
    }
    
    public boolean checkIsDefenderWaiting(final Clan clan) {
        return Objects.nonNull(this.getDefenderWaitingClan(clan));
    }
    
    public void clearSiegeClan() {
        final CastleDAO castleDAO = (CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class);
        castleDAO.deleteSiegeByCastle(this.castle.getId());
        castleDAO.deleteSiegeByClan(this.castle.getOwnerId());
        this.attackers.clear();
        this.defenders.clear();
        this.defendersWaiting.clear();
    }
    
    private void clearSiegeWaitingClan() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).deleteWaintingClansByCastle(this.castle.getId());
        this.defendersWaiting.clear();
    }
    
    public void killedControlTower(final Npc ct) {
        this.controlTowerCount = Math.max(this.controlTowerCount - 1, 0);
    }
    
    public void listRegisterClan(final Player player) {
        player.sendPacket(new SiegeInfo(this.castle, player));
    }
    
    public void registerAttacker(final Player player) {
        this.registerAttacker(player, false);
    }
    
    public void registerAttacker(final Player player, final boolean force) {
        if (Objects.isNull(player.getClan())) {
            return;
        }
        final Clan clan = player.getClan();
        if (this.castle.getOwnerId() != 0) {
            final int allyId = ClanTable.getInstance().getClan(this.castle.getOwnerId()).getAllyId();
            if (allyId != 0 && clan.getAllyId() == allyId && !force) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
                return;
            }
        }
        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, this.castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            }
            else {
                this.saveSiegeClan(clan, SiegeClanType.ATTACKER);
            }
            return;
        }
        if (this.checkIfCanRegister(player, SiegeClanType.ATTACKER)) {
            this.saveSiegeClan(clan, SiegeClanType.ATTACKER);
        }
    }
    
    private void saveSiegeClan(final Clan clan, final SiegeClanType typeId) {
        final SiegeClanData siegeClan = new SiegeClanData(clan.getId(), typeId, this.castle.getId());
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).save(siegeClan);
        switch (typeId) {
            case DEFENDER:
            case OWNER: {
                this.addDefender(siegeClan);
                break;
            }
            case DEFENDER_PENDING: {
                this.addDefenderWaiting(siegeClan);
                break;
            }
            case ATTACKER: {
                this.addAttacker(siegeClan);
                break;
            }
        }
    }
    
    private boolean checkIfCanRegister(final Player player, final SiegeClanType type) {
        if (this.isRegistrationOver) {
            player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED)).addCastleId(this.castle.getId()));
        }
        else if (this.isInProgress) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
        }
        else if (Objects.isNull(player.getClan()) || player.getClan().getLevel() < SiegeManager.getInstance().getSiegeClanMinLevel()) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
        }
        else if (player.getClan().getId() == this.castle.getOwnerId()) {
            player.sendPacket(SystemMessageId.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
        }
        else if (player.getClan().getCastleId() > 0) {
            player.sendPacket(SystemMessageId.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
        }
        else if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), this.castle.getId())) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
        }
        else if (this.checkIfAlreadyRegisteredForSameDay(player.getClan())) {
            player.sendPacket(SystemMessageId.YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_CASTLE_SIEGE);
        }
        else if (type == SiegeClanType.ATTACKER && this.getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans()) {
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
        }
        else {
            if ((type != SiegeClanType.DEFENDER && type != SiegeClanType.DEFENDER_PENDING) || this.getDefenderClans().size() + this.getDefendersWaiting().size() < SiegeManager.getInstance().getDefenderMaxClans()) {
                return true;
            }
            player.sendPacket(SystemMessageId.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
        }
        return false;
    }
    
    public void registerDefender(final Player player) {
        this.registerDefender(player, false);
    }
    
    public void registerDefender(final Player player, final boolean force) {
        if (this.castle.getOwnerId() <= 0) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.castle.getName()));
            return;
        }
        final Clan clan = player.getClan();
        if (force) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, this.castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
            }
            else {
                this.saveSiegeClan(clan, SiegeClanType.DEFENDER_PENDING);
            }
            return;
        }
        if (this.checkIfCanRegister(player, SiegeClanType.DEFENDER_PENDING)) {
            this.saveSiegeClan(clan, SiegeClanType.DEFENDER_PENDING);
        }
    }
    
    public void removeSiegeClan(final Player player) {
        this.removeSiegeClan(player.getClan());
    }
    
    public void removeSiegeClan(final Clan clan) {
        if (Objects.isNull(clan) || clan.getCastleId() == this.castle.getId() || !SiegeManager.getInstance().checkIsRegistered(clan, this.castle.getId())) {
            return;
        }
        this.removeSiegeClan(clan.getId());
    }
    
    public void removeSiegeClan(final int clanId) {
        if (clanId <= 0) {
            return;
        }
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).deleteSiegeClanByCastle(clanId, this.castle.getId());
        this.attackers.remove(clanId);
        this.defendersWaiting.remove(clanId);
        this.defenders.remove(clanId);
    }
    
    private void teleportSpectatorsInZone(final TeleportWhereType teleportWhere) {
        this.castle.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere), p -> !p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
    }
    
    private void teleportAttackersInZone(final TeleportWhereType teleportWhere) {
        this.attackers.values().stream().map(a -> ClanTable.getInstance().getClan(a.getClanId())).forEach(clan -> clan.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed()));
    }
    
    private void teleportNotOwnerInZone(final TeleportWhereType teleportWhere) {
        this.castle.getZone().forEachPlayer(p -> p.teleToLocation(teleportWhere), p -> !p.inObserverMode() && (p.getClanId() <= 0 || p.getClanId() != this.castle.getOwnerId()) && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
    }
    
    private void teleportOnwersInZone(final TeleportWhereType teleportWhere) {
        final SiegeClanData defenderClan = this.getDefenderClan(this.castle.getOwnerId());
        if (Objects.nonNull(defenderClan)) {
            final Clan clan = ClanTable.getInstance().getClan(defenderClan.getClanId());
            clan.forEachOnlineMember(p -> p.teleToLocation(teleportWhere), p -> p.isInSiege() && !p.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && !p.isJailed());
        }
    }
    
    public boolean checkIfAlreadyRegisteredForSameDay(final Clan clan) {
        for (final Siege siege : SiegeManager.getInstance().getSieges()) {
            if (siege == this) {
                continue;
            }
            if (ChronoUnit.DAYS.between(siege.getSiegeDate(), this.getSiegeDate()) == 0L && (siege.checkIsAttacker(clan) || siege.checkIsDefender(clan) || siege.checkIsDefenderWaiting(clan))) {
                return true;
            }
        }
        return false;
    }
    
    private void removeTowers() {
        this.flameTowers.forEach(FlameTower::deleteMe);
        this.flameTowers.clear();
        this.controlTowers.forEach(Npc::deleteMe);
        this.controlTowers.clear();
    }
    
    private void removeFlags() {
        this.attackers.values().forEach(SiegeClanData::removeFlags);
        this.attackers.values().forEach(SiegeClanData::removeFlags);
    }
    
    private void removeDefenderFlags() {
        this.defenders.values().forEach(SiegeClanData::removeFlags);
    }
    
    @Override
    public final SiegeClanData getAttackerClan(final Clan clan) {
        if (Objects.isNull(clan)) {
            return null;
        }
        return this.getAttackerClan(clan.getId());
    }
    
    @Override
    public final SiegeClanData getAttackerClan(final int clanId) {
        return (SiegeClanData)this.attackers.get(clanId);
    }
    
    public final int getAttackerRespawnDelay() {
        return SiegeManager.getInstance().getAttackerRespawnDelay();
    }
    
    public final Castle getCastle() {
        return this.castle;
    }
    
    @Override
    public final SiegeClanData getDefenderClan(final Clan clan) {
        if (clan == null) {
            return null;
        }
        return this.getDefenderClan(clan.getId());
    }
    
    @Override
    public final SiegeClanData getDefenderClan(final int clanId) {
        return (SiegeClanData)this.getDefenderClans().get(clanId);
    }
    
    @Override
    public final IntMap<SiegeClanData> getDefenderClans() {
        return this.defenders;
    }
    
    public final SiegeClanData getDefenderWaitingClan(final Clan clan) {
        if (clan == null) {
            return null;
        }
        return this.getDefenderWaitingClan(clan.getId());
    }
    
    public final SiegeClanData getDefenderWaitingClan(final int clanId) {
        return (SiegeClanData)this.defendersWaiting.get(clanId);
    }
    
    public final IntMap<SiegeClanData> getDefendersWaiting() {
        return this.defendersWaiting;
    }
    
    public final boolean isInProgress() {
        return this.isInProgress;
    }
    
    public final boolean getIsRegistrationOver() {
        return this.isRegistrationOver;
    }
    
    @Override
    public final LocalDateTime getSiegeDate() {
        return this.castle.getSiegeDate();
    }
    
    public void endTimeRegistration(final boolean automatic) {
        this.castle.setSiegeTimeRegistrationEnd(LocalDateTime.now());
        if (!automatic) {
            this.saveSiegeDate();
        }
    }
    
    @Override
    public Set<Npc> getFlag(final Clan clan) {
        if (Objects.nonNull(clan)) {
            return (Set<Npc>)Util.computeIfNonNull((Object)this.getAttackerClan(clan), (Function)SiegeClanData::getFlags);
        }
        return null;
    }
    
    public int getControlTowerCount() {
        return this.controlTowerCount;
    }
    
    @Override
    public boolean giveFame() {
        return true;
    }
    
    @Override
    public int getFameFrequency() {
        return Config.CASTLE_ZONE_FAME_TASK_FREQUENCY;
    }
    
    @Override
    public int getFameAmount() {
        return Config.CASTLE_ZONE_FAME_AQUIRE_POINTS;
    }
    
    @Override
    public void updateSiege() {
    }
    
    public void announceToPlayer(final SystemMessage message, final boolean bothSides) {
        final Stream<SiegeClanData> stream = bothSides ? Stream.concat(this.getDefenderClans().values().stream(), this.getAttackerClans().values().stream()) : this.getDefenderClans().values().stream();
        stream.map(siegeClan -> ClanTable.getInstance().getClan(siegeClan.getClanId())).filter(Objects::nonNull).forEach(clan -> {
            Objects.requireNonNull(message);
            clan.forEachOnlineMember(message::sendTo);
        });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Siege.class);
    }
    
    private class ScheduleEndSiegeTask implements Runnable
    {
        @Override
        public void run() {
            if (!Siege.this.isInProgress) {
                return;
            }
            final Duration timeRemaining = Duration.between(Instant.now(), Siege.this.endTime);
            if (timeRemaining.compareTo(ChronoUnit.HOURS.getDuration()) > 0) {
                Siege.this.announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int)timeRemaining.toHours()), true);
                ThreadPool.schedule((Runnable)this, timeRemaining.minusHours(1L));
            }
            else if (timeRemaining.toMinutes() > 10L) {
                Siege.this.announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int)timeRemaining.toMinutes()), true);
                ThreadPool.schedule((Runnable)this, timeRemaining.minusMinutes(10L));
            }
            else if (timeRemaining.toMinutes() > 5L) {
                Siege.this.announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION).addInt((int)timeRemaining.toMinutes()), true);
                ThreadPool.schedule((Runnable)this, timeRemaining.minusMinutes(5L));
            }
            else if (timeRemaining.toSeconds() > 10L) {
                Siege.this.announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S).addInt((int)timeRemaining.toSeconds()), true);
                ThreadPool.schedule((Runnable)this, timeRemaining.minusSeconds(10L));
            }
            else if (timeRemaining.toSeconds() > 0L) {
                Siege.this.announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S).addInt((int)timeRemaining.toSeconds()), true);
                ThreadPool.schedule((Runnable)this, 1L, TimeUnit.SECONDS);
            }
            else {
                Siege.this.castle.getSiege().endSiege();
            }
        }
    }
    
    private class ScheduleStartSiegeTask implements Runnable
    {
        private final Castle _castleInst;
        
        public ScheduleStartSiegeTask(final Castle pCastle) {
            this._castleInst = pCastle;
        }
        
        @Override
        public void run() {
            Siege.this.scheduledStartSiegeTask.cancel(false);
            if (Siege.this.isInProgress) {
                return;
            }
            if (Siege.this.castle.isSiegeTimeRegistrationSeason()) {
                final Duration regTimeRemaining = Duration.between(Instant.now(), Siege.this.castle.getSiegeTimeRegistrationEnd());
                if (regTimeRemaining.compareTo(Duration.ZERO) > 0) {
                    Siege.this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this._castleInst), regTimeRemaining);
                    return;
                }
                Siege.this.endTimeRegistration(true);
            }
            final Duration duration = Duration.between(Instant.now(), Siege.this.getSiegeDate());
            if (duration.compareTo(ChronoUnit.DAYS.getDuration()) > 0) {
                Siege.this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this._castleInst), duration.minusDays(1L));
            }
            else if (duration.compareTo(ChronoUnit.HOURS.getDuration()) > 0) {
                Siege.this.isRegistrationOver = true;
                Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED)).addCastleId(Siege.this.castle.getId()));
                Siege.this.clearSiegeWaitingClan();
                Siege.this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this._castleInst), duration.minusHours(1L));
            }
            else if (duration.compareTo(Duration.ZERO) > 0) {
                Siege.this.scheduledStartSiegeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleStartSiegeTask(this._castleInst), duration);
            }
            else {
                this._castleInst.getSiege().startSiege();
            }
        }
    }
}
