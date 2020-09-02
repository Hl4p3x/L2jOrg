// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.BossDAO;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.tasks.GrandBossManagerStoreTask;
import java.sql.SQLException;
import java.util.Date;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.commons.database.DatabaseFactory;
import java.util.HashMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IStorable;

public final class GrandBossManager implements IStorable
{
    private static final String UPDATE_GRAND_BOSS_DATA = "UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?";
    private static final String UPDATE_GRAND_BOSS_DATA2 = "UPDATE grandboss_data set status = ? where boss_id = ?";
    protected static Logger LOGGER;
    protected static Map<Integer, GrandBoss> _bosses;
    protected static Map<Integer, StatsSet> _storedInfo;
    private final Map<Integer, Integer> _bossStatus;
    
    private GrandBossManager() {
        this._bossStatus = new HashMap<Integer, Integer>();
        this.init();
    }
    
    private void init() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s = con.createStatement();
                try {
                    final ResultSet rs = s.executeQuery("SELECT * from grandboss_data ORDER BY boss_id");
                    try {
                        while (rs.next()) {
                            final int bossId = rs.getInt("boss_id");
                            if (NpcData.getInstance().getTemplate(bossId) != null) {
                                final StatsSet info = new StatsSet();
                                info.set("loc_x", rs.getInt("loc_x"));
                                info.set("loc_y", rs.getInt("loc_y"));
                                info.set("loc_z", rs.getInt("loc_z"));
                                info.set("heading", rs.getInt("heading"));
                                info.set("respawn_time", rs.getLong("respawn_time"));
                                info.set("currentHP", rs.getDouble("currentHP"));
                                info.set("currentMP", rs.getDouble("currentMP"));
                                final int status = rs.getInt("status");
                                this._bossStatus.put(bossId, status);
                                GrandBossManager._storedInfo.put(bossId, info);
                                GrandBossManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), NpcData.getInstance().getTemplate(bossId).getName(), bossId, (status == 0) ? "Alive" : "Dead"));
                                if (status <= 0) {
                                    continue;
                                }
                                GrandBossManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;)Ljava/lang/String;, this.getClass().getSimpleName(), NpcData.getInstance().getTemplate(bossId).getName(), new Date(info.getLong("respawn_time"))));
                            }
                            else {
                                GrandBossManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, bossId));
                            }
                        }
                        GrandBossManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), GrandBossManager._storedInfo.size()));
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
        catch (SQLException e) {
            GrandBossManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), e.getMessage()), (Throwable)e);
        }
        catch (Exception e2) {
            GrandBossManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), e2.getMessage()), (Throwable)e2);
        }
        ThreadPool.scheduleAtFixedRate((Runnable)new GrandBossManagerStoreTask(), 300000L, 300000L);
    }
    
    public int getBossStatus(final int bossId) {
        if (!this._bossStatus.containsKey(bossId)) {
            return -1;
        }
        return this._bossStatus.get(bossId);
    }
    
    public void setBossStatus(final int bossId, final int status) {
        this._bossStatus.put(bossId, status);
        GrandBossManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), NpcData.getInstance().getTemplate(bossId).getName(), bossId, status));
        this.updateDb(bossId, true);
    }
    
    public void addBoss(final GrandBoss boss) {
        if (boss != null) {
            GrandBossManager._bosses.put(boss.getId(), boss);
        }
    }
    
    public GrandBoss getBoss(final int bossId) {
        return GrandBossManager._bosses.get(bossId);
    }
    
    public StatsSet getStatsSet(final int bossId) {
        return GrandBossManager._storedInfo.get(bossId);
    }
    
    public void setStatsSet(final int bossId, final StatsSet info) {
        GrandBossManager._storedInfo.put(bossId, info);
        this.updateDb(bossId, false);
    }
    
    @Override
    public boolean storeMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                for (final Map.Entry<Integer, StatsSet> e : GrandBossManager._storedInfo.entrySet()) {
                    final GrandBoss boss = GrandBossManager._bosses.get(e.getKey());
                    final StatsSet info = e.getValue();
                    if (boss == null || info == null) {
                        final PreparedStatement update = con.prepareStatement("UPDATE grandboss_data set status = ? where boss_id = ?");
                        try {
                            update.setInt(1, this._bossStatus.get(e.getKey()));
                            update.setInt(2, e.getKey());
                            update.executeUpdate();
                            update.clearParameters();
                            if (update == null) {
                                continue;
                            }
                            update.close();
                        }
                        catch (Throwable t) {
                            if (update != null) {
                                try {
                                    update.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                    }
                    else {
                        final PreparedStatement update = con.prepareStatement("UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?");
                        try {
                            update.setInt(1, boss.getX());
                            update.setInt(2, boss.getY());
                            update.setInt(3, boss.getZ());
                            update.setInt(4, boss.getHeading());
                            update.setLong(5, info.getLong("respawn_time"));
                            double hp = boss.getCurrentHp();
                            double mp = boss.getCurrentMp();
                            if (boss.isDead()) {
                                hp = boss.getMaxHp();
                                mp = boss.getMaxMp();
                            }
                            update.setDouble(6, hp);
                            update.setDouble(7, mp);
                            update.setInt(8, this._bossStatus.get(e.getKey()));
                            update.setInt(9, e.getKey());
                            update.executeUpdate();
                            update.clearParameters();
                            if (update == null) {
                                continue;
                            }
                            update.close();
                        }
                        catch (Throwable t2) {
                            if (update != null) {
                                try {
                                    update.close();
                                }
                                catch (Throwable exception2) {
                                    t2.addSuppressed(exception2);
                                }
                            }
                            throw t2;
                        }
                    }
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
        catch (SQLException e2) {
            GrandBossManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()), (Throwable)e2);
            return false;
        }
        return true;
    }
    
    private void updateDb(final int bossId, final boolean statusOnly) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final GrandBoss boss = GrandBossManager._bosses.get(bossId);
                final StatsSet info = GrandBossManager._storedInfo.get(bossId);
                if (statusOnly || boss == null || info == null) {
                    ((BossDAO)DatabaseAccess.getDAO((Class)BossDAO.class)).updateStatus(bossId, this._bossStatus.get(bossId));
                }
                else {
                    final PreparedStatement ps = con.prepareStatement("UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?");
                    try {
                        ps.setInt(1, boss.getX());
                        ps.setInt(2, boss.getY());
                        ps.setInt(3, boss.getZ());
                        ps.setInt(4, boss.getHeading());
                        ps.setLong(5, info.getLong("respawn_time"));
                        double hp = boss.getCurrentHp();
                        double mp = boss.getCurrentMp();
                        if (boss.isDead()) {
                            hp = boss.getMaxHp();
                            mp = boss.getMaxMp();
                        }
                        ps.setDouble(6, hp);
                        ps.setDouble(7, mp);
                        ps.setInt(8, this._bossStatus.get(bossId));
                        ps.setInt(9, bossId);
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
        catch (SQLException e) {
            GrandBossManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void cleanUp() {
        this.storeMe();
        GrandBossManager._bosses.clear();
        GrandBossManager._storedInfo.clear();
        this._bossStatus.clear();
    }
    
    public static GrandBossManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        GrandBossManager.LOGGER = LoggerFactory.getLogger(GrandBossManager.class.getName());
        GrandBossManager._bosses = new ConcurrentHashMap<Integer, GrandBoss>();
        GrandBossManager._storedInfo = new HashMap<Integer, StatsSet>();
    }
    
    private static class Singleton
    {
        private static final GrandBossManager INSTANCE;
        
        static {
            INSTANCE = new GrandBossManager();
        }
    }
}
