// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.model.item.combination.CombinationItem;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneOK;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestNewEnchantPushOne extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.isInStoreMode()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        if (player.isProcessingTransaction() || player.isProcessingRequest()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        final CompoundRequest request = new CompoundRequest(player);
        if (!player.addRequest(request)) {
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }
        request.setItemOne(this._objectId);
        final Item itemOne = request.getItemOne();
        if (itemOne == null) {
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            player.removeRequest(request.getClass());
            return;
        }
        final List<CombinationItem> combinationItems = CombinationItemsManager.getInstance().getItemsByFirstSlot(itemOne.getId());
        if (combinationItems.isEmpty()) {
            ((GameClient)this.client).sendPacket(ExEnchantOneFail.STATIC_PACKET);
            player.removeRequest(request.getClass());
            return;
        }
        ((GameClient)this.client).sendPacket(ExEnchantOneOK.STATIC_PACKET);
    }
}
