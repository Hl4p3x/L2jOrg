// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExSearchOrc extends ServerPacket
{
    public static final ExSearchOrc STATIC_PACKET;
    
    private ExSearchOrc() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ORC_MOVE);
    }
    
    static {
        STATIC_PACKET = new ExSearchOrc();
    }
}
