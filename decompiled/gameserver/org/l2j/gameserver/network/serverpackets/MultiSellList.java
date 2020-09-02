// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Iterator;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.MultisellEntryHolder;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;

public final class MultiSellList extends AbstractItemPacket
{
    private final PreparedMultisellListHolder _list;
    private final boolean _finished;
    private int _size;
    private int _index;
    
    public MultiSellList(final PreparedMultisellListHolder list, final int index) {
        this._list = list;
        this._index = index;
        this._size = list.getEntries().size() - index;
        if (this._size > 40) {
            this._finished = false;
            this._size = 40;
        }
        else {
            this._finished = true;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MULTI_SELL_LIST);
        this.writeByte((byte)0);
        this.writeInt(this._list.getId());
        this.writeByte((byte)0);
        this.writeInt(1 + this._index / 40);
        this.writeInt((int)(this._finished ? 1 : 0));
        this.writeInt(40);
        this.writeInt(this._size);
        this.writeByte((byte)0);
        this.writeByte((byte)(byte)(this._list.isChanceMultisell() ? 1 : 0));
        this.writeInt(32);
        while (this._size-- > 0) {
            final ItemInfo itemEnchantment = this._list.getItemEnchantment(this._index);
            final MultisellEntryHolder entry = this._list.getEntries().get(this._index++);
            this.writeInt(this._index);
            this.writeByte((byte)(byte)(entry.isStackable() ? 1 : 0));
            this.writeShort((short)((itemEnchantment != null) ? itemEnchantment.getEnchantLevel() : 0));
            this.writeItemAugment(itemEnchantment);
            this.writeItemElemental(itemEnchantment);
            this.writeItemEnsoulOptions(itemEnchantment);
            this.writeShort((short)entry.getProducts().size());
            this.writeShort((short)entry.getIngredients().size());
            for (final ItemChanceHolder product : entry.getProducts()) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(product.getId());
                final ItemInfo displayItemEnchantment = (this._list.isMaintainEnchantment() && itemEnchantment != null && template != null && template.getClass().equals(itemEnchantment.getTemplate().getClass())) ? itemEnchantment : null;
                this.writeInt(product.getId());
                if (template != null) {
                    this.writeLong(template.getBodyPart().getId());
                    this.writeShort((short)template.getType2());
                }
                else {
                    this.writeLong(0L);
                    this.writeShort((short)(-1));
                }
                this.writeLong(this._list.getProductCount(product));
                this.writeShort((short)((product.getEnchantmentLevel() > 0) ? product.getEnchantmentLevel() : ((displayItemEnchantment != null) ? displayItemEnchantment.getEnchantLevel() : 0)));
                this.writeInt((int)Math.ceil(product.getChance()));
                this.writeItemAugment(displayItemEnchantment);
                this.writeItemElemental(displayItemEnchantment);
                this.writeItemEnsoulOptions(displayItemEnchantment);
            }
            for (final ItemChanceHolder ingredient : entry.getIngredients()) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(ingredient.getId());
                final ItemInfo displayItemEnchantment = (itemEnchantment != null && itemEnchantment.getId() == ingredient.getId()) ? itemEnchantment : null;
                this.writeInt(ingredient.getId());
                this.writeShort((short)((template != null) ? template.getType2() : 65535));
                this.writeLong(this._list.getIngredientCount(ingredient));
                this.writeShort((short)((ingredient.getEnchantmentLevel() > 0) ? ingredient.getEnchantmentLevel() : ((displayItemEnchantment != null) ? displayItemEnchantment.getEnchantLevel() : 0)));
                this.writeItemAugment(displayItemEnchantment);
                this.writeItemElemental(displayItemEnchantment);
                this.writeItemEnsoulOptions(displayItemEnchantment);
            }
        }
    }
}
