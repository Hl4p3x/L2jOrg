// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import java.util.concurrent.TimeUnit;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.ItemInfo;
import java.util.ArrayList;
import org.slf4j.Logger;

public final class ItemAuction
{
    private static final Logger LOGGER;
    private static final long ENDING_TIME_EXTEND_5;
    private static final long ENDING_TIME_EXTEND_3;
    private static final String DELETE_ITEM_AUCTION_BID = "DELETE FROM item_auction_bid WHERE auctionId = ? AND playerObjId = ?";
    private static final String INSERT_ITEM_AUCTION_BID = "INSERT INTO item_auction_bid (auctionId, playerObjId, playerBid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playerBid = ?";
    private final int _auctionId;
    private final int _instanceId;
    private final long _startingTime;
    private final AuctionItem _auctionItem;
    private final ArrayList<ItemAuctionBid> _auctionBids;
    private final Object _auctionStateLock;
    private final ItemInfo _itemInfo;
    private volatile long _endingTime;
    private volatile ItemAuctionState _auctionState;
    private volatile ItemAuctionExtendState _scheduledAuctionEndingExtendState;
    private volatile ItemAuctionExtendState _auctionEndingExtendState;
    private ItemAuctionBid _highestBid;
    private int _lastBidPlayerObjId;
    
    public ItemAuction(final int auctionId, final int instanceId, final long startingTime, final long endingTime, final AuctionItem auctionItem) {
        this(auctionId, instanceId, startingTime, endingTime, auctionItem, new ArrayList<ItemAuctionBid>(), ItemAuctionState.CREATED);
    }
    
    public ItemAuction(final int auctionId, final int instanceId, final long startingTime, final long endingTime, final AuctionItem auctionItem, final ArrayList<ItemAuctionBid> auctionBids, final ItemAuctionState auctionState) {
        this._auctionId = auctionId;
        this._instanceId = instanceId;
        this._startingTime = startingTime;
        this._endingTime = endingTime;
        this._auctionItem = auctionItem;
        this._auctionBids = auctionBids;
        this._auctionState = auctionState;
        this._auctionStateLock = new Object();
        this._scheduledAuctionEndingExtendState = ItemAuctionExtendState.INITIAL;
        this._auctionEndingExtendState = ItemAuctionExtendState.INITIAL;
        final Item item = this._auctionItem.createNewItemInstance();
        this._itemInfo = new ItemInfo(item);
        World.getInstance().removeObject(item);
        for (final ItemAuctionBid bid : this._auctionBids) {
            if (this._highestBid == null || this._highestBid.getLastBid() < bid.getLastBid()) {
                this._highestBid = bid;
            }
        }
    }
    
    public final ItemAuctionState getAuctionState() {
        final ItemAuctionState auctionState;
        synchronized (this._auctionStateLock) {
            auctionState = this._auctionState;
        }
        return auctionState;
    }
    
    public final boolean setAuctionState(final ItemAuctionState expected, final ItemAuctionState wanted) {
        synchronized (this._auctionStateLock) {
            if (this._auctionState != expected) {
                return false;
            }
            this._auctionState = wanted;
            this.storeMe();
            return true;
        }
    }
    
    public final int getAuctionId() {
        return this._auctionId;
    }
    
    public final int getInstanceId() {
        return this._instanceId;
    }
    
    public final ItemInfo getItemInfo() {
        return this._itemInfo;
    }
    
    public final Item createNewItemInstance() {
        return this._auctionItem.createNewItemInstance();
    }
    
    public final long getAuctionInitBid() {
        return this._auctionItem.getAuctionInitBid();
    }
    
    public final ItemAuctionBid getHighestBid() {
        return this._highestBid;
    }
    
    public final ItemAuctionExtendState getAuctionEndingExtendState() {
        return this._auctionEndingExtendState;
    }
    
    public final ItemAuctionExtendState getScheduledAuctionEndingExtendState() {
        return this._scheduledAuctionEndingExtendState;
    }
    
    public final void setScheduledAuctionEndingExtendState(final ItemAuctionExtendState state) {
        this._scheduledAuctionEndingExtendState = state;
    }
    
    public final long getStartingTime() {
        return this._startingTime;
    }
    
    public final long getEndingTime() {
        return this._endingTime;
    }
    
    public final long getStartingTimeRemaining() {
        return Math.max(this._endingTime - System.currentTimeMillis(), 0L);
    }
    
    public final long getFinishingTimeRemaining() {
        return Math.max(this._endingTime - System.currentTimeMillis(), 0L);
    }
    
    public final void storeMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO item_auction (auctionId,instanceId,auctionItemId,startingTime,endingTime,auctionStateId) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE auctionStateId=?");
                try {
                    statement.setInt(1, this._auctionId);
                    statement.setInt(2, this._instanceId);
                    statement.setInt(3, this._auctionItem.getAuctionItemId());
                    statement.setLong(4, this._startingTime);
                    statement.setLong(5, this._endingTime);
                    statement.setByte(6, this._auctionState.getStateId());
                    statement.setByte(7, this._auctionState.getStateId());
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            ItemAuction.LOGGER.warn("", (Throwable)e);
        }
    }
    
    public final int getAndSetLastBidPlayerObjectId(final int playerObjId) {
        final int lastBid = this._lastBidPlayerObjId;
        this._lastBidPlayerObjId = playerObjId;
        return lastBid;
    }
    
    private void updatePlayerBid(final ItemAuctionBid bid, final boolean delete) {
        this.updatePlayerBidInternal(bid, delete);
    }
    
    final void updatePlayerBidInternal(final ItemAuctionBid bid, final boolean delete) {
        final String query = delete ? "DELETE FROM item_auction_bid WHERE auctionId = ? AND playerObjId = ?" : "INSERT INTO item_auction_bid (auctionId, playerObjId, playerBid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playerBid = ?";
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement(query);
                try {
                    ps.setInt(1, this._auctionId);
                    ps.setInt(2, bid.getPlayerObjId());
                    if (!delete) {
                        ps.setLong(3, bid.getLastBid());
                        ps.setLong(4, bid.getLastBid());
                    }
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            ItemAuction.LOGGER.warn("", (Throwable)e);
        }
    }
    
    public final void registerBid(final Player player, final long newBid) {
        if (player == null) {
            throw new NullPointerException();
        }
        if (newBid < this._auctionItem.getAuctionInitBid()) {
            player.sendPacket(SystemMessageId.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            return;
        }
        if (newBid > 100000000000L) {
            player.sendPacket(SystemMessageId.THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID);
            return;
        }
        if (this.getAuctionState() != ItemAuctionState.STARTED) {
            return;
        }
        final int playerObjId = player.getObjectId();
        synchronized (this._auctionBids) {
            if (this._highestBid != null && newBid < this._highestBid.getLastBid()) {
                player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
                return;
            }
            ItemAuctionBid bid = this.getBidFor(playerObjId);
            if (bid == null) {
                if (!this.reduceItemCount(player, newBid)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                    return;
                }
                bid = new ItemAuctionBid(playerObjId, newBid);
                this._auctionBids.add(bid);
            }
            else {
                if (!bid.isCanceled()) {
                    if (newBid < bid.getLastBid()) {
                        player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
                        return;
                    }
                    if (!this.reduceItemCount(player, newBid - bid.getLastBid())) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                        return;
                    }
                }
                else if (!this.reduceItemCount(player, newBid)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                    return;
                }
                bid.setLastBid(newBid);
            }
            this.onPlayerBid(player, bid);
            this.updatePlayerBid(bid, false);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUBMITTED_A_BID_FOR_THE_AUCTION_OF_S1);
            sm.addLong(newBid);
            player.sendPacket(sm);
        }
    }
    
    private void onPlayerBid(final Player player, final ItemAuctionBid bid) {
        if (this._highestBid == null) {
            this._highestBid = bid;
        }
        else if (this._highestBid.getLastBid() < bid.getLastBid()) {
            final Player old = this._highestBid.getPlayer();
            if (old != null) {
                old.sendPacket(SystemMessageId.YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA);
            }
            this._highestBid = bid;
        }
        if (this._endingTime - System.currentTimeMillis() <= 600000L) {
            switch (this._auctionEndingExtendState) {
                case INITIAL: {
                    this._auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_5_MIN;
                    this._endingTime += ItemAuction.ENDING_TIME_EXTEND_5;
                    this.broadcastToAllBidders(SystemMessage.getSystemMessage(SystemMessageId.BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES));
                    break;
                }
                case EXTEND_BY_5_MIN: {
                    if (this.getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                        this._auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_3_MIN;
                        this._endingTime += ItemAuction.ENDING_TIME_EXTEND_3;
                        this.broadcastToAllBidders(SystemMessage.getSystemMessage(SystemMessageId.BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES));
                        break;
                    }
                    break;
                }
                case EXTEND_BY_3_MIN: {
                    if (Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID > 0L && this.getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                        this._auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
                        this._endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
                        break;
                    }
                    break;
                }
                case EXTEND_BY_CONFIG_PHASE_A: {
                    if (this.getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId() && this._scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B) {
                        this._auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B;
                        this._endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
                        break;
                    }
                    break;
                }
                case EXTEND_BY_CONFIG_PHASE_B: {
                    if (this.getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId() && this._scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A) {
                        this._endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
                        this._auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public final void broadcastToAllBidders(final ServerPacket packet) {
        ThreadPool.execute(() -> this.broadcastToAllBiddersInternal(packet));
    }
    
    public final void broadcastToAllBiddersInternal(final ServerPacket packet) {
        int i = this._auctionBids.size();
        while (i-- > 0) {
            final ItemAuctionBid bid = this._auctionBids.get(i);
            if (bid != null) {
                final Player player = bid.getPlayer();
                if (player == null) {
                    continue;
                }
                player.sendPacket(packet);
            }
        }
    }
    
    public final boolean cancelBid(final Player player) {
        if (player == null) {
            throw new NullPointerException();
        }
        switch (this.getAuctionState()) {
            case CREATED: {
                return false;
            }
            case FINISHED: {
                if (this._startingTime < System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(Config.ALT_ITEM_AUCTION_EXPIRED_AFTER, TimeUnit.DAYS)) {
                    return false;
                }
                break;
            }
        }
        final int playerObjId = player.getObjectId();
        synchronized (this._auctionBids) {
            if (this._highestBid == null) {
                return false;
            }
            final int bidIndex = this.getBidIndexFor(playerObjId);
            if (bidIndex == -1) {
                return false;
            }
            final ItemAuctionBid bid = this._auctionBids.get(bidIndex);
            if (bid.getPlayerObjId() == this._highestBid.getPlayerObjId()) {
                if (this.getAuctionState() == ItemAuctionState.FINISHED) {
                    return false;
                }
                player.sendPacket(SystemMessageId.YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_BUT_THE_RESERVE_HAS_NOT_BEEN_MET);
                return true;
            }
            else {
                if (bid.isCanceled()) {
                    return false;
                }
                this.increaseItemCount(player, bid.getLastBid());
                bid.cancelBid();
                this.updatePlayerBid(bid, this.getAuctionState() == ItemAuctionState.FINISHED);
                player.sendPacket(SystemMessageId.YOU_HAVE_CANCELED_YOUR_BID);
            }
        }
        return true;
    }
    
    public final void clearCanceledBids() {
        if (this.getAuctionState() != ItemAuctionState.FINISHED) {
            throw new IllegalStateException("Attempt to clear canceled bids for non-finished auction");
        }
        synchronized (this._auctionBids) {
            for (final ItemAuctionBid bid : this._auctionBids) {
                if (bid != null) {
                    if (!bid.isCanceled()) {
                        continue;
                    }
                    this.updatePlayerBid(bid, true);
                }
            }
        }
    }
    
    private boolean reduceItemCount(final Player player, final long count) {
        if (!player.reduceAdena("ItemAuction", count, player, true)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
            return false;
        }
        return true;
    }
    
    private void increaseItemCount(final Player player, final long count) {
        player.addAdena("ItemAuction", count, player, true);
    }
    
    public final long getLastBid(final Player player) {
        final ItemAuctionBid bid = this.getBidFor(player.getObjectId());
        return (bid != null) ? bid.getLastBid() : -1L;
    }
    
    public final ItemAuctionBid getBidFor(final int playerObjId) {
        final int index = this.getBidIndexFor(playerObjId);
        return (index != -1) ? this._auctionBids.get(index) : null;
    }
    
    private int getBidIndexFor(final int playerObjId) {
        int i = this._auctionBids.size();
        while (i-- > 0) {
            final ItemAuctionBid bid = this._auctionBids.get(i);
            if (bid != null && bid.getPlayerObjId() == playerObjId) {
                return i;
            }
        }
        return -1;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemAuctionManager.class);
        ENDING_TIME_EXTEND_5 = TimeUnit.MILLISECONDS.convert(5L, TimeUnit.MINUTES);
        ENDING_TIME_EXTEND_3 = TimeUnit.MILLISECONDS.convert(3L, TimeUnit.MINUTES);
    }
}
