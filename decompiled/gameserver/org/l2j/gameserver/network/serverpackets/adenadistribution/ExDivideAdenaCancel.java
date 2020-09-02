// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.adenadistribution;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExDivideAdenaCancel extends ServerPacket
{
    public static final ExDivideAdenaCancel STATIC_PACKET;
    
    private ExDivideAdenaCancel() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DIVIDE_ADENA_CANCEL);
        this.writeByte((byte)0);
    }
    
    static {
        STATIC_PACKET = new ExDivideAdenaCancel();
    }
}
