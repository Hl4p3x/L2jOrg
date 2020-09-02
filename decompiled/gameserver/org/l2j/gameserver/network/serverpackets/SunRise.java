// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class SunRise extends ServerPacket
{
    public static final SunRise STATIC_PACKET;
    
    private SunRise() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SUNRISE);
    }
    
    static {
        STATIC_PACKET = new SunRise();
    }
}
