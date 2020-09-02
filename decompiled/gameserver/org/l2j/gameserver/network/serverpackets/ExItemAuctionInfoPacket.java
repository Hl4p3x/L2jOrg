// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.auction.ItemAuctionBid;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.auction.ItemAuctionState;
import org.l2j.gameserver.model.item.auction.ItemAuction;

public final class ExItemAuctionInfoPacket extends AbstractItemPacket
{
    private final boolean _refresh;
    private final int _timeRemaining;
    private final ItemAuction _currentAuction;
    private final ItemAuction _nextAuction;
    
    public ExItemAuctionInfoPacket(final boolean refresh, final ItemAuction currentAuction, final ItemAuction nextAuction) {
        if (currentAuction == null) {
            throw new NullPointerException();
        }
        if (currentAuction.getAuctionState() != ItemAuctionState.STARTED) {
            this._timeRemaining = 0;
        }
        else {
            this._timeRemaining = (int)(currentAuction.getFinishingTimeRemaining() / 1000L);
        }
        this._refresh = refresh;
        this._currentAuction = currentAuction;
        this._nextAuction = nextAuction;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ITEM_AUCTION_INFO);
        this.writeByte((byte)(byte)(this._refresh ? 0 : 1));
        this.writeInt(this._currentAuction.getInstanceId());
        final ItemAuctionBid highestBid = this._currentAuction.getHighestBid();
        this.writeLong((highestBid != null) ? highestBid.getLastBid() : this._currentAuction.getAuctionInitBid());
        this.writeInt(this._timeRemaining);
        this.writeItem(this._currentAuction.getItemInfo());
        if (this._nextAuction != null) {
            this.writeLong(this._nextAuction.getAuctionInitBid());
            this.writeInt((int)(this._nextAuction.getStartingTime() / 1000L));
            this.writeItem(this._nextAuction.getItemInfo());
        }
    }
}
