// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestCrystallizeItem extends ClientPacket
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
            RequestCrystallizeItem.LOGGER.debug("RequestCrystalizeItem: activeChar was null");
            return;
        }
        if (this._count <= 0L) {
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this._objectId, activeChar.getName()));
            return;
        }
        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE || !activeChar.isInCrystallize()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        final int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
        if (skillLevel <= 0) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            if (activeChar.getRace() != Race.DWARF && activeChar.getClassId().getId() != 117 && activeChar.getClassId().getId() != 55) {
                RequestCrystallizeItem.LOGGER.info("Player {} used crystalize with classid: {}", (Object)activeChar, (Object)activeChar.getClassId().getId());
            }
            return;
        }
        final PlayerInventory inventory = activeChar.getInventory();
        if (inventory != null) {
            final Item item = inventory.getItemByObjectId(this._objectId);
            if (item == null || item.isHeroItem()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (this._count > item.getCount()) {
                this._count = activeChar.getInventory().getItemByObjectId(this._objectId).getCount();
            }
        }
        final Item itemToRemove = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (itemToRemove == null || itemToRemove.isTimeLimitedItem()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!itemToRemove.getTemplate().isCrystallizable() || itemToRemove.getTemplate().getCrystalCount() <= 0 || itemToRemove.getTemplate().getCrystalType() == CrystalType.NONE) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
            return;
        }
        if (activeChar.getInventory().isBlocked(itemToRemove)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
            return;
        }
        boolean canCrystallize = true;
        switch (itemToRemove.getTemplate().getCrystalType()) {
            case D: {
                if (skillLevel < 1) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case C: {
                if (skillLevel < 2) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case B: {
                if (skillLevel < 3) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case A: {
                if (skillLevel < 4) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
            case S: {
                if (skillLevel < 5) {
                    canCrystallize = false;
                    break;
                }
                break;
            }
        }
        if (!canCrystallize) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(itemToRemove);
        if (crystallizationRewards == null || crystallizationRewards.isEmpty()) {
            activeChar.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
            return;
        }
        if (itemToRemove.isEquipped()) {
            final Set<Item> unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(itemToRemove.getLocationSlot()));
            final InventoryUpdate iu = new InventoryUpdate();
            for (final Item item2 : unequiped) {
                iu.addModifiedItem(item2);
            }
            activeChar.sendInventoryUpdate(iu);
            SystemMessage sm;
            if (itemToRemove.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(itemToRemove.getEnchantLevel());
                sm.addItemName(itemToRemove);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                sm.addItemName(itemToRemove);
            }
            ((GameClient)this.client).sendPacket(sm);
        }
        final Item removedItem = activeChar.getInventory().destroyItem("Crystalize", this._objectId, this._count, activeChar, null);
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(removedItem);
        activeChar.sendInventoryUpdate(iu);
        for (final ItemChanceHolder holder : crystallizationRewards) {
            final double rand = Rnd.nextDouble() * 100.0;
            if (rand < holder.getChance()) {
                final Item createdItem = activeChar.getInventory().addItem("Crystalize", holder.getId(), holder.getCount(), activeChar, activeChar);
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(createdItem);
                sm.addLong(holder.getCount());
                ((GameClient)this.client).sendPacket(sm);
            }
        }
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_CRYSTALLIZED);
        sm.addItemName(removedItem);
        ((GameClient)this.client).sendPacket(sm);
        activeChar.broadcastUserInfo();
        activeChar.setInCrystallize(false);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestCrystallizeItem.class);
    }
}
