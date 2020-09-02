// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import org.l2j.gameserver.model.entity.Siege;
import java.util.function.Consumer;
import java.util.function.Function;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.ClanMember;
import java.util.Collection;
import org.l2j.gameserver.model.Clan;
import java.util.Iterator;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.WorldObject;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import java.time.LocalDateTime;
import org.l2j.gameserver.model.entity.Castle;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.InstanceListManager;

public final class CastleManager implements InstanceListManager
{
    private static final Logger LOGGER;
    private static final int[] castleCirclets;
    private final IntMap<Castle> castles;
    private final IntMap<LocalDateTime> castleSiegesDate;
    
    private CastleManager() {
        this.castles = (IntMap<Castle>)new HashIntMap();
        this.castleSiegesDate = (IntMap<LocalDateTime>)new CHashIntMap();
    }
    
    public final Castle findNearestCastle(final WorldObject obj) {
        return this.findNearestCastle(obj, Long.MAX_VALUE);
    }
    
    public final Castle findNearestCastle(final WorldObject obj, long maxDistance) {
        Castle nearestCastle = this.getCastle(obj);
        if (nearestCastle == null) {
            for (final Castle castle : this.castles.values()) {
                final double distance = castle.getDistance(obj);
                if (maxDistance > distance) {
                    maxDistance = (long)distance;
                    nearestCastle = castle;
                }
            }
        }
        return nearestCastle;
    }
    
    public final Castle getCastleById(final int castleId) {
        return (Castle)this.castles.get(castleId);
    }
    
    public final Castle getCastleByOwner(final Clan clan) {
        for (final Castle temp : this.castles.values()) {
            if (temp.getOwnerId() == clan.getId()) {
                return temp;
            }
        }
        return null;
    }
    
    public final Castle getCastle(final String name) {
        for (final Castle temp : this.castles.values()) {
            if (temp.getName().equalsIgnoreCase(name.trim())) {
                return temp;
            }
        }
        return null;
    }
    
    public final Castle getCastle(final int x, final int y, final int z) {
        return this.castles.values().stream().filter(c -> c.checkIfInZone(x, y, z)).findFirst().orElse(null);
    }
    
    public final Castle getCastle(final ILocational loc) {
        return this.getCastle(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public final Collection<Castle> getCastles() {
        return (Collection<Castle>)this.castles.values();
    }
    
    public int getCircletByCastleId(final int castleId) {
        if (castleId > 0 && castleId < 10) {
            return CastleManager.castleCirclets[castleId];
        }
        return 0;
    }
    
    public void removeCirclet(final Clan clan, final int castleId) {
        for (final ClanMember member : clan.getMembers()) {
            this.removeCirclet(member, castleId);
        }
    }
    
    public void removeCirclet(final ClanMember member, final int castleId) {
        if (member == null) {
            return;
        }
        final Player player = member.getPlayerInstance();
        final int circletId = this.getCircletByCastleId(castleId);
        if (circletId != 0) {
            if (player != null) {
                try {
                    final Item circlet = player.getInventory().getItemByItemId(circletId);
                    if (circlet != null) {
                        if (circlet.isEquipped()) {
                            player.getInventory().unEquipItemInSlot(InventorySlot.fromId(circlet.getLocationSlot()));
                        }
                        player.destroyItemByItemId("CastleCircletRemoval", circletId, 1L, player, true);
                    }
                    return;
                }
                catch (NullPointerException ex) {}
            }
            ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).deleteByIdAndOwner(circletId, member.getObjectId());
        }
    }
    
    @Override
    public void loadInstances() {
        ((CastleDAO)DatabaseAccess.getDAO((Class)CastleDAO.class)).findAll().stream().map((Function<? super Object, ?>)Castle::new).forEach(c -> this.castles.put(c.getId(), (Object)c));
        CastleManager.LOGGER.info("Loaded {} castles", (Object)this.castles.size());
    }
    
    @Override
    public void updateReferences() {
    }
    
    @Override
    public void activateInstances() {
        this.castles.values().forEach(Castle::activateInstance);
    }
    
    public void registerSiegeDate(final Castle castle, final LocalDateTime siegeDate) {
        castle.setSiegeDate(siegeDate);
        this.castleSiegesDate.put(castle.getId(), (Object)siegeDate);
    }
    
    public Siege getSiegeOnLocation(final ILocational loc) {
        return this.castles.values().stream().map((Function<? super Object, ? extends Siege>)Castle::getSiege).filter(s -> s.checkIfInZone(loc)).findFirst().orElse(null);
    }
    
    public int getSiegesOnDate(final LocalDateTime siegeDate) {
        return (int)this.castleSiegesDate.values().stream().filter(date -> ChronoUnit.DAYS.between(siegeDate, date) == 0L).count();
    }
    
    public static void init() {
        getInstance().loadInstances();
    }
    
    public static CastleManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CastleManager.class);
        castleCirclets = new int[] { 0, 6838, 6835, 6839, 6837, 6840, 6834, 6836, 8182, 8183 };
    }
    
    private static class Singleton
    {
        private static final CastleManager INSTANCE;
        
        static {
            INSTANCE = new CastleManager();
        }
    }
}
