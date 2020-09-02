// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.buylist.Product;
import java.util.Collection;

public class ShopPreviewList extends ServerPacket
{
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;
    
    public ShopPreviewList(final ProductList list, final long currentMoney) {
        this._listId = list.getListId();
        this._list = list.getProducts();
        this._money = currentMoney;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.BUY_PREVIEW_LIST);
        this.writeInt(5056);
        this.writeLong(this._money);
        this.writeInt(this._listId);
        int newlength = 0;
        for (final Product product : this._list) {
            if (product.isEquipable()) {
                ++newlength;
            }
        }
        this.writeShort((short)newlength);
        for (final Product product : this._list) {
            if (product.isEquipable()) {
                this.writeInt(product.getItemId());
                this.writeShort(product.getType2());
                if (product.getType1() != 4) {
                    this.writeLong(product.getBodyPart().getId());
                }
                else {
                    this.writeLong(0L);
                }
                this.writeLong((long)Config.WEAR_PRICE);
            }
        }
    }
}
