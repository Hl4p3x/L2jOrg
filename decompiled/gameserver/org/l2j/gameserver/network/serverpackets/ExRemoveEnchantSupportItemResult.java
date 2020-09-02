// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRemoveEnchantSupportItemResult extends ServerPacket
{
    public static final ExRemoveEnchantSupportItemResult STATIC_PACKET;
    
    private ExRemoveEnchantSupportItemResult() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REMOVE_ENCHANT_SUPPORT_ITEM_RESULT);
    }
    
    static {
        STATIC_PACKET = new ExRemoveEnchantSupportItemResult();
    }
}
