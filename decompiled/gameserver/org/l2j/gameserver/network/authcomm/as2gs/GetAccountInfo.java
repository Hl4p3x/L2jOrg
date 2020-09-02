// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.SetAccountInfo;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.commons.database.DatabaseFactory;
import org.slf4j.Logger;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class GetAccountInfo extends ReceivablePacket
{
    private static final Logger _log;
    private String _account;
    
    @Override
    protected void readImpl() {
        this._account = this.readString();
    }
    
    @Override
    protected void runImpl() {
        int playerSize = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT COUNT(1) FROM characters WHERE account_name=?");
                try {
                    statement.setString(1, this._account);
                    final ResultSet rset = statement.executeQuery();
                    if (rset.next()) {
                        playerSize = rset.getInt(1);
                    }
                    AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(this._account, playerSize));
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
            GetAccountInfo._log.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e), (Throwable)e);
        }
    }
    
    static {
        _log = LoggerFactory.getLogger((Class)GetAccountInfo.class);
    }
}
