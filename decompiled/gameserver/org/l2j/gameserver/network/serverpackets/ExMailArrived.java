// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExMailArrived extends ServerPacket
{
    public static final ExMailArrived STATIC_PACKET;
    
    private ExMailArrived() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MAIL_ARRIVED);
    }
    
    static {
        STATIC_PACKET = new ExMailArrived();
    }
}
