// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDissmissMPCCRoom extends ServerPacket
{
    public static final ExDissmissMPCCRoom STATIC_PACKET;
    
    private ExDissmissMPCCRoom() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DISMISS_MPCC_ROOM);
    }
    
    static {
        STATIC_PACKET = new ExDissmissMPCCRoom();
    }
}
