// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRequestHackShield extends ServerPacket
{
    public static final ExRequestHackShield STATIC_PACKET;
    
    private ExRequestHackShield() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REQUEST_HACK_SHIELD);
    }
    
    static {
        STATIC_PACKET = new ExRequestHackShield();
    }
}
