// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.punishment;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.handler.IPunishmentHandler;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.handler.PunishmentHandler;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;

public class PunishmentTask implements Runnable
{
    protected static final Logger LOGGER;
    private static final String INSERT_QUERY = "INSERT INTO punishments (`key`, `affect`, `type`, `expiration`, `reason`, `punishedBy`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE punishments SET expiration = ? WHERE id = ?";
    private final String _key;
    private final PunishmentAffect _affect;
    private final PunishmentType _type;
    private final long _expirationTime;
    private final String _reason;
    private final String _punishedBy;
    private int _id;
    private boolean _isStored;
    private ScheduledFuture<?> _task;
    
    public PunishmentTask(final Object key, final PunishmentAffect affect, final PunishmentType type, final long expirationTime, final String reason, final String punishedBy) {
        this(0, key, affect, type, expirationTime, reason, punishedBy, false);
    }
    
    public PunishmentTask(final int id, final Object key, final PunishmentAffect affect, final PunishmentType type, final long expirationTime, final String reason, final String punishedBy, final boolean isStored) {
        this._task = null;
        this._id = id;
        this._key = String.valueOf(key);
        this._affect = affect;
        this._type = type;
        this._expirationTime = expirationTime;
        this._reason = reason;
        this._punishedBy = punishedBy;
        this._isStored = isStored;
        this.startPunishment();
    }
    
    public Object getKey() {
        return this._key;
    }
    
    public PunishmentAffect getAffect() {
        return this._affect;
    }
    
    public PunishmentType getType() {
        return this._type;
    }
    
    public final long getExpirationTime() {
        return this._expirationTime;
    }
    
    public String getReason() {
        return this._reason;
    }
    
    public String getPunishedBy() {
        return this._punishedBy;
    }
    
    public boolean isStored() {
        return this._isStored;
    }
    
    public final boolean isExpired() {
        return this._expirationTime > 0L && System.currentTimeMillis() > this._expirationTime;
    }
    
    private void startPunishment() {
        if (this.isExpired()) {
            return;
        }
        this.onStart();
        if (this._expirationTime > 0L) {
            this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)this, this._expirationTime - System.currentTimeMillis());
        }
    }
    
    public final void stopPunishment() {
        this.abortTask();
        this.onEnd();
    }
    
    private void abortTask() {
        if (this._task != null) {
            if (!this._task.isCancelled() && !this._task.isDone()) {
                this._task.cancel(false);
            }
            this._task = null;
        }
    }
    
    private void onStart() {
        if (!this._isStored) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement st = con.prepareStatement("INSERT INTO punishments (`key`, `affect`, `type`, `expiration`, `reason`, `punishedBy`) VALUES (?, ?, ?, ?, ?, ?)", 1);
                    try {
                        st.setString(1, this._key);
                        st.setString(2, this._affect.name());
                        st.setString(3, this._type.name());
                        st.setLong(4, this._expirationTime);
                        st.setString(5, this._reason);
                        st.setString(6, this._punishedBy);
                        st.execute();
                        final ResultSet rset = st.getGeneratedKeys();
                        try {
                            if (rset.next()) {
                                this._id = rset.getInt(1);
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
                        this._isStored = true;
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
            catch (SQLException e) {
                PunishmentTask.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/punishment/PunishmentAffect;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this._affect, this._key), (Throwable)e);
            }
        }
        final IPunishmentHandler handler = PunishmentHandler.getInstance().getHandler(this._type);
        if (handler != null) {
            handler.onStart(this);
        }
    }
    
    private void onEnd() {
        if (this._isStored) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement st = con.prepareStatement("UPDATE punishments SET expiration = ? WHERE id = ?");
                    try {
                        st.setLong(1, System.currentTimeMillis());
                        st.setLong(2, this._id);
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
                PunishmentTask.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/punishment/PunishmentAffect;Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._affect, this._key, this._id), (Throwable)e);
            }
        }
        if (this._type == PunishmentType.CHAT_BAN && this._affect == PunishmentAffect.CHARACTER) {
            final Player player = World.getInstance().findPlayer(Integer.valueOf(this._key));
            if (player != null) {
                player.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.NO_CHAT);
            }
        }
        final IPunishmentHandler handler = PunishmentHandler.getInstance().getHandler(this._type);
        if (handler != null) {
            handler.onEnd(this);
        }
    }
    
    @Override
    public final void run() {
        PunishmentManager.getInstance().stopPunishment(this._key, this._affect, this._type);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PunishmentTask.class);
    }
}
