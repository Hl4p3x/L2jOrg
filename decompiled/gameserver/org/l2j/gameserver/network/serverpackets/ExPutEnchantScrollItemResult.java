// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutEnchantScrollItemResult extends ServerPacket
{
    private final int _result;
    
    public ExPutEnchantScrollItemResult(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_ENCHANT_SCROLL_ITEM_RESULT);
        this.writeInt(this._result);
    }
}
