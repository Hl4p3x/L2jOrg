// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExEnchantRetryToPutItemFail extends ServerPacket
{
    public static final ExEnchantRetryToPutItemFail STATIC_PACKET;
    
    private ExEnchantRetryToPutItemFail() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_RETRY_TO_PUT_ITEMS_FAIL);
    }
    
    static {
        STATIC_PACKET = new ExEnchantRetryToPutItemFail();
    }
}
