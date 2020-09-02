// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.store;

import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBRProductList extends ServerPacket
{
    private final Player player;
    private final int type;
    private final Collection<L2StoreProduct> products;
    
    public ExBRProductList(final Player activeChar, final int type, final Collection<L2StoreProduct> items) {
        this.player = activeChar;
        this.type = type;
        this.products = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_PRODUCT_LIST_ACK);
        this.writeLong(this.player.getAdena());
        this.writeLong(0L);
        this.writeByte(this.type);
        this.writeInt(this.products.size());
        for (final L2StoreProduct brItem : this.products) {
            this.writeProduct(brItem);
        }
    }
    
    private void writeProduct(final L2StoreProduct product) {
        this.writeInt(product.getId());
        this.writeByte(product.getCategory());
        this.writeByte(product.getPaymentType());
        this.writeInt(product.getPrice());
        this.writeByte(product.getPanelType());
        this.writeInt((int)product.getRecommended());
        this.writeInt(product.getStartSale());
        this.writeInt(product.getEndSale());
        this.writeByte(product.getDaysOfWeek());
        this.writeByte(product.getStartHour());
        this.writeByte(product.getStartMinute());
        this.writeByte(product.getStopHour());
        this.writeByte(product.getStopMinute());
        this.writeInt(product.getStock());
        this.writeInt((int)product.getMaxStock());
        this.writeByte(product.getSalePercent());
        this.writeByte(product.getMinLevel());
        this.writeByte(product.getMaxLevel());
        this.writeInt((int)product.getMinBirthday());
        this.writeInt((int)product.getMaxBirthday());
        this.writeInt((int)product.getRestrictionAmount());
        this.writeInt((int)product.getAvailableCount());
        this.writeByte(product.getItems().size());
        for (final L2StoreItem item : product.getItems()) {
            this.writeItem(item);
        }
    }
    
    private void writeItem(final L2StoreItem item) {
        this.writeInt(item.getId());
        this.writeInt((int)item.getCount());
        this.writeInt(item.getWeight());
        this.writeInt(item.isTradable());
    }
}
