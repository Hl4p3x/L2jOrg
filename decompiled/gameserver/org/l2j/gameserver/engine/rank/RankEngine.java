// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.rank;

import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.data.database.data.RankHistoryData;
import java.util.List;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.world.World;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.RankDAO;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.rank.ExBowAction;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.data.database.data.RankData;
import io.github.joealisson.primitive.IntMap;

public class RankEngine
{
    private IntMap<RankData> rankersSnapshot;
    private ScheduledFuture<?> bowTask;
    
    private RankEngine() {
        final ListenersContainer listeners = Listeners.players();
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, e -> this.addRankersSkills(e.getPlayer()), this));
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_PEACE_ZONE_ENTER, this::scheduleBow, this));
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_PEACE_ZONE_EXIT, this::cancelBowTask, this));
    }
    
    private void cancelBowTask(final OnPlayerPeaceZoneExit event) {
        if (event.getPlayer().getRank() == 1 && Objects.nonNull(this.bowTask)) {
            this.bowTask.cancel(false);
            this.bowTask = null;
        }
    }
    
    private void scheduleBow(final OnPlayerPeaceZoneEnter event) {
        final Player player = event.getPlayer();
        if (player.getRank() == 1 && event.getZone().getPlayersInsideCount() > 1L && (Objects.isNull(this.bowTask) || this.bowTask.isCancelled())) {
            final Player player2;
            this.bowTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedDelay(() -> Broadcast.toSelfAndKnownPlayersInRadius(player2, new ExBowAction(player2), 1000, p -> p.isInsideZone(ZoneType.PEACE)), 20L, 180L, TimeUnit.SECONDS);
        }
    }
    
    private void addRankersSkills(final Player player) {
        final RankData rankData = (RankData)this.rankersSnapshot.get(player.getObjectId());
        if (Objects.isNull(rankData)) {
            return;
        }
        player.setRank(rankData.getRank());
        player.setRankRace(rankData.getRankRace());
        final int rank = player.getRank();
        CommonSkill skill = null;
        if (rank <= 100) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_I.getSkill());
            skill = CommonSkill.RANKER_THIRD_CLASS;
        }
        if (rank <= 30) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_II.getSkill());
            skill = CommonSkill.RANKER_SECOND_CLASS;
        }
        if (rank == 1) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_III.getSkill());
            skill = CommonSkill.RANKER_FIRST_CLASS;
        }
        if (Objects.nonNull(skill)) {
            skill.getSkill().applyEffects(player, player);
        }
        if (player.getRankRace() == 1) {
            player.addSkill(CommonSkill.RANKER_RACE_BENEFIT.getSkill());
            Util.doIfNonNull((Object)this.getRaceRankerSkill(player), s -> s.getSkill().applyEffects(player, player));
        }
        if (player.getRank() == 1 || player.getRankRace() == 1) {
            player.sendPacket(new UserInfo(player, new UserInfoType[] { UserInfoType.RANKER }));
        }
    }
    
    private void loadRankers() {
        this.rankersSnapshot = ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findAllSnapshot();
    }
    
    public void updateRankers() {
        this.rankersSnapshot.values().forEach(this::removeRankerSkills);
        this.updateDatabase();
        this.loadRankers();
        this.rankersSnapshot.values().forEach(this::addRankersSkills);
    }
    
    private void addRankersSkills(final RankData rankData) {
        Util.doIfNonNull((Object)World.getInstance().findPlayer(rankData.getPlayerId()), (Consumer)this::addRankersSkills);
    }
    
    private void removeRankerSkills(final RankData rankData) {
        Util.doIfNonNull((Object)World.getInstance().findPlayer(rankData.getPlayerId()), player -> {
            this.removeRankersSkill(player);
            player.setRank(0);
            player.setRankRace(0);
        });
    }
    
    private void removeRankersSkill(final Player player) {
        player.removeSkill(CommonSkill.RANKER_BENEFIT_I.getId(), true);
        player.removeSkill(CommonSkill.RANKER_BENEFIT_II.getId(), true);
        player.removeSkill(CommonSkill.RANKER_BENEFIT_III.getId(), true);
        player.removeSkill(CommonSkill.RANKER_RACE_BENEFIT.getId(), true);
        player.stopSkillEffects(true, CommonSkill.RANKER_FIRST_CLASS.getId());
        player.stopSkillEffects(true, CommonSkill.RANKER_SECOND_CLASS.getId());
        player.stopSkillEffects(true, CommonSkill.RANKER_THIRD_CLASS.getId());
        Util.doIfNonNull((Object)this.getRaceRankerSkill(player), s -> player.stopSkillEffects(true, s.getId()));
    }
    
    private CommonSkill getRaceRankerSkill(final Player player) {
        if (player.getRankRace() == 1) {
            CommonSkill commonSkill = null;
            switch (player.getRace()) {
                case HUMAN: {
                    commonSkill = CommonSkill.RANKER_HUMAN;
                    break;
                }
                case ELF: {
                    commonSkill = CommonSkill.RANKER_ELF;
                    break;
                }
                case DARK_ELF: {
                    commonSkill = CommonSkill.RANKER_DARK_ELF;
                    break;
                }
                case ORC: {
                    commonSkill = CommonSkill.RANKER_ORC;
                    break;
                }
                case DWARF: {
                    commonSkill = CommonSkill.RANKER_DWARF;
                    break;
                }
                case JIN_KAMAEL: {
                    commonSkill = CommonSkill.RANKER_JIN_KAMAEL;
                    break;
                }
                default: {
                    commonSkill = null;
                    break;
                }
            }
            return commonSkill;
        }
        return null;
    }
    
    private void updateDatabase() {
        final RankDAO dao = (RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class);
        dao.clearSnapshot();
        dao.updateSnapshot();
        final Instant now = Instant.now();
        dao.updateRankersHistory(now.getEpochSecond());
        dao.removeOldRankersHistory(now.minus(7L, (TemporalUnit)ChronoUnit.DAYS).getEpochSecond());
    }
    
    public RankData getRank(final Player player) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findPlayerRank(player.getObjectId());
    }
    
    public List<RankData> getRankers() {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findAll();
    }
    
    public List<RankData> getRaceRankers(final int race) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findAllByRace(race);
    }
    
    public List<RankData> getClanRankers(final int clanId) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findByClan(clanId);
    }
    
    public List<RankData> getFriendRankers(final Player player) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findFriendRankers(player.getObjectId());
    }
    
    public List<RankData> getRankersByPlayer(final Player player) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findRankersNextToPlayer(player.getObjectId());
    }
    
    public List<RankData> getRaceRankersByPlayer(final Player player) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findRaceRankersNextToPlayer(player.getObjectId(), player.getRace().ordinal());
    }
    
    public List<RankHistoryData> getPlayerHistory(final Player player) {
        return ((RankDAO)DatabaseAccess.getDAO((Class)RankDAO.class)).findPlayerHistory(player.getObjectId());
    }
    
    public static void init() {
        getInstance().loadRankers();
    }
    
    public static RankEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final RankEngine INSTANCE;
        
        static {
            INSTANCE = new RankEngine();
        }
    }
}
