// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPutItemResultForVariationMake;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.network.GameClient;

public final class RequestConfirmTargetItem extends AbstractRefinePacket
{
    private int _itemObjId;
    
    public void readImpl() {
        this._itemObjId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(this._itemObjId);
        if (item == null) {
            return;
        }
        if (!VariationData.getInstance().hasFeeData(item.getId())) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        if (AbstractRefinePacket.isValid(activeChar, item)) {
            ((GameClient)this.client).sendPacket(new ExPutItemResultForVariationMake(this._itemObjId, item.getId()));
            return;
        }
        if (item.isAugmented()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
            return;
        }
        ((GameClient)this.client).sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
    }
}
