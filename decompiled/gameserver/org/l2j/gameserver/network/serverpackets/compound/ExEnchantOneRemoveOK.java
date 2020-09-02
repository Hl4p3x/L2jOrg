// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExEnchantOneRemoveOK extends ServerPacket
{
    public static final ExEnchantOneRemoveOK STATIC_PACKET;
    
    private ExEnchantOneRemoveOK() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_ONE_REMOVE_OK);
    }
    
    static {
        STATIC_PACKET = new ExEnchantOneRemoveOK();
    }
}
