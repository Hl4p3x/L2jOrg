// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ExPurchaseLimitShopItemBuy;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.LCoinShopRequest;
import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.network.GameClient;

public class RequestPurchaseLimitShopItemBuy extends ClientPacket
{
    private int productId;
    private int amount;
    
    @Override
    protected void readImpl() throws Exception {
        this.readByte();
        this.productId = this.readInt();
        this.amount = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final LCoinShopProductInfo product = LCoinShopData.getInstance().getProductInfo(this.productId);
        final ItemHolder productItem = product.getProduction();
        final List<ItemHolder> ingredients = product.getIngredients();
        if (player.hasItemRequest() || player.hasRequest(LCoinShopRequest.class, (Class<? extends AbstractRequest>[])new Class[0]) || !this.hasIngredients(player, ingredients)) {
            player.sendPacket(new ExPurchaseLimitShopItemBuy(LCoinShopData.getInstance().getProductInfo(this.productId), true));
            return;
        }
        player.addRequest(new LCoinShopRequest(player));
        this.consumeIngredients(player, ingredients);
        player.addItem("LCoinShop", productItem.getId(), productItem.getCount() * this.amount, player, true);
        player.removeRequest(LCoinShopRequest.class);
        player.sendPacket(new ExPurchaseLimitShopItemBuy(LCoinShopData.getInstance().getProductInfo(this.productId), false));
    }
    
    private boolean hasIngredients(final Player player, final List<ItemHolder> ingredients) {
        for (final ItemHolder ingredient : ingredients) {
            if (player.getInventory().getInventoryItemCount(ingredient.getId(), -1) < ingredient.getCount() * this.amount) {
                return false;
            }
        }
        return true;
    }
    
    private void consumeIngredients(final Player player, final List<ItemHolder> ingredients) {
        ingredients.forEach(ingredient -> {
            switch (ingredient.getId()) {
                case 57: {
                    player.reduceAdena("LCoinShop", ingredient.getCount() * this.amount, player, true);
                    break;
                }
                case 91663: {
                    player.addLCoins(ingredient.getCount() * this.amount);
                    break;
                }
                default: {
                    player.getInventory().destroyItemByItemId("LCoinShop", ingredient.getId(), ingredient.getCount() * this.amount, player, this);
                    break;
                }
            }
        });
    }
}
