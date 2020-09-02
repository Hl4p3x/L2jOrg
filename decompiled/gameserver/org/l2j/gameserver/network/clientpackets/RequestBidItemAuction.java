// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.auction.ItemAuction;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.GameClient;

public final class RequestBidItemAuction extends ClientPacket
{
    private int _instanceId;
    private long _bid;
    
    public void readImpl() {
        this._instanceId = this.readInt();
        this._bid = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("auction")) {
            activeChar.sendMessage("You are bidding too fast.");
            return;
        }
        if (this._bid < 0L || this._bid > Inventory.MAX_ADENA) {
            return;
        }
        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(this._instanceId);
        if (instance != null) {
            final ItemAuction auction = instance.getCurrentAuction();
            if (auction != null) {
                auction.registerBid(activeChar, this._bid);
            }
        }
    }
}
