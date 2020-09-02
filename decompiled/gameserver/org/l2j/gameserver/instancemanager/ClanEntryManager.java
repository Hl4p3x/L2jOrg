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
import java.util.OptionalInt;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.clan.entry.PledgeWaitingInfo;
import java.util.Map;
import org.slf4j.Logger;

public class ClanEntryManager
{
    protected static final Logger LOGGER;
    private static final Map<Integer, PledgeWaitingInfo> _waitingList;
    private static final Map<Integer, PledgeRecruitInfo> _clanList;
    private static final Map<Integer, Map<Integer, PledgeApplicantInfo>> _applicantList;
    private static final Map<Integer, ScheduledFuture<?>> _clanLocked;
    private static final Map<Integer, ScheduledFuture<?>> _playerLocked;
    private static final String INSERT_APPLICANT = "INSERT INTO pledge_applicant VALUES (?, ?, ?, ?)";
    private static final String DELETE_APPLICANT = "DELETE FROM pledge_applicant WHERE charId = ? AND clanId = ?";
    private static final String INSERT_WAITING_LIST = "INSERT INTO pledge_waiting_list VALUES (?, ?)";
    private static final String DELETE_WAITING_LIST = "DELETE FROM pledge_waiting_list WHERE char_id = ?";
    private static final String INSERT_CLAN_RECRUIT = "INSERT INTO pledge_recruit VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_CLAN_RECRUIT = "UPDATE pledge_recruit SET karma = ?, information = ?, detailed_information = ?, application_type = ?, recruit_type = ? WHERE clan_id = ?";
    private static final String DELETE_CLAN_RECRUIT = "DELETE FROM pledge_recruit WHERE clan_id = ?";
    private static final List<Comparator<PledgeWaitingInfo>> PLAYER_COMPARATOR;
    private static final List<Comparator<PledgeRecruitInfo>> CLAN_COMPARATOR;
    private static final long LOCK_TIME;
    
    private ClanEntryManager() {
        this.load();
    }
    
    private static void lockPlayer(final int playerId) {
        ClanEntryManager._playerLocked.put(playerId, ThreadPool.schedule(() -> ClanEntryManager._playerLocked.remove(playerId), ClanEntryManager.LOCK_TIME));
    }
    
    private static void lockClan(final int clanId) {
        ClanEntryManager._clanLocked.put(clanId, ThreadPool.schedule(() -> ClanEntryManager._clanLocked.remove(clanId), ClanEntryManager.LOCK_TIME));
    }
    
    private void load() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s = con.createStatement();
                try {
                    final ResultSet rs = s.executeQuery("SELECT * FROM pledge_recruit");
                    try {
                        while (rs.next()) {
                            final int clanId = rs.getInt("clan_id");
                            ClanEntryManager._clanList.put(clanId, new PledgeRecruitInfo(clanId, rs.getInt("karma"), rs.getString("information"), rs.getString("detailed_information"), rs.getInt("application_type"), rs.getInt("recruit_type")));
                            if (ClanTable.getInstance().getClan(clanId) == null) {
                                this.removeFromClanList(clanId);
                            }
                        }
                        ClanEntryManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), ClanEntryManager._clanList.size()));
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (s != null) {
                        s.close();
                    }
                }
                catch (Throwable t2) {
                    if (s != null) {
                        try {
                            s.close();
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
            ClanEntryManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s = con.createStatement();
                try {
                    final ResultSet rs = s.executeQuery("SELECT a.char_id, a.karma, b.base_class, b.level, b.char_name FROM pledge_waiting_list as a LEFT JOIN characters as b ON a.char_id = b.charId");
                    try {
                        while (rs.next()) {
                            ClanEntryManager._waitingList.put(rs.getInt("char_id"), new PledgeWaitingInfo(rs.getInt("char_id"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("base_class"), rs.getString("char_name")));
                        }
                        ClanEntryManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), ClanEntryManager._waitingList.size()));
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t4) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception4) {
                                t4.addSuppressed(exception4);
                            }
                        }
                        throw t4;
                    }
                    if (s != null) {
                        s.close();
                    }
                }
                catch (Throwable t5) {
                    if (s != null) {
                        try {
                            s.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                    }
                    throw t5;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t6) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception6) {
                        t6.addSuppressed(exception6);
                    }
                }
                throw t6;
            }
        }
        catch (Exception e) {
            ClanEntryManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s = con.createStatement();
                try {
                    final ResultSet rs = s.executeQuery("SELECT a.charId, a.clanId, a.karma, a.message, b.base_class, b.level, b.char_name FROM pledge_applicant as a LEFT JOIN characters as b ON a.charId = b.charId");
                    try {
                        while (rs.next()) {
                            ClanEntryManager._applicantList.computeIfAbsent(Integer.valueOf(rs.getInt("clanId")), k -> new ConcurrentHashMap()).put(rs.getInt("charId"), new PledgeApplicantInfo(rs.getInt("charId"), rs.getString("char_name"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("clanId"), rs.getString("message")));
                        }
                        ClanEntryManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), ClanEntryManager._applicantList.size()));
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t7) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception7) {
                                t7.addSuppressed(exception7);
                            }
                        }
                        throw t7;
                    }
                    if (s != null) {
                        s.close();
                    }
                }
                catch (Throwable t8) {
                    if (s != null) {
                        try {
                            s.close();
                        }
                        catch (Throwable exception8) {
                            t8.addSuppressed(exception8);
                        }
                    }
                    throw t8;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t9) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception9) {
                        t9.addSuppressed(exception9);
                    }
                }
                throw t9;
            }
        }
        catch (Exception e) {
            ClanEntryManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
    }
    
    public Map<Integer, PledgeWaitingInfo> getWaitingList() {
        return ClanEntryManager._waitingList;
    }
    
    public Map<Integer, PledgeRecruitInfo> getClanList() {
        return ClanEntryManager._clanList;
    }
    
    public Map<Integer, Map<Integer, PledgeApplicantInfo>> getApplicantList() {
        return ClanEntryManager._applicantList;
    }
    
    public Map<Integer, PledgeApplicantInfo> getApplicantListForClan(final int clanId) {
        return ClanEntryManager._applicantList.getOrDefault(clanId, Collections.emptyMap());
    }
    
    public PledgeApplicantInfo getPlayerApplication(final int clanId, final int playerId) {
        return ClanEntryManager._applicantList.getOrDefault(clanId, Collections.emptyMap()).get(playerId);
    }
    
    public boolean removePlayerApplication(final int clanId, final int playerId) {
        final Map<Integer, PledgeApplicantInfo> clanApplicantList = ClanEntryManager._applicantList.get(clanId);
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM pledge_applicant WHERE charId = ? AND clanId = ?");
                try {
                    statement.setInt(1, playerId);
                    statement.setInt(2, clanId);
                    statement.executeUpdate();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
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
            ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        return clanApplicantList != null && clanApplicantList.remove(playerId) != null;
    }
    
    public boolean addPlayerApplicationToClan(final int clanId, final PledgeApplicantInfo info) {
        if (!ClanEntryManager._playerLocked.containsKey(info.getPlayerId())) {
            ClanEntryManager._applicantList.computeIfAbsent(Integer.valueOf(clanId), k -> new ConcurrentHashMap()).put(info.getPlayerId(), info);
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO pledge_applicant VALUES (?, ?, ?, ?)");
                    try {
                        statement.setInt(1, info.getPlayerId());
                        statement.setInt(2, info.getRequestClanId());
                        statement.setInt(3, info.getKarma());
                        statement.setString(4, info.getMessage());
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            return true;
        }
        return false;
    }
    
    public OptionalInt getClanIdForPlayerApplication(final int playerId) {
        return ClanEntryManager._applicantList.entrySet().stream().filter(e -> e.getValue().containsKey(playerId)).mapToInt(e -> e.getKey()).findFirst();
    }
    
    public boolean addToWaitingList(final int playerId, final PledgeWaitingInfo info) {
        if (!ClanEntryManager._playerLocked.containsKey(playerId)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO pledge_waiting_list VALUES (?, ?)");
                    try {
                        statement.setInt(1, info.getPlayerId());
                        statement.setInt(2, info.getKarma());
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            ClanEntryManager._waitingList.put(playerId, info);
            return true;
        }
        return false;
    }
    
    public boolean removeFromWaitingList(final int playerId) {
        if (ClanEntryManager._waitingList.containsKey(playerId)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("DELETE FROM pledge_waiting_list WHERE char_id = ?");
                    try {
                        statement.setInt(1, playerId);
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            ClanEntryManager._waitingList.remove(playerId);
            lockPlayer(playerId);
            return true;
        }
        return false;
    }
    
    public boolean addToClanList(final int clanId, final PledgeRecruitInfo info) {
        if (!ClanEntryManager._clanList.containsKey(clanId) && !ClanEntryManager._clanLocked.containsKey(clanId)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO pledge_recruit VALUES (?, ?, ?, ?, ?, ?)");
                    try {
                        statement.setInt(1, info.getClanId());
                        statement.setInt(2, info.getKarma());
                        statement.setString(3, info.getInformation());
                        statement.setString(4, info.getDetailedInformation());
                        statement.setInt(5, info.getApplicationType());
                        statement.setInt(6, info.getRecruitType());
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            ClanEntryManager._clanList.put(clanId, info);
            return true;
        }
        return false;
    }
    
    public boolean updateClanList(final int clanId, final PledgeRecruitInfo info) {
        if (ClanEntryManager._clanList.containsKey(clanId) && !ClanEntryManager._clanLocked.containsKey(clanId)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("UPDATE pledge_recruit SET karma = ?, information = ?, detailed_information = ?, application_type = ?, recruit_type = ? WHERE clan_id = ?");
                    try {
                        statement.setInt(1, info.getKarma());
                        statement.setString(2, info.getInformation());
                        statement.setString(3, info.getDetailedInformation());
                        statement.setInt(4, info.getApplicationType());
                        statement.setInt(5, info.getRecruitType());
                        statement.setInt(6, info.getClanId());
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            return ClanEntryManager._clanList.replace(clanId, info) != null;
        }
        return false;
    }
    
    public boolean removeFromClanList(final int clanId) {
        if (ClanEntryManager._clanList.containsKey(clanId)) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("DELETE FROM pledge_recruit WHERE clan_id = ?");
                    try {
                        statement.setInt(1, clanId);
                        statement.executeUpdate();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                ClanEntryManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            ClanEntryManager._clanList.remove(clanId);
            lockClan(clanId);
            return true;
        }
        return false;
    }
    
    public List<PledgeWaitingInfo> getSortedWaitingList(final int levelMin, final int levelMax, final int role, int sortBy, final boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, ClanEntryManager.PLAYER_COMPARATOR.size() - 1);
        return ClanEntryManager._waitingList.values().stream().filter(p -> p.getPlayerLvl() >= levelMin && p.getPlayerLvl() <= levelMax).sorted(descending ? ClanEntryManager.PLAYER_COMPARATOR.get(sortBy).reversed() : ClanEntryManager.PLAYER_COMPARATOR.get(sortBy)).collect((Collector<? super PledgeWaitingInfo, ?, List<PledgeWaitingInfo>>)Collectors.toList());
    }
    
    public List<PledgeWaitingInfo> queryWaitingListByName(final String name) {
        return ClanEntryManager._waitingList.values().stream().filter(p -> p.getPlayerName().toLowerCase().contains(name)).collect((Collector<? super PledgeWaitingInfo, ?, List<PledgeWaitingInfo>>)Collectors.toList());
    }
    
    public List<PledgeRecruitInfo> getSortedClanListByName(final String query, final int type) {
        return (List<PledgeRecruitInfo>)((type == 1) ? ClanEntryManager._clanList.values().stream().filter(p -> p.getClanName().toLowerCase().contains(query)).collect((Collector<? super PledgeRecruitInfo, ?, List<? super PledgeRecruitInfo>>)Collectors.toList()) : ClanEntryManager._clanList.values().stream().filter(p -> p.getClanLeaderName().toLowerCase().contains(query)).collect((Collector<? super PledgeRecruitInfo, ?, List<? super PledgeRecruitInfo>>)Collectors.toList()));
    }
    
    public PledgeRecruitInfo getClanById(final int clanId) {
        return ClanEntryManager._clanList.get(clanId);
    }
    
    public boolean isClanRegistred(final int clanId) {
        return ClanEntryManager._clanList.get(clanId) != null;
    }
    
    public boolean isPlayerRegistred(final int playerId) {
        return ClanEntryManager._waitingList.get(playerId) != null;
    }
    
    public List<PledgeRecruitInfo> getUnSortedClanList() {
        return new ArrayList<PledgeRecruitInfo>(ClanEntryManager._clanList.values());
    }
    
    public List<PledgeRecruitInfo> getSortedClanList(final int clanLevel, final int karma, int sortBy, final boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, ClanEntryManager.CLAN_COMPARATOR.size() - 1);
        final boolean b;
        return ClanEntryManager._clanList.values().stream().filter(p -> {
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
        }).sorted(descending ? ClanEntryManager.CLAN_COMPARATOR.get(sortBy).reversed() : ClanEntryManager.CLAN_COMPARATOR.get(sortBy)).collect((Collector<? super PledgeRecruitInfo, ?, List<PledgeRecruitInfo>>)Collectors.toList());
    }
    
    public long getPlayerLockTime(final int playerId) {
        return (ClanEntryManager._playerLocked.get(playerId) == null) ? 0L : ClanEntryManager._playerLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }
    
    public long getClanLockTime(final int playerId) {
        return (ClanEntryManager._clanLocked.get(playerId) == null) ? 0L : ClanEntryManager._clanLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }
    
    public static ClanEntryManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanEntryManager.class);
        _waitingList = new ConcurrentHashMap<Integer, PledgeWaitingInfo>();
        _clanList = new ConcurrentHashMap<Integer, PledgeRecruitInfo>();
        _applicantList = new ConcurrentHashMap<Integer, Map<Integer, PledgeApplicantInfo>>();
        _clanLocked = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
        _playerLocked = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
        PLAYER_COMPARATOR = Arrays.asList(null, Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeWaitingInfo::getPlayerName), Comparator.comparingInt(PledgeWaitingInfo::getKarma), Comparator.comparingInt(PledgeWaitingInfo::getPlayerLvl), Comparator.comparingInt(PledgeWaitingInfo::getPlayerClassId));
        CLAN_COMPARATOR = Arrays.asList(null, Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeRecruitInfo::getClanName), Comparator.comparing((Function<? super Object, ? extends Comparable>)PledgeRecruitInfo::getClanLeaderName), Comparator.comparingInt(PledgeRecruitInfo::getClanLevel), Comparator.comparingInt(PledgeRecruitInfo::getKarma));
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
