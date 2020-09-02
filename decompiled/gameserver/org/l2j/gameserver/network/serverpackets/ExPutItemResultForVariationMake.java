// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutItemResultForVariationMake extends ServerPacket
{
    private final int _itemObjId;
    private final int _itemId;
    
    public ExPutItemResultForVariationMake(final int itemObjId, final int itemId) {
        this._itemObjId = itemObjId;
        this._itemId = itemId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_ITEM_RESULT_FOR_VARIATION_MAKE);
        this.writeInt(this._itemObjId);
        this.writeInt(this._itemId);
        this.writeInt(1);
    }
}
