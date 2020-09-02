// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPutCommissionResultForVariationMake;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.network.GameClient;

public final class RequestConfirmGemStone extends AbstractRefinePacket
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
        final Item refinerItem = activeChar.getInventory().getItemByObjectId(this._mineralItemObjId);
        if (refinerItem == null) {
            return;
        }
        final Item gemStoneItem = activeChar.getInventory().getItemByObjectId(this._feeItemObjId);
        if (gemStoneItem == null) {
            return;
        }
        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), refinerItem.getId());
        if (!AbstractRefinePacket.isValid(activeChar, targetItem, refinerItem, gemStoneItem, fee)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        if (this._feeCount != fee.getItemCount()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT);
            return;
        }
        ((GameClient)this.client).sendPacket(new ExPutCommissionResultForVariationMake(this._feeItemObjId, this._feeCount, gemStoneItem.getId()));
    }
}
