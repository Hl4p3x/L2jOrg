// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.skills.SkillCaster;
import java.util.Iterator;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestDestroyItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private long _count;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._count = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._count <= 0L) {
            if (this._count < 0L) {
                GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName(), this._objectId));
            }
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("destroy")) {
            activeChar.sendMessage("You are destroying items too fast.");
            return;
        }
        long count = this._count;
        if (activeChar.isProcessingTransaction() || activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            ((GameClient)this.client).sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }
        final Item itemToRemove = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (itemToRemove == null) {
            if (activeChar.isGM()) {
                final WorldObject obj = World.getInstance().findObject(this._objectId);
                if (GameUtils.isItem(obj)) {
                    if (this._count > ((Item)obj).getCount()) {
                        count = ((Item)obj).getCount();
                    }
                    AdminCommandHandler.getInstance().useAdminCommand(activeChar, invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, this._objectId, count), true);
                }
                return;
            }
            ((GameClient)this.client).sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
        }
        else {
            if (activeChar.isCastingNow(s -> s.getSkill().getItemConsumeId() == itemToRemove.getId())) {
                ((GameClient)this.client).sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
                return;
            }
            final int itemId = itemToRemove.getId();
            if (!Config.DESTROY_ALL_ITEMS && !activeChar.canOverrideCond(PcCondOverride.DESTROY_ALL_ITEMS) && !itemToRemove.isDestroyable()) {
                if (itemToRemove.isHeroItem()) {
                    ((GameClient)this.client).sendPacket(SystemMessageId.HERO_WEAPONS_CANNOT_BE_DESTROYED);
                }
                else {
                    ((GameClient)this.client).sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
                }
                return;
            }
            if (!itemToRemove.isStackable() && count > 1L) {
                GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getAccountName(), this._objectId));
                return;
            }
            if (activeChar.getInventory().isBlocked(itemToRemove)) {
                activeChar.sendMessage("You cannot use this item.");
                return;
            }
            if (this._count > itemToRemove.getCount()) {
                count = itemToRemove.getCount();
            }
            if (itemToRemove.getTemplate().isPetItem()) {
                final Summon pet = activeChar.getPet();
                if (pet != null && pet.getControlObjectId() == this._objectId) {
                    pet.unSummon(activeChar);
                }
                try {
                    final Connection con = DatabaseFactory.getInstance().getConnection();
                    try {
                        final PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
                        try {
                            statement.setInt(1, this._objectId);
                            statement.execute();
                            if (statement != null) {
                                statement.close();
                            }
                        }
                        catch (Throwable t) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                        if (con != null) {
                            con.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (con != null) {
                            try {
                                con.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                }
                catch (Exception e) {
                    RequestDestroyItem.LOGGER.warn("could not delete pet objectid: ", (Throwable)e);
                }
            }
            if (itemToRemove.isTimeLimitedItem()) {
                itemToRemove.endOfLife();
            }
            if (itemToRemove.isEquipped()) {
                if (itemToRemove.getEnchantLevel() > 0) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                    sm.addInt(itemToRemove.getEnchantLevel());
                    sm.addItemName(itemToRemove);
                    ((GameClient)this.client).sendPacket(sm);
                }
                else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                    sm.addItemName(itemToRemove);
                    ((GameClient)this.client).sendPacket(sm);
                }
                final Set<Item> unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(itemToRemove.getLocationSlot()));
                final InventoryUpdate iu = new InventoryUpdate();
                for (final Item itm : unequiped) {
                    iu.addModifiedItem(itm);
                }
                activeChar.sendInventoryUpdate(iu);
            }
            final Item removedItem = activeChar.getInventory().destroyItem("Destroy", itemToRemove, count, activeChar, null);
            if (removedItem == null) {
                return;
            }
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                if (removedItem.getCount() == 0L) {
                    iu.addRemovedItem(removedItem);
                }
                else {
                    iu.addModifiedItem(removedItem);
                }
                activeChar.sendInventoryUpdate(iu);
            }
            else {
                activeChar.sendItemList();
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestDestroyItem.class);
    }
}
