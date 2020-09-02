// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCloseMPCC extends ServerPacket
{
    public static final ExCloseMPCC STATIC_PACKET;
    
    private ExCloseMPCC() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CLOSE_MPCC);
    }
    
    static {
        STATIC_PACKET = new ExCloseMPCC();
    }
}
