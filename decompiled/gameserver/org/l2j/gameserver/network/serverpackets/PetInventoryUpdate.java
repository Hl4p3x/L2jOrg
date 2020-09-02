// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ItemInfo;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;

public class PetInventoryUpdate extends AbstractInventoryUpdate
{
    public PetInventoryUpdate() {
    }
    
    public PetInventoryUpdate(final Item item) {
        super(item);
    }
    
    public PetInventoryUpdate(final List<ItemInfo> items) {
        super(items);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_INVENTORY_UPDATE);
        this.writeItems();
    }
}
