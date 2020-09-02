// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.options.Variation;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVariationResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.network.GameClient;

public final class RequestRefine extends AbstractRefinePacket
{
    private int _targetItemObjId;
    private int _mineralItemObjId;
    private int _feeItemObjId;
    private long _feeCount;
    
    public void readImpl() {
        this._targetItemObjId = this.readInt();
        this._mineralItemObjId = this.readInt();
        this._feeItemObjId = this.readInt();
        this._feeCount = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item targetItem = activeChar.getInventory().getItemByObjectId(this._targetItemObjId);
        if (targetItem == null) {
            return;
        }
        final Item mineralItem = activeChar.getInventory().getItemByObjectId(this._mineralItemObjId);
        if (mineralItem == null) {
            return;
        }
        final Item feeItem = activeChar.getInventory().getItemByObjectId(this._feeItemObjId);
        if (feeItem == null) {
            return;
        }
        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), mineralItem.getId());
        if (!AbstractRefinePacket.isValid(activeChar, targetItem, mineralItem, feeItem, fee)) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }
        if (this._feeCount != fee.getItemCount()) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }
        final Variation variation = VariationData.getInstance().getVariation(mineralItem.getId());
        if (variation == null) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            return;
        }
        final VariationInstance augment = VariationData.getInstance().generateRandomVariation(variation, targetItem);
        if (augment == null) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            return;
        }
        final InventoryUpdate iu = new InventoryUpdate();
        if (targetItem.isEquipped()) {
            final Set<Item> unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(targetItem.getLocationSlot()));
            for (final Item itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            activeChar.broadcastUserInfo();
        }
        if (!activeChar.destroyItem("RequestRefine", mineralItem, 1L, null, false)) {
            return;
        }
        if (!activeChar.destroyItem("RequestRefine", feeItem, this._feeCount, null, false)) {
            return;
        }
        targetItem.setAugmentation(augment, true);
        activeChar.sendPacket(new ExVariationResult(augment.getOption1Id(), augment.getOption2Id(), true));
        iu.addModifiedItem(targetItem);
        activeChar.sendInventoryUpdate(iu);
    }
}
