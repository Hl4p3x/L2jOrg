// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class PetItemList extends AbstractItemPacket
{
    private final Collection<Item> _items;
    
    public PetItemList(final Collection<Item> items) {
        this._items = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_ITEMLIST);
        this.writeShort((short)this._items.size());
        for (final Item item : this._items) {
            this.writeItem(item);
        }
    }
}
