// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCloseCommission extends ServerPacket
{
    public static final ExCloseCommission STATIC_PACKET;
    
    private ExCloseCommission() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CLOSE_COMMISSION);
    }
    
    static {
        STATIC_PACKET = new ExCloseCommission();
    }
}
