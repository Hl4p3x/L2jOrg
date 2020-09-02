// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.adenadistribution;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExDivideAdenaStart extends ServerPacket
{
    public static final ExDivideAdenaStart STATIC_PACKET;
    
    private ExDivideAdenaStart() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DIVIDE_ADENA_START);
    }
    
    static {
        STATIC_PACKET = new ExDivideAdenaStart();
    }
}
