// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDone;
import java.util.Objects;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.network.GameClient;
import java.util.Collection;
import org.l2j.commons.util.Util;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.slf4j.Logger;

public final class SendWareHouseDepositList extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 12;
    private List<ItemHolder> items;
    
    public SendWareHouseDepositList() {
        this.items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        final int size = this.readInt();
        if (size <= 0 || size > Config.MAX_ITEM_IN_PACKET || size * 12 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this.items = new ArrayList<ItemHolder>(size);
        for (int i = 0; i < size; ++i) {
            final int objId = this.readInt();
            final long count = this.readLong();
            if (objId < 1 || count < 0L) {
                this.items = null;
                throw new InvalidDataPacketException();
            }
            this.items.add(new ItemHolder(objId, count));
        }
    }
    
    public void runImpl() {
        if (Util.isNullOrEmpty((Collection)this.items)) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        final Warehouse warehouse = player.getActiveWarehouse();
        final Npc manager = player.getLastFolkNPC();
        final PlayerInventory inventory = player.getInventory();
        try {
            inventory.setInventoryBlock((IntCollection)StreamUtil.collectToSet(this.items.stream().mapToInt(ItemHolder::getId)), InventoryBlockType.BLACKLIST);
            if (Objects.isNull(warehouse) || !this.checkWarehouseManager(manager, player) || !this.checkTransanction(warehouse, manager)) {
                ((GameClient)this.client).sendPacket(new WarehouseDone(false));
                return;
            }
            this.deposit(player, warehouse, manager, inventory);
            ((GameClient)this.client).sendPacket(new WarehouseDone(true));
            ((GameClient)this.client).sendPacket(SystemMessageId.ITEM_HAS_BEEN_STORED_SUCCESSFULLY);
        }
        finally {
            inventory.unblock();
        }
    }
    
    private void deposit(final Player player, final Warehouse warehouse, final Npc manager, final PlayerInventory inventory) {
        final InventoryUpdate inventoryUpdate = new InventoryUpdate();
        for (final ItemHolder i : this.items) {
            final Item modifiedItem = inventory.getItemByObjectId(i.getId());
            inventory.transferItem(warehouse.getName(), i.getId(), i.getCount(), warehouse, player, manager);
            if (Objects.nonNull(inventory.getItemByObjectId(i.getId()))) {
                inventoryUpdate.addModifiedItem(modifiedItem);
            }
            else {
                inventoryUpdate.addRemovedItem(modifiedItem);
            }
        }
        player.sendInventoryUpdate(inventoryUpdate);
    }
    
    private boolean checkWarehouseManager(final Npc manager, final Player player) {
        return player.isGM() || (GameUtils.isWarehouseManager(manager) && manager.canInteract(player));
    }
    
    private boolean checkTransanction(final Warehouse warehouse, final Npc manager) {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.nonNull(player.getActiveTradeList())) {
            return false;
        }
        if (!warehouse.isPrivate() && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return false;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getReputation() < 0) {
            return false;
        }
        if (player.hasItemRequest()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            player.sendMessage("You are depositing items too fast.");
            return false;
        }
        return this.checkItemsAndCharge(warehouse, player, manager);
    }
    
    private boolean checkItemsAndCharge(final Warehouse warehouse, final Player player, final Npc manager) {
        final long fee = this.items.size() * 30;
        final PlayerInventory inventory = player.getInventory();
        long currentAdena = inventory.getAdena();
        int slots = 0;
        for (final ItemHolder i : this.items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "deposit");
            if (Objects.isNull(item)) {
                SendWareHouseDepositList.LOGGER.warn("Error depositing a warehouse object for player {} (validity check)", (Object)player);
                return false;
            }
            if (!item.isDepositable(warehouse.getType()) || !inventory.isNotInUse(item)) {
                return false;
            }
            if (item.getId() == 57) {
                currentAdena -= i.getCount();
            }
            if (!item.isStackable()) {
                slots += (int)i.getCount();
            }
            else {
                if (!Objects.isNull(warehouse.getItemByItemId(item.getId()))) {
                    continue;
                }
                ++slots;
            }
        }
        if (!warehouse.validateCapacity(slots)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return false;
        }
        if (currentAdena < fee || !player.reduceAdena(warehouse.getName(), fee, manager, false)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return false;
        }
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SendWareHouseDepositList.class);
    }
}
