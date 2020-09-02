// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.slf4j.Logger;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class LoginServerFail extends ReceivablePacket
{
    private static final Logger logger;
    private static final String[] REASONS;
    private String _reason;
    private boolean _restartConnection;
    
    public LoginServerFail() {
        this._restartConnection = true;
    }
    
    @Override
    protected void readImpl() {
        final int reasonId = this.readByte();
        if (this.available() <= 0) {
            this._reason = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, LoginServerFail.REASONS[reasonId]);
        }
        else {
            this._reason = this.readString();
            this._restartConnection = (this.readByte() > 0);
        }
    }
    
    @Override
    protected void runImpl() {
        LoginServerFail.logger.warn(this._reason);
        if (!this._restartConnection) {
            AuthServerCommunication.getInstance().shutdown();
        }
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)LoginServerFail.class);
        REASONS = new String[] { "none", "IP banned", "IP reserved", "wrong hexid", "ID reserved", "no free ID", "not authed", "already logged in" };
    }
}
