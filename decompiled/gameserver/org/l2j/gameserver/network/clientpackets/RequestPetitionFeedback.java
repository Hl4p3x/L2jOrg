// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.model.actor.instance.Player;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class RequestPetitionFeedback extends ClientPacket
{
    private static final Logger LOGGER;
    private static final String INSERT_FEEDBACK = "INSERT INTO petition_feedback VALUES (?,?,?,?,?)";
    private int _rate;
    private String _message;
    
    public void readImpl() {
        this.readInt();
        this._rate = this.readInt();
        this._message = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getLastPetitionGmName() == null) {
            return;
        }
        if (this._rate > 4 || this._rate < 0) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO petition_feedback VALUES (?,?,?,?,?)");
                try {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getLastPetitionGmName());
                    statement.setInt(3, this._rate);
                    statement.setString(4, this._message);
                    statement.setLong(5, System.currentTimeMillis());
                    statement.execute();
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
        catch (SQLException e) {
            RequestPetitionFeedback.LOGGER.error("Error while saving petition feedback");
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPetitionFeedback.class);
    }
}
