// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import java.util.Iterator;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipProductList extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        final Player player = client.getPlayer();
        final IntMap<L2StoreProduct> products = L2Store.getInstance().getPrimeItems();
        final L2StoreProduct gift = L2Store.getInstance().getVipGiftOfTier(player.getVipTier());
        this.writeId(ServerExPacketId.EX_BR_VIP_PRODUCT_LIST_ACK);
        this.writeLong(player.getAdena());
        this.writeLong(player.getGoldCoin());
        this.writeLong(player.getSilverCoin());
        this.writeByte(1);
        if (Objects.nonNull(gift)) {
            this.writeInt(products.size() + 1);
            this.writeProduct(gift);
        }
        else {
            this.writeInt(products.size());
        }
        for (final L2StoreProduct product : products.values()) {
            this.writeProduct(product);
        }
    }
    
    private void writeProduct(final L2StoreProduct product) {
        this.writeInt(product.getId());
        this.writeByte(product.getCategory());
        this.writeByte(product.getPaymentType());
        this.writeInt(product.getPrice());
        this.writeInt(product.getSilverCoin());
        this.writeByte(product.getPanelType());
        this.writeByte(product.getVipTier());
        this.writeByte(10);
        this.writeByte(product.getItems().size());
        for (final L2StoreItem item : product.getItems()) {
            this.writeInt(item.getId());
            this.writeInt((int)item.getCount());
        }
    }
}
