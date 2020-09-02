// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.StatsSet;

public final class AuctionItem
{
    private final int _auctionItemId;
    private final int _auctionLength;
    private final long _auctionInitBid;
    private final int _itemId;
    private final long _itemCount;
    private final StatsSet _itemExtra;
    
    public AuctionItem(final int auctionItemId, final int auctionLength, final long auctionInitBid, final int itemId, final long itemCount, final StatsSet itemExtra) {
        this._auctionItemId = auctionItemId;
        this._auctionLength = auctionLength;
        this._auctionInitBid = auctionInitBid;
        this._itemId = itemId;
        this._itemCount = itemCount;
        this._itemExtra = itemExtra;
    }
    
    public final boolean checkItemExists() {
        return ItemEngine.getInstance().getTemplate(this._itemId) != null;
    }
    
    public final int getAuctionItemId() {
        return this._auctionItemId;
    }
    
    public final int getAuctionLength() {
        return this._auctionLength;
    }
    
    public final long getAuctionInitBid() {
        return this._auctionInitBid;
    }
    
    public final int getItemId() {
        return this._itemId;
    }
    
    public final long getItemCount() {
        return this._itemCount;
    }
    
    public final Item createNewItemInstance() {
        final Item item = new Item(IdFactory.getInstance().getNextId(), this._itemId);
        World.getInstance().addObject(item);
        item.setCount(this._itemCount);
        return item;
    }
}
