// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.handler.ActionShiftHandler;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.handler.ActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Map;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.world.WorldRegion;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.interfaces.IDecayable;
import org.l2j.gameserver.model.interfaces.IUniqueId;
import org.l2j.gameserver.model.interfaces.ISpawnable;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class WorldObject extends ListenersContainer implements IIdentifiable, INamable, ISpawnable, IUniqueId, IDecayable, IPositionable
{
    private String name;
    protected int objectId;
    private WorldRegion worldRegion;
    private InstanceType instanceType;
    private volatile Map<String, Object> scripts;
    private volatile int x;
    private volatile int y;
    private volatile int z;
    private volatile int _heading;
    private volatile boolean spawned;
    private Instance instance;
    private boolean invisible;
    private boolean targetable;
    
    public WorldObject(final int objectId) {
        this.x = 0;
        this.y = 0;
        this.z = -10000;
        this._heading = 0;
        this.targetable = true;
        this.setInstanceType(InstanceType.L2Object);
        this.objectId = objectId;
    }
    
    public final InstanceType getInstanceType() {
        return this.instanceType;
    }
    
    protected final void setInstanceType(final InstanceType newInstanceType) {
        this.instanceType = newInstanceType;
    }
    
    public final boolean isInstanceTypes(final InstanceType... instanceTypes) {
        return this.instanceType.isTypes(instanceTypes);
    }
    
    public final void onAction(final Player player) {
        this.onAction(player, true);
    }
    
    public void onAction(final Player player, final boolean interact) {
        final IActionHandler handler = ActionHandler.getInstance().getHandler(this.getInstanceType());
        if (Objects.nonNull(handler)) {
            handler.action(player, this, interact);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    public void onActionShift(final Player player) {
        final IActionShiftHandler handler = ActionShiftHandler.getInstance().getHandler(this.getInstanceType());
        if (Objects.nonNull(handler)) {
            handler.action(player, this, true);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    public void onForcedAttack(final Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    public void onSpawn() {
    }
    
    @Override
    public boolean decayMe() {
        this.spawned = false;
        World.getInstance().removeVisibleObject(this, this.worldRegion);
        World.getInstance().removeObject(this);
        return true;
    }
    
    public void refreshID() {
        World.getInstance().removeObject(this);
        IdFactory.getInstance().releaseId(this.getObjectId());
        this.objectId = IdFactory.getInstance().getNextId();
    }
    
    @Override
    public final boolean spawnMe() {
        synchronized (this) {
            this.spawned = true;
            this.setWorldRegion(World.getInstance().getRegion(this));
            World.getInstance().addObject(this);
            this.worldRegion.addVisibleObject(this);
        }
        World.getInstance().addVisibleObject(this, this.worldRegion);
        this.onSpawn();
        return true;
    }
    
    public final void spawnMe(final int x, final int y, final int z) {
        synchronized (this) {
            this.setXYZ(x, y, z);
        }
        this.spawnMe();
    }
    
    public boolean canBeAttacked() {
        return false;
    }
    
    public abstract boolean isAutoAttackable(final Creature attacker);
    
    public final boolean isSpawned() {
        return this.spawned;
    }
    
    public final void setSpawned(final boolean value) {
        this.spawned = value;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    @Override
    public final int getObjectId() {
        return this.objectId;
    }
    
    public abstract void sendInfo(final Player activeChar);
    
    public void sendPacket(final ServerPacket... packets) {
    }
    
    public void sendPacket(final SystemMessageId id) {
    }
    
    public Player getActingPlayer() {
        return null;
    }
    
    public boolean isServitor() {
        return false;
    }
    
    public boolean isVehicle() {
        return false;
    }
    
    public boolean isTargetable() {
        return this.targetable;
    }
    
    public void setTargetable(final boolean targetable) {
        if (this.targetable != targetable && !(this.targetable = targetable)) {
            World.getInstance().forEachVisibleObject(this, Creature.class, Creature::forgetTarget, creature -> this == creature.getTarget());
        }
    }
    
    public boolean isInsideZone(final ZoneType zone) {
        return false;
    }
    
    public final <T> T addScript(final T script) {
        if (this.scripts == null) {
            synchronized (this) {
                if (this.scripts == null) {
                    this.scripts = new ConcurrentHashMap<String, Object>();
                }
            }
        }
        this.scripts.put(script.getClass().getName(), script);
        return script;
    }
    
    public final <T> T removeScript(final Class<T> script) {
        if (this.scripts == null) {
            return null;
        }
        return (T)this.scripts.remove(script.getName());
    }
    
    public final <T> T getScript(final Class<T> script) {
        if (this.scripts == null) {
            return null;
        }
        return (T)this.scripts.get(script.getName());
    }
    
    public void removeStatusListener(final Creature object) {
    }
    
    public final void setXYZInvisible(final int x, final int y, final int z) {
        this.setSpawned(false);
        this.setXYZ(x, y, z);
    }
    
    public final void setLocationInvisible(final ILocational loc) {
        this.setXYZInvisible(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public final WorldRegion getWorldRegion() {
        return this.worldRegion;
    }
    
    public void setWorldRegion(final WorldRegion value) {
        this.worldRegion = value;
    }
    
    @Override
    public int getX() {
        return this.x;
    }
    
    @Override
    public int getY() {
        return this.y;
    }
    
    @Override
    public int getZ() {
        return this.z;
    }
    
    @Override
    public int getHeading() {
        return this._heading;
    }
    
    @Override
    public void setHeading(final int newHeading) {
        this._heading = newHeading;
    }
    
    public int getInstanceId() {
        return Util.zeroIfNullOrElse((Object)this.instance, Instance::getId);
    }
    
    public boolean isInInstance() {
        return this.instance != null;
    }
    
    public Instance getInstanceWorld() {
        return this.instance;
    }
    
    @Override
    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this._heading);
    }
    
    @Override
    public void setLocation(final Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this._heading = loc.getHeading();
    }
    
    @Override
    public void setXYZ(int x, int y, final int z) {
        if (x > 294912) {
            x = 289912;
        }
        if (x < -294912) {
            x = -289912;
        }
        if (y > 294912) {
            y = 289912;
        }
        if (y < -262144) {
            y = -257144;
        }
        this.x = x;
        this.y = y;
        this.z = z;
        if (this.spawned) {
            World.getInstance().switchRegionIfNeed(this);
        }
    }
    
    @Override
    public void setXYZ(final ILocational loc) {
        this.setXYZ(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public void setInstanceById(final int id) {
        final Instance instance = InstanceManager.getInstance().getInstance(id);
        if (id != 0 && instance == null) {
            return;
        }
        this.setInstance(instance);
    }
    
    public synchronized void setInstance(final Instance newInstance) {
        if (this.instance == newInstance) {
            return;
        }
        if (this.instance != null) {
            this.instance.onInstanceChange(this, false);
        }
        if ((this.instance = newInstance) != null) {
            newInstance.onInstanceChange(this, true);
        }
    }
    
    public double calculateDirectionTo(final ILocational target) {
        return MathUtil.calculateAngleFrom(this, target);
    }
    
    public boolean isInvisible() {
        return this.invisible;
    }
    
    public void setInvisible(final boolean invis) {
        this.invisible = invis;
        if (invis) {
            final DeleteObject deletePacket = new DeleteObject(this);
            final ServerPacket serverPacket;
            World.getInstance().forEachVisibleObject(this, Player.class, player -> {
                if (!this.isVisibleFor(player)) {
                    player.sendPacket(serverPacket);
                }
                return;
            });
        }
        this.broadcastInfo();
    }
    
    public boolean isVisibleFor(final Player player) {
        return !this.invisible || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS);
    }
    
    public void broadcastInfo() {
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (this.isVisibleFor(player)) {
                this.sendInfo(player);
            }
        });
    }
    
    public boolean isInvul() {
        return false;
    }
    
    public boolean isInSurroundingRegion(final WorldObject worldObject) {
        return Objects.nonNull(this.worldRegion) && this.worldRegion.isInSurroundingRegion(worldObject);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof WorldObject && ((WorldObject)obj).getObjectId() == this.getObjectId();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this.name, this.objectId);
    }
}
