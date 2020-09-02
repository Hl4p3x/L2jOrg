// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.item.container.ClanWarehouse;
import org.l2j.gameserver.model.item.container.PlayerWarehouse;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;

public final class SendWareHouseWithDrawList extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 12;
    private ItemHolder[] _items;
    
    public SendWareHouseWithDrawList() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
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
        if (this._items == null) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("withdraw")) {
            player.sendMessage("You are withdrawing items too fast.");
            return;
        }
        final ItemContainer warehouse = player.getActiveWarehouse();
        if (warehouse == null) {
            return;
        }
        final Npc manager = player.getLastFolkNPC();
        if ((manager == null || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM()) {
            return;
        }
        if (!(warehouse instanceof PlayerWarehouse) && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getReputation() < 0) {
            return;
        }
        if (Config.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH) {
            if (warehouse instanceof ClanWarehouse && !player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE)) {
                return;
            }
        }
        else if (warehouse instanceof ClanWarehouse && !player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);
            return;
        }
        int weight = 0;
        int slots = 0;
        for (final ItemHolder i : this._items) {
            final Item item = warehouse.getItemByObjectId(i.getId());
            if (item == null || item.getCount() < i.getCount()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                return;
            }
            weight += (int)(i.getCount() * item.getTemplate().getWeight());
            if (!item.isStackable()) {
                slots += (int)i.getCount();
            }
            else if (player.getInventory().getItemByItemId(item.getId()) == null) {
                ++slots;
            }
        }
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return;
        }
        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (final ItemHolder j : this._items) {
            final Item oldItem = warehouse.getItemByObjectId(j.getId());
            if (oldItem == null || oldItem.getCount() < j.getCount()) {
                SendWareHouseWithDrawList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                return;
            }
            final Item newItem = warehouse.transferItem(warehouse.getName(), j.getId(), j.getCount(), player.getInventory(), player, manager);
            if (newItem == null) {
                SendWareHouseWithDrawList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                return;
            }
            if (playerIU != null) {
                if (newItem.getCount() > j.getCount()) {
                    playerIU.addModifiedItem(newItem);
                }
                else {
                    playerIU.addNewItem(newItem);
                }
            }
        }
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        }
        else {
            player.sendItemList();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SendWareHouseWithDrawList.class);
    }
}
