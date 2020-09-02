// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ClanMember;
import java.util.Iterator;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.engine.item.shop.LCoinShop;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.util.Objects;
import org.w3c.dom.Node;
import org.l2j.gameserver.util.GameXmlReader;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class DailyTaskManager extends AbstractEventManager<AbstractEvent>
{
    private static final Logger LOGGER;
    IntSet resetSkills;
    
    private DailyTaskManager() {
        this.resetSkills = (IntSet)new HashIntSet();
    }
    
    @Override
    public void config(final GameXmlReader reader, final Node configNode) {
        final Node dailyConfig = configNode.getFirstChild();
        if (Objects.nonNull(dailyConfig) && dailyConfig.getNodeName().equals("daily-config")) {
            for (Node skillNode = dailyConfig.getFirstChild(); Objects.nonNull(skillNode); skillNode = skillNode.getNextSibling()) {
                this.resetSkills.add(reader.parseInt(skillNode.getAttributes(), "id"));
            }
        }
    }
    
    @Override
    public void onInitialized() {
    }
    
    private void onReset() {
        ClanTable.getInstance().getClans().forEach(Clan::resetClanBonus);
        this.resetDailyMissionRewards();
        this.resetDailySkills();
        RankEngine.getInstance().updateRankers();
        this.resetPlayersData();
        LCoinShop.getInstance().reloadShopHistory();
        DailyTaskManager.LOGGER.info("Daily task has been reset.");
    }
    
    private void resetPlayersData() {
        World.getInstance().forEachPlayer(player -> {
            player.setExtendDrop("");
            player.setRecomLeft(20);
            player.setRecomHave(player.getRecomHave() - 20);
            player.sendPacket(new ExVoteSystemInfo(player));
            if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatEnabled()) {
                player.setWorldChatUsed(0);
                player.sendPacket(new ExWorldChatCnt(player));
            }
            if (player.getVipTier() > 0) {
                VipEngine.getInstance().checkVipTierExpiration(player);
            }
            player.storeVariables();
            player.resetRevengeData();
            player.broadcastUserInfo();
            return;
        });
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetExtendDrop();
        DailyTaskManager.LOGGER.info("Daily Extend Drop has been reset.");
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).resetRecommends();
        if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatEnabled()) {
            ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetWorldChatPoint();
            DailyTaskManager.LOGGER.info("Daily world chat points has been reset.");
        }
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetRevengeData();
    }
    
    private void onSave() {
        GlobalVariablesManager.getInstance().storeMe();
    }
    
    private void onClansTask() {
        for (final Clan clan : ClanTable.getInstance().getClans()) {
            this.checkNewLeader(clan);
            ClanRewardManager.getInstance().checkArenaProgress(clan);
        }
        GlobalVariablesManager.getInstance().resetRaidBonus();
        DailyTaskManager.LOGGER.info("Clans has been updated");
    }
    
    private void checkNewLeader(final Clan clan) {
        if (clan.getNewLeaderId() != 0) {
            final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
            if (Objects.nonNull(member)) {
                clan.setNewLeader(member);
            }
        }
    }
    
    private void onVitalityReset() {
        if (!Config.ENABLE_VITALITY) {
            return;
        }
        World.getInstance().forEachPlayer(player -> player.setVitalityPoints(140000, false));
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).resetVitality(140000);
        DailyTaskManager.LOGGER.info("Vitality has been reset");
    }
    
    private void resetDailySkills() {
        final PlayerDAO playerDao = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        final IntSet resetSkills = this.resetSkills;
        final PlayerDAO obj = playerDao;
        Objects.requireNonNull(obj);
        resetSkills.forEach(obj::deleteSkillSave);
        DailyTaskManager.LOGGER.info("Daily skill reuse cleaned.");
    }
    
    private void resetDailyMissionRewards() {
        final EventScheduler scheduler = this.getScheduler("reset");
        final long lastReset = Objects.nonNull(scheduler) ? scheduler.getLastRun() : 0L;
        MissionData.getInstance().getMissions().forEach(mission -> mission.reset(lastReset));
    }
    
    public static DailyTaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)DailyTaskManager.class);
    }
    
    private static class Singleton
    {
        private static final DailyTaskManager INSTANCE;
        
        static {
            INSTANCE = new DailyTaskManager();
        }
    }
}
