// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.combination.CombinationItemReward;
import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantSucess;
import org.l2j.gameserver.model.item.combination.CombinationItemType;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantFail;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantTry extends ClientPacket
{
    public void readImpl() {
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
            ((GameClient)this.client).sendPacket(ExEnchantFail.STATIC_PACKET);
            return;
        }
        request.setProcessing(true);
        final Item itemOne = request.getItemOne();
        final Item itemTwo = request.getItemTwo();
        if (itemOne == null || itemTwo == null) {
            ((GameClient)this.client).sendPacket(ExEnchantFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        if (itemOne.getObjectId() == itemTwo.getObjectId() && itemOne.getCount() < 2L) {
            ((GameClient)this.client).sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }
        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
        if (combinationItem == null) {
            ((GameClient)this.client).sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(itemOne);
        iu.addRemovedItem(itemTwo);
        if (activeChar.destroyItem("Compound-Item-One", itemOne, 1L, null, true) && activeChar.destroyItem("Compound-Item-Two", itemTwo, 1L, null, true)) {
            final double random = Rnd.nextDouble() * 100.0;
            final boolean success = random <= combinationItem.getChance();
            final CombinationItemReward rewardItem = combinationItem.getReward(success ? CombinationItemType.ON_SUCCESS : CombinationItemType.ON_FAILURE);
            final Item item = activeChar.addItem("Compound-Result", rewardItem.getId(), rewardItem.getCount(), null, true);
            if (success) {
                ((GameClient)this.client).sendPacket(new ExEnchantSucess(item.getId()));
            }
            else {
                ((GameClient)this.client).sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            }
        }
        activeChar.sendInventoryUpdate(iu);
        activeChar.removeRequest(request.getClass());
    }
}
