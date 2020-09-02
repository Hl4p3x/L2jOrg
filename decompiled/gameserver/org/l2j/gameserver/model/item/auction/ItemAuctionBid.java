// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;

public final class ItemAuctionBid
{
    private final int _playerObjId;
    private long _lastBid;
    
    public ItemAuctionBid(final int playerObjId, final long lastBid) {
        this._playerObjId = playerObjId;
        this._lastBid = lastBid;
    }
    
    public final int getPlayerObjId() {
        return this._playerObjId;
    }
    
    public final long getLastBid() {
        return this._lastBid;
    }
    
    final void setLastBid(final long lastBid) {
        this._lastBid = lastBid;
    }
    
    final void cancelBid() {
        this._lastBid = -1L;
    }
    
    final boolean isCanceled() {
        return this._lastBid <= 0L;
    }
    
    final Player getPlayer() {
        return World.getInstance().findPlayer(this._playerObjId);
    }
}
