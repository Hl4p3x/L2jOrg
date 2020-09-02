// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.world.zone.ZoneRegion;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Iterator;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.VehicleStats;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.InstanceType;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;

public abstract class Vehicle extends Creature
{
    protected final Set<Player> _passengers;
    protected int _dockId;
    protected Location _oustLoc;
    protected VehiclePathPoint[] _currentPath;
    protected int _runState;
    private Runnable _engine;
    
    public Vehicle(final CreatureTemplate template) {
        super(template);
        this._passengers = (Set<Player>)ConcurrentHashMap.newKeySet();
        this._dockId = 0;
        this._oustLoc = null;
        this._currentPath = null;
        this._runState = 0;
        this._engine = null;
        this.setInstanceType(InstanceType.L2Vehicle);
        this.setIsFlying(true);
    }
    
    public boolean isBoat() {
        return false;
    }
    
    public boolean canBeControlled() {
        return this._engine == null;
    }
    
    public void registerEngine(final Runnable r) {
        this._engine = r;
    }
    
    public void runEngine(final int delay) {
        if (this._engine != null) {
            ThreadPool.schedule(this._engine, (long)delay);
        }
    }
    
    public void executePath(final VehiclePathPoint[] path) {
        this._runState = 0;
        this._currentPath = path;
        if (this._currentPath != null && this._currentPath.length > 0) {
            final VehiclePathPoint point = this._currentPath[0];
            if (point.getMoveSpeed() > 0) {
                this.getStats().setMoveSpeed((float)point.getMoveSpeed());
            }
            if (point.getRotationSpeed() > 0) {
                this.getStats().setRotationSpeed(point.getRotationSpeed());
            }
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(point.getX(), point.getY(), point.getZ(), 0));
            return;
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }
    
    @Override
    public boolean moveToNextRoutePoint() {
        this._move = null;
        if (this._currentPath != null) {
            ++this._runState;
            if (this._runState < this._currentPath.length) {
                final VehiclePathPoint point = this._currentPath[this._runState];
                if (!this.isMovementDisabled()) {
                    if (point.getMoveSpeed() != 0) {
                        if (point.getMoveSpeed() > 0) {
                            this.getStats().setMoveSpeed((float)point.getMoveSpeed());
                        }
                        if (point.getRotationSpeed() > 0) {
                            this.getStats().setRotationSpeed(point.getRotationSpeed());
                        }
                        final MoveData m = new MoveData();
                        m.disregardingGeodata = false;
                        m.onGeodataPathIndex = -1;
                        m._xDestination = point.getX();
                        m._yDestination = point.getY();
                        m._zDestination = point.getZ();
                        m._heading = 0;
                        final double distance = MathUtil.calculateDistance2D(point, this);
                        if (distance > 1.0) {
                            this.setHeading(MathUtil.calculateHeadingFrom(this, point));
                        }
                        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();
                        this._move = m;
                        WorldTimeController.getInstance().registerMovingObject(this);
                        return true;
                    }
                    point.setHeading(point.getRotationSpeed());
                    this.teleToLocation(point, false);
                    this._currentPath = null;
                }
            }
            else {
                this._currentPath = null;
            }
        }
        this.runEngine(10);
        return false;
    }
    
    @Override
    public VehicleStats getStats() {
        return (VehicleStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new VehicleStats(this));
    }
    
    public boolean isInDock() {
        return this._dockId > 0;
    }
    
    public void setInDock(final int d) {
        this._dockId = d;
    }
    
    public int getDockId() {
        return this._dockId;
    }
    
    public Location getOustLoc() {
        return (this._oustLoc != null) ? this._oustLoc : MapRegionManager.getInstance().getTeleToLocation(this, TeleportWhereType.TOWN);
    }
    
    public void setOustLoc(final Location loc) {
        this._oustLoc = loc;
    }
    
    public void oustPlayers() {
        final Iterator<Player> iter = this._passengers.iterator();
        while (iter.hasNext()) {
            final Player player = iter.next();
            iter.remove();
            if (player != null) {
                this.oustPlayer(player);
            }
        }
    }
    
    public void oustPlayer(final Player player) {
        player.setVehicle(null);
        player.setInVehiclePosition(null);
        this.removePassenger(player);
    }
    
    public boolean addPassenger(final Player player) {
        if (player == null || this._passengers.contains(player)) {
            return false;
        }
        if (player.getVehicle() != null && player.getVehicle() != this) {
            return false;
        }
        this._passengers.add(player);
        return true;
    }
    
    public void removePassenger(final Player player) {
        try {
            this._passengers.remove(player);
        }
        catch (Exception ex) {}
    }
    
    public boolean isEmpty() {
        return this._passengers.isEmpty();
    }
    
    public Set<Player> getPassengers() {
        return this._passengers;
    }
    
    public void broadcastToPassengers(final ServerPacket sm) {
        for (final Player player : this._passengers) {
            if (player != null) {
                player.sendPacket(sm);
            }
        }
    }
    
    public void payForRide(final int itemId, final int count, final int oustX, final int oustY, final int oustZ) {
        Item ticket;
        InventoryUpdate iu;
        World.getInstance().forEachVisibleObjectInRange(this, Player.class, 1000, player -> {
            if (player.isInBoat() && player.getBoat() == this) {
                if (itemId > 0) {
                    ticket = player.getInventory().getItemByItemId(itemId);
                    if (ticket == null || player.getInventory().destroyItem("Boat", ticket, count, player, this) == null) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                        player.teleToLocation(new Location(oustX, oustY, oustZ), true);
                        return;
                    }
                    else {
                        iu = new InventoryUpdate();
                        iu.addModifiedItem(ticket);
                        player.sendInventoryUpdate(iu);
                    }
                }
                this.addPassenger(player);
            }
        });
    }
    
    @Override
    public boolean updatePosition() {
        final boolean result = super.updatePosition();
        for (final Player player : this._passengers) {
            if (player != null && player.getVehicle() == this) {
                player.setXYZ(this.getX(), this.getY(), this.getZ());
                player.revalidateZone(false);
            }
        }
        return result;
    }
    
    @Override
    public void teleToLocation(final ILocational loc, final boolean allowRandomOffset) {
        if (this.isMoving()) {
            this.stopMove(null);
        }
        this.setIsTeleporting(true);
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        for (final Player player : this._passengers) {
            if (player != null) {
                player.teleToLocation(loc, false);
            }
        }
        this.decayMe();
        this.setXYZ(loc);
        if (loc.getHeading() != 0) {
            this.setHeading(loc.getHeading());
        }
        this.onTeleported();
        this.revalidateZone(true);
    }
    
    @Override
    public void stopMove(final Location loc) {
        this._move = null;
        if (loc != null) {
            this.setXYZ(loc);
            this.setHeading(loc.getHeading());
            this.revalidateZone(true);
        }
    }
    
    @Override
    public boolean deleteMe() {
        this._engine = null;
        try {
            if (this.isMoving()) {
                this.stopMove(null);
            }
        }
        catch (Exception e) {
            Vehicle.LOGGER.error("Failed stopMove().", (Throwable)e);
        }
        try {
            this.oustPlayers();
        }
        catch (Exception e) {
            Vehicle.LOGGER.error("Failed oustPlayers().", (Throwable)e);
        }
        final ZoneRegion oldZoneRegion = ZoneManager.getInstance().getRegion(this);
        try {
            this.decayMe();
        }
        catch (Exception e2) {
            Vehicle.LOGGER.error("Failed decayMe().", (Throwable)e2);
        }
        oldZoneRegion.removeFromZones(this);
        return super.deleteMe();
    }
    
    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }
    
    @Override
    public int getLevel() {
        return 0;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }
    
    @Override
    public void detachAI() {
    }
    
    @Override
    public boolean isVehicle() {
        return true;
    }
}
