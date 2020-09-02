// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import java.util.Iterator;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipProductList extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        final Player player = client.getPlayer();
        final IntMap<PrimeShopProduct> products = PrimeShopData.getInstance().getPrimeItems();
        final PrimeShopProduct gift = PrimeShopData.getInstance().getVipGiftOfTier(player.getVipTier());
        this.writeId(ServerExPacketId.EX_BR_VIP_PRODUCT_LIST_ACK);
        this.writeLong(player.getAdena());
        this.writeLong(player.getRustyCoin());
        this.writeLong(player.getSilverCoin());
        this.writeByte(1);
        if (Objects.nonNull(gift)) {
            this.writeInt(products.size() + 1);
            this.putProduct(gift);
        }
        else {
            this.writeInt(products.size());
        }
        for (final PrimeShopProduct product : products.values()) {
            this.putProduct(product);
        }
    }
    
    private void putProduct(final PrimeShopProduct product) {
        this.writeInt(product.getId());
        this.writeByte(product.getCategory());
        this.writeByte(product.getPaymentType());
        this.writeInt(product.getPrice());
        this.writeInt(product.getSilverCoin());
        this.writeByte(product.getPanelType());
        this.writeByte(product.getVipTier());
        this.writeByte(10);
        this.writeByte(product.getItems().size());
        for (final PrimeShopItem item : product.getItems()) {
            this.writeInt(item.getId());
            this.writeInt((int)item.getCount());
        }
    }
}
