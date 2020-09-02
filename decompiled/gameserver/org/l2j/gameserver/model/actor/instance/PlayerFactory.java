// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.List;
import org.l2j.gameserver.model.item.EquipableItem;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.PcItemTemplate;
import org.l2j.gameserver.data.xml.impl.InitialEquipmentData;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.model.Location;
import java.time.LocalDate;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.idfactory.IdFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.InitialShortcutData;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import java.util.Objects;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class PlayerFactory
{
    private static final Logger LOGGER;
    
    private PlayerFactory() {
    }
    
    public static Player loadPlayer(final GameClient client, final int playerId) {
        final PlayerDAO playerDAO = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        final PlayerData playerData = playerDAO.findById(playerId);
        if (Objects.isNull(playerData)) {
            return null;
        }
        final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(playerData.getClassId());
        final Player player = new Player(client, playerData, template);
        client.setPlayer(player);
        player.setVariables(((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).findById(playerId));
        player.setStatsData(playerDAO.findPlayerStatsData(playerId));
        if (Objects.isNull(player.getStatsData())) {
            player.setStatsData(PlayerStatsData.init(playerId));
            player.updateCharacteristicPoints();
        }
        player.setTeleportFavorites(playerDAO.findTeleportFavorites(playerId));
        player.setHeading(playerData.getHeading());
        player.getStats().setExp(playerData.getExp());
        player.getStats().setLevel(playerData.getLevel());
        player.getStats().setSp(playerData.getSp());
        player.setReputation(playerData.getReputation());
        player.setFame(playerData.getFame());
        player.setPvpKills(playerData.getPvP());
        player.setPkKills(playerData.getPk());
        player.setOnlineTime(playerData.getOnlineTime());
        player.setNoble(playerData.isNobless());
        player.getStats().setVitalityPoints(playerData.getVitalityPoints());
        player.setHero(Hero.getInstance().isHero(playerId));
        if (player.getLevel() >= 40) {
            player.initElementalSpirits();
        }
        if (playerData.getClanId() > 0) {
            player.setClan(ClanTable.getInstance().getClan(playerData.getClanId()));
        }
        if (player.getClan() != null) {
            if (player.getClan().getLeaderId() != player.getObjectId()) {
                if (player.getPowerGrade() == 0) {
                    player.setPowerGrade(5);
                }
                player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
            }
            else {
                player.getClanPrivileges().setAll();
                player.setPowerGrade(1);
            }
            player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        }
        else {
            if (player.isNoble()) {
                player.setPledgeClass(5);
            }
            if (player.isHero()) {
                player.setPledgeClass(8);
            }
            player.getClanPrivileges().clear();
        }
        player.setTitle(playerData.getTitle());
        if (playerData.getTitleColor() != 15530402) {
            player.getAppearance().setTitleColor(playerData.getTitleColor());
        }
        player.setFistsWeaponItem(player.findFistsWeaponItem());
        player.setUptime(System.currentTimeMillis());
        player.setClassIndex(0);
        player._activeClass = playerData.getClassId();
        player.setXYZInvisible(playerData.getX(), playerData.getY(), playerData.getZ());
        player.setLastServerPosition(playerData.getX(), playerData.getY(), playerData.getZ());
        player.setBookMarkSlot(playerData.getBookMarkSlot());
        player.setLang(playerData.getLanguage());
        if (player.isGM()) {
            final long masks = Long.parseLong(player.getCondOverrideKey());
            player.setOverrideCond(masks);
        }
        player.getInventory().restore();
        player.restoreCharData();
        player.rewardSkills();
        if (playerData.getLastAccess() == 0L) {
            InitialShortcutData.getInstance().registerAllShortcuts(player);
        }
        player.getFreight().restore();
        if (!Config.WAREHOUSE_CACHE) {
            player.getWarehouse();
        }
        player.restoreItemReuse();
        player.restoreShortCuts();
        EventDispatcher.getInstance().notifyEvent(new OnPlayerLoad(player), Listeners.players());
        player.initStatusUpdateCache();
        player.setCurrentCp(playerData.getCurrentCp());
        player.setCurrentHp(playerData.getHp());
        player.setCurrentMp(playerData.getMp());
        player.setOriginalCpHpMp(playerData.getCurrentCp(), playerData.getHp(), playerData.getMp());
        if (playerData.getHp() < 0.5) {
            player.setIsDead(true);
            player.stopHpMpRegeneration();
        }
        player.setPet(World.getInstance().findPet(player.getObjectId()));
        final Summon pet = player.getPet();
        if (pet != null) {
            pet.setOwner(player);
        }
        if (player.hasServitors()) {
            for (final Summon summon : player.getServitors().values()) {
                summon.setOwner(player);
            }
        }
        player.getStats().recalculateStats(false);
        player.refreshOverloaded(false);
        player.restoreFriendList();
        player.loadRecommendations();
        player.startRecoGiveTask();
        player.startOnlineTimeUpdateTask();
        player.setOnlineStatus(true, false);
        SaveTaskManager.getInstance().registerPlayer(player);
        for (final PlayerSelectInfo info : client.getPlayersInfo()) {
            if (info.getObjectId() != player.getObjectId()) {
                player.getAccountChars().put(info.getObjectId(), (Object)info.getName());
            }
        }
        return player;
    }
    
    public static void savePlayerData(final PlayerTemplate template, final PlayerData data) {
        data.setId(IdFactory.getInstance().getNextId());
        if (Config.STARTING_LEVEL > 1) {
            data.setLevel(Config.STARTING_LEVEL);
            data.setExperience(LevelData.getInstance().getExpForLevel(Config.STARTING_LEVEL));
        }
        if (Config.STARTING_SP > 0) {
            data.setSp(Config.STARTING_SP);
        }
        final double hp = template.getBaseHpMax(data.getLevel()) * BaseStats.CON.getValue(template.getBaseCON());
        data.setMaxHp(hp);
        data.setHp(hp);
        final double mp = template.getBaseMpMax(data.getLevel()) * BaseStats.MEN.getValue(template.getBaseMEN());
        data.setMaxMp(mp);
        data.setMp(mp);
        if (Config.CUSTOM_STARTING_LOC) {
            data.setX(Config.CUSTOM_STARTING_LOC_X);
            data.setY(Config.CUSTOM_STARTING_LOC_Y);
            data.setZ(Config.CUSTOM_STARTING_LOC_Z);
        }
        else {
            final Location createLoc = template.getCreationPoint();
            data.setX(createLoc.getX());
            data.setY(createLoc.getY());
            data.setZ(createLoc.getZ());
        }
        data.setRace(template.getRace().ordinal());
        data.setTitleColor(15530402);
        data.setCreateDate(LocalDate.now());
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).save((Object)data);
    }
    
    public static void init(final GameClient client, final PlayerData data) {
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).save(PlayerStatsData.init(data.getCharId()));
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).save((Object)PlayerVariableData.init(data.getCharId(), data.getFace(), data.getHairStyle(), data.getHairColor()));
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).resetRecommends();
        addItems(data);
        client.addPlayerInfo(restorePlayerInfo(data));
    }
    
    private static void addItems(final PlayerData data) {
        int nextLocData = 0;
        if (Config.STARTING_ADENA > 0L) {
            ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).saveItem(data.getCharId(), IdFactory.getInstance().getNextId(), 57, Config.STARTING_ADENA, ItemLocation.INVENTORY, nextLocData++);
        }
        final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(data.getClassId());
        for (final PcItemTemplate ie : initialItems) {
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(ie.getId());
            if (Objects.isNull(template)) {
                PlayerFactory.LOGGER.warn("Could not create item during player creation: itemId {}, amount {}", (Object)ie.getId(), (Object)ie.getCount());
            }
            else {
                ItemLocation loc = null;
                int locData = 0;
                Label_0205: {
                    if (ie.isEquipped()) {
                        final ItemTemplate itemTemplate = template;
                        final EquipableItem equipable;
                        if (itemTemplate instanceof EquipableItem && (equipable = (EquipableItem)itemTemplate) == (EquipableItem)itemTemplate) {
                            loc = ItemLocation.PAPERDOLL;
                            locData = equipable.getBodyPart().slot().getId();
                            break Label_0205;
                        }
                    }
                    loc = ItemLocation.INVENTORY;
                    locData = nextLocData++;
                }
                ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).saveItem(data.getCharId(), IdFactory.getInstance().getNextId(), ie.getId(), ie.getCount(), loc, locData);
            }
        }
    }
    
    public static void deletePlayer(final PlayerData data) {
        if (data.getClanId() > 0) {
            final Clan clan = ClanTable.getInstance().getClan(data.getClanId());
            if (clan != null) {
                clan.removeClanMember(data.getCharId(), 0L);
            }
        }
        deleteCharByObjId(data.getCharId());
    }
    
    public static void deleteCharByObjId(final int objId) {
        if (objId < 0) {
            return;
        }
        ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).deleteByOwner(objId);
        final ItemDAO itemDAO = (ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class);
        itemDAO.deleteVariationsByOwner(objId);
        itemDAO.deleteSpecialAbilitiesByOwner(objId);
        itemDAO.deleteByOwner(objId);
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteById(objId);
        PlayerNameTable.getInstance().removeName(objId);
    }
    
    public static List<PlayerSelectInfo> loadPlayersInfo(final GameClient client) {
        final List<PlayerSelectInfo> playersInfo = new ArrayList<PlayerSelectInfo>(7);
        try {
            for (final PlayerData playerData : ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findPlayersByAccount(client.getAccountName())) {
                final PlayerSelectInfo playerInfo = restorePlayerInfo(playerData);
                if (Objects.nonNull(playerInfo)) {
                    playersInfo.add(playerInfo);
                }
            }
        }
        catch (Exception e) {
            PlayerFactory.LOGGER.warn("Could not restore player info", (Throwable)e);
        }
        return playersInfo;
    }
    
    private static PlayerSelectInfo restorePlayerInfo(final PlayerData data) {
        final long deleteTime = data.getDeleteTime();
        if (deleteTime > 0L && System.currentTimeMillis() > deleteTime) {
            deletePlayer(data);
            return null;
        }
        return new PlayerSelectInfo(data);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayerFactory.class);
    }
}
