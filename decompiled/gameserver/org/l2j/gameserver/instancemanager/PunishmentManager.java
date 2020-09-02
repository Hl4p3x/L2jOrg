// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.commons.database.DatabaseFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.holders.PunishmentHolder;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import java.util.Map;
import org.slf4j.Logger;

public final class PunishmentManager
{
    private static final Logger LOGGER;
    private final Map<PunishmentAffect, PunishmentHolder> _tasks;
    
    private PunishmentManager() {
        this._tasks = new ConcurrentHashMap<PunishmentAffect, PunishmentHolder>();
        this.load();
    }
    
    private void load() {
        for (final PunishmentAffect affect : PunishmentAffect.values()) {
            this._tasks.put(affect, new PunishmentHolder());
        }
        int initiated = 0;
        int expired = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement st = con.createStatement();
                try {
                    final ResultSet rset = st.executeQuery("SELECT * FROM punishments");
                    try {
                        while (rset.next()) {
                            final int id = rset.getInt("id");
                            final String key = rset.getString("key");
                            final PunishmentAffect affect2 = PunishmentAffect.getByName(rset.getString("affect"));
                            final PunishmentType type = PunishmentType.getByName(rset.getString("type"));
                            final long expirationTime = rset.getLong("expiration");
                            final String reason = rset.getString("reason");
                            final String punishedBy = rset.getString("punishedBy");
                            if (type != null && affect2 != null) {
                                if (expirationTime > 0L && System.currentTimeMillis() > expirationTime) {
                                    ++expired;
                                }
                                else {
                                    ++initiated;
                                    this._tasks.get(affect2).addPunishment(new PunishmentTask(id, key, affect2, type, expirationTime, reason, punishedBy, true));
                                }
                            }
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
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
            PunishmentManager.LOGGER.warn("Error while loading punishments: ", (Throwable)e);
        }
        PunishmentManager.LOGGER.info("Loaded {} active and {} expired punishments.", (Object)initiated, (Object)expired);
    }
    
    public void startPunishment(final PunishmentTask task) {
        this._tasks.get(task.getAffect()).addPunishment(task);
    }
    
    public void stopPunishment(final PunishmentAffect affect, final PunishmentType type) {
        final PunishmentHolder holder = this._tasks.get(affect);
        if (holder != null) {
            holder.stopPunishment(type);
        }
    }
    
    public void stopPunishment(final Object key, final PunishmentAffect affect, final PunishmentType type) {
        final PunishmentTask task = this.getPunishment(key, affect, type);
        if (task != null) {
            this._tasks.get(affect).stopPunishment(task);
        }
    }
    
    public boolean hasPunishment(final Object key, final PunishmentAffect affect, final PunishmentType type) {
        final PunishmentHolder holder = this._tasks.get(affect);
        return holder.hasPunishment(String.valueOf(key), type);
    }
    
    public long getPunishmentExpiration(final Object key, final PunishmentAffect affect, final PunishmentType type) {
        final PunishmentTask p = this.getPunishment(key, affect, type);
        return (p != null) ? p.getExpirationTime() : 0L;
    }
    
    public PunishmentTask getPunishment(final Object key, final PunishmentAffect affect, final PunishmentType type) {
        return this._tasks.get(affect).getPunishment(String.valueOf(key), type);
    }
    
    public static PunishmentManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PunishmentManager.class);
    }
    
    private static class Singleton
    {
        private static final PunishmentManager INSTANCE;
        
        static {
            INSTANCE = new PunishmentManager();
        }
    }
}
