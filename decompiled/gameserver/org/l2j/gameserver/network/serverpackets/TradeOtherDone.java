// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class TradeOtherDone extends ServerPacket
{
    public static final TradeOtherDone STATIC_PACKET;
    
    private TradeOtherDone() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_PRESS_OTHER_OK);
    }
    
    static {
        STATIC_PACKET = new TradeOtherDone();
    }
}
