// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import java.security.NoSuchAlgorithmException;
import org.l2j.gameserver.network.GameClient;
import java.util.Arrays;
import java.security.MessageDigest;
import org.slf4j.Logger;

public class GameGuardReply extends ClientPacket
{
    private static final Logger LOGGER;
    private static final byte[] VALID;
    private final byte[] _reply;
    
    public GameGuardReply() {
        this._reply = new byte[8];
    }
    
    public void readImpl() {
        this.readBytes(this._reply, 0, 4);
        this.readInt();
        this.readBytes(this._reply, 4, 4);
    }
    
    public void runImpl() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            final byte[] result = md.digest(this._reply);
            if (Arrays.equals(result, GameGuardReply.VALID)) {
                ((GameClient)this.client).setGameGuardOk(true);
            }
        }
        catch (NoSuchAlgorithmException e) {
            GameGuardReply.LOGGER.warn(e.getLocalizedMessage(), (Throwable)e);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GameGuardReply.class);
        VALID = new byte[] { -120, 64, 28, -89, -125, 66, -23, 21, -34, -61, 104, -10, 45, 35, -15, 63, -18, 104, 91, -59 };
    }
}
