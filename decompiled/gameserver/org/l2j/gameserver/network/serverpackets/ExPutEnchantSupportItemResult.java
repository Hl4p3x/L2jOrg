// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutEnchantSupportItemResult extends ServerPacket
{
    private final int _result;
    
    public ExPutEnchantSupportItemResult(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_ENCHANT_SUPPORT_ITEM_RESULT);
        this.writeInt(this._result);
    }
}
