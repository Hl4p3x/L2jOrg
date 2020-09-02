// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.Config;
import java.util.concurrent.TimeUnit;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Collection;
import java.util.Date;
import org.l2j.commons.threading.ThreadPool;
import java.util.Arrays;
import java.util.Comparator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.sql.SQLException;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.StatsSet;
import java.util.HashMap;
import org.w3c.dom.Node;
import java.util.concurrent.ScheduledFuture;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;

public final class ItemAuctionInstance
{
    protected static final Logger LOGGER;
    private static final long START_TIME_SPACE;
    private static final long FINISH_TIME_SPACE;
    private static final String SELECT_AUCTION_ID_BY_INSTANCE_ID = "SELECT auctionId FROM item_auction WHERE instanceId = ?";
    private static final String SELECT_AUCTION_INFO = "SELECT auctionItemId, startingTime, endingTime, auctionStateId FROM item_auction WHERE auctionId = ? ";
    private static final String DELETE_AUCTION_INFO_BY_AUCTION_ID = "DELETE FROM item_auction WHERE auctionId = ?";
    private static final String DELETE_AUCTION_BID_INFO_BY_AUCTION_ID = "DELETE FROM item_auction_bid WHERE auctionId = ?";
    private static final String SELECT_PLAYERS_ID_BY_AUCTION_ID = "SELECT playerObjId, playerBid FROM item_auction_bid WHERE auctionId = ?";
    private final SimpleDateFormat DATE_FORMAT;
    private final int _instanceId;
    private final AtomicInteger _auctionIds;
    private final Map<Integer, ItemAuction> _auctions;
    private final ArrayList<AuctionItem> _items;
    private final AuctionDateGenerator _dateGenerator;
    private ItemAuction _currentAuction;
    private ItemAuction _nextAuction;
    private ScheduledFuture<?> _stateTask;
    
    public ItemAuctionInstance(final int instanceId, final AtomicInteger auctionIds, final Node node) throws Exception {
        this.DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
        this._instanceId = instanceId;
        this._auctionIds = auctionIds;
        this._auctions = new HashMap<Integer, ItemAuction>();
        this._items = new ArrayList<AuctionItem>();
        final NamedNodeMap nanode = node.getAttributes();
        final StatsSet generatorConfig = new StatsSet();
        int i = nanode.getLength();
        while (i-- > 0) {
            final Node n = nanode.item(i);
            if (n != null) {
                generatorConfig.set(n.getNodeName(), n.getNodeValue());
            }
        }
        this._dateGenerator = new AuctionDateGenerator(generatorConfig);
        for (Node na = node.getFirstChild(); na != null; na = na.getNextSibling()) {
            try {
                if ("item".equalsIgnoreCase(na.getNodeName())) {
                    final NamedNodeMap naa = na.getAttributes();
                    final int auctionItemId = Integer.parseInt(naa.getNamedItem("auctionItemId").getNodeValue());
                    final int auctionLenght = Integer.parseInt(naa.getNamedItem("auctionLenght").getNodeValue());
                    final long auctionInitBid = Integer.parseInt(naa.getNamedItem("auctionInitBid").getNodeValue());
                    final int itemId = Integer.parseInt(naa.getNamedItem("itemId").getNodeValue());
                    final int itemCount = Integer.parseInt(naa.getNamedItem("itemCount").getNodeValue());
                    if (auctionLenght < 1) {
                        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this._instanceId, itemId));
                    }
                    final StatsSet itemExtra = new StatsSet();
                    final AuctionItem item = new AuctionItem(auctionItemId, auctionLenght, auctionInitBid, itemId, itemCount, itemExtra);
                    if (!item.checkItemExists()) {
                        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId));
                    }
                    for (final AuctionItem tmp : this._items) {
                        if (tmp.getAuctionItemId() == auctionItemId) {
                            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, auctionItemId));
                        }
                    }
                    this._items.add(item);
                    for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling()) {
                        if ("extra".equalsIgnoreCase(nb.getNodeName())) {
                            final NamedNodeMap nab = nb.getAttributes();
                            int j = nab.getLength();
                            while (j-- > 0) {
                                final Node n2 = nab.item(j);
                                if (n2 != null) {
                                    itemExtra.set(n2.getNodeName(), n2.getNodeValue());
                                }
                            }
                        }
                    }
                }
            }
            catch (IllegalArgumentException e) {
                ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            }
        }
        if (this._items.isEmpty()) {
            throw new IllegalArgumentException("No items defined");
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT auctionId FROM item_auction WHERE instanceId = ?");
                try {
                    ps.setInt(1, this._instanceId);
                    final ResultSet rset = ps.executeQuery();
                    try {
                        while (rset.next()) {
                            final int auctionId = rset.getInt(1);
                            try {
                                final ItemAuction auction = this.loadAuction(auctionId);
                                if (auction != null) {
                                    this._auctions.put(auctionId, auction);
                                }
                                else {
                                    ItemAuctionManager.deleteAuction(auctionId);
                                }
                            }
                            catch (SQLException e2) {
                                ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), auctionId), (Throwable)e2);
                            }
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (SQLException e3) {
            ItemAuctionInstance.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e3);
            return;
        }
        ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getClass().getSimpleName(), this._items.size(), this._auctions.size(), this._instanceId));
        this.checkAndSetCurrentAndNextAuction();
    }
    
    public final ItemAuction getCurrentAuction() {
        return this._currentAuction;
    }
    
    public final ItemAuction getNextAuction() {
        return this._nextAuction;
    }
    
    public final void shutdown() {
        final ScheduledFuture<?> stateTask = this._stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
    }
    
    private AuctionItem getAuctionItem(final int auctionItemId) {
        int i = this._items.size();
        while (i-- > 0) {
            final AuctionItem item = this._items.get(i);
            if (item.getAuctionItemId() == auctionItemId) {
                return item;
            }
        }
        return null;
    }
    
    final void checkAndSetCurrentAndNextAuction() {
        final ItemAuction[] auctions = this._auctions.values().toArray(new ItemAuction[this._auctions.size()]);
        ItemAuction currentAuction = null;
        ItemAuction nextAuction = null;
        Label_0380: {
            switch (auctions.length) {
                case 0: {
                    nextAuction = this.createAuction(System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE);
                    break;
                }
                case 1: {
                    switch (auctions[0].getAuctionState()) {
                        case CREATED: {
                            if (auctions[0].getStartingTime() < System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE) {
                                currentAuction = auctions[0];
                                nextAuction = this.createAuction(System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE);
                                break Label_0380;
                            }
                            nextAuction = auctions[0];
                            break Label_0380;
                        }
                        case STARTED: {
                            currentAuction = auctions[0];
                            nextAuction = this.createAuction(Math.max(currentAuction.getEndingTime() + ItemAuctionInstance.FINISH_TIME_SPACE, System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE));
                            break Label_0380;
                        }
                        case FINISHED: {
                            currentAuction = auctions[0];
                            nextAuction = this.createAuction(System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE);
                            break Label_0380;
                        }
                        default: {
                            throw new IllegalArgumentException();
                        }
                    }
                    break;
                }
                default: {
                    Arrays.sort(auctions, Comparator.comparingLong(ItemAuction::getStartingTime).reversed());
                    final long currentTime = System.currentTimeMillis();
                    for (final ItemAuction auction : auctions) {
                        if (auction.getAuctionState() == ItemAuctionState.STARTED) {
                            currentAuction = auction;
                            break;
                        }
                        if (auction.getStartingTime() <= currentTime) {
                            currentAuction = auction;
                            break;
                        }
                    }
                    for (final ItemAuction auction : auctions) {
                        if (auction.getStartingTime() > currentTime && currentAuction != auction) {
                            nextAuction = auction;
                            break;
                        }
                    }
                    if (nextAuction == null) {
                        nextAuction = this.createAuction(System.currentTimeMillis() + ItemAuctionInstance.START_TIME_SPACE);
                        break;
                    }
                    break;
                }
            }
        }
        this._auctions.put(nextAuction.getAuctionId(), nextAuction);
        this._currentAuction = currentAuction;
        this._nextAuction = nextAuction;
        if (currentAuction != null && currentAuction.getAuctionState() != ItemAuctionState.FINISHED) {
            if (currentAuction.getAuctionState() == ItemAuctionState.STARTED) {
                this.setStateTask(ThreadPool.schedule((Runnable)new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getEndingTime() - System.currentTimeMillis(), 0L)));
            }
            else {
                this.setStateTask(ThreadPool.schedule((Runnable)new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getStartingTime() - System.currentTimeMillis(), 0L)));
            }
            ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), currentAuction.getAuctionId(), this._instanceId));
        }
        else {
            this.setStateTask(ThreadPool.schedule((Runnable)new ScheduleAuctionTask(nextAuction), Math.max(nextAuction.getStartingTime() - System.currentTimeMillis(), 0L)));
            ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), nextAuction.getAuctionId(), this.DATE_FORMAT.format(new Date(nextAuction.getStartingTime())), this._instanceId));
        }
    }
    
    public final ItemAuction getAuction(final int auctionId) {
        return this._auctions.get(auctionId);
    }
    
    public final ItemAuction[] getAuctionsByBidder(final int bidderObjId) {
        final Collection<ItemAuction> auctions = this.getAuctions();
        final ArrayList<ItemAuction> stack = new ArrayList<ItemAuction>(auctions.size());
        for (final ItemAuction auction : this.getAuctions()) {
            if (auction.getAuctionState() != ItemAuctionState.CREATED) {
                final ItemAuctionBid bid = auction.getBidFor(bidderObjId);
                if (bid == null) {
                    continue;
                }
                stack.add(auction);
            }
        }
        return stack.toArray(new ItemAuction[stack.size()]);
    }
    
    public final Collection<ItemAuction> getAuctions() {
        final Collection<ItemAuction> auctions;
        synchronized (this._auctions) {
            auctions = this._auctions.values();
        }
        return auctions;
    }
    
    final void onAuctionFinished(final ItemAuction auction) {
        auction.broadcastToAllBiddersInternal(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_S_AUCTION_HAS_ENDED)).addInt(auction.getAuctionId()));
        final ItemAuctionBid bid = auction.getHighestBid();
        if (bid != null) {
            final Item item = auction.createNewItemInstance();
            final Player player = bid.getPlayer();
            if (player != null) {
                player.getWarehouse().addItem("ItemAuction", item, null, null);
                player.sendPacket(SystemMessageId.YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL_WAREHOUSE);
                ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), auction.getAuctionId(), player.getName(), this._instanceId));
            }
            else {
                item.setOwnerId(bid.getPlayerObjId());
                item.setItemLocation(ItemLocation.WAREHOUSE);
                item.updateDatabase();
                World.getInstance().removeObject(item);
                ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), auction.getAuctionId(), PlayerNameTable.getInstance().getNameById(bid.getPlayerObjId()), this._instanceId));
            }
            auction.clearCanceledBids();
        }
        else {
            ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), auction.getAuctionId(), this._instanceId));
        }
    }
    
    final void setStateTask(final ScheduledFuture<?> future) {
        final ScheduledFuture<?> stateTask = this._stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
        this._stateTask = future;
    }
    
    private ItemAuction createAuction(final long after) {
        final AuctionItem auctionItem = this._items.get(Rnd.get(this._items.size()));
        final long startingTime = this._dateGenerator.nextDate(after);
        final long endingTime = startingTime + TimeUnit.MILLISECONDS.convert(auctionItem.getAuctionLength(), TimeUnit.MINUTES);
        final ItemAuction auction = new ItemAuction(this._auctionIds.getAndIncrement(), this._instanceId, startingTime, endingTime, auctionItem);
        auction.storeMe();
        return auction;
    }
    
    private ItemAuction loadAuction(final int auctionId) throws SQLException {
        final Connection con = DatabaseFactory.getInstance().getConnection();
        try {
            int auctionItemId = 0;
            long startingTime = 0L;
            long endingTime = 0L;
            byte auctionStateId = 0;
            final PreparedStatement ps = con.prepareStatement("SELECT auctionItemId, startingTime, endingTime, auctionStateId FROM item_auction WHERE auctionId = ? ");
            try {
                ps.setInt(1, auctionId);
                final ResultSet rset = ps.executeQuery();
                try {
                    if (!rset.next()) {
                        ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, auctionId));
                        final ItemAuction itemAuction = null;
                        if (rset != null) {
                            rset.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (con != null) {
                            con.close();
                        }
                        return itemAuction;
                    }
                    auctionItemId = rset.getInt(1);
                    startingTime = rset.getLong(2);
                    endingTime = rset.getLong(3);
                    auctionStateId = rset.getByte(4);
                    if (rset != null) {
                        rset.close();
                    }
                }
                catch (Throwable t) {
                    if (rset != null) {
                        try {
                            rset.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (Throwable t2) {
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
            if (startingTime >= endingTime) {
                ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, auctionId));
                final ItemAuction itemAuction2 = null;
                if (con != null) {
                    con.close();
                }
                return itemAuction2;
            }
            final AuctionItem auctionItem = this.getAuctionItem(auctionItemId);
            if (auctionItem == null) {
                ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, auctionItemId, auctionId));
                final ItemAuction itemAuction3 = null;
                if (con != null) {
                    con.close();
                }
                return itemAuction3;
            }
            final ItemAuctionState auctionState = ItemAuctionState.stateForStateId(auctionStateId);
            if (auctionState == null) {
                ItemAuctionInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(BI)Ljava/lang/String;, auctionStateId, auctionId));
                final ItemAuction itemAuction4 = null;
                if (con != null) {
                    con.close();
                }
                return itemAuction4;
            }
            if (auctionState == ItemAuctionState.FINISHED && startingTime < System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(Config.ALT_ITEM_AUCTION_EXPIRED_AFTER, TimeUnit.DAYS)) {
                ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), auctionId));
                PreparedStatement ps2 = con.prepareStatement("DELETE FROM item_auction WHERE auctionId = ?");
                try {
                    ps2.setInt(1, auctionId);
                    ps2.execute();
                    if (ps2 != null) {
                        ps2.close();
                    }
                }
                catch (Throwable t3) {
                    if (ps2 != null) {
                        try {
                            ps2.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                ps2 = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId = ?");
                try {
                    ps2.setInt(1, auctionId);
                    ps2.execute();
                    if (ps2 != null) {
                        ps2.close();
                    }
                }
                catch (Throwable t4) {
                    if (ps2 != null) {
                        try {
                            ps2.close();
                        }
                        catch (Throwable exception4) {
                            t4.addSuppressed(exception4);
                        }
                    }
                    throw t4;
                }
                final ItemAuction itemAuction5 = null;
                if (con != null) {
                    con.close();
                }
                return itemAuction5;
            }
            final ArrayList<ItemAuctionBid> auctionBids = new ArrayList<ItemAuctionBid>();
            final PreparedStatement ps3 = con.prepareStatement("SELECT playerObjId, playerBid FROM item_auction_bid WHERE auctionId = ?");
            try {
                ps3.setInt(1, auctionId);
                final ResultSet rs = ps3.executeQuery();
                try {
                    while (rs.next()) {
                        final int playerObjId = rs.getInt(1);
                        final long playerBid = rs.getLong(2);
                        final ItemAuctionBid bid = new ItemAuctionBid(playerObjId, playerBid);
                        auctionBids.add(bid);
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Throwable t5) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                    }
                    throw t5;
                }
                if (ps3 != null) {
                    ps3.close();
                }
            }
            catch (Throwable t6) {
                if (ps3 != null) {
                    try {
                        ps3.close();
                    }
                    catch (Throwable exception6) {
                        t6.addSuppressed(exception6);
                    }
                }
                throw t6;
            }
            final ItemAuction itemAuction6 = new ItemAuction(auctionId, this._instanceId, startingTime, endingTime, auctionItem, auctionBids, auctionState);
            if (con != null) {
                con.close();
            }
            return itemAuction6;
        }
        catch (Throwable t7) {
            if (con != null) {
                try {
                    con.close();
                }
                catch (Throwable exception7) {
                    t7.addSuppressed(exception7);
                }
            }
            throw t7;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemAuctionInstance.class);
        START_TIME_SPACE = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.MINUTES);
        FINISH_TIME_SPACE = TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES);
    }
    
    private final class ScheduleAuctionTask implements Runnable
    {
        private final ItemAuction _auction;
        
        public ScheduleAuctionTask(final ItemAuction auction) {
            this._auction = auction;
        }
        
        @Override
        public final void run() {
            try {
                this.runImpl();
            }
            catch (Exception e) {
                ItemAuctionInstance.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._auction.getAuctionId()), (Throwable)e);
            }
        }
        
        private void runImpl() throws Exception {
            final ItemAuctionState state = this._auction.getAuctionState();
            switch (state) {
                case CREATED: {
                    if (!this._auction.setAuctionState(state, ItemAuctionState.STARTED)) {
                        throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/auction/ItemAuctionState;Lorg/l2j/gameserver/model/item/auction/ItemAuctionState;)Ljava/lang/String;, ItemAuctionState.STARTED, state));
                    }
                    ItemAuctionInstance.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this._auction.getAuctionId(), this._auction.getInstanceId()));
                    ItemAuctionInstance.this.checkAndSetCurrentAndNextAuction();
                    break;
                }
                case STARTED: {
                    switch (this._auction.getAuctionEndingExtendState()) {
                        case EXTEND_BY_5_MIN: {
                            if (this._auction.getScheduledAuctionEndingExtendState() == ItemAuctionExtendState.INITIAL) {
                                this._auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_5_MIN);
                                ItemAuctionInstance.this.setStateTask(ThreadPool.schedule((Runnable)this, Math.max(this._auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_3_MIN: {
                            if (this._auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_3_MIN) {
                                this._auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_3_MIN);
                                ItemAuctionInstance.this.setStateTask(ThreadPool.schedule((Runnable)this, Math.max(this._auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_CONFIG_PHASE_A: {
                            if (this._auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B) {
                                this._auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B);
                                ItemAuctionInstance.this.setStateTask(ThreadPool.schedule((Runnable)this, Math.max(this._auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_CONFIG_PHASE_B: {
                            if (this._auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A) {
                                this._auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A);
                                ItemAuctionInstance.this.setStateTask(ThreadPool.schedule((Runnable)this, Math.max(this._auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                    }
                    if (!this._auction.setAuctionState(state, ItemAuctionState.FINISHED)) {
                        throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/auction/ItemAuctionState;Lorg/l2j/gameserver/model/item/auction/ItemAuctionState;)Ljava/lang/String;, ItemAuctionState.FINISHED, state));
                    }
                    ItemAuctionInstance.this.onAuctionFinished(this._auction);
                    ItemAuctionInstance.this.checkAndSetCurrentAndNextAuction();
                    break;
                }
                default: {
                    throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/auction/ItemAuctionState;)Ljava/lang/String;, state));
                }
            }
        }
    }
}
