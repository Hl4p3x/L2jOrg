// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import java.sql.ResultSet;
import org.l2j.gameserver.world.zone.type.ResidenceZone;
import java.util.concurrent.TimeUnit;
import org.l2j.commons.util.Util;
import org.l2j.commons.threading.ThreadPool;
import java.time.temporal.TemporalAmount;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import java.util.Objects;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.time.temporal.Temporal;
import java.time.Duration;
import java.time.Instant;
import org.l2j.gameserver.world.zone.type.ClanHallZone;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanHallDAO;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.Clan;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.ClanHallTeleportHolder;
import org.l2j.gameserver.model.actor.instance.Door;
import java.util.List;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.slf4j.Logger;
import org.l2j.gameserver.model.residences.AbstractResidence;

public final class ClanHall extends AbstractResidence
{
    private static final Logger LOGGER;
    private final int lease;
    private final ClanHallGrade grade;
    private final ClanHallType type;
    private final int minBid;
    private final int deposit;
    private final List<Integer> npcs;
    private final List<Door> doors;
    private final List<ClanHallTeleportHolder> teleports;
    private final Location ownerLocation;
    private final Location banishLocation;
    protected ScheduledFuture<?> checkPaymentTask;
    Clan owner;
    long paidUntil;
    
    public ClanHall(final StatsSet params) {
        super(params.getInt("id"));
        this.checkPaymentTask = null;
        this.owner = null;
        this.paidUntil = 0L;
        this.setName(params.getString("name"));
        this.grade = params.getEnum("grade", ClanHallGrade.class);
        this.type = params.getEnum("type", ClanHallType.class);
        this.minBid = params.getInt("minBid");
        this.lease = params.getInt("lease");
        this.deposit = params.getInt("deposit");
        this.npcs = params.getList("npcList", Integer.class);
        this.doors = params.getList("doorList", Door.class);
        this.teleports = params.getList("teleportList", ClanHallTeleportHolder.class);
        this.ownerLocation = params.getLocation("owner_loc");
        this.banishLocation = params.getLocation("banish_loc");
        this.load();
        this.initResidenceZone();
        this.initFunctions();
    }
    
    @Override
    protected void load() {
        final ClanHallDAO clanHallDao = (ClanHallDAO)DatabaseAccess.getDAO((Class)ClanHallDAO.class);
        final ClanHallDAO clanHallDAO;
        clanHallDao.findById(this.getId(), result -> {
            try {
                if (result.next()) {
                    this.paidUntil = result.getLong("paid_until");
                    this.setOwner(result.getInt("owner_id"));
                }
                else {
                    clanHallDAO.save(this.getId(), 0, 0L);
                }
            }
            catch (SQLException e) {
                ClanHall.LOGGER.error(e.getMessage(), (Throwable)e);
            }
        });
    }
    
    @Override
    protected void initResidenceZone() {
        ZoneManager.getInstance().getAllZones(ClanHallZone.class).stream().filter(z -> z.getResidenceId() == this.getId()).findFirst().ifPresent(x$0 -> this.setResidenceZone(x$0));
    }
    
    public void updateDB() {
        ((ClanHallDAO)DatabaseAccess.getDAO((Class)ClanHallDAO.class)).save(this.getId(), this.getOwnerId(), this.paidUntil);
    }
    
    public int getCostFailDay() {
        final Duration failDay = Duration.between(Instant.ofEpochMilli(this.paidUntil), Instant.now());
        return failDay.isNegative() ? 0 : ((int)failDay.toDays());
    }
    
    public void banishOthers() {
        this.getResidenceZone().banishForeigners(this.getOwnerId());
    }
    
    public void openCloseDoors(final boolean open) {
        this.doors.forEach(door -> door.openCloseMe(open));
    }
    
    public ClanHallGrade getGrade() {
        return this.grade;
    }
    
    public List<Door> getDoors() {
        return this.doors;
    }
    
    public List<Integer> getNpcs() {
        return this.npcs;
    }
    
    public ClanHallType getType() {
        return this.type;
    }
    
    public Clan getOwner() {
        return this.owner;
    }
    
    public void setOwner(final int clanId) {
        this.setOwner(ClanTable.getInstance().getClan(clanId));
    }
    
    public void setOwner(final Clan clan) {
        if (Objects.nonNull(clan)) {
            (this.owner = clan).setHideoutId(this.getId());
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
            if (this.paidUntil == 0L) {
                this.paidUntil = Instant.now().plus((TemporalAmount)Duration.ofDays(7L)).toEpochMilli();
            }
            final int failDays = this.getCostFailDay();
            final long time = (failDays > 0) ? ((failDays > 8) ? Instant.now().toEpochMilli() : Instant.ofEpochMilli(this.paidUntil).plus((TemporalAmount)Duration.ofDays(failDays + 1)).toEpochMilli()) : this.paidUntil;
            this.checkPaymentTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new CheckPaymentTask(), time - System.currentTimeMillis());
        }
        else {
            if (Objects.nonNull(this.owner)) {
                this.owner.setHideoutId(0);
                this.owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this.owner));
                this.removeFunctions();
            }
            this.owner = null;
            this.paidUntil = 0L;
            if (this.checkPaymentTask != null) {
                this.checkPaymentTask.cancel(true);
                this.checkPaymentTask = null;
            }
        }
        this.updateDB();
    }
    
    @Override
    public int getOwnerId() {
        return Util.zeroIfNullOrElse((Object)this.owner, Clan::getId);
    }
    
    public long getNextPayment() {
        return Objects.nonNull(this.checkPaymentTask) ? (System.currentTimeMillis() + this.checkPaymentTask.getDelay(TimeUnit.MILLISECONDS)) : 0L;
    }
    
    public Location getOwnerLocation() {
        return this.ownerLocation;
    }
    
    public Location getBanishLocation() {
        return this.banishLocation;
    }
    
    public int getMinBid() {
        return this.minBid;
    }
    
    public int getLease() {
        return this.lease;
    }
    
    public int getDeposit() {
        return this.deposit;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this.getName(), this.getId());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanHallManager.class);
    }
    
    class CheckPaymentTask implements Runnable
    {
        @Override
        public void run() {
            if (Objects.nonNull(ClanHall.this.owner)) {
                if (ClanHall.this.owner.getWarehouse().getAdena() < ClanHall.this.lease) {
                    if (ClanHall.this.getCostFailDay() > 8) {
                        ClanHall.this.owner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
                        ClanHall.this.setOwner(null);
                    }
                    else {
                        ClanHall.this.checkPaymentTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new CheckPaymentTask(), 1L, TimeUnit.DAYS);
                        ClanHall.this.owner.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW)).addInt(ClanHall.this.lease));
                    }
                }
                else {
                    ClanHall.this.owner.getWarehouse().destroyItem("Clan Hall Lease", 57, ClanHall.this.lease, null, null);
                    ClanHall.this.paidUntil = Instant.ofEpochMilli(ClanHall.this.paidUntil).plus((TemporalAmount)Duration.ofDays(7L)).toEpochMilli();
                    ClanHall.this.checkPaymentTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new CheckPaymentTask(), ClanHall.this.paidUntil - System.currentTimeMillis());
                    ClanHall.this.updateDB();
                }
            }
        }
    }
}
