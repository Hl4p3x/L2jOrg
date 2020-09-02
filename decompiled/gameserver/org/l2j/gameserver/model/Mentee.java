// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.DatabaseFactory;
import org.slf4j.Logger;

public class Mentee
{
    private static final Logger LOGGER;
    private final int _objectId;
    private String _name;
    private int _classId;
    private int _currentLevel;
    
    public Mentee(final int objectId) {
        this._objectId = objectId;
        this.load();
    }
    
    public void load() {
        final Player player = this.getPlayerInstance();
        if (player == null) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("SELECT char_name, level, base_class FROM characters WHERE charId = ?");
                    try {
                        statement.setInt(1, this._objectId);
                        final ResultSet rset = statement.executeQuery();
                        try {
                            if (rset.next()) {
                                this._name = rset.getString("char_name");
                                this._classId = rset.getInt("base_class");
                                this._currentLevel = rset.getInt("level");
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
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                Mentee.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
        }
        else {
            this._name = player.getName();
            this._classId = player.getBaseClass();
            this._currentLevel = player.getLevel();
        }
    }
    
    public int getObjectId() {
        return this._objectId;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getClassId() {
        if (this.isOnline() && this.getPlayerInstance().getClassId().getId() != this._classId) {
            this._classId = this.getPlayerInstance().getClassId().getId();
        }
        return this._classId;
    }
    
    public int getLevel() {
        if (this.isOnline() && this.getPlayerInstance().getLevel() != this._currentLevel) {
            this._currentLevel = this.getPlayerInstance().getLevel();
        }
        return this._currentLevel;
    }
    
    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(this._objectId);
    }
    
    public boolean isOnline() {
        return this.getPlayerInstance() != null && this.getPlayerInstance().isOnlineInt() > 0;
    }
    
    public int isOnlineInt() {
        return this.isOnline() ? this.getPlayerInstance().isOnlineInt() : 0;
    }
    
    public void sendPacket(final ServerPacket packet) {
        if (this.isOnline()) {
            this.getPlayerInstance().sendPacket(packet);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Mentee.class);
    }
}
