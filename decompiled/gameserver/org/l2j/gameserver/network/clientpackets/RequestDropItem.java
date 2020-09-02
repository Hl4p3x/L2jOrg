// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestDropItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private long _count;
    private int _x;
    private int _y;
    private int _z;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._count = this.readLong();
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.isDead()) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getDropItem().tryPerformAction("drop item")) {
            return;
        }
        final Item item = player.getInventory().getItemByObjectId(this._objectId);
        if (item == null || this._count == 0L || !player.validateItemManipulation(this._objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) || (!item.isDropable() && (!player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) || !Config.GM_TRADE_RESTRICTED_ITEMS)) || (item.getItemType() == EtcItemType.PET_COLLAR && player.havePetInvItems()) || player.isInsideZone(ZoneType.NO_ITEM_DROP)) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (item.isQuestItem() && (!player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) || !Config.GM_TRADE_RESTRICTED_ITEMS)) {
            return;
        }
        if (this._count > item.getCount()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (Config.PLAYER_SPAWN_PROTECTION > 0 && player.isInvul() && !player.isGM()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (this._count < 0L) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, player.getName(), player.getAccountName(), this._objectId));
            return;
        }
        if (!item.isStackable() && this._count > 1L) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, player.getName(), player.getAccountName(), this._objectId));
            return;
        }
        if (Config.JAIL_DISABLE_TRANSACTION && player.isJailed()) {
            player.sendMessage("You cannot drop items in Jail.");
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }
        if (player.isProcessingTransaction() || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return;
        }
        if (player.isFlying()) {
            return;
        }
        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }
        if (player.isCastingNow()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (3 == item.getTemplate().getType2() && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) {
            player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED);
            return;
        }
        if (!MathUtil.isInsideRadius2D(player, this._x, this._y, 150) || Math.abs(this._z - player.getZ()) > 50) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }
        if (player.getInventory().isBlocked(item)) {
            player.sendMessage("You cannot use this item.");
            return;
        }
        if (item.isEquipped()) {
            player.getInventory().unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
            player.broadcastUserInfo();
            player.sendItemList();
        }
        final Item dropedItem = player.dropItem("Drop", this._objectId, this._count, this._x, this._y, this._z, null, false, false);
        if (player.isGM()) {
            final String target = (player.getTarget() != null) ? player.getTarget().getName() : "no-target";
            GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), player.getObjectId()), "Drop", target, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;IIII)Ljava/lang/String;, dropedItem.getId(), dropedItem.getItemName(), dropedItem.getObjectId(), player.getX(), player.getY(), player.getZ()));
        }
        if (dropedItem != null && dropedItem.getId() == 57 && dropedItem.getCount() >= 1000000L) {
            final String msg = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;JIII)Ljava/lang/String;, player.getName(), dropedItem.getCount(), this._x, this._y, this._z);
            RequestDropItem.LOGGER.warn(msg);
            AdminData.getInstance().broadcastMessageToGMs(msg);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestDropItem.class);
    }
}
