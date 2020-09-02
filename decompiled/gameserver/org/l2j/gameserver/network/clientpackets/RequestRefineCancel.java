// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExVariationCancelResult;
import org.l2j.gameserver.network.GameClient;

public final class RequestRefineCancel extends ClientPacket
{
    private int _targetItemObjId;
    
    public void readImpl() {
        this._targetItemObjId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item targetItem = activeChar.getInventory().getItemByObjectId(this._targetItemObjId);
        if (targetItem == null) {
            ((GameClient)this.client).sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }
        if (targetItem.getOwnerId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(((GameClient)this.client).getPlayer(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((GameClient)this.client).getPlayer().getName(), ((GameClient)this.client).getPlayer().getAccountName()));
            return;
        }
        if (!targetItem.isAugmented()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            ((GameClient)this.client).sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }
        final long price = VariationData.getInstance().getCancelFee(targetItem.getId(), targetItem.getAugmentation().getMineralId());
        if (price < 0L) {
            ((GameClient)this.client).sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }
        if (!activeChar.reduceAdena("RequestRefineCancel", price, targetItem, true)) {
            ((GameClient)this.client).sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }
        if (targetItem.isEquipped()) {
            activeChar.disarmWeapons();
        }
        targetItem.removeAugmentation();
        ((GameClient)this.client).sendPacket(ExVariationCancelResult.STATIC_PACKET_SUCCESS);
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(targetItem);
        activeChar.sendInventoryUpdate(iu);
    }
}
