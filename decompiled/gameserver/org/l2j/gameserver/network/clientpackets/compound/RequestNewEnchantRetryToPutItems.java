// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantRetryToPutItems extends ClientPacket
{
    private int _firstItemObjectId;
    private int _secondItemObjectId;
    
    public void readImpl() {
        this._firstItemObjectId = this.readInt();
        this._secondItemObjectId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isInStoreMode()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }
        if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }
        final CompoundRequest request = new CompoundRequest(activeChar);
        if (!activeChar.addRequest(request)) {
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }
        request.setItemOne(this._firstItemObjectId);
        final Item itemOne = request.getItemOne();
        if (itemOne == null) {
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        request.setItemTwo(this._secondItemObjectId);
        final Item itemTwo = request.getItemTwo();
        if (itemTwo == null) {
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
        if (combinationItem == null) {
            ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        ((GameClient)this.client).sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
    }
}
