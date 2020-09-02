// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.buylist.Product;
import java.util.Iterator;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.slf4j.Logger;

public final class RequestBuyItem extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 12;
    private static final int CUSTOM_CB_SELL_LIST = 423;
    private int _listId;
    private List<ItemHolder> _items;
    
    public RequestBuyItem() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._listId = this.readInt();
        final int size = this.readInt();
        if (size <= 0 || size > Config.MAX_ITEM_IN_PACKET || size * 12 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<ItemHolder>(size);
        for (int i = 0; i < size; ++i) {
            final int itemId = this.readInt();
            final long count = this.readLong();
            if (itemId < 1 || count < 1L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items.add(new ItemHolder(itemId, count));
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            player.sendMessage("You are buying too fast.");
            return;
        }
        if (this._items == null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getReputation() < 0) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final WorldObject target = player.getTarget();
        Merchant merchant = null;
        if (!player.isGM() && this._listId != 423) {
            if (!(target instanceof Merchant) || !MathUtil.isInsideRadius3D(player, target, 250) || player.getInstanceWorld() != target.getInstanceWorld()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            merchant = (Merchant)target;
        }
        if (merchant == null && !player.isGM() && this._listId != 423) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final ProductList buyList = BuyListData.getInstance().getBuyList(this._listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, player.getName(), player.getAccountName(), this._listId));
            return;
        }
        double castleTaxRate = 0.0;
        if (merchant != null) {
            if (!buyList.isNpcAllowed(merchant.getId())) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            castleTaxRate = merchant.getCastleTaxRate(TaxType.BUY);
        }
        long subTotal = 0L;
        long slots = 0L;
        long weight = 0L;
        for (final ItemHolder i : this._items) {
            final Product product = buyList.getProductByItemId(i.getId());
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, player.getName(), player.getAccountName(), this._listId, i.getId()));
                return;
            }
            if (!product.isStackable() && i.getCount() > 1L) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED));
                return;
            }
            long price = product.getPrice();
            if (price < 0L) {
                RequestBuyItem.LOGGER.warn("ERROR, no price found .. wrong buylist ??");
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (price == 0L && !player.isGM() && Config.ONLY_GM_ITEMS_FREE) {
                player.sendMessage("Ohh Cheat dont work? You have a problem now!");
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                return;
            }
            if (product.hasLimitedStock() && i.getCount() > product.getCount()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (Inventory.MAX_ADENA / i.getCount() < price) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
            price *= (long)(1.0 + castleTaxRate + product.getBaseTaxRate());
            subTotal += i.getCount() * price;
            if (subTotal > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
            weight += i.getCount() * product.getWeight();
            if (player.getInventory().getItemByItemId(product.getItemId()) != null) {
                continue;
            }
            ++slots;
        }
        if (!player.isGM() && (weight > 2147483647L || weight < 0L || !player.getInventory().validateWeight((int)weight))) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!player.isGM() && (slots > 2147483647L || slots < 0L || !player.getInventory().validateCapacity((int)slots))) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (subTotal < 0L || !player.reduceAdena("Buy", subTotal, player.getLastFolkNPC(), false)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        for (final ItemHolder i : this._items) {
            final Product product = buyList.getProductByItemId(i.getId());
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, player.getName(), player.getAccountName(), this._listId, i.getId()));
            }
            else if (product.hasLimitedStock()) {
                if (!product.decreaseCount(i.getCount())) {
                    continue;
                }
                player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
            }
            else {
                player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
            }
        }
        if (merchant != null) {
            merchant.handleTaxPayment((long)(subTotal * castleTaxRate));
        }
        ((GameClient)this.client).sendPacket(new ExUserInfoInvenWeight(player));
        ((GameClient)this.client).sendPacket(new ExBuySellList(player, true));
        player.sendPacket(SystemMessageId.EXCHANGE_IS_SUCCESSFUL);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestBuyItem.class);
    }
}
