// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRestartClient extends ServerPacket
{
    public static final ExRestartClient STATIC_PACKET;
    
    private ExRestartClient() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESTART_CLIENT);
    }
    
    static {
        STATIC_PACKET = new ExRestartClient();
    }
}
