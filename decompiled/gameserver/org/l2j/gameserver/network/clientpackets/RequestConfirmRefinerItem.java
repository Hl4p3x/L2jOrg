// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPutIntensiveResultForVariationMake;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.network.GameClient;

public class RequestConfirmRefinerItem extends AbstractRefinePacket
{
    private int _targetItemObjId;
    private int _refinerItemObjId;
    
    public void readImpl() {
        this._targetItemObjId = this.readInt();
        this._refinerItemObjId = this.readInt();
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
        final Item refinerItem = activeChar.getInventory().getItemByObjectId(this._refinerItemObjId);
        if (refinerItem == null) {
            return;
        }
        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), refinerItem.getId());
        if (fee == null || !AbstractRefinePacket.isValid(activeChar, targetItem, refinerItem)) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        activeChar.sendPacket(new ExPutIntensiveResultForVariationMake(this._refinerItemObjId, refinerItem.getId(), fee.getItemId(), fee.getItemCount()));
    }
}
