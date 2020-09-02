// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collection;
import org.l2j.gameserver.model.ItemInfo;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;

public class InventoryUpdate extends AbstractInventoryUpdate
{
    public InventoryUpdate() {
    }
    
    public InventoryUpdate(final Item item) {
        super(item);
    }
    
    public InventoryUpdate(final List<ItemInfo> items) {
        super(items);
    }
    
    public InventoryUpdate(final Collection<Item> items) {
        super(items);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.INVENTORY_UPDATE);
        this.writeItems();
    }
}
