// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class ExShowBaseAttributeCancelWindow extends ServerPacket
{
    private final Collection<Item> _items;
    private long _price;
    
    public ExShowBaseAttributeCancelWindow(final Player player) {
        this._items = player.getInventory().getItems(Item::hasAttributes, (Predicate<Item>[])new Predicate[0]);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_BASE_ATTRIBUTE_CANCEL_WINDOW);
        this.writeInt(this._items.size());
        for (final Item item : this._items) {
            this.writeInt(item.getObjectId());
            this.writeLong(this.getPrice(item));
        }
    }
    
    private long getPrice(final Item item) {
        switch (item.getTemplate().getCrystalType()) {
            case S: {
                if (item.isWeapon()) {
                    this._price = 50000L;
                    break;
                }
                this._price = 40000L;
                break;
            }
        }
        return this._price;
    }
}
