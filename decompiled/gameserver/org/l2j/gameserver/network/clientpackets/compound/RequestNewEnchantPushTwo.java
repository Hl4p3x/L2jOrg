// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantTwoOK;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantTwoFail;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantPushTwo extends ClientPacket
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
        if (activeChar.isInStoreMode()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        final CompoundRequest request = activeChar.getRequest(CompoundRequest.class);
        if (request == null || request.isProcessing()) {
            ((GameClient)this.client).sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }
        request.setItemTwo(this._objectId);
        final Item itemOne = request.getItemOne();
        final Item itemTwo = request.getItemTwo();
        if (itemOne == null || itemTwo == null) {
            ((GameClient)this.client).sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }
        if (itemOne.getObjectId() == itemTwo.getObjectId() && itemOne.getCount() < 2L) {
            ((GameClient)this.client).sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }
        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
        if (combinationItem == null) {
            ((GameClient)this.client).sendPacket(ExEnchantTwoFail.STATIC_PACKET);
            return;
        }
        ((GameClient)this.client).sendPacket(ExEnchantTwoOK.STATIC_PACKET);
    }
}
