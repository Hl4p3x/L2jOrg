// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantTwoRemoveOK;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantTwoRemoveFail;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantRemoveTwo extends ClientPacket
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
            ((GameClient)this.client).sendPacket(ExEnchantTwoRemoveFail.STATIC_PACKET);
            return;
        }
        final Item item = request.getItemTwo();
        if (item == null || item.getObjectId() != this._objectId) {
            ((GameClient)this.client).sendPacket(ExEnchantTwoRemoveFail.STATIC_PACKET);
            return;
        }
        request.setItemTwo(0);
        ((GameClient)this.client).sendPacket(ExEnchantTwoRemoveOK.STATIC_PACKET);
    }
}
