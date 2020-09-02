// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.container.PlayerFreight;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;

public class RequestPackageSend extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 12;
    private ItemHolder[] _items;
    private int _objectId;
    
    public RequestPackageSend() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._objectId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 12 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ItemHolder[count];
        for (int i = 0; i < count; ++i) {
            final int objId = this.readInt();
            final long cnt = this.readLong();
            if (objId < 1 || cnt < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items[i] = new ItemHolder(objId, cnt);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this._items == null || player == null || !player.getAccountChars().containsKey(this._objectId)) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            player.sendMessage("You depositing items too fast.");
            return;
        }
        final Npc manager = player.getLastFolkNPC();
        if (manager == null || !MathUtil.isInsideRadius2D(player, manager, 250)) {
            return;
        }
        if (player.hasItemRequest()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        if (player.getActiveTradeList() != null) {
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getReputation() < 0) {
            return;
        }
        final int fee = this._items.length * Config.ALT_FREIGHT_PRICE;
        long currentAdena = player.getAdena();
        int slots = 0;
        final ItemContainer warehouse = new PlayerFreight(this._objectId);
        for (final ItemHolder i : this._items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "freight");
            if (item == null) {
                RequestPackageSend.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                warehouse.deleteMe();
                return;
            }
            if (!item.isFreightable()) {
                warehouse.deleteMe();
                return;
            }
            if (item.getId() == 57) {
                currentAdena -= i.getCount();
            }
            else if (!item.isStackable()) {
                slots += (int)i.getCount();
            }
            else if (warehouse.getItemByItemId(item.getId()) == null) {
                ++slots;
            }
        }
        if (!warehouse.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            warehouse.deleteMe();
            return;
        }
        if (currentAdena < fee || !player.reduceAdena(warehouse.getName(), fee, manager, false)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            warehouse.deleteMe();
            return;
        }
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (final ItemHolder j : this._items) {
            final Item oldItem = player.checkItemManipulation(j.getId(), j.getCount(), "deposit");
            if (oldItem == null) {
                RequestPackageSend.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                warehouse.deleteMe();
                return;
            }
            final Item newItem = player.getInventory().transferItem("Trade", j.getId(), j.getCount(), warehouse, player, null);
            if (newItem == null) {
                RequestPackageSend.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            }
            else {
                if (playerIU != null) {
                    if (oldItem.getCount() > 0L && oldItem != newItem) {
                        playerIU.addModifiedItem(oldItem);
                    }
                    else {
                        playerIU.addRemovedItem(oldItem);
                    }
                }
                World.getInstance().removeObject(oldItem);
                World.getInstance().removeObject(newItem);
            }
        }
        warehouse.deleteMe();
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        }
        else {
            player.sendItemList();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPackageSend.class);
    }
}
