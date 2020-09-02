// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Elementals;
import java.util.HashSet;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import org.l2j.gameserver.enums.AttributeType;

public class ExChooseInventoryAttributeItem extends ServerPacket
{
    private final int _itemId;
    private final long _count;
    private final AttributeType _atribute;
    private final int _level;
    private final Set<Integer> _items;
    
    public ExChooseInventoryAttributeItem(final Player activeChar, final Item stone) {
        this._items = new HashSet<Integer>();
        this._itemId = stone.getDisplayId();
        this._count = stone.getCount();
        this._atribute = AttributeType.findByClientId(Elementals.getItemElement(this._itemId));
        if (this._atribute == AttributeType.NONE) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, stone));
        }
        this._level = Elementals.getMaxElementLevel(this._itemId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM);
        this.writeInt(this._itemId);
        this.writeLong(this._count);
        this.writeInt((int)((this._atribute == AttributeType.FIRE) ? 1 : 0));
        this.writeInt((int)((this._atribute == AttributeType.WATER) ? 1 : 0));
        this.writeInt((int)((this._atribute == AttributeType.WIND) ? 1 : 0));
        this.writeInt((int)((this._atribute == AttributeType.EARTH) ? 1 : 0));
        this.writeInt((int)((this._atribute == AttributeType.HOLY) ? 1 : 0));
        this.writeInt((int)((this._atribute == AttributeType.DARK) ? 1 : 0));
        this.writeInt(this._level);
        this.writeInt(this._items.size());
        this._items.forEach(x$0 -> this.writeInt(x$0));
    }
}
