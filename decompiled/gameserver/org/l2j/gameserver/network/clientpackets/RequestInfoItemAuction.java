// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.auction.ItemAuction;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExItemAuctionInfoPacket;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestInfoItemAuction extends ClientPacket
{
    private int _instanceId;
    
    public void readImpl() {
        this._instanceId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getItemAuction().tryPerformAction("RequestInfoItemAuction")) {
            return;
        }
        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(this._instanceId);
        if (instance == null) {
            return;
        }
        final ItemAuction auction = instance.getCurrentAuction();
        if (auction == null) {
            return;
        }
        activeChar.updateLastItemAuctionRequest();
        ((GameClient)this.client).sendPacket(new ExItemAuctionInfoPacket(true, auction, instance.getNextAuction()));
    }
}
