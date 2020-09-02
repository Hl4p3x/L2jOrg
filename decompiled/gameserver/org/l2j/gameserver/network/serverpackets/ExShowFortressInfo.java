// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowFortressInfo extends ServerPacket
{
    public static final ExShowFortressInfo STATIC_PACKET;
    
    private ExShowFortressInfo() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_FORTRESS_INFO);
        this.writeInt(0);
    }
    
    static {
        STATIC_PACKET = new ExShowFortressInfo();
    }
}
