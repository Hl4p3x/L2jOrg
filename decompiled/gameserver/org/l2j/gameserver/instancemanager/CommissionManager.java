// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionBuyItem;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionDelete;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import java.sql.Timestamp;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionRegister;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import org.l2j.commons.threading.ThreadPool;
import java.time.temporal.Temporal;
import java.time.Duration;
import java.time.Instant;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.l2j.gameserver.model.commission.CommissionItem;
import java.util.Map;
import org.slf4j.Logger;

public final class CommissionManager
{
    private static final Logger LOGGER;
    private static final int INTERACTION_DISTANCE = 250;
    private static final int ITEMS_LIMIT_PER_REQUEST = 999;
    private static final int MAX_ITEMS_REGISTRED_PER_PLAYER = 10;
    private static final long MIN_REGISTRATION_AND_SALE_FEE = 1000L;
    private static final double REGISTRATION_FEE_PER_DAY = 0.001;
    private static final double SALE_FEE_PER_DAY = 0.005;
    private static final String SELECT_ALL_ITEMS = "SELECT * FROM `items` WHERE `loc` = ?";
    private static final String SELECT_ALL_COMMISSION_ITEMS = "SELECT * FROM `commission_items`";
    private static final String INSERT_COMMISSION_ITEM = "INSERT INTO `commission_items`(`item_object_id`, `price_per_unit`, `start_time`, `duration_in_days`) VALUES (?, ?, ?, ?)";
    private static final String DELETE_COMMISSION_ITEM = "DELETE FROM `commission_items` WHERE `commission_id` = ?";
    private final Map<Long, CommissionItem> _commissionItems;
    
    private CommissionManager() {
        this._commissionItems = new ConcurrentSkipListMap<Long, CommissionItem>();
        final Map<Integer, Item> itemInstances = new HashMap<Integer, Item>();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM `items` WHERE `loc` = ?");
                try {
                    ps.setString(1, ItemLocation.COMMISSION.name());
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            final Item itemInstance = new Item(rs);
                            itemInstances.put(itemInstance.getObjectId(), itemInstance);
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
                final Statement st = con.createStatement();
                try {
                    final ResultSet rs = st.executeQuery("SELECT * FROM `commission_items`");
                    try {
                        while (rs.next()) {
                            final long commissionId = rs.getLong("commission_id");
                            final Item itemInstance2 = itemInstances.get(rs.getInt("item_object_id"));
                            if (itemInstance2 == null) {
                                CommissionManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, commissionId));
                            }
                            else {
                                final CommissionItem commissionItem = new CommissionItem(commissionId, itemInstance2, rs.getLong("price_per_unit"), rs.getTimestamp("start_time").toInstant(), rs.getByte("duration_in_days"));
                                this._commissionItems.put(commissionItem.getCommissionId(), commissionItem);
                                if (commissionItem.getEndTime().isBefore(Instant.now())) {
                                    this.expireSale(commissionItem);
                                }
                                else {
                                    commissionItem.setSaleEndTask(ThreadPool.schedule(() -> this.expireSale(commissionItem), Duration.between(Instant.now(), commissionItem.getEndTime()).toMillis()));
                                }
                            }
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception3) {
                                t3.addSuppressed(exception3);
                            }
                        }
                        throw t3;
                    }
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t4) {
                    if (st != null) {
                        try {
                            st.close();
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
        catch (SQLException e) {
            CommissionManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
    }
    
    public static boolean isPlayerAllowedToInteract(final Player player) {
        final Npc npc = player.getLastFolkNPC();
        return npc instanceof org.l2j.gameserver.model.actor.instance.CommissionManager && MathUtil.isInsideRadius3D(npc, player, 250);
    }
    
    public void showAuctions(final Player player, final Predicate<ItemTemplate> filter) {
        final List<CommissionItem> commissionItems = this._commissionItems.values().stream().filter(c -> filter.test(c.getItemInfo().getTemplate())).limit(999L).collect((Collector<? super CommissionItem, ?, List<CommissionItem>>)Collectors.toList());
        if (commissionItems.isEmpty()) {
            player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.CommissionListReplyType.ITEM_DOES_NOT_EXIST));
            return;
        }
        int chunks = commissionItems.size() / 120;
        if (commissionItems.size() > chunks * 120) {
            ++chunks;
        }
        for (int i = chunks - 1; i >= 0; --i) {
            player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.CommissionListReplyType.AUCTIONS, commissionItems, i, i * 120));
        }
    }
    
    public void showPlayerAuctions(final Player player) {
        final List<CommissionItem> commissionItems = this._commissionItems.values().stream().filter(c -> c.getItemInstance().getOwnerId() == player.getObjectId()).limit(10L).collect((Collector<? super CommissionItem, ?, List<CommissionItem>>)Collectors.toList());
        if (!commissionItems.isEmpty()) {
            player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.CommissionListReplyType.PLAYER_AUCTIONS, commissionItems));
        }
        else {
            player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.CommissionListReplyType.PLAYER_AUCTIONS_EMPTY));
        }
    }
    
    public void registerItem(final Player player, final int itemObjectId, final long itemCount, final long pricePerUnit, final byte durationInDays) {
        if (itemCount < 1L) {
            player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }
        final long totalPrice = itemCount * pricePerUnit;
        if (totalPrice <= 1000L) {
            player.sendPacket(SystemMessageId.THE_ITEM_CANNOT_BE_REGISTERED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }
        Item itemInstance = player.getInventory().getItemByObjectId(itemObjectId);
        if (itemInstance == null || !itemInstance.isAvailable(player, false, false) || itemInstance.getCount() < itemCount) {
            player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }
        synchronized (this) {
            final long playerRegisteredItems = this._commissionItems.values().stream().filter(c -> c.getItemInstance().getOwnerId() == player.getObjectId()).count();
            if (playerRegisteredItems >= 10L) {
                player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }
            final long registrationFee = (long)Math.max(1000.0, totalPrice * 0.001 * durationInDays);
            if (!player.getInventory().reduceAdena("Commission Registration Fee", registrationFee, player, null)) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_REGISTER_THE_ITEM);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }
            itemInstance = player.getInventory().detachItem("Commission Registration", itemInstance, itemCount, ItemLocation.COMMISSION, player, null);
            if (itemInstance == null) {
                player.getInventory().addAdena("Commission error refund", registrationFee, player, null);
                player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement ps = con.prepareStatement("INSERT INTO `commission_items`(`item_object_id`, `price_per_unit`, `start_time`, `duration_in_days`) VALUES (?, ?, ?, ?)", 1);
                    try {
                        final Instant startTime = Instant.now();
                        ps.setInt(1, itemInstance.getObjectId());
                        ps.setLong(2, pricePerUnit);
                        ps.setTimestamp(3, Timestamp.from(startTime));
                        ps.setByte(4, durationInDays);
                        ps.executeUpdate();
                        final ResultSet rs = ps.getGeneratedKeys();
                        try {
                            if (rs.next()) {
                                final CommissionItem commissionItem = new CommissionItem(rs.getLong(1), itemInstance, pricePerUnit, startTime, durationInDays);
                                final ScheduledFuture<?> saleEndTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.expireSale(commissionItem), Duration.between(Instant.now(), commissionItem.getEndTime()).toMillis());
                                commissionItem.setSaleEndTask(saleEndTask);
                                this._commissionItems.put(commissionItem.getCommissionId(), commissionItem);
                                player.getLastCommissionInfos().put(itemInstance.getId(), new ExResponseCommissionInfo(itemInstance.getId(), pricePerUnit, itemCount, (byte)((durationInDays - 1) / 2)));
                                player.sendPacket(SystemMessageId.THE_ITEM_HAS_BEEN_SUCCESSFULLY_REGISTERED);
                                player.sendPacket(ExResponseCommissionRegister.SUCCEED);
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
                CommissionManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, this.getClass().getSimpleName(), itemInstance), (Throwable)e);
                player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
            }
        }
    }
    
    public void deleteItem(final Player player, final long commissionId) {
        final CommissionItem commissionItem = this.getCommissionItem(commissionId);
        if (commissionItem == null) {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }
        if (commissionItem.getItemInstance().getOwnerId() != player.getObjectId()) {
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }
        if (!player.isInventoryUnder80(false) || player.getWeightPenalty() >= 3) {
            player.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }
        if (this._commissionItems.remove(commissionId) == null || !commissionItem.getSaleEndTask().cancel(false)) {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }
        if (this.deleteItemFromDB(commissionId)) {
            player.getInventory().addItem("Commission Cancellation", commissionItem.getItemInstance(), player, null);
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_FOR_THE_ITEM_IS_SUCCESSFUL);
            player.sendPacket(ExResponseCommissionDelete.SUCCEED);
        }
        else {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
        }
    }
    
    public void buyItem(final Player player, final long commissionId) {
        final CommissionItem commissionItem = this.getCommissionItem(commissionId);
        if (commissionItem == null) {
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }
        final Item itemInstance = commissionItem.getItemInstance();
        if (itemInstance.getOwnerId() == player.getObjectId()) {
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }
        if (!player.isInventoryUnder80(false) || player.getWeightPenalty() >= 3) {
            player.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }
        final long totalPrice = itemInstance.getCount() * commissionItem.getPricePerUnit();
        if (!player.getInventory().reduceAdena("Commission Registration Fee", totalPrice, player, null)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }
        if (this._commissionItems.remove(commissionId) == null || !commissionItem.getSaleEndTask().cancel(false)) {
            player.getInventory().addAdena("Commission error refund", totalPrice, player, null);
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }
        if (this.deleteItemFromDB(commissionId)) {
            final long saleFee = (long)Math.max(1000.0, totalPrice * 0.005 * commissionItem.getDurationInDays());
            final MailData mail = MailData.of(itemInstance.getOwnerId(), itemInstance, MailType.COMMISSION_ITEM_SOLD);
            final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
            attachement.addItem("Commission Item Sold", 57, totalPrice - saleFee, player, null);
            mail.attach(attachement);
            MailEngine.getInstance().sendMail(mail);
            player.sendPacket(new ExResponseCommissionBuyItem(commissionItem));
            player.getInventory().addItem("Commission Buy Item", commissionItem.getItemInstance(), player, null);
        }
        else {
            player.getInventory().addAdena("Commission error refund", totalPrice, player, null);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
        }
    }
    
    private boolean deleteItemFromDB(final long commissionId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM `commission_items` WHERE `commission_id` = ?");
                try {
                    ps.setLong(1, commissionId);
                    if (ps.executeUpdate() > 0) {
                        final boolean b = true;
                        if (ps != null) {
                            ps.close();
                        }
                        if (con != null) {
                            con.close();
                        }
                        return b;
                    }
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
            CommissionManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, this.getClass().getSimpleName(), commissionId), (Throwable)e);
        }
        return false;
    }
    
    private void expireSale(final CommissionItem commissionItem) {
        if (this._commissionItems.remove(commissionItem.getCommissionId()) != null && this.deleteItemFromDB(commissionItem.getCommissionId())) {
            final MailData mail = MailData.of(commissionItem.getItemInstance().getOwnerId(), commissionItem.getItemInstance(), MailType.COMMISSION_ITEM_RETURNED);
            MailEngine.getInstance().sendMail(mail);
        }
    }
    
    public CommissionItem getCommissionItem(final long commissionId) {
        return this._commissionItems.get(commissionId);
    }
    
    public boolean hasCommissionItems(final int objectId) {
        return this._commissionItems.values().stream().anyMatch(item -> item.getItemInstance().getObjectId() == objectId);
    }
    
    public static CommissionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CommissionManager.class);
    }
    
    private static class Singleton
    {
        private static final CommissionManager INSTANCE;
        
        static {
            INSTANCE = new CommissionManager();
        }
    }
}
