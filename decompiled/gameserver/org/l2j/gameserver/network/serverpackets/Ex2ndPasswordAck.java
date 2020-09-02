// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class Ex2ndPasswordAck extends ServerPacket
{
    public static int SUCCESS;
    public static int WRONG_PATTERN;
    private final int _status;
    private final int _response;
    
    public Ex2ndPasswordAck(final int status, final int response) {
        this._status = status;
        this._response = response;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_2ND_PASSWORD_ACK);
        this.writeByte((byte)this._status);
        this.writeInt((int)((this._response == Ex2ndPasswordAck.WRONG_PATTERN) ? 1 : 0));
        this.writeInt(0);
    }
    
    static {
        Ex2ndPasswordAck.SUCCESS = 0;
        Ex2ndPasswordAck.WRONG_PATTERN = 1;
    }
}
