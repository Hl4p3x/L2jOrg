// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class SunSet extends ServerPacket
{
    public static final SunSet STATIC_PACKET;
    
    private SunSet() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SUNSET);
    }
    
    static {
        STATIC_PACKET = new SunSet();
    }
}
