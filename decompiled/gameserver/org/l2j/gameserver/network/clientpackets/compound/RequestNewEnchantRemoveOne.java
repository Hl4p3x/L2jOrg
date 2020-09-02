// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneRemoveOK;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneRemoveFail;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantRemoveOne extends ClientPacket
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
            ((GameClient)this.client).sendPacket(ExEnchantOneRemoveFail.STATIC_PACKET);
            return;
        }
        final Item item = request.getItemOne();
        if (item == null || item.getObjectId() != this._objectId) {
            ((GameClient)this.client).sendPacket(ExEnchantOneRemoveFail.STATIC_PACKET);
            return;
        }
        request.setItemOne(0);
        ((GameClient)this.client).sendPacket(ExEnchantOneRemoveOK.STATIC_PACKET);
    }
}
