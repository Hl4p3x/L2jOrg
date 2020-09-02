// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class SendTradeRequest extends ServerPacket
{
    private final int senderId;
    
    public SendTradeRequest(final int senderId) {
        this.senderId = senderId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_REQUEST);
        this.writeInt(this.senderId);
    }
}
