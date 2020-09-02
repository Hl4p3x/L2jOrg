// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class NormalCamera extends ServerPacket
{
    public static final NormalCamera STATIC_PACKET;
    
    private NormalCamera() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NORMAL_CAMERA);
    }
    
    static {
        STATIC_PACKET = new NormalCamera();
    }
}
