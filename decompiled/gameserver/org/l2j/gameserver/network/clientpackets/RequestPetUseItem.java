// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestPetUseItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || !activeChar.hasPet()) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getUseItem().tryPerformAction("pet use item")) {
            return;
        }
        final Pet pet = activeChar.getPet();
        final Item item = pet.getInventory().getItemByObjectId(this._objectId);
        if (item == null) {
            return;
        }
        if (!item.getTemplate().isForNpc()) {
            activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
            return;
        }
        if (activeChar.isAlikeDead() || pet.isDead()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addItemName(item);
            activeChar.sendPacket(sm);
            return;
        }
        final int reuseDelay = item.getReuseDelay();
        if (reuseDelay > 0) {
            final long reuse = pet.getItemRemainingReuseTime(item.getObjectId());
            if (reuse > 0L) {
                return;
            }
        }
        if (!item.isEquipped() && !item.getTemplate().checkCondition(pet, pet, true)) {
            return;
        }
        this.useItem(pet, item, activeChar);
    }
    
    private void useItem(final Pet pet, final Item item, final Player activeChar) {
        if (item.isEquipable()) {
            if (!item.getTemplate().isConditionAttached()) {
                activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                return;
            }
            if (item.isEquipped()) {
                pet.getInventory().unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
            }
            else {
                pet.getInventory().equipItem(item);
            }
            activeChar.sendPacket(new PetItemList(pet.getInventory().getItems()));
            pet.updateAndBroadcastStatus(1);
        }
        else {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
            if (handler != null) {
                if (handler.useItem(pet, item, false)) {
                    final int reuseDelay = item.getReuseDelay();
                    if (reuseDelay > 0) {
                        activeChar.addTimeStampItem(item, reuseDelay);
                    }
                    activeChar.sendPacket(new PetItemList(pet.getInventory().getItems()));
                    pet.updateAndBroadcastStatus(1);
                }
            }
            else {
                activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                RequestPetUseItem.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, item.getId()));
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPetUseItem.class);
    }
}
