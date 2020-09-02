// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ServerClose extends ServerPacket
{
    public static final ServerClose STATIC_PACKET;
    
    private ServerClose() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SERVER_CLOSE);
    }
    
    static {
        STATIC_PACKET = new ServerClose();
    }
}
