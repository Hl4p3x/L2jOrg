// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public class ExChangeAttributeItemList extends AbstractItemPacket
{
    private final ItemInfo[] _itemsList;
    private final int _itemId;
    
    public ExChangeAttributeItemList(final int itemId, final ItemInfo[] itemsList) {
        this._itemId = itemId;
        this._itemsList = itemsList;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_ITEM_LIST);
        this.writeInt(this._itemId);
        this.writeInt(this._itemsList.length);
        for (final ItemInfo item : this._itemsList) {
            this.writeItem(item);
        }
    }
}
