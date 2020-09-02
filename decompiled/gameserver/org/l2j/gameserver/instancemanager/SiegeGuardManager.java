// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.SiegeDAO;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.sql.PreparedStatement;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.CastleDataManager;
import org.l2j.gameserver.model.holders.SiegeGuardHolder;
import org.l2j.gameserver.model.entity.Castle;
import java.sql.ResultSet;
import java.sql.Connection;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.Spawn;
import java.util.Map;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Set;
import org.slf4j.Logger;

public final class SiegeGuardManager
{
    private static final Logger LOGGER;
    private static final Set<Item> _droppedTickets;
    private static final Map<Integer, Set<Spawn>> _siegeGuardSpawn;
    
    private SiegeGuardManager() {
        SiegeGuardManager._droppedTickets.clear();
        this.load();
    }
    
    private void load() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final ResultSet rs = con.createStatement().executeQuery("SELECT * FROM castle_siege_guards Where isHired = 1");
                try {
                    while (rs.next()) {
                        final int npcId = rs.getInt("npcId");
                        final int x = rs.getInt("x");
                        final int y = rs.getInt("y");
                        final int z = rs.getInt("z");
                        final Castle castle = CastleManager.getInstance().getCastle(x, y, z);
                        if (castle == null) {
                            SiegeGuardManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, x, y, z));
                        }
                        else {
                            final SiegeGuardHolder holder = this.getSiegeGuardByNpc(castle.getId(), npcId);
                            if (holder == null || castle.getSiege().isInProgress()) {
                                continue;
                            }
                            final Item dropticket = new Item(holder.getItemId());
                            dropticket.setItemLocation(ItemLocation.VOID);
                            dropticket.dropMe(null, x, y, z);
                            World.getInstance().addObject(dropticket);
                            SiegeGuardManager._droppedTickets.add(dropticket);
                        }
                    }
                    SiegeGuardManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), SiegeGuardManager._droppedTickets.size()));
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
            SiegeGuardManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public SiegeGuardHolder getSiegeGuardByItem(final int castleId, final int itemId) {
        return CastleDataManager.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> g.getItemId() == itemId).findFirst().orElse(null);
    }
    
    public SiegeGuardHolder getSiegeGuardByNpc(final int castleId, final int npcId) {
        return CastleDataManager.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> g.getNpcId() == npcId).findFirst().orElse(null);
    }
    
    public boolean isTooCloseToAnotherTicket(final Player player) {
        return SiegeGuardManager._droppedTickets.stream().anyMatch(g -> MathUtil.isInsideRadius3D(g, player, 25));
    }
    
    public boolean isAtNpcLimit(final int castleId, final int itemId) {
        final long count = SiegeGuardManager._droppedTickets.stream().filter(i -> i.getId() == itemId).count();
        final SiegeGuardHolder holder = this.getSiegeGuardByItem(castleId, itemId);
        return count >= holder.getMaxNpcAmout();
    }
    
    public void addTicket(final int itemId, final Player player) {
        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle == null) {
            return;
        }
        if (this.isAtNpcLimit(castle.getId(), itemId)) {
            return;
        }
        final SiegeGuardHolder holder = this.getSiegeGuardByItem(castle.getId(), itemId);
        if (holder != null) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("Insert Into castle_siege_guards (castleId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)");
                    try {
                        statement.setInt(1, castle.getId());
                        statement.setInt(2, holder.getNpcId());
                        statement.setInt(3, player.getX());
                        statement.setInt(4, player.getY());
                        statement.setInt(5, player.getZ());
                        statement.setInt(6, player.getHeading());
                        statement.setInt(7, 0);
                        statement.setInt(8, 1);
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
            catch (Exception e) {
                SiegeGuardManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, castle.getName(), e.getMessage()), (Throwable)e);
            }
            this.spawnMercenary(player, holder);
            final Item dropticket = new Item(itemId);
            dropticket.setItemLocation(ItemLocation.VOID);
            dropticket.dropMe(null, player.getX(), player.getY(), player.getZ());
            World.getInstance().addObject(dropticket);
            SiegeGuardManager._droppedTickets.add(dropticket);
        }
    }
    
    private void spawnMercenary(final IPositionable pos, final SiegeGuardHolder holder) {
        final NpcTemplate template = NpcData.getInstance().getTemplate(holder.getNpcId());
        if (template != null) {
            final Defender npc = new Defender(template);
            npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
            npc.setDecayed(false);
            npc.setHeading(pos.getHeading());
            npc.spawnMe(pos.getX(), pos.getY(), pos.getZ() + 20);
            npc.scheduleDespawn(3000L);
            npc.setIsImmobilized(holder.isStationary());
        }
    }
    
    public void deleteTickets(final int castleId) {
        for (final Item ticket : SiegeGuardManager._droppedTickets) {
            if (ticket != null && this.getSiegeGuardByItem(castleId, ticket.getId()) != null) {
                ticket.decayMe();
                SiegeGuardManager._droppedTickets.remove(ticket);
            }
        }
    }
    
    public void removeTicket(final Item item) {
        final Castle castle = CastleManager.getInstance().getCastle(item);
        if (castle == null) {
            return;
        }
        final SiegeGuardHolder holder = this.getSiegeGuardByItem(castle.getId(), item.getId());
        if (holder == null) {
            return;
        }
        this.removeSiegeGuard(holder.getNpcId(), item);
        SiegeGuardManager._droppedTickets.remove(item);
    }
    
    private void loadSiegeGuard(final Castle castle) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_siege_guards Where castleId = ? And isHired = ?");
                try {
                    ps.setInt(1, castle.getId());
                    ps.setInt(2, (castle.getOwnerId() > 0) ? 1 : 0);
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            final Spawn spawn = new Spawn(rs.getInt("npcId"));
                            spawn.setAmount(1);
                            spawn.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                            spawn.setHeading(rs.getInt("heading"));
                            spawn.setRespawnDelay(rs.getInt("respawnDelay"));
                            spawn.setLocationId(0);
                            this.getSpawnedGuards(castle.getId()).add(spawn);
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
        catch (Exception e) {
            SiegeGuardManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, castle.getName(), e.getMessage()), (Throwable)e);
        }
    }
    
    public void removeSiegeGuard(final int npcId, final IPositionable pos) {
        ((SiegeDAO)DatabaseAccess.getDAO((Class)SiegeDAO.class)).deleteGuard(npcId, pos.getX(), pos.getY(), pos.getZ());
    }
    
    public void removeSiegeGuards(final Castle castle) {
        ((SiegeDAO)DatabaseAccess.getDAO((Class)SiegeDAO.class)).deleteGuardsOfCastle(castle.getId());
    }
    
    public void spawnSiegeGuard(final Castle castle) {
        try {
            final boolean isHired = castle.getOwnerId() > 0;
            this.loadSiegeGuard(castle);
            for (final Spawn spawn : this.getSpawnedGuards(castle.getId())) {
                if (spawn != null) {
                    spawn.init();
                    if (isHired || spawn.getRespawnDelay() == 0) {
                        spawn.stopRespawn();
                    }
                    final SiegeGuardHolder holder = this.getSiegeGuardByNpc(castle.getId(), spawn.getLastSpawn().getId());
                    if (holder == null) {
                        continue;
                    }
                    spawn.getLastSpawn().setIsImmobilized(holder.isStationary());
                }
            }
        }
        catch (Exception e) {
            SiegeGuardManager.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()), (Throwable)e);
        }
    }
    
    public void unspawnSiegeGuard(final Castle castle) {
        for (final Spawn spawn : this.getSpawnedGuards(castle.getId())) {
            if (spawn != null && spawn.getLastSpawn() != null) {
                spawn.stopRespawn();
                spawn.getLastSpawn().doDie(spawn.getLastSpawn());
            }
        }
        this.getSpawnedGuards(castle.getId()).clear();
    }
    
    public Set<Spawn> getSpawnedGuards(final int castleId) {
        return SiegeGuardManager._siegeGuardSpawn.computeIfAbsent(Integer.valueOf(castleId), key -> ConcurrentHashMap.newKeySet());
    }
    
    public static SiegeGuardManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SiegeGuardManager.class);
        _droppedTickets = ConcurrentHashMap.newKeySet();
        _siegeGuardSpawn = new ConcurrentHashMap<Integer, Set<Spawn>>();
    }
    
    private static class Singleton
    {
        protected static final SiegeGuardManager INSTANCE;
        
        static {
            INSTANCE = new SiegeGuardManager();
        }
    }
}
