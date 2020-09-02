// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Summon;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class ExBuySellList extends AbstractItemPacket
{
    private final boolean _done;
    private final int _inventorySlots;
    private Collection<Item> _sellList;
    private Collection<Item> _refundList;
    private double _castleTaxRate;
    
    public ExBuySellList(final Player player, final boolean done) {
        this._refundList = null;
        this._castleTaxRate = 1.0;
        final Summon pet = player.getPet();
        final Summon summon;
        this._sellList = player.getInventory().getItems(item -> !item.isEquipped() && item.isSellable() && (summon == null || item.getObjectId() != summon.getControlObjectId()), (Predicate<Item>[])new Predicate[0]);
        this._inventorySlots = player.getInventory().getItems(item -> !item.isQuestItem(), (Predicate<Item>[])new Predicate[0]).size();
        if (player.hasRefund()) {
            this._refundList = player.getRefund().getItems();
        }
        this._done = done;
    }
    
    public ExBuySellList(final Player player, final boolean done, final double castleTaxRate) {
        this(player, done);
        this._castleTaxRate = 1.0 - castleTaxRate;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BUY_SELL_LIST);
        this.writeInt(1);
        this.writeInt(this._inventorySlots);
        if (this._sellList != null) {
            this.writeShort((short)this._sellList.size());
            for (final Item item : this._sellList) {
                this.writeItem(item);
                this.writeLong((long)(item.getTemplate().getReferencePrice() / 2L * this._castleTaxRate));
            }
        }
        else {
            this.writeShort((short)0);
        }
        if (this._refundList != null && !this._refundList.isEmpty()) {
            this.writeShort((short)this._refundList.size());
            int i = 0;
            for (final Item item2 : this._refundList) {
                this.writeItem(item2);
                this.writeInt(i++);
                this.writeLong(item2.getTemplate().getReferencePrice() / 2L * item2.getCount());
            }
        }
        else {
            this.writeShort((short)0);
        }
        this.writeByte((byte)(byte)(this._done ? 1 : 0));
    }
}
