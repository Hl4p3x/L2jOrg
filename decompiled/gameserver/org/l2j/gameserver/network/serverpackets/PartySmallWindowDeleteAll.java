// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class PartySmallWindowDeleteAll extends ServerPacket
{
    public static final PartySmallWindowDeleteAll STATIC_PACKET;
    
    private PartySmallWindowDeleteAll() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE_ALL);
    }
    
    static {
        STATIC_PACKET = new PartySmallWindowDeleteAll();
    }
}
