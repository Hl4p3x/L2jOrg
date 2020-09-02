// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExVariationCancelResult extends ServerPacket
{
    public static final ExVariationCancelResult STATIC_PACKET_SUCCESS;
    public static final ExVariationCancelResult STATIC_PACKET_FAILURE;
    private final int _result;
    
    private ExVariationCancelResult(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VARIATION_CANCEL_RESULT);
        this.writeInt(this._result);
    }
    
    static {
        STATIC_PACKET_SUCCESS = new ExVariationCancelResult(1);
        STATIC_PACKET_FAILURE = new ExVariationCancelResult(0);
    }
}
