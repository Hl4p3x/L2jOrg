// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Calendar;
import org.slf4j.Logger;

public class Couple
{
    private static final Logger LOGGER;
    private int _Id;
    private int _player1Id;
    private int _player2Id;
    private boolean _maried;
    private Calendar _affiancedDate;
    private Calendar _weddingDate;
    
    public Couple(final int coupleId) {
        this._Id = 0;
        this._player1Id = 0;
        this._player2Id = 0;
        this._maried = false;
        this._Id = coupleId;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM mods_wedding WHERE id = ?");
                try {
                    ps.setInt(1, this._Id);
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            this._player1Id = rs.getInt("player1Id");
                            this._player2Id = rs.getInt("player2Id");
                            this._maried = rs.getBoolean("married");
                            (this._affiancedDate = Calendar.getInstance()).setTimeInMillis(rs.getLong("affianceDate"));
                            (this._weddingDate = Calendar.getInstance()).setTimeInMillis(rs.getLong("weddingDate"));
                        }
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
            Couple.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public Couple(final Player player1, final Player player2) {
        this._Id = 0;
        this._player1Id = 0;
        this._player2Id = 0;
        this._maried = false;
        final int _tempPlayer1Id = player1.getObjectId();
        final int _tempPlayer2Id = player2.getObjectId();
        this._player1Id = _tempPlayer1Id;
        this._player2Id = _tempPlayer2Id;
        (this._affiancedDate = Calendar.getInstance()).setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        (this._weddingDate = Calendar.getInstance()).setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("INSERT INTO mods_wedding (id, player1Id, player2Id, married, affianceDate, weddingDate) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    ps.setInt(1, this._Id = IdFactory.getInstance().getNextId());
                    ps.setInt(2, this._player1Id);
                    ps.setInt(3, this._player2Id);
                    ps.setBoolean(4, false);
                    ps.setLong(5, this._affiancedDate.getTimeInMillis());
                    ps.setLong(6, this._weddingDate.getTimeInMillis());
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
            Couple.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void marry() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE mods_wedding set married = ?, weddingDate = ? where id = ?");
                try {
                    ps.setBoolean(1, true);
                    this._weddingDate = Calendar.getInstance();
                    ps.setLong(2, this._weddingDate.getTimeInMillis());
                    ps.setInt(3, this._Id);
                    ps.execute();
                    this._maried = true;
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
            Couple.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void divorce() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM mods_wedding WHERE id=?");
                try {
                    ps.setInt(1, this._Id);
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
            Couple.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final int getId() {
        return this._Id;
    }
    
    public final int getPlayer1Id() {
        return this._player1Id;
    }
    
    public final int getPlayer2Id() {
        return this._player2Id;
    }
    
    public final boolean getMaried() {
        return this._maried;
    }
    
    public final Calendar getAffiancedDate() {
        return this._affiancedDate;
    }
    
    public final Calendar getWeddingDate() {
        return this._weddingDate;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Couple.class);
    }
}
