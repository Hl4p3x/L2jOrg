// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExClosePartyRoom extends ServerPacket
{
    public static final ExClosePartyRoom STATIC_PACKET;
    
    private ExClosePartyRoom() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DISMISS_PARTY_ROOM);
    }
    
    static {
        STATIC_PACKET = new ExClosePartyRoom();
    }
}
