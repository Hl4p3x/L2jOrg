// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutEnchantTargetItemResult extends ServerPacket
{
    private final int objectId;
    
    public ExPutEnchantTargetItemResult(final int objectId) {
        this.objectId = objectId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_ENCHANT_TARGET_ITEM_RESULT);
        this.writeInt(this.objectId);
    }
}
