// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;

public class ExPutItemResultForVariationCancel extends ServerPacket
{
    private final int _itemObjId;
    private final int _itemId;
    private final int _itemAug1;
    private final int _itemAug2;
    private final long _price;
    
    public ExPutItemResultForVariationCancel(final Item item, final long price) {
        this._itemObjId = item.getObjectId();
        this._itemId = item.getDisplayId();
        this._price = price;
        this._itemAug1 = item.getAugmentation().getOption1Id();
        this._itemAug2 = item.getAugmentation().getOption2Id();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_ITEM_RESULT_FOR_VARIATION_CANCEL);
        this.writeInt(this._itemObjId);
        this.writeInt(this._itemId);
        this.writeInt(this._itemAug1);
        this.writeInt(this._itemAug2);
        this.writeLong(this._price);
        this.writeInt(1);
    }
}
