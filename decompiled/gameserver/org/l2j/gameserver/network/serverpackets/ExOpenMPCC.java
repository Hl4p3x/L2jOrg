// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExOpenMPCC extends ServerPacket
{
    public static final ExOpenMPCC STATIC_PACKET;
    
    private ExOpenMPCC() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OPEN_MPCC);
    }
    
    static {
        STATIC_PACKET = new ExOpenMPCC();
    }
}
