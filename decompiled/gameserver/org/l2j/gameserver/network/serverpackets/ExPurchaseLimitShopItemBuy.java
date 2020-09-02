// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;

public class ExPurchaseLimitShopItemBuy extends ServerPacket
{
    private final boolean isFailToBuy;
    private final LCoinShopProductInfo productInfo;
    
    public ExPurchaseLimitShopItemBuy(final LCoinShopProductInfo info, final boolean isFailToBuy) {
        this.productInfo = info;
        this.isFailToBuy = isFailToBuy;
    }
    
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeByte((int)(this.isFailToBuy ? 1 : 0));
        this.writeByte(3);
        this.writeInt(this.productInfo.getId());
        this.writeInt(this.productInfo.getProduction().getId());
        this.writeInt(this.productInfo.getLimitPerDay());
    }
}
