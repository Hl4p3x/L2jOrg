// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ShopPreviewInfo;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import java.util.EnumMap;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.slf4j.Logger;

public final class RequestPreviewItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int _unk;
    private int _listId;
    private int _count;
    private int[] _items;
    
    public void readImpl() throws InvalidDataPacketException {
        this._unk = this.readInt();
        this._listId = this.readInt();
        this._count = this.readInt();
        if (this._count < 0) {
            this._count = 0;
        }
        if (this._count > 100) {
            throw new InvalidDataPacketException();
        }
        this._items = new int[this._count];
        for (int i = 0; i < this._count; ++i) {
            this._items[i] = this.readInt();
        }
    }
    
    public void runImpl() {
        if (this._items == null) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            activeChar.sendMessage("You are buying too fast.");
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getReputation() < 0) {
            return;
        }
        final WorldObject target = activeChar.getTarget();
        if (!activeChar.isGM() && (!(target instanceof Merchant) || !MathUtil.isInsideRadius2D(activeChar, target, 250))) {
            return;
        }
        if (this._count < 1 || this._listId >= 4000000) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Merchant merchant = (target instanceof Merchant) ? ((Merchant)target) : null;
        if (merchant == null) {
            RequestPreviewItem.LOGGER.warn("Null merchant!");
            return;
        }
        final ProductList buyList = BuyListData.getInstance().getBuyList(this._listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName(), this._listId));
            return;
        }
        long totalPrice = 0L;
        final EnumMap<InventorySlot, Integer> items = new EnumMap<InventorySlot, Integer>(InventorySlot.class);
        for (int i = 0; i < this._count; ++i) {
            final int itemId = this._items[i];
            final Product product = buyList.getProductByItemId(itemId);
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName(), this._listId, itemId));
                return;
            }
            final InventorySlot slot = product.getBodyPart().slot();
            if (!Objects.isNull(slot)) {
                if (items.containsKey(slot)) {
                    activeChar.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                    return;
                }
                items.put(slot, itemId);
                totalPrice += Config.WEAR_PRICE;
                if (totalPrice > Inventory.MAX_ADENA) {
                    GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName(), Inventory.MAX_ADENA));
                    return;
                }
            }
        }
        if (totalPrice < 0L || !activeChar.reduceAdena("Wear", totalPrice, activeChar.getLastFolkNPC(), true)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }
        if (!items.isEmpty()) {
            activeChar.sendPacket(new ShopPreviewInfo(items));
            ThreadPool.schedule((Runnable)new RemoveWearItemsTask(activeChar), (long)(Config.WEAR_DELAY * 1000));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPreviewItem.class);
    }
    
    private class RemoveWearItemsTask implements Runnable
    {
        private final Player activeChar;
        
        protected RemoveWearItemsTask(final Player player) {
            this.activeChar = player;
        }
        
        @Override
        public void run() {
            try {
                this.activeChar.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT);
                this.activeChar.sendPacket(new ExUserInfoEquipSlot(this.activeChar));
            }
            catch (Exception e) {
                RequestPreviewItem.LOGGER.error(e.getLocalizedMessage(), (Throwable)e);
            }
        }
    }
}
