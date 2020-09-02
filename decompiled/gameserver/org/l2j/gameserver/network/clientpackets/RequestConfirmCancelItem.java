// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public final class RequestConfirmCancelItem extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (item == null) {
            return;
        }
        if (item.getOwnerId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(((GameClient)this.client).getPlayer(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((GameClient)this.client).getPlayer().getName(), ((GameClient)this.client).getPlayer().getAccountName()));
            return;
        }
        if (!item.isAugmented()) {
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            return;
        }
        if (item.isPvp() && !Config.ALT_ALLOW_AUGMENT_PVP_ITEMS) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        final long price = VariationData.getInstance().getCancelFee(item.getId(), item.getAugmentation().getMineralId());
        if (price < 0L) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        activeChar.sendPacket(new ExPutItemResultForVariationCancel(item, price));
    }
}
