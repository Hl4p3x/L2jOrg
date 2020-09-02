// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import java.util.List;

public final class RequestSaveInventoryOrder extends ClientPacket
{
    private static final int LIMIT = 125;
    private List<InventoryOrder> _order;
    
    public void readImpl() {
        int sz = this.readInt();
        sz = Math.min(sz, 125);
        this._order = new ArrayList<InventoryOrder>(sz);
        for (int i = 0; i < sz; ++i) {
            final int objectId = this.readInt();
            final int order = this.readInt();
            this._order.add(new InventoryOrder(objectId, order));
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player != null) {
            final Inventory inventory = player.getInventory();
            for (final InventoryOrder order : this._order) {
                final Item item = inventory.getItemByObjectId(order.objectID);
                if (item != null && item.getItemLocation() == ItemLocation.INVENTORY) {
                    item.setItemLocation(ItemLocation.INVENTORY, order.order);
                }
            }
        }
    }
    
    private static class InventoryOrder
    {
        int order;
        int objectID;
        
        public InventoryOrder(final int id, final int ord) {
            this.objectID = id;
            this.order = ord;
        }
    }
}
