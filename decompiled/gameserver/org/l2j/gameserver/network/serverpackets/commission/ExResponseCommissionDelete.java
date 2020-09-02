// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExResponseCommissionDelete extends ServerPacket
{
    public static final ExResponseCommissionDelete SUCCEED;
    public static final ExResponseCommissionDelete FAILED;
    private final int _result;
    
    private ExResponseCommissionDelete(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_DELETE);
        this.writeInt(this._result);
    }
    
    static {
        SUCCEED = new ExResponseCommissionDelete(1);
        FAILED = new ExResponseCommissionDelete(0);
    }
}
