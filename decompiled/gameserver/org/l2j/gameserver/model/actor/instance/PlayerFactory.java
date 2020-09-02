// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import java.util.Objects;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class PlayerFactory
{
    private static final Logger LOGGER;
    
    public static Player loadPlayer(final GameClient client, final int playerId) {
        final PlayerDAO playerDAO = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        final PlayerData playerData = playerDAO.findById(playerId);
        if (Objects.isNull(playerData)) {
            return null;
        }
        final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(playerData.getClassId());
        final Player player = new Player(playerData, template);
        player.setClient(client);
        client.setPlayer(player);
        player.setVariables(Objects.requireNonNullElseGet(((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).findById(playerId), () -> PlayerVariableData.init(playerId)));
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
        player.getFreight().restore();
        if (!Config.WAREHOUSE_CACHE) {
            player.getWarehouse();
        }
        player.restoreItemReuse();
        player.restoreShortCuts();
        EventDispatcher.getInstance().notifyEvent(new OnPlayerLoad(player), Listeners.players());
        player.initStatusUpdateCache();
        player.setCurrentCp(playerData.getCurrentCp());
        player.setCurrentHp(playerData.getCurrentHp());
        player.setCurrentMp(playerData.getCurrentMp());
        player.setOriginalCpHpMp(playerData.getCurrentCp(), playerData.getCurrentHp(), playerData.getCurrentMp());
        if (playerData.getCurrentHp() < 0.5) {
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
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement stmt = con.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=? AND charId<>?");
                try {
                    stmt.setString(1, playerData.getAccountName());
                    stmt.setInt(2, playerId);
                    final ResultSet chars = stmt.executeQuery();
                    while (chars.next()) {
                        player.getAccountChars().put(chars.getInt("charId"), (Object)chars.getString("char_name"));
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (Throwable t) {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            PlayerFactory.LOGGER.error("Failed loading character.", (Throwable)e);
        }
        return player;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayerFactory.class);
    }
}
