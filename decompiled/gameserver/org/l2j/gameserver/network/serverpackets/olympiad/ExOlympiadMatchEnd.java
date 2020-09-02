// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMatchEnd extends ServerPacket
{
    public static final ExOlympiadMatchEnd STATIC_PACKET;
    
    private ExOlympiadMatchEnd() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_MATCH_END);
    }
    
    static {
        STATIC_PACKET = new ExOlympiadMatchEnd();
    }
}
