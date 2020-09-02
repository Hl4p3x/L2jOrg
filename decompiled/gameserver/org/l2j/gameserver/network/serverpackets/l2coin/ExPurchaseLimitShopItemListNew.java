// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.l2coin;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPurchaseLimitShopItemListNew extends ServerPacket
{
    private final byte index;
    
    public ExPurchaseLimitShopItemListNew(final byte index) {
        this.index = index;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW);
        this.writeByte(this.index);
        final IntMap<LCoinShopProductInfo> products = LCoinShopData.getInstance().getProductInfos();
        this.writeInt(products.size());
        products.values().forEach(product -> {
            this.writeInt(product.getId());
            this.writeInt(product.getProduction().getId());
            this.writeIngredients(product.getIngredients());
            this.writeInt(product.getRemainAmount());
            this.writeInt(product.getRemainTime());
            this.writeInt(product.getRemainServerItemAmount());
        });
    }
    
    private void writeIngredients(final List<ItemHolder> ingredients) {
        for (int i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                this.writeInt(ingredients.get(i).getId());
            }
            else {
                this.writeInt(0);
            }
        }
        for (int i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                this.writeLong(ingredients.get(i).getCount());
            }
            else {
                this.writeLong(0L);
            }
        }
    }
}
