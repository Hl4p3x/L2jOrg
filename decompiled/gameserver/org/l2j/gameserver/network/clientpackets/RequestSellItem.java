// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.UniqueItemHolder;
import java.util.List;

public final class RequestSellItem extends ClientPacket
{
    private static final int BATCH_LENGTH = 16;
    private static final int CUSTOM_CB_SELL_LIST = 423;
    private int _listId;
    private List<UniqueItemHolder> _items;
    
    public RequestSellItem() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._listId = this.readInt();
        final int size = this.readInt();
        if (size <= 0 || size > Config.MAX_ITEM_IN_PACKET || size * 16 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<UniqueItemHolder>(size);
        for (int i = 0; i < size; ++i) {
            final int objectId = this.readInt();
            final int itemId = this.readInt();
            final long count = this.readLong();
            if (objectId < 1 || itemId < 1 || count < 1L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items.add(new UniqueItemHolder(itemId, objectId, count));
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
            if (target == null || !MathUtil.isInsideRadius3D(player, target, 250) || player.getInstanceId() != target.getInstanceId()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (!(target instanceof Merchant)) {
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
        if (merchant != null && !buyList.isNpcAllowed(merchant.getId())) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        long totalPrice = 0L;
        for (final UniqueItemHolder i : this._items) {
            final Item item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
            if (item != null) {
                if (!item.isSellable()) {
                    continue;
                }
                final long price = item.getReferencePrice() / 2L;
                totalPrice += price * i.getCount();
                if (Inventory.MAX_ADENA / i.getCount() < price || totalPrice > Inventory.MAX_ADENA) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                    return;
                }
                if (Config.ALLOW_REFUND) {
                    player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, merchant);
                }
                else {
                    player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, merchant);
                }
            }
        }
        if (merchant != null) {
            final long profit = (long)(totalPrice * (1.0 - merchant.getCastleTaxRate(TaxType.SELL)));
            merchant.handleTaxPayment(totalPrice - profit);
            totalPrice = profit;
        }
        player.addAdena("Sell", totalPrice, merchant, false);
        ((GameClient)this.client).sendPacket(new ExUserInfoInvenWeight(player));
        ((GameClient)this.client).sendPacket(new ExBuySellList(player, true));
    }
}
