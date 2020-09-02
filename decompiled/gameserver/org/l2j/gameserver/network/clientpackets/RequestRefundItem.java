// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.slf4j.Logger;

public final class RequestRefundItem extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 4;
    private static final int CUSTOM_CB_SELL_LIST = 423;
    private int _listId;
    private int[] _items;
    
    public RequestRefundItem() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._listId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 4 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new int[count];
        for (int i = 0; i < count; ++i) {
            this._items[i] = this.readInt();
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("refund")) {
            player.sendMessage("You are using refund too fast.");
            return;
        }
        if (this._items == null || !player.hasRefund()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final WorldObject target = player.getTarget();
        Merchant merchant = null;
        if (!player.isGM() && this._listId != 423) {
            if (!(target instanceof Merchant) || !MathUtil.isInsideRadius3D(player, target, 250) || player.getInstanceId() != target.getInstanceId()) {
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
        long weight = 0L;
        long adena = 0L;
        long slots = 0L;
        final Item[] refund = player.getRefund().getItems().toArray(new Item[0]);
        final int[] objectIds = new int[this._items.length];
        for (int i = 0; i < this._items.length; ++i) {
            final int idx = this._items[i];
            if (idx < 0 || idx >= refund.length) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                return;
            }
            for (int j = i + 1; j < this._items.length; ++j) {
                if (idx == this._items[j]) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                    return;
                }
            }
            final Item item = refund[idx];
            final ItemTemplate template = item.getTemplate();
            objectIds[i] = item.getObjectId();
            for (int k = 0; k < i; ++k) {
                if (objectIds[i] == objectIds[k]) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                    return;
                }
            }
            final long count = item.getCount();
            weight += count * template.getWeight();
            adena += count * template.getReferencePrice() / 2L;
            if (!template.isStackable()) {
                slots += count;
            }
            else if (player.getInventory().getItemByItemId(template.getId()) == null) {
                ++slots;
            }
        }
        if (weight > 2147483647L || weight < 0L || !player.getInventory().validateWeight((int)weight)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (slots > 2147483647L || slots < 0L || !player.getInventory().validateCapacity((int)slots)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (adena < 0L || !player.reduceAdena("Refund", adena, player.getLastFolkNPC(), false)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        for (int i = 0; i < this._items.length; ++i) {
            final Item item2 = player.getRefund().transferItem("Refund", objectIds[i], Long.MAX_VALUE, player.getInventory(), player, player.getLastFolkNPC());
            if (item2 == null) {
                RequestRefundItem.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            }
        }
        ((GameClient)this.client).sendPacket(new ExUserInfoInvenWeight(player));
        ((GameClient)this.client).sendPacket(new ExBuySellList(player, true));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestRefundItem.class);
    }
}
