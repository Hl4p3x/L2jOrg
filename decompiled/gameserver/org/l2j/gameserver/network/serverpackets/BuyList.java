// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.buylist.Product;
import java.util.Collection;

public final class BuyList extends AbstractItemPacket
{
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;
    private final int _inventorySlots;
    private final double _castleTaxRate;
    
    public BuyList(final ProductList list, final Player player, final double castleTaxRate) {
        this._listId = list.getListId();
        this._list = list.getProducts();
        this._money = player.getAdena();
        this._inventorySlots = player.getInventory().getItems(item -> !item.isQuestItem(), (Predicate<Item>[])new Predicate[0]).size();
        this._castleTaxRate = castleTaxRate;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BUY_SELL_LIST);
        this.writeInt(0);
        this.writeLong(this._money);
        this.writeInt(this._listId);
        this.writeInt(this._inventorySlots);
        this.writeShort((short)this._list.size());
        for (final Product product : this._list) {
            if (product.getCount() > 0L || !product.hasLimitedStock()) {
                this.writeItem(product);
                this.writeLong((long)(product.getPrice() * (1.0 + this._castleTaxRate + product.getBaseTaxRate())));
            }
        }
    }
}
