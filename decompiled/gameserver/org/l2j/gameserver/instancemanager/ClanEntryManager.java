// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.Arrays;
import java.util.function.Function;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.commons.util.CommonUtil;
import java.util.function.ToIntFunction;
import java.util.OptionalInt;
import java.util.Objects;
import io.github.joealisson.primitive.Containers;
import org.l2j.commons.threading.ThreadPool;
import java.util.Iterator;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PledgeRecruitDAO;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.data.database.data.PledgeWaitingData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class ClanEntryManager
{
    private static final Logger LOGGER;
    private IntMap<PledgeWaitingData> waitings;
    private IntMap<PledgeRecruitData> clans;
    private final IntMap<IntMap<PledgeApplicantData>> applicants;
    private final IntMap<ScheduledFuture<?>> clanLocked;
    private final IntMap<ScheduledFuture<?>> playerLocked;
    private static final List<Comparator<PledgeWaitingData>> PLAYER_COMPARATOR;
    private static final List<Comparator<PledgeRecruitData>> CLAN_COMPARATOR;
    private static final long LOCK_TIME;
    
    private ClanEntryManager() {
        this.waitings = (IntMap<PledgeWaitingData>)new CHashIntMap();
        this.clans = (IntMap<PledgeRecruitData>)new CHashIntMap();
        this.applicants = (IntMap<IntMap<PledgeApplicantData>>)new CHashIntMap();
        this.clanLocked = (IntMap<ScheduledFuture<?>>)new CHashIntMap();
        this.playerLocked = (IntMap<ScheduledFuture<?>>)new CHashIntMap();
    }
    
    private void load() {
        final PledgeRecruitDAO pledgeRecruitDAO = (PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class);
        this.clans = (IntMap<PledgeRecruitData>)pledgeRecruitDAO.findAll(pledgeRecruit -> pledgeRecruit.setClan(ClanTable.getInstance().getClan(pledgeRecruit.getClanId())));
        ClanEntryManager.LOGGER.info("Loaded {} clan entry", (Object)this.clans.size());
        this.waitings = (IntMap<PledgeWaitingData>)pledgeRecruitDAO.findAllWaiting();
        ClanEntryManager.LOGGER.info("Loaded {} player in waiting list", (Object)this.waitings.size());
        final List<PledgeApplicantData> applicantList = pledgeRecruitDAO.findAllApplicant();
        for (final PledgeApplicantData applicant : applicantList) {
            ((IntMap)this.applicants.computeIfAbsent(applicant.getRequestClanId(), k -> new CHashIntMap())).put(applicant.getPlayerId(), (Object)applicant);
        }
        ClanEntryManager.LOGGER.info("Loaded {} player applications", (Object)applicantList.size());
    }
    
    private void lockPlayer(final int playerId) {
        this.playerLocked.put(playerId, (Object)ThreadPool.schedule(() -> this.playerLocked.remove(playerId), ClanEntryManager.LOCK_TIME));
    }
    
    private void lockClan(final int clanId) {
        this.clanLocked.put(clanId, (Object)ThreadPool.schedule(() -> this.clanLocked.remove(clanId), ClanEntryManager.LOCK_TIME));
    }
    
    public IntMap<PledgeApplicantData> getApplicantListForClan(final int clanId) {
        return (IntMap<PledgeApplicantData>)this.applicants.getOrDefault(clanId, (Object)Containers.emptyIntMap());
    }
    
    public PledgeApplicantData getPlayerApplication(final int clanId, final int playerId) {
        return (PledgeApplicantData)((IntMap)this.applicants.getOrDefault(clanId, (Object)Containers.emptyIntMap())).get(playerId);
    }
    
    public void removePlayerApplication(final int clanId, final int playerId) {
        final IntMap<PledgeApplicantData> clanApplicantList = (IntMap<PledgeApplicantData>)this.applicants.get(clanId);
        if (Objects.nonNull(clanApplicantList) && Objects.nonNull(clanApplicantList.remove(playerId))) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).deleteApplicant(playerId, clanId);
        }
    }
    
    public boolean addPlayerApplicationToClan(final int clanId, final PledgeApplicantData info) {
        if (!this.playerLocked.containsKey(info.getPlayerId())) {
            ((IntMap)this.applicants.computeIfAbsent(clanId, k -> new CHashIntMap())).put(info.getPlayerId(), (Object)info);
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).save(info);
            return true;
        }
        return false;
    }
    
    public OptionalInt getClanIdForPlayerApplication(final int playerId) {
        return this.applicants.entrySet().stream().filter(e -> ((IntMap)e.getValue()).containsKey(playerId)).mapToInt(IntMap.Entry::getKey).findFirst();
    }
    
    public boolean addToWaitingList(final int playerId, final PledgeWaitingData info) {
        if (!this.playerLocked.containsKey(playerId)) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).save(info);
            this.waitings.put(playerId, (Object)info);
            return true;
        }
        return false;
    }
    
    public boolean removeFromWaitingList(final int playerId) {
        if (this.waitings.containsKey(playerId)) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).deleteWaiting(playerId);
            this.waitings.remove(playerId);
            this.lockPlayer(playerId);
            return true;
        }
        return false;
    }
    
    public boolean addToClanList(final int clanId, final PledgeRecruitData info) {
        if (!this.clans.containsKey(clanId) && !this.clanLocked.containsKey(clanId)) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).save((Object)info);
            this.clans.put(clanId, (Object)info);
            return true;
        }
        return false;
    }
    
    public boolean updateClanList(final int clanId, final PledgeRecruitData info) {
        if (this.clans.containsKey(clanId) && !this.clanLocked.containsKey(clanId)) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).save((Object)info);
            return this.clans.replace(clanId, (Object)info) != null;
        }
        return false;
    }
    
    public boolean removeFromClanList(final int clanId) {
        if (this.clans.containsKey(clanId)) {
            ((PledgeRecruitDAO)DatabaseAccess.getDAO((Class)PledgeRecruitDAO.class)).deleteRecruit(clanId);
            this.clans.remove(clanId);
            this.lockClan(clanId);
            return true;
        }
        return false;
    }
    
    public List<PledgeWaitingData> getSortedWaitingList(final int levelMin, final int levelMax, final int role, int sortBy, final boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, ClanEntryManager.PLAYER_COMPARATOR.size() - 1);
        return this.waitings.values().stream().filter(p -> p.getPlayerLvl() >= levelMin && p.getPlayerLvl() <= levelMax).sorted((Comparator<? super Object>)(descending ? ClanEntryManager.PLAYER_COMPARATOR.get(sortBy).reversed() : ClanEntryManager.PLAYER_COMPARATOR.get(sortBy))).collect((Collector<? super Object, ?, List<PledgeWaitingData>>)Collectors.toList());
    }
    
    public List<PledgeWaitingData> queryWaitingListByName(final String name) {
        return this.waitings.values().stream().filter(p -> p.getPlayerName().toLowerCase().contains(name)).collect((Collector<? super Object, ?, List<PledgeWaitingData>>)Collectors.toList());
    }
    
    public List<PledgeRecruitData> getSortedClanListByName(final String query, final int type) {
        return (List<PledgeRecruitData>)((type == 1) ? this.clans.values().stream().filter(p -> p.getClanName().toLowerCase().contains(query)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()) : this.clans.values().stream().filter(p -> p.getClanLeaderName().toLowerCase().contains(query)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
    }
    
    public PledgeRecruitData getClanById(final int clanId) {
        return (PledgeRecruitData)this.clans.get(clanId);
    }
    
    public boolean isClanRegistred(final int clanId) {
        return this.clans.get(clanId) != null;
    }
    
    public boolean isPlayerRegistred(final int playerId) {
        return this.waitings.get(playerId) != null;
    }
    
    public List<PledgeRecruitData> getUnSortedClanList() {
        return new ArrayList<PledgeRecruitData>(this.clans.values());
    }
    
    public List<PledgeRecruitData> getSortedClanList(final int clanLevel, final int karma, int sortBy, final boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, ClanEntryManager.CLAN_COMPARATOR.size() - 1);
        final boolean b;
        return this.clans.values().stream().filter(p -> {
            if (clanLevel >= 0 || karma < 0 || karma == p.getKarma()) {
                if (clanLevel >= 0 && karma < 0) {
                    if (clanLevel != ((p.getClan() != null) ? p.getClanLevel() : 0)) {
                        return 1 != 0;
                    }
                }
                if (clanLevel >= 0 && karma >= 0) {
                    if (clanLevel != ((p.getClan() != null) ? p.getClanLevel() : 0) || karma != p.getKarma()) {
                        return 1 != 0;
                    }
                }
                return b;
            }
            return b;
        }).sorted((Comparator<? super Object>)(descending ? ClanEntryManager.CLAN_COMPARATOR.get(sortBy).reversed() : ClanEntryManager.CLAN_COMPARATOR.get(sortBy))).collect((Collector<? super Object, ?, List<PledgeRecruitData>>)Collectors.toList());
    }
    
    public long getPlayerLockTime(final int playerId) {
        return (this.playerLocked.get(playerId) == null) ? 0L : ((ScheduledFuture)this.playerLocked.get(playerId)).getDelay(TimeUnit.MINUTES);
    }
    
    public long getClanLockTime(final int playerId) {
        return (this.clanLocked.get(playerId) == null) ? 0L : ((ScheduledFuture)this.clanLocked.get(playerId)).getDelay(TimeUnit.MINUTES);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ClanEntryManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanEntryManager.class);
        PLAYER_COMPARATOR = Arrays.asList(null, Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeWaitingData::getPlayerName), Comparator.comparingInt(PledgeWaitingData::getKarma), Comparator.comparingInt(PledgeWaitingData::getPlayerLvl), Comparator.comparingInt(PledgeWaitingData::getPlayerClassId));
        CLAN_COMPARATOR = Arrays.asList(null, Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeRecruitData::getClanName), Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeRecruitData::getClanLeaderName), Comparator.comparingInt(PledgeRecruitData::getClanLevel), Comparator.comparingInt(PledgeRecruitData::getKarma));
        LOCK_TIME = TimeUnit.MINUTES.toMillis(5L);
    }
    
    private static class Singleton
    {
        private static final ClanEntryManager INSTANCE;
        
        static {
            INSTANCE = new ClanEntryManager();
        }
    }
}
