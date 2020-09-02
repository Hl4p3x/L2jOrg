// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.engine.mission.MissionData;
import java.util.Objects;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import java.util.List;
import java.util.Collections;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ClanMember;
import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class DailyTaskManager extends AbstractEventManager<AbstractEvent<?>>
{
    private static final Logger LOGGER;
    
    private DailyTaskManager() {
    }
    
    @Override
    public void onInitialized() {
    }
    
    private void onReset() {
        this.resetClanBonus();
        this.resetExtendDrop();
        this.resetDailyMissionRewards();
        this.resetDailySkills();
        this.resetRecommends();
        this.resetWorldChatPoints();
        this.resetTrainingCamp();
        this.resetVipTierExpired();
        this.resetRankers();
        this.resetRevengeData();
    }
    
    private void onSave() {
        GlobalVariablesManager.getInstance().storeMe();
    }
    
    private void onClansTask() {
        this.onClanLeaderApply();
        GlobalVariablesManager.getInstance().resetRaidBonus();
        this.onClanResetRaids();
    }
    
    private void onClanResetRaids() {
        ClanTable.getInstance().forEachClan(clan -> ClanRewardManager.getInstance().checkArenaProgress(clan));
    }
    
    private void onClanLeaderApply() {
        for (final Clan clan : ClanTable.getInstance().getClans()) {
            if (clan.getNewLeaderId() != 0) {
                final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
                if (member == null) {
                    continue;
                }
                clan.setNewLeader(member);
            }
        }
        DailyTaskManager.LOGGER.info("Clan leaders has been updated");
    }
    
    private void onVitalityReset() {
        if (!Config.ENABLE_VITALITY) {
            return;
        }
        for (final Player player : World.getInstance().getPlayers()) {
            player.setVitalityPoints(140000, false);
            for (final SubClass subclass : player.getSubClasses().values()) {
                subclass.setVitalityPoints(140000);
            }
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                PreparedStatement st = con.prepareStatement("UPDATE character_subclasses SET vitality_points = ?");
                try {
                    st.setInt(1, 140000);
                    st.execute();
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t) {
                    if (st != null) {
                        try {
                            st.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                st = con.prepareStatement("UPDATE characters SET vitality_points = ?");
                try {
                    st.setInt(1, 140000);
                    st.execute();
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t2) {
                    if (st != null) {
                        try {
                            st.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            DailyTaskManager.LOGGER.warn("Error while updating vitality", (Throwable)e);
        }
        DailyTaskManager.LOGGER.info("Vitality resetted");
    }
    
    private void resetClanBonus() {
        ClanTable.getInstance().getClans().forEach(Clan::resetClanBonus);
        DailyTaskManager.LOGGER.info("Daily clan bonus has been resetted.");
    }
    
    private void resetExtendDrop() {
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetExtendDrop();
        World.getInstance().getPlayers().forEach(player -> {
            player.setExtendDrop("");
            player.storeVariables();
            return;
        });
        DailyTaskManager.LOGGER.info("Daily Extend Drop has been resetted.");
    }
    
    private void resetDailySkills() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final List<SkillHolder> dailySkills = this.getVariables().getList("reset_skills", SkillHolder.class, Collections.emptyList());
                for (final SkillHolder skill : dailySkills) {
                    final PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE skill_id=?;");
                    try {
                        ps.setInt(1, skill.getSkillId());
                        ps.execute();
                        if (ps == null) {
                            continue;
                        }
                        ps.close();
                    }
                    catch (Throwable t) {
                        if (ps != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
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
            DailyTaskManager.LOGGER.error("Could not reset daily skill reuse: ", (Throwable)e);
        }
        DailyTaskManager.LOGGER.info("Daily skill reuse cleaned.");
    }
    
    private void resetRevengeData() {
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetRevengeData();
        World.getInstance().forEachPlayer(player -> player.resetRevengeData());
    }
    
    private void resetWorldChatPoints() {
        if (!((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatEnabled()) {
            return;
        }
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).resetWorldChatPoint();
        World.getInstance().getPlayers().forEach(player -> {
            player.setWorldChatUsed(0);
            player.sendPacket(new ExWorldChatCnt(player));
            player.storeVariables();
            return;
        });
        DailyTaskManager.LOGGER.info("Daily world chat points has been resetted.");
    }
    
    private void resetRecommends() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = 0 WHERE rec_have <= 20");
                try {
                    ps.setInt(1, 0);
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = GREATEST(rec_have - 20,0) WHERE rec_have > 20");
                try {
                    ps.setInt(1, 0);
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            DailyTaskManager.LOGGER.error("Could not reset Recommendations System: ", (Throwable)e);
        }
        World.getInstance().getPlayers().forEach(player -> {
            player.setRecomLeft(0);
            player.setRecomHave(player.getRecomHave() - 20);
            player.sendPacket(new ExVoteSystemInfo(player));
            player.broadcastUserInfo();
        });
    }
    
    private void resetTrainingCamp() {
        if (Config.TRAINING_CAMP_ENABLE) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ?");
                    try {
                        ps.setString(1, "TRAINING_CAMP_DURATION");
                        ps.executeUpdate();
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (Throwable t) {
                        if (ps != null) {
                            try {
                                ps.close();
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
                DailyTaskManager.LOGGER.error("Could not reset Training Camp: ", (Throwable)e);
            }
            World.getInstance().getPlayers().forEach(player -> {
                player.resetTraingCampDuration();
                player.getAccountVariables().storeMe();
                return;
            });
            DailyTaskManager.LOGGER.info("Training Camp daily time has been resetted.");
        }
    }
    
    private void resetVipTierExpired() {
        World.getInstance().getPlayers().forEach(player -> {
            if (player.getVipTier() < 1) {
                return;
            }
            else {
                VipEngine.getInstance().checkVipTierExpiration(player);
                return;
            }
        });
        DailyTaskManager.LOGGER.info("VIP expiration time has been checked.");
    }
    
    private void resetRankers() {
        RankEngine.getInstance().updateRankers();
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
