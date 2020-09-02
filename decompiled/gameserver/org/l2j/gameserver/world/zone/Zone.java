// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import java.util.function.Function;
import java.util.stream.Stream;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.function.Predicate;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Arrays;
import java.util.Objects;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.InstanceType;
import org.slf4j.Logger;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class Zone extends ListenersContainer
{
    private static final Logger LOGGER;
    public static final int FIGHTER = 1;
    public static final int MAGE = 2;
    private final int id;
    private boolean enabled;
    private boolean checkAffected;
    private String name;
    private int minLvl;
    private int maxLvl;
    private int[] races;
    private int[] classes;
    private char classType;
    private InstanceType target;
    private boolean allowStore;
    private AbstractZoneSettings settings;
    private int instanceTemplateId;
    protected ZoneArea area;
    protected IntMap<Creature> creatures;
    
    protected Zone(final int id) {
        this.checkAffected = false;
        this.name = null;
        this.target = InstanceType.Creature;
        this.creatures = (IntMap<Creature>)new CHashIntMap();
        this.id = id;
        this.maxLvl = 255;
        this.allowStore = true;
        this.enabled = true;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setParameter(final String name, final String value) {
        this.checkAffected = true;
        switch (name) {
            default: {
                Zone.LOGGER.warn("Unknown parameter - {} in zone: {}", (Object)name, (Object)this.id);
                break;
            }
            case "name": {
                this.name = value;
                break;
            }
            case "affectedLvlMin": {
                this.minLvl = Integer.parseInt(value);
                break;
            }
            case "affectedLvlMax": {
                this.maxLvl = Integer.parseInt(value);
                break;
            }
            case "affectedClassType": {
                this.classType = (char)(value.equalsIgnoreCase("Fighter") ? 1 : 2);
                break;
            }
            case "targetClass": {
                this.target = Enum.valueOf(InstanceType.class, value);
                break;
            }
            case "allowStore": {
                this.allowStore = Boolean.parseBoolean(value);
                break;
            }
            case "default_enabled": {
                this.enabled = Boolean.parseBoolean(value);
                break;
            }
            case "instanceId": {
                this.instanceTemplateId = Integer.parseInt(value);
                break;
            }
            case "affectedRace": {
                if (Objects.isNull(this.races)) {
                    (this.races = new int[1])[0] = Integer.parseInt(value);
                    break;
                }
                (this.races = Arrays.copyOf(this.races, this.races.length + 1))[this.races.length - 1] = Integer.parseInt(value);
                break;
            }
            case "affectedClassId": {
                if (Objects.isNull(this.classes)) {
                    (this.classes = new int[1])[0] = Integer.parseInt(value);
                    break;
                }
                (this.classes = Arrays.copyOf(this.classes, this.classes.length + 1))[this.classes.length - 1] = Integer.parseInt(value);
                break;
            }
        }
    }
    
    private boolean isAffected(final Creature creature) {
        if (!this.isEnabled()) {
            return false;
        }
        final Instance instance = creature.getInstanceWorld();
        if (Objects.nonNull(instance)) {
            if (instance.getTemplateId() != this.instanceTemplateId) {
                return false;
            }
        }
        else if (this.instanceTemplateId > 0) {
            return false;
        }
        if (creature.getLevel() < this.minLvl || creature.getLevel() > this.maxLvl) {
            return false;
        }
        if (!creature.isInstanceTypes(this.target)) {
            return false;
        }
        if (GameUtils.isPlayer(creature)) {
            final boolean isMage = ((Player)creature).isMageClass();
            if ((isMage && this.classType == '\u0001') || (!isMage && this.classType == '\u0002')) {
                return false;
            }
            if (Objects.nonNull(this.races) && Arrays.stream(this.races).noneMatch(id -> id == creature.getRace().ordinal())) {
                return false;
            }
            if (Objects.nonNull(this.classes)) {
                return Arrays.stream(this.classes).anyMatch(id -> id == ((Player)creature).getClassId().getId());
            }
        }
        return true;
    }
    
    public boolean isInsideZone(final int x, final int y, final int z) {
        return this.area.isInsideZone(x, y, z);
    }
    
    public boolean isInsideZone(final int x, final int y) {
        return this.isInsideZone(x, y, this.area.getHighZ());
    }
    
    public boolean isInsideZone(final ILocational loc) {
        return this.isInsideZone(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public boolean isInsideZone(final WorldObject object) {
        return this.isInsideZone(object.getX(), object.getY(), object.getZ());
    }
    
    public double getDistanceToZone(final WorldObject object) {
        return this.area.getDistanceToZone(object.getX(), object.getY());
    }
    
    protected void revalidateInZone(final Creature creature) {
        if (this.isInsideZone(creature)) {
            if (this.checkAffected && !this.isAffected(creature)) {
                return;
            }
            if (this.creatures.putIfAbsent(creature.getObjectId(), (Object)creature) == null) {
                this.onEnter(creature);
                EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneEnter(creature, this), this);
            }
        }
        else {
            this.removeCreature(creature);
        }
    }
    
    protected void removeCreature(final Creature creature) {
        if (this.creatures.containsKey(creature.getObjectId())) {
            this.creatures.remove(creature.getObjectId());
            this.onExit(creature);
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneExit(creature, this), this);
        }
    }
    
    public boolean isCreatureInZone(final Creature creature) {
        return this.creatures.containsKey(creature.getObjectId());
    }
    
    protected void onDieInside(final Creature creature) {
    }
    
    protected void onReviveInside(final Creature creature) {
    }
    
    public void onPlayerLoginInside(final Player player) {
    }
    
    public void onPlayerLogoutInside(final Player player) {
    }
    
    public void forEachCreature(final Consumer<Creature> action) {
        this.creatures.values().forEach(action);
    }
    
    public void forEachCreature(final Consumer<Creature> action, final Predicate<Creature> filter) {
        this.creatures.values().stream().filter(filter).forEach(action);
    }
    
    public void forAnyCreature(final Consumer<Creature> action, final Predicate<Creature> filter) {
        this.creatures.values().stream().filter(filter).findAny().ifPresent(action);
    }
    
    public void forEachPlayer(final Consumer<Player> action, final Predicate<Player> filter) {
        this.toPlayerStream(this.creatures.values().stream()).filter(filter).forEach(action);
    }
    
    public void forEachPlayer(final Consumer<Player> action) {
        this.toPlayerStream(this.creatures.values().stream()).forEach(action);
    }
    
    public long getPlayersInsideCount() {
        return this.creatures.values().stream().filter(GameUtils::isPlayer).count();
    }
    
    public void broadcastPacket(final ServerPacket packet) {
        if (this.creatures.isEmpty()) {
            return;
        }
        final Stream<Player> playerStream = this.toPlayerStream(this.creatures.values().parallelStream());
        Objects.requireNonNull(packet);
        playerStream.forEach(packet::sendTo);
    }
    
    private Stream<Player> toPlayerStream(final Stream<Creature> stream) {
        return stream.filter(GameUtils::isPlayer).map((Function<? super Creature, ? extends Player>)WorldObject::getActingPlayer);
    }
    
    public void visualizeZone(final int z) {
        this.area.visualizeZone(z);
    }
    
    public void oustAllPlayers() {
        if (this.creatures.isEmpty()) {
            return;
        }
        this.toPlayerStream(this.creatures.values().parallelStream()).forEach(player -> player.teleToLocation(TeleportWhereType.TOWN));
    }
    
    public void movePlayersTo(final Location loc) {
        if (this.creatures.isEmpty()) {
            return;
        }
        this.toPlayerStream(this.creatures.values().parallelStream()).forEach(p -> p.teleToLocation(loc));
    }
    
    public ZoneArea getArea() {
        return this.area;
    }
    
    public void setArea(final ZoneArea area) {
        if (this.area != null) {
            throw new IllegalStateException("Zone already set");
        }
        this.area = area;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public AbstractZoneSettings getSettings() {
        return this.settings;
    }
    
    public void setSettings(final AbstractZoneSettings settings) {
        if (this.settings != null) {
            this.settings.clear();
        }
        this.settings = settings;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean state) {
        this.enabled = state;
    }
    
    public InstanceType getTargetType() {
        return this.target;
    }
    
    public void setTargetType(final InstanceType type) {
        this.target = type;
        this.checkAffected = true;
    }
    
    protected boolean getAllowStore() {
        return this.allowStore;
    }
    
    public int getInstanceTemplateId() {
        return this.instanceTemplateId;
    }
    
    protected abstract void onEnter(final Creature character);
    
    protected abstract void onExit(final Creature character);
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.id, this.name);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Zone.class);
    }
}
