// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExResponseCommissionRegister extends ServerPacket
{
    public static final ExResponseCommissionRegister SUCCEED;
    public static final ExResponseCommissionRegister FAILED;
    private final int _result;
    
    private ExResponseCommissionRegister(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_REGISTER);
        this.writeInt(this._result);
    }
    
    static {
        SUCCEED = new ExResponseCommissionRegister(1);
        FAILED = new ExResponseCommissionRegister(0);
    }
}
