// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListSell;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;

public class SetPrivateStoreListSell extends ClientPacket
{
    private static final int BATCH_LENGTH = 20;
    private boolean _packageSale;
    private Item[] _items;
    
    public SetPrivateStoreListSell() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._packageSale = this.readIntAsBoolean();
        final int count = this.readInt();
        if (count < 1 || count > Config.MAX_ITEM_IN_PACKET || count * 20 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new Item[count];
        for (int i = 0; i < count; ++i) {
            final int itemId = this.readInt();
            final long cnt = this.readLong();
            final long price = this.readLong();
            if (itemId < 1 || cnt < 1L || price < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items[i] = new Item(itemId, cnt, price);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (this._items == null) {
            player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel()) {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendPacket(new PrivateStoreManageListSell(1, player, this._packageSale));
            player.sendPacket(new PrivateStoreManageListSell(2, player, this._packageSale));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isInsideZone(ZoneType.NO_STORE)) {
            player.sendPacket(new PrivateStoreManageListSell(1, player, this._packageSale));
            player.sendPacket(new PrivateStoreManageListSell(2, player, this._packageSale));
            player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (this._items.length > player.getPrivateSellStoreLimit()) {
            player.sendPacket(new PrivateStoreManageListSell(1, player, this._packageSale));
            player.sendPacket(new PrivateStoreManageListSell(2, player, this._packageSale));
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        final TradeList tradeList = player.getSellList();
        tradeList.clear();
        tradeList.setPackaged(this._packageSale);
        long totalCost = player.getAdena();
        for (final Item i : this._items) {
            if (!i.addToTradeList(tradeList)) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
            totalCost += i.getPrice();
            if (totalCost > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
        }
        player.sitDown();
        if (this._packageSale) {
            player.setPrivateStoreType(PrivateStoreType.PACKAGE_SELL);
        }
        else {
            player.setPrivateStoreType(PrivateStoreType.SELL);
        }
        player.broadcastUserInfo();
        if (this._packageSale) {
            player.broadcastPacket(new ExPrivateStoreSetWholeMsg(player));
        }
        else {
            player.broadcastPacket(new PrivateStoreMsgSell(player));
        }
    }
    
    private static class Item
    {
        private final int _objectId;
        private final long _count;
        private final long _price;
        
        public Item(final int objectId, final long count, final long price) {
            this._objectId = objectId;
            this._count = count;
            this._price = price;
        }
        
        public boolean addToTradeList(final TradeList list) {
            if (Inventory.MAX_ADENA / this._count < this._price) {
                return false;
            }
            list.addItem(this._objectId, this._count, this._price);
            return true;
        }
        
        public long getPrice() {
            return this._count * this._price;
        }
    }
}
