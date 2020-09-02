// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public class ExPurchaseLimitShopItemList extends ServerPacket
{
    private void writeIngredientsBlock(final List<ItemHolder> ingredients) {
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
        for (int i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                this.writeLong(ingredients.get(i).getCount());
            }
            else {
                this.writeLong(0L);
            }
        }
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
    
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST);
        final IntMap<LCoinShopProductInfo> productInfos = LCoinShopData.getInstance().getProductInfos();
        this.writeByte(3);
        this.writeInt(productInfos.size());
        productInfos.forEach((key, product) -> {
            this.writeInt(key);
            this.writeInt(product.getProduction().getId());
            this.writeByte((int)((product.getLimitPerDay() > 0) ? 1 : 0));
            this.writeShort(product.getMinLevel());
            this.writeInt(product.getLimitPerDay());
            this.writeIngredientsBlock(product.getIngredients());
            this.writeInt(product.getIngredients().size());
            this.writeByte(product.getCategory().ordinal() + 1);
            this.writeByte(product.isEvent());
            this.writeInt(0);
            this.writeInt(0);
        });
        this.writeShort(0);
    }
}
