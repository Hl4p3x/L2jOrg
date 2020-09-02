// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.network.GameClient;
import java.util.Calendar;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.World;
import java.util.Iterator;
import org.l2j.gameserver.model.TradeItem;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class OfflineTradersTable
{
    private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)";
    private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`) VALUES (?,?,?,?)";
    private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
    private static final String CLEAR_OFFLINE_TABLE_PLAYER = "DELETE FROM character_offline_trade WHERE `charId`=?";
    private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
    private static final String CLEAR_OFFLINE_TABLE_ITEMS_PLAYER = "DELETE FROM character_offline_trade_items WHERE `charId`=?";
    private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
    private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE `charId`=?";
    private static final Logger LOGGER;
    
    private OfflineTradersTable() {
    }
    
    public static synchronized void onTransaction(final Player trader, final boolean finished, final boolean firstCall) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement stm1 = con.prepareStatement("DELETE FROM character_offline_trade_items WHERE `charId`=?");
                try {
                    final PreparedStatement stm2 = con.prepareStatement("DELETE FROM character_offline_trade WHERE `charId`=?");
                    try {
                        final PreparedStatement stm3 = con.prepareStatement("INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`) VALUES (?,?,?,?)");
                        try {
                            final PreparedStatement stm4 = con.prepareStatement("INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)");
                            try {
                                String title = null;
                                stm1.setInt(1, trader.getObjectId());
                                stm1.execute();
                                stm1.close();
                                if (finished) {
                                    stm2.setInt(1, trader.getObjectId());
                                    stm2.execute();
                                    stm2.close();
                                }
                                else {
                                    try {
                                        if (trader.getClient() == null || trader.getClient().isDetached()) {
                                            switch (trader.getPrivateStoreType()) {
                                                case BUY: {
                                                    if (firstCall) {
                                                        title = trader.getBuyList().getTitle();
                                                    }
                                                    for (final TradeItem i : trader.getBuyList().getItems()) {
                                                        stm3.setInt(1, trader.getObjectId());
                                                        stm3.setInt(2, i.getItem().getId());
                                                        stm3.setLong(3, i.getCount());
                                                        stm3.setLong(4, i.getPrice());
                                                        stm3.executeUpdate();
                                                        stm3.clearParameters();
                                                    }
                                                    break;
                                                }
                                                case SELL:
                                                case PACKAGE_SELL: {
                                                    if (firstCall) {
                                                        title = trader.getSellList().getTitle();
                                                    }
                                                    if (trader.isSellingBuffs()) {
                                                        for (final SellBuffHolder holder : trader.getSellingBuffs()) {
                                                            stm3.setInt(1, trader.getObjectId());
                                                            stm3.setInt(2, holder.getSkillId());
                                                            stm3.setLong(3, 0L);
                                                            stm3.setLong(4, holder.getPrice());
                                                            stm3.executeUpdate();
                                                            stm3.clearParameters();
                                                        }
                                                        break;
                                                    }
                                                    for (final TradeItem i : trader.getSellList().getItems()) {
                                                        stm3.setInt(1, trader.getObjectId());
                                                        stm3.setInt(2, i.getObjectId());
                                                        stm3.setLong(3, i.getCount());
                                                        stm3.setLong(4, i.getPrice());
                                                        stm3.executeUpdate();
                                                        stm3.clearParameters();
                                                    }
                                                    break;
                                                }
                                                case MANUFACTURE: {
                                                    if (firstCall) {
                                                        title = trader.getStoreName();
                                                    }
                                                    for (final ManufactureItem j : trader.getManufactureItems().values()) {
                                                        stm3.setInt(1, trader.getObjectId());
                                                        stm3.setInt(2, j.getRecipeId());
                                                        stm3.setLong(3, 0L);
                                                        stm3.setLong(4, j.getCost());
                                                        stm3.executeUpdate();
                                                        stm3.clearParameters();
                                                    }
                                                    break;
                                                }
                                            }
                                            stm3.close();
                                            if (firstCall) {
                                                stm4.setInt(1, trader.getObjectId());
                                                stm4.setLong(2, trader.getOfflineStartTime());
                                                stm4.setInt(3, trader.isSellingBuffs() ? PrivateStoreType.SELL_BUFFS.getId() : trader.getPrivateStoreType().getId());
                                                stm4.setString(4, title);
                                                stm4.executeUpdate();
                                                stm4.clearParameters();
                                                stm4.close();
                                            }
                                        }
                                    }
                                    catch (Exception e) {
                                        OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/Exception;)Ljava/lang/String;, trader.getObjectId(), e), (Throwable)e);
                                    }
                                }
                                if (stm4 != null) {
                                    stm4.close();
                                }
                            }
                            catch (Throwable t) {
                                if (stm4 != null) {
                                    try {
                                        stm4.close();
                                    }
                                    catch (Throwable exception) {
                                        t.addSuppressed(exception);
                                    }
                                }
                                throw t;
                            }
                            if (stm3 != null) {
                                stm3.close();
                            }
                        }
                        catch (Throwable t2) {
                            if (stm3 != null) {
                                try {
                                    stm3.close();
                                }
                                catch (Throwable exception2) {
                                    t2.addSuppressed(exception2);
                                }
                            }
                            throw t2;
                        }
                        if (stm2 != null) {
                            stm2.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (stm2 != null) {
                            try {
                                stm2.close();
                            }
                            catch (Throwable exception3) {
                                t3.addSuppressed(exception3);
                            }
                        }
                        throw t3;
                    }
                    if (stm1 != null) {
                        stm1.close();
                    }
                }
                catch (Throwable t4) {
                    if (stm1 != null) {
                        try {
                            stm1.close();
                        }
                        catch (Throwable exception4) {
                            t4.addSuppressed(exception4);
                        }
                    }
                    throw t4;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t5) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception5) {
                        t5.addSuppressed(exception5);
                    }
                }
                throw t5;
            }
        }
        catch (Exception e2) {
            OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e2), (Throwable)e2);
        }
    }
    
    public static synchronized void removeTrader(final int traderObjId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement stm1 = con.prepareStatement("DELETE FROM character_offline_trade_items WHERE `charId`=?");
                try {
                    final PreparedStatement stm2 = con.prepareStatement("DELETE FROM character_offline_trade WHERE `charId`=?");
                    try {
                        stm1.setInt(1, traderObjId);
                        stm1.execute();
                        stm1.close();
                        stm2.setInt(1, traderObjId);
                        stm2.execute();
                        stm2.close();
                        if (stm2 != null) {
                            stm2.close();
                        }
                    }
                    catch (Throwable t) {
                        if (stm2 != null) {
                            try {
                                stm2.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (stm1 != null) {
                        stm1.close();
                    }
                }
                catch (Throwable t2) {
                    if (stm1 != null) {
                        try {
                            stm1.close();
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
        catch (Exception e) {
            OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/Exception;)Ljava/lang/String;, traderObjId, e), (Throwable)e);
        }
    }
    
    public void storeOffliners() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement stm1 = con.prepareStatement("DELETE FROM character_offline_trade");
                try {
                    final PreparedStatement stm2 = con.prepareStatement("DELETE FROM character_offline_trade_items");
                    try {
                        final PreparedStatement stm3 = con.prepareStatement("INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)");
                        try {
                            final PreparedStatement stm_items = con.prepareStatement("INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`) VALUES (?,?,?,?)");
                            try {
                                stm1.execute();
                                stm2.execute();
                                con.setAutoCommit(false);
                                for (final Player pc : World.getInstance().getPlayers()) {
                                    try {
                                        if (pc.getPrivateStoreType() == PrivateStoreType.NONE || (pc.getClient() != null && !pc.getClient().isDetached())) {
                                            continue;
                                        }
                                        stm3.setInt(1, pc.getObjectId());
                                        stm3.setLong(2, pc.getOfflineStartTime());
                                        stm3.setInt(3, pc.isSellingBuffs() ? PrivateStoreType.SELL_BUFFS.getId() : pc.getPrivateStoreType().getId());
                                        String title = null;
                                        switch (pc.getPrivateStoreType()) {
                                            case BUY: {
                                                if (!Config.OFFLINE_TRADE_ENABLE) {
                                                    continue;
                                                }
                                                title = pc.getBuyList().getTitle();
                                                for (final TradeItem i : pc.getBuyList().getItems()) {
                                                    stm_items.setInt(1, pc.getObjectId());
                                                    stm_items.setInt(2, i.getItem().getId());
                                                    stm_items.setLong(3, i.getCount());
                                                    stm_items.setLong(4, i.getPrice());
                                                    stm_items.executeUpdate();
                                                    stm_items.clearParameters();
                                                }
                                                break;
                                            }
                                            case SELL:
                                            case PACKAGE_SELL: {
                                                if (!Config.OFFLINE_TRADE_ENABLE) {
                                                    continue;
                                                }
                                                title = pc.getSellList().getTitle();
                                                if (pc.isSellingBuffs()) {
                                                    for (final SellBuffHolder holder : pc.getSellingBuffs()) {
                                                        stm_items.setInt(1, pc.getObjectId());
                                                        stm_items.setInt(2, holder.getSkillId());
                                                        stm_items.setLong(3, 0L);
                                                        stm_items.setLong(4, holder.getPrice());
                                                        stm_items.executeUpdate();
                                                        stm_items.clearParameters();
                                                    }
                                                    break;
                                                }
                                                for (final TradeItem i : pc.getSellList().getItems()) {
                                                    stm_items.setInt(1, pc.getObjectId());
                                                    stm_items.setInt(2, i.getObjectId());
                                                    stm_items.setLong(3, i.getCount());
                                                    stm_items.setLong(4, i.getPrice());
                                                    stm_items.executeUpdate();
                                                    stm_items.clearParameters();
                                                }
                                                break;
                                            }
                                            case MANUFACTURE: {
                                                if (!Config.OFFLINE_CRAFT_ENABLE) {
                                                    continue;
                                                }
                                                title = pc.getStoreName();
                                                for (final ManufactureItem j : pc.getManufactureItems().values()) {
                                                    stm_items.setInt(1, pc.getObjectId());
                                                    stm_items.setInt(2, j.getRecipeId());
                                                    stm_items.setLong(3, 0L);
                                                    stm_items.setLong(4, j.getCost());
                                                    stm_items.executeUpdate();
                                                    stm_items.clearParameters();
                                                }
                                                break;
                                            }
                                        }
                                        stm3.setString(4, title);
                                        stm3.executeUpdate();
                                        stm3.clearParameters();
                                        con.commit();
                                    }
                                    catch (Exception e) {
                                        OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/Exception;)Ljava/lang/String;, this.getClass().getSimpleName(), pc.getObjectId(), e), (Throwable)e);
                                    }
                                }
                                OfflineTradersTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                                if (stm_items != null) {
                                    stm_items.close();
                                }
                            }
                            catch (Throwable t) {
                                if (stm_items != null) {
                                    try {
                                        stm_items.close();
                                    }
                                    catch (Throwable exception) {
                                        t.addSuppressed(exception);
                                    }
                                }
                                throw t;
                            }
                            if (stm3 != null) {
                                stm3.close();
                            }
                        }
                        catch (Throwable t2) {
                            if (stm3 != null) {
                                try {
                                    stm3.close();
                                }
                                catch (Throwable exception2) {
                                    t2.addSuppressed(exception2);
                                }
                            }
                            throw t2;
                        }
                        if (stm2 != null) {
                            stm2.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (stm2 != null) {
                            try {
                                stm2.close();
                            }
                            catch (Throwable exception3) {
                                t3.addSuppressed(exception3);
                            }
                        }
                        throw t3;
                    }
                    if (stm1 != null) {
                        stm1.close();
                    }
                }
                catch (Throwable t4) {
                    if (stm1 != null) {
                        try {
                            stm1.close();
                        }
                        catch (Throwable exception4) {
                            t4.addSuppressed(exception4);
                        }
                    }
                    throw t4;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t5) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception5) {
                        t5.addSuppressed(exception5);
                    }
                }
                throw t5;
            }
        }
        catch (Exception e2) {
            OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Exception;)Ljava/lang/String;, this.getClass().getSimpleName(), e2), (Throwable)e2);
        }
    }
    
    public void restoreOfflineTraders() {
        OfflineTradersTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        int nTraders = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement stm = con.createStatement();
                try {
                    final ResultSet rs = stm.executeQuery("SELECT * FROM character_offline_trade");
                    try {
                        while (rs.next()) {
                            final long time = rs.getLong("time");
                            if (Config.OFFLINE_MAX_DAYS > 0) {
                                final Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(time);
                                cal.add(6, Config.OFFLINE_MAX_DAYS);
                                if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                                    continue;
                                }
                            }
                            final int typeId = rs.getInt("type");
                            boolean isSellBuff = false;
                            if (typeId == PrivateStoreType.SELL_BUFFS.getId()) {
                                isSellBuff = true;
                            }
                            final PrivateStoreType type = isSellBuff ? PrivateStoreType.PACKAGE_SELL : PrivateStoreType.findById(typeId);
                            if (type == null) {
                                OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, rs.getInt("type")));
                            }
                            else {
                                if (type == PrivateStoreType.NONE) {
                                    continue;
                                }
                                Player player = null;
                                try {
                                    final GameClient client = new GameClient(null);
                                    client.setDetached(true);
                                    player = PlayerFactory.loadPlayer(client, rs.getInt("charId"));
                                    player.setOnlineStatus(true, false);
                                    client.setAccountName(player.getAccountNamePlayer());
                                    player.setOfflineStartTime(time);
                                    if (isSellBuff) {
                                        player.setIsSellingBuffs(true);
                                    }
                                    player.spawnMe(player.getX(), player.getY(), player.getZ());
                                    final PreparedStatement stm_items = con.prepareStatement("SELECT * FROM character_offline_trade_items WHERE `charId`=?");
                                    try {
                                        stm_items.setInt(1, player.getObjectId());
                                        final ResultSet items = stm_items.executeQuery();
                                        try {
                                            switch (type) {
                                                case BUY: {
                                                    while (items.next()) {
                                                        if (player.getBuyList().addItemByItemId(items.getInt(2), items.getLong(3), items.getLong(4)) == null) {
                                                            continue;
                                                        }
                                                    }
                                                    player.getBuyList().setTitle(rs.getString("title"));
                                                    break;
                                                }
                                                case SELL:
                                                case PACKAGE_SELL: {
                                                    if (player.isSellingBuffs()) {
                                                        while (items.next()) {
                                                            player.getSellingBuffs().add(new SellBuffHolder(items.getInt("item"), items.getLong("price")));
                                                        }
                                                    }
                                                    else {
                                                        while (items.next()) {
                                                            if (player.getSellList().addItem(items.getInt(2), items.getLong(3), items.getLong(4)) == null) {
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                    player.getSellList().setTitle(rs.getString("title"));
                                                    player.getSellList().setPackaged(type == PrivateStoreType.PACKAGE_SELL);
                                                    break;
                                                }
                                                case MANUFACTURE: {
                                                    while (items.next()) {
                                                        player.getManufactureItems().put(items.getInt(2), new ManufactureItem(items.getInt(2), items.getLong(4)));
                                                    }
                                                    player.setStoreName(rs.getString("title"));
                                                    break;
                                                }
                                            }
                                            if (items != null) {
                                                items.close();
                                            }
                                        }
                                        catch (Throwable t) {
                                            if (items != null) {
                                                try {
                                                    items.close();
                                                }
                                                catch (Throwable exception) {
                                                    t.addSuppressed(exception);
                                                }
                                            }
                                            throw t;
                                        }
                                        if (stm_items != null) {
                                            stm_items.close();
                                        }
                                    }
                                    catch (Throwable t2) {
                                        if (stm_items != null) {
                                            try {
                                                stm_items.close();
                                            }
                                            catch (Throwable exception2) {
                                                t2.addSuppressed(exception2);
                                            }
                                        }
                                        throw t2;
                                    }
                                    player.sitDown();
                                    if (Config.OFFLINE_SET_NAME_COLOR) {
                                        player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
                                    }
                                    player.setPrivateStoreType(type);
                                    player.setOnlineStatus(true, true);
                                    player.restoreEffects();
                                    player.broadcastUserInfo();
                                    ++nTraders;
                                }
                                catch (Exception e) {
                                    OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), player), (Throwable)e);
                                    if (player == null) {
                                        continue;
                                    }
                                    Disconnection.of(player).defaultSequence(false);
                                }
                            }
                        }
                        OfflineTradersTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), nTraders));
                        if (!Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
                            final Statement stm2 = con.createStatement();
                            try {
                                stm2.execute("DELETE FROM character_offline_trade");
                                stm2.execute("DELETE FROM character_offline_trade_items");
                                if (stm2 != null) {
                                    stm2.close();
                                }
                            }
                            catch (Throwable t3) {
                                if (stm2 != null) {
                                    try {
                                        stm2.close();
                                    }
                                    catch (Throwable exception3) {
                                        t3.addSuppressed(exception3);
                                    }
                                }
                                throw t3;
                            }
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t4) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception4) {
                                t4.addSuppressed(exception4);
                            }
                        }
                        throw t4;
                    }
                    if (stm != null) {
                        stm.close();
                    }
                }
                catch (Throwable t5) {
                    if (stm != null) {
                        try {
                            stm.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                    }
                    throw t5;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t6) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception6) {
                        t6.addSuppressed(exception6);
                    }
                }
                throw t6;
            }
        }
        catch (Exception e2) {
            OfflineTradersTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e2);
        }
    }
    
    public static OfflineTradersTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)OfflineTradersTable.class);
    }
    
    private static class Singleton
    {
        private static final OfflineTradersTable INSTANCE;
        
        static {
            INSTANCE = new OfflineTradersTable();
        }
    }
}
