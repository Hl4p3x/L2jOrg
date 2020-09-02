// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class LeaveWorld extends ServerPacket
{
    public static final LeaveWorld STATIC_PACKET;
    
    private LeaveWorld() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.LOGOUT_OK);
    }
    
    static {
        STATIC_PACKET = new LeaveWorld();
    }
}
