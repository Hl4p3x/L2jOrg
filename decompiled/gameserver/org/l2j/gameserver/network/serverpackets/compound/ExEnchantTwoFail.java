// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExEnchantTwoFail extends ServerPacket
{
    public static final ExEnchantTwoFail STATIC_PACKET;
    
    private ExEnchantTwoFail() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_TWO_FAIL);
    }
    
    static {
        STATIC_PACKET = new ExEnchantTwoFail();
    }
}
