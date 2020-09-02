// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShowCommission extends ServerPacket
{
    public static final ExShowCommission STATIC_PACKET;
    
    private ExShowCommission() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_COMMISSION);
        this.writeInt(1);
    }
    
    static {
        STATIC_PACKET = new ExShowCommission();
    }
}
