// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.l2j.commons.threading.ThreadPool;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.CastleFunctionData;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.data.xml.impl.CastleDataManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.TowerSpawn;
import org.l2j.gameserver.Config;
import java.time.temporal.Temporal;
import java.time.Duration;
import java.time.LocalDateTime;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.gameserver.world.zone.type.ResidenceZone;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.ArrayList;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.data.database.data.CastleData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.world.zone.type.ResidenceTeleportZone;
import org.l2j.gameserver.world.zone.type.SiegeZone;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Door;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.model.residences.AbstractResidence;

public final class Castle extends AbstractResidence
{
    public static final int FUNC_TELEPORT = 1;
    public static final int FUNC_RESTORE_HP = 2;
    public static final int FUNC_RESTORE_MP = 3;
    public static final int FUNC_RESTORE_EXP = 4;
    public static final int FUNC_SUPPORT = 5;
    protected static final Logger LOGGER;
    private final IntMap<Door> doors;
    private final List<Npc> sideNpcs;
    private final List<Artefact> artefacts;
    private final IntMap<CastleFunction> functions;
    int ownerId;
    private Siege siege;
    private SiegeZone zone;
    private ResidenceTeleportZone teleZone;
    private Clan formerOwner;
    private final CastleData data;
    
    public Castle(final CastleData data) {
        super(data.getId());
        this.doors = (IntMap<Door>)new HashIntMap();
        this.sideNpcs = new ArrayList<Npc>();
        this.artefacts = new ArrayList<Artefact>(1);
        this.functions = (IntMap<CastleFunction>)new CHashIntMap();
        this.ownerId = 0;
        this.siege = null;
        this.zone = null;
        this.formerOwner = null;
        this.setName(data.getName());
        this.data = data;
        this.load();
        this.initResidenceZone();
        this.spawnSideNpcs();
    }
    
    @Override
    protected void load() {
        this.ownerId = ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findOwnerClanIdByCastle(this.getId());
        if (this.ownerId != 0) {
            this.loadFunctions();
            this.loadDoorUpgrade();
        }
    }
    
    @Override
    protected void initResidenceZone() {
        for (final CastleZone zone : ZoneManager.getInstance().getAllZones(CastleZone.class)) {
            if (zone.getResidenceId() == this.getId()) {
                this.setResidenceZone(zone);
                break;
            }
        }
    }
    
    private void spawnSideNpcs() {
        this.sideNpcs.stream().filter(Objects::nonNull).forEach(Npc::deleteMe);
        this.sideNpcs.clear();
        for (final CastleSpawnHolder holder : this.getSideSpawns()) {
            try {
                final Spawn spawn = new Spawn(holder.getNpcId());
                spawn.setXYZ(holder);
                spawn.setHeading(holder.getHeading());
                final Npc npc = spawn.doSpawn(false);
                spawn.stopRespawn();
                npc.broadcastInfo();
                this.sideNpcs.add(npc);
            }
            catch (Exception e) {
                Castle.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
        }
    }
    
    private void loadFunctions() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).findFunctionsByCastle(this.getId()).forEach(functionData -> this.functions.put(functionData.getType(), (Object)new CastleFunction(functionData, 0, true)));
    }
    
    public CastleFunction getCastleFunction(final int type) {
        if (this.functions.containsKey(type)) {
            return (CastleFunction)this.functions.get(type);
        }
        return null;
    }
    
    public synchronized void engrave(final Clan clan, final WorldObject target, final CastleSide side) {
        if (!(target instanceof Artefact) || !this.artefacts.contains(target)) {
            return;
        }
        this.setSide(side);
        this.setOwner(clan);
        this.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_HAS_SUCCEEDED_IN_S2).addString(clan.getName()).addString(this.getName()), true);
    }
    
    public void addToTreasury(long amount) {
        if (this.ownerId <= 0) {
            return;
        }
        final String lowerCase = this.getName().toLowerCase();
        switch (lowerCase) {
            case "schuttgart":
            case "goddard": {
                final Castle rune = CastleManager.getInstance().getCastle("rune");
                if (Objects.nonNull(rune)) {
                    final long runeTax = (long)(amount * rune.getTaxRate(TaxType.BUY));
                    if (rune.getOwnerId() > 0) {
                        rune.addToTreasury(runeTax);
                    }
                    amount -= runeTax;
                }
                break;
            }
            case "dion":
            case "giran":
            case "gludio":
            case "innadril":
            case "oren": {
                final Castle aden = CastleManager.getInstance().getCastle("aden");
                if (aden != null) {
                    final long adenTax = (long)(amount * aden.getTaxRate(TaxType.BUY));
                    if (aden.getOwnerId() > 0) {
                        aden.addToTreasury(adenTax);
                    }
                    amount -= adenTax;
                    break;
                }
                break;
            }
        }
        this.addToTreasuryNoTax(amount);
    }
    
    public void addToTreasuryNoTax(long amount) {
        if (this.ownerId <= 0) {
            return;
        }
        if (amount < 0L) {
            amount *= -1L;
            if (this.data.getTreasury() < amount) {
                return;
            }
            this.data.updateTreasury(-amount);
        }
        else if (this.data.getTreasury() + amount > Inventory.MAX_ADENA) {
            this.data.setTreasury(Inventory.MAX_ADENA);
        }
        else {
            this.data.updateTreasury(amount);
        }
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).updateTreasury(this.getId(), this.data.getTreasury());
    }
    
    public void banishForeigners() {
        this.getResidenceZone().banishForeigners(this.ownerId);
    }
    
    public boolean checkIfInZone(final int x, final int y, final int z) {
        return this.getZone().isInsideZone(x, y, z);
    }
    
    public boolean checkIfInZone(final ILocational loc) {
        return this.getZone().isInsideZone(loc);
    }
    
    public SiegeZone getZone() {
        if (Objects.isNull(this.zone)) {
            for (final SiegeZone zone : ZoneManager.getInstance().getAllZones(SiegeZone.class)) {
                if (zone.getSiegeObjectId() == this.getId()) {
                    this.zone = zone;
                    break;
                }
            }
        }
        return this.zone;
    }
    
    @Override
    public CastleZone getResidenceZone() {
        return (CastleZone)super.getResidenceZone();
    }
    
    public ResidenceTeleportZone getTeleZone() {
        if (Objects.isNull(this.teleZone)) {
            for (final ResidenceTeleportZone zone : ZoneManager.getInstance().getAllZones(ResidenceTeleportZone.class)) {
                if (zone.getResidenceId() == this.getId()) {
                    this.teleZone = zone;
                    break;
                }
            }
        }
        return this.teleZone;
    }
    
    public void oustAllPlayers() {
        this.getTeleZone().oustAllPlayers();
    }
    
    public double getDistance(final WorldObject obj) {
        return this.getZone().getDistanceToZone(obj);
    }
    
    private void openCloseDoor(final Player player, final Door door, final boolean open) {
        if (Objects.isNull(door) || (player.getClanId() != this.ownerId && !player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS))) {
            return;
        }
        if (open) {
            door.openMe();
        }
        else {
            door.closeMe();
        }
    }
    
    public void openCloseDoor(final Player player, final int doorId, final boolean open) {
        this.openCloseDoor(player, this.getDoor(doorId), open);
    }
    
    public void openCloseDoor(final Player activeChar, final String doorName, final boolean open) {
        this.openCloseDoor(activeChar, this.getDoor(doorName), open);
    }
    
    public void removeUpgrade() {
        this.removeDoorUpgrade();
        this.removeTrapUpgrade();
        this.functions.keySet().forEach(this::removeFunction);
        this.functions.clear();
    }
    
    public void removeOwner(final Clan clan) {
        if (Objects.nonNull(clan)) {
            this.formerOwner = clan;
            if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).removeCastleCirclets()) {
                CastleManager.getInstance().removeCirclet(this.formerOwner, this.getId());
            }
            for (final Player member : clan.getOnlineMembers(0)) {
                this.removeResidentialSkills(member);
                member.sendSkillList();
            }
            clan.setCastleId(0);
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
        }
        this.setSide(CastleSide.NEUTRAL);
        this.updateOwnerInDB(null);
        if (this.getSiege().isInProgress()) {
            this.getSiege().midVictory();
        }
        this.functions.keySet().forEach(this::removeFunction);
        this.functions.clear();
    }
    
    public void spawnDoor() {
        this.spawnDoor(false);
    }
    
    public void spawnDoor(final boolean isDoorWeak) {
        for (final Door door : this.doors.values()) {
            if (door.isDead()) {
                door.doRevive();
                door.setCurrentHp(isDoorWeak ? ((double)(door.getMaxHp() / 2.0f)) : ((double)door.getMaxHp()));
            }
            if (door.isOpen()) {
                door.closeMe();
            }
        }
    }
    
    public void removeFunction(final int functionType) {
        this.functions.remove(functionType);
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).deleteFunction(this.getId(), functionType);
    }
    
    public boolean updateFunctions(final Player player, final int type, final int level, final int lease, final long rate, final boolean addNew) {
        if (Objects.isNull(player)) {
            return false;
        }
        if (lease > 0 && !player.destroyItemByItemId("Consume", 57, lease, null, true)) {
            return false;
        }
        if (addNew) {
            this.functions.put(type, (Object)new CastleFunction(type, level, lease, 0, rate, 0, false));
        }
        else if (level == 0 && lease == 0) {
            this.removeFunction(type);
        }
        else {
            final int diffLease = lease - ((CastleFunction)this.functions.get(type)).getLease();
            if (diffLease > 0) {
                this.functions.remove(type);
                this.functions.put(type, (Object)new CastleFunction(type, level, lease, 0, rate, -1, false));
            }
            else {
                ((CastleFunction)this.functions.get(type)).setLease(lease);
                ((CastleFunction)this.functions.get(type)).setLevel(level);
                ((CastleFunction)this.functions.get(type)).dbSave();
            }
        }
        return true;
    }
    
    public void activateInstance() {
        this.loadDoor();
    }
    
    private void loadDoor() {
        DoorDataManager.getInstance().getDoors().stream().filter(d -> Util.falseIfNullOrElse((Object)d.getCastle(), c -> c.getId() == this.getId())).forEach(d -> this.doors.put(d.getId(), (Object)d));
    }
    
    private void loadDoorUpgrade() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).withDoorUpgradeDo(this.getId(), this::processDoorUpgrade);
    }
    
    private void processDoorUpgrade(final ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                this.setDoorUpgrade(resultSet.getInt(1), resultSet.getInt(2), false);
            }
        }
        catch (SQLException e) {
            Castle.LOGGER.error(e.getMessage(), (Throwable)e);
        }
    }
    
    public void setDoorUpgrade(final int doorId, final int ratio, final boolean save) {
        final Door door = this.doors.isEmpty() ? DoorDataManager.getInstance().getDoor(doorId) : this.getDoor(doorId);
        if (Objects.isNull(door)) {
            return;
        }
        door.getStats().setUpgradeHpRatio(ratio);
        door.setCurrentHp(door.getMaxHp());
        if (save) {
            ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).saveDoorUpgrade(this.getId(), doorId, ratio);
        }
    }
    
    private void removeDoorUpgrade() {
        for (final Door door : this.doors.values()) {
            door.getStats().setUpgradeHpRatio(1);
            door.setCurrentHp(door.getCurrentHp());
        }
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).deleteDoorUpgradeByCastle(this.getId());
    }
    
    private void updateOwnerInDB(final Clan clan) {
        if (Objects.nonNull(clan)) {
            this.ownerId = clan.getId();
        }
        else {
            this.ownerId = 0;
            CastleManorManager.getInstance().resetManorData(this.getId());
        }
        final ClanDAO clanDao = (ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class);
        clanDao.removeOwnerClanByCastle(this.getId());
        if (this.ownerId != 0) {
            clanDao.updateOwnedCastle(this.ownerId, this.getId());
        }
        if (Objects.nonNull(clan)) {
            clan.setCastleId(this.getId());
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
            clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
        }
    }
    
    public final Door getDoor(final int doorId) {
        return (Door)this.doors.get(doorId);
    }
    
    public final Door getDoor(final String doorName) {
        return this.doors.values().stream().filter(d -> d.getTemplate().getName().equals(doorName)).findFirst().orElse(null);
    }
    
    public final Collection<Door> getDoors() {
        return (Collection<Door>)this.doors.values();
    }
    
    @Override
    public final int getOwnerId() {
        return this.ownerId;
    }
    
    public final Clan getOwner() {
        return (this.ownerId != 0) ? ClanTable.getInstance().getClan(this.ownerId) : null;
    }
    
    public void setOwner(final Clan clan) {
        if (this.ownerId > 0 && (Objects.isNull(clan) || clan.getId() != this.ownerId)) {
            final Clan oldOwner = this.getOwner();
            if (Objects.nonNull(oldOwner)) {
                if (Objects.isNull(this.formerOwner)) {
                    this.formerOwner = oldOwner;
                    if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).removeCastleCirclets()) {
                        CastleManager.getInstance().removeCirclet(this.formerOwner, this.getId());
                    }
                }
                Util.doIfNonNull((Object)oldOwner.getLeader().getPlayerInstance(), oldleader -> {
                    if (oldleader.getMountType() == MountType.WYVERN) {
                        oldleader.dismount();
                    }
                    return;
                });
                oldOwner.setCastleId(0);
                oldOwner.forEachOnlineMember(m -> {
                    this.removeResidentialSkills(m);
                    m.sendSkillList();
                    m.broadcastUserInfo();
                    return;
                });
            }
        }
        this.updateOwnerInDB(clan);
        this.setShowNpcCrest(false);
        if (this.getSiege().isInProgress()) {
            this.getSiege().midVictory();
        }
        if (Objects.nonNull(clan)) {
            clan.forEachOnlineMember(m -> {
                this.giveResidentialSkills(m);
                m.sendSkillList();
            });
        }
    }
    
    public final Siege getSiege() {
        if (Objects.isNull(this.siege)) {
            this.siege = new Siege(this);
        }
        return this.siege;
    }
    
    public final LocalDateTime getSiegeDate() {
        return this.data.getSiegeDate();
    }
    
    public boolean isSiegeTimeRegistrationSeason() {
        return Duration.between(LocalDateTime.now(), this.data.getSiegeDate()).toDays() > 0L;
    }
    
    public LocalDateTime getSiegeTimeRegistrationEnd() {
        return this.data.getSiegeTimeRegistrationEnd();
    }
    
    public void setSiegeTimeRegistrationEnd(final LocalDateTime date) {
        this.data.setSiegeTimeRegistrationEnd(date);
    }
    
    public final int getTaxPercent(final TaxType type) {
        int n = 0;
        switch (this.data.getSide()) {
            case LIGHT: {
                n = ((type == TaxType.BUY) ? Config.CASTLE_BUY_TAX_LIGHT : Config.CASTLE_SELL_TAX_LIGHT);
                break;
            }
            case DARK: {
                n = ((type == TaxType.BUY) ? Config.CASTLE_BUY_TAX_DARK : Config.CASTLE_SELL_TAX_DARK);
                break;
            }
            default: {
                n = ((type == TaxType.BUY) ? Config.CASTLE_BUY_TAX_NEUTRAL : Config.CASTLE_SELL_TAX_NEUTRAL);
                break;
            }
        }
        return n;
    }
    
    public final double getTaxRate(final TaxType taxType) {
        return this.getTaxPercent(taxType) / 100.0;
    }
    
    public final long getTreasury() {
        return this.data.getTreasury();
    }
    
    public final boolean isShowNpcCrest() {
        return this.data.isShowNpcCrest();
    }
    
    public final void setShowNpcCrest(final boolean showNpcCrest) {
        if (this.data.isShowNpcCrest() != showNpcCrest) {
            this.data.setShowNpcCrest(showNpcCrest);
            this.updateShowNpcCrest();
        }
    }
    
    public void updateClansReputation() {
        final Clan owner = this.getOwner();
        if (Objects.nonNull(this.formerOwner)) {
            if (this.formerOwner != owner) {
                final int maxReward = Math.max(0, this.formerOwner.getReputationScore());
                this.formerOwner.takeReputationScore(Config.LOOSE_CASTLE_POINTS, true);
                if (Objects.nonNull(owner)) {
                    owner.addReputationScore(Math.min(Config.TAKE_CASTLE_POINTS, maxReward), true);
                }
            }
            else {
                this.formerOwner.addReputationScore(Config.CASTLE_DEFENDED_POINTS, true);
            }
        }
        else if (Objects.nonNull(owner)) {
            owner.addReputationScore(Config.TAKE_CASTLE_POINTS, true);
        }
    }
    
    public void updateShowNpcCrest() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).updateShowNpcCrest(this.getId(), this.data.isShowNpcCrest());
    }
    
    public void registerArtefact(final Artefact artefact) {
        this.artefacts.add(artefact);
    }
    
    public List<Artefact> getArtefacts() {
        return this.artefacts;
    }
    
    public void setTicketBuyCount(final int count) {
        this.data.setTicketBuyCount(count);
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).updateTicketBuyCount(this.getId(), this.data.getTicketBuyCount());
    }
    
    public int getTrapUpgradeLevel(final int towerIndex) {
        return Util.zeroIfNullOrElse((Object)SiegeManager.getInstance().getFlameTowers(this.getId()).get(towerIndex), TowerSpawn::getUpgradeLevel);
    }
    
    public void setTrapUpgrade(final int towerIndex, final int level, final boolean save) {
        if (save) {
            ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).saveTrapUpgrade(this.getId(), towerIndex, level);
        }
        Util.doIfNonNull((Object)SiegeManager.getInstance().getFlameTowers(this.getId()).get(towerIndex), spawn -> spawn.setUpgradeLevel(level));
    }
    
    private void removeTrapUpgrade() {
        SiegeManager.getInstance().getFlameTowers(this.getId()).forEach(tower -> tower.setUpgradeLevel(0));
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).deleteTrapUpgradeByCastle(this.getId());
    }
    
    @Override
    public void giveResidentialSkills(final Player player) {
        super.giveResidentialSkills(player);
        final Skill skill = (this.data.getSide() == CastleSide.DARK) ? CommonSkill.ABILITY_OF_DARKNESS.getSkill() : CommonSkill.ABILITY_OF_LIGHT.getSkill();
        player.addSkill(skill);
    }
    
    @Override
    public void removeResidentialSkills(final Player player) {
        super.removeResidentialSkills(player);
        player.removeSkill(CommonSkill.ABILITY_OF_DARKNESS.getId());
        player.removeSkill(CommonSkill.ABILITY_OF_LIGHT.getId());
    }
    
    public List<CastleSpawnHolder> getSideSpawns() {
        return CastleDataManager.getInstance().getSpawnsForSide(this.getId(), this.getSide());
    }
    
    public CastleSide getSide() {
        return this.data.getSide();
    }
    
    public void setSide(final CastleSide side) {
        if (this.getSide() == side) {
            return;
        }
        this.data.setSide(side);
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).updateSide(this.getId(), side);
        Broadcast.toAllOnlinePlayers(new ExCastleState(this));
        this.spawnSideNpcs();
    }
    
    public boolean isInSiege() {
        return this.getSiege().isInProgress();
    }
    
    public void updateSiegeDate() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).save((Object)this.data);
    }
    
    public void setSiegeDate(final LocalDateTime siegeDate) {
        this.data.setSiegeDate(siegeDate);
        this.updateSiegeDate();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Castle.class);
    }
    
    public class CastleFunction
    {
        private final CastleFunctionData functionData;
        public boolean cwh;
        private final int tempFee;
        
        CastleFunction(final CastleFunctionData data, final int tempLease, final boolean cwh) {
            this.functionData = data;
            this.tempFee = tempLease;
            this.initializeTask(cwh);
        }
        
        public CastleFunction(final int type, final int level, final int lease, final int tempLease, final long rate, final int time, final boolean cwh) {
            this.functionData = new CastleFunctionData(type, level, lease, rate, time);
            this.cwh = cwh;
            this.tempFee = tempLease;
        }
        
        private void initializeTask(final boolean cwh) {
            if (Castle.this.ownerId <= 0) {
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (this.functionData.getEndTime() > currentTime) {
                ThreadPool.schedule((Runnable)new FunctionTask(cwh), this.functionData.getEndTime() - currentTime);
            }
            else {
                ThreadPool.schedule((Runnable)new FunctionTask(cwh), 0L);
            }
        }
        
        public void dbSave() {
            ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).save(this.functionData);
        }
        
        public int getLease() {
            return this.functionData.getLease();
        }
        
        public void setLease(final int lease) {
            this.functionData.setLease(lease);
        }
        
        public void setLevel(final int level) {
            this.functionData.setLevel(level);
        }
        
        public int getLevel() {
            return this.functionData.getLevel();
        }
        
        public long getEndTime() {
            return this.functionData.getEndTime();
        }
        
        public long getRate() {
            return this.functionData.getRate();
        }
        
        private class FunctionTask implements Runnable
        {
            public FunctionTask(final boolean cwh) {
                CastleFunction.this.cwh = cwh;
            }
            
            @Override
            public void run() {
                try {
                    if (Castle.this.ownerId <= 0) {
                        return;
                    }
                    final Clan owner = Castle.this.getOwner();
                    if (Objects.nonNull(owner) && (owner.getWarehouse().getAdena() >= CastleFunction.this.functionData.getLease() || !CastleFunction.this.cwh)) {
                        int fee = CastleFunction.this.functionData.getLease();
                        if (CastleFunction.this.functionData.getEndTime() == -1L) {
                            fee = CastleFunction.this.tempFee;
                        }
                        CastleFunction.this.functionData.setEndTime(System.currentTimeMillis() + CastleFunction.this.functionData.getRate());
                        CastleFunction.this.dbSave();
                        if (CastleFunction.this.cwh) {
                            owner.getWarehouse().destroyItemByItemId("CS_function_fee", 57, fee, null, null);
                        }
                        ThreadPool.schedule((Runnable)new FunctionTask(true), CastleFunction.this.functionData.getRate());
                    }
                    else {
                        Castle.this.removeFunction(CastleFunction.this.functionData.getType());
                    }
                }
                catch (Exception e) {
                    Castle.LOGGER.error(e.getMessage(), (Throwable)e);
                }
            }
        }
    }
}
