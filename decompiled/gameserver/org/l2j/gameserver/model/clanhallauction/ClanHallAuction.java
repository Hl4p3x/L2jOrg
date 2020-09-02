// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.clanhallauction;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Comparator;
import java.util.Optional;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import org.l2j.gameserver.model.Clan;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Map;
import org.slf4j.Logger;

public class ClanHallAuction
{
    private static final Logger LOGGER;
    private static final String LOAD_CLANHALL_BIDDERS = "SELECT * FROM clanhall_auctions_bidders WHERE clanHallId=?";
    private static final String DELETE_CLANHALL_BIDDERS = "DELETE FROM clanhall_auctions_bidders WHERE clanHallId=?";
    private static final String INSERT_CLANHALL_BIDDER = "REPLACE INTO clanhall_auctions_bidders (clanHallId, clanId, bid, bidTime) VALUES (?,?,?,?)";
    private static final String DELETE_CLANHALL_BIDDER = "DELETE FROM clanhall_auctions_bidders WHERE clanId=?";
    private final int _clanHallId;
    private volatile Map<Integer, Bidder> _bidders;
    
    public ClanHallAuction(final int clanHallId) {
        this._clanHallId = clanHallId;
        this.loadBidder();
    }
    
    private final void loadBidder() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM clanhall_auctions_bidders WHERE clanHallId=?");
                try {
                    ps.setInt(1, this._clanHallId);
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            final Clan clan = ClanTable.getInstance().getClan(rs.getInt("clanId"));
                            this.addBid(clan, rs.getLong("bid"), rs.getLong("bidTime"));
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
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
        catch (SQLException e) {
            ClanHallAuction.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._clanHallId), (Throwable)e);
        }
    }
    
    public Map<Integer, Bidder> getBids() {
        return (this._bidders == null) ? Collections.emptyMap() : this._bidders;
    }
    
    public void addBid(final Clan clan, final long bid) {
        this.addBid(clan, bid, System.currentTimeMillis());
    }
    
    public void addBid(final Clan clan, final long bid, final long bidTime) {
        if (this._bidders == null) {
            synchronized (this) {
                if (this._bidders == null) {
                    this._bidders = new ConcurrentHashMap<Integer, Bidder>();
                }
            }
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("REPLACE INTO clanhall_auctions_bidders (clanHallId, clanId, bid, bidTime) VALUES (?,?,?,?)");
                try {
                    ps.setInt(1, this._clanHallId);
                    ps.setInt(2, clan.getId());
                    ps.setLong(3, bid);
                    ps.setLong(4, bidTime);
                    ps.execute();
                    this._bidders.put(clan.getId(), new Bidder(clan, bid, bidTime));
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
            ClanHallAuction.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, clan.getName(), this._clanHallId), (Throwable)e);
        }
    }
    
    public void removeBid(final Clan clan) {
        this.getBids().remove(clan.getId());
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM clanhall_auctions_bidders WHERE clanId=?");
                try {
                    ps.setInt(1, clan.getId());
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
        catch (Exception e) {
            ClanHallAuction.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, clan.getName(), this._clanHallId), (Throwable)e);
        }
    }
    
    public long getHighestBid() {
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(this._clanHallId);
        return this.getBids().values().stream().mapToLong(Bidder::getBid).max().orElse(clanHall.getMinBid());
    }
    
    public long getClanBid(final Clan clan) {
        return this.getBids().get(clan.getId()).getBid();
    }
    
    public Optional<Bidder> getHighestBidder() {
        return this.getBids().values().stream().sorted(Comparator.comparingLong(Bidder::getBid).reversed()).findFirst();
    }
    
    public int getBidCount() {
        return this.getBids().values().size();
    }
    
    public void returnAdenas(final Bidder bidder) {
        bidder.getClan().getWarehouse().addItem("Clan Hall Auction Outbid", 57, bidder.getBid(), null, null);
    }
    
    public void finalizeAuctions() {
        final Optional<Bidder> potentialHighestBidder = this.getHighestBidder();
        if (potentialHighestBidder.isPresent()) {
            final Bidder highestBidder = potentialHighestBidder.get();
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(this._clanHallId);
            clanHall.setOwner(highestBidder.getClan());
            this.getBids().clear();
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement ps = con.prepareStatement("DELETE FROM clanhall_auctions_bidders WHERE clanHallId=?");
                    try {
                        ps.setInt(1, this._clanHallId);
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
            catch (Exception e) {
                ClanHallAuction.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._clanHallId), (Throwable)e);
            }
        }
    }
    
    public int getClanHallId() {
        return this._clanHallId;
    }
    
    public long getRemaingTime() {
        return ClanHallAuctionManager.getInstance().getScheduler("endAuction").getRemainingTime(TimeUnit.MILLISECONDS);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanHallAuction.class);
    }
}
