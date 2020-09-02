// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.data.xml.FenceDataManager;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Summon;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Predicate;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.ai.CtrlEvent;
import java.util.Iterator;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import java.util.function.Consumer;
import java.util.Collection;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class World
{
    private static final Logger LOGGER;
    public static final int GRACIA_MAX_X = -166168;
    private static final int SHIFT_BY = 11;
    public static final int TILE_SIZE = 32768;
    public static final int TILE_X_MIN = 11;
    public static final int TILE_X_MAX = 28;
    public static final int TILE_Y_MIN = 10;
    public static final int TILE_Y_MAX = 26;
    public static final int TILE_ZERO_COORD_X = 20;
    public static final int TILE_ZERO_COORD_Y = 18;
    public static final int MAP_MIN_X = -294912;
    public static final int MAP_MAX_X = 294912;
    public static final int MAP_MIN_Y = -262144;
    public static final int MAP_MAX_Y = 294912;
    private static final int OFFSET_X;
    private static final int OFFSET_Y;
    private static final int REGIONS_X;
    private static final int REGIONS_Y;
    private final IntMap<Player> players;
    private final IntMap<WorldObject> objects;
    private final IntMap<Pet> pets;
    private final AtomicInteger partyNumber;
    private final AtomicInteger memberInPartyNumber;
    private final WorldRegion[][] regions;
    
    private World() {
        this.players = (IntMap<Player>)new CHashIntMap();
        this.objects = (IntMap<WorldObject>)new CHashIntMap();
        this.pets = (IntMap<Pet>)new CHashIntMap();
        this.partyNumber = new AtomicInteger();
        this.memberInPartyNumber = new AtomicInteger();
        this.regions = new WorldRegion[World.REGIONS_X + 1][World.REGIONS_Y + 1];
    }
    
    private void initRegions() {
        for (int x = 0; x <= World.REGIONS_X; ++x) {
            for (int y = 0; y <= World.REGIONS_Y; ++y) {
                if (Objects.isNull(this.regions[x][y])) {
                    this.regions[x][y] = new WorldRegion(x, y);
                }
                final List<WorldRegion> surroundingRegions = this.initSurroundingRegions(x, y);
                this.regions[x][y].setSurroundingRegions(surroundingRegions.toArray(WorldRegion[]::new));
            }
        }
        World.LOGGER.info("World Region Grid set up: {} by {}", (Object)World.REGIONS_X, (Object)World.REGIONS_Y);
    }
    
    private List<WorldRegion> initSurroundingRegions(final int rootX, final int rootY) {
        final List<WorldRegion> surroundingRegions = new ArrayList<WorldRegion>(9);
        for (int x = rootX - 1; x <= rootX + 1; ++x) {
            for (int y = rootY - 1; y <= rootY + 1; ++y) {
                if (x >= 0 && x <= World.REGIONS_X && y >= 0 && y <= World.REGIONS_Y) {
                    if (Objects.isNull(this.regions[x][y])) {
                        this.regions[x][y] = new WorldRegion(x, y);
                    }
                    surroundingRegions.add(this.regions[x][y]);
                }
            }
        }
        return surroundingRegions;
    }
    
    public void addObject(final WorldObject object) {
        if (this.objects.putIfAbsent(object.getObjectId(), (Object)object) != null) {
            World.LOGGER.warn("Object {} already exists in the world. Stack Trace: {}", (Object)object, (Object)CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
        }
        if (GameUtils.isPlayer(object)) {
            this.onPlayerEnter((Player)object);
        }
    }
    
    private void onPlayerEnter(final Player player) {
        final Player existingPlayer = (Player)this.players.putIfAbsent(player.getObjectId(), (Object)player);
        if (Objects.nonNull(existingPlayer)) {
            Disconnection.of(existingPlayer).defaultSequence(false);
            Disconnection.of(player).defaultSequence(false);
            World.LOGGER.warn("Duplicate character!? Disconnected both characters {})", (Object)player);
        }
    }
    
    public void removeObject(final WorldObject object) {
        this.objects.remove(object.getObjectId());
        if (GameUtils.isPlayer(object)) {
            this.players.remove(object.getObjectId());
        }
    }
    
    public WorldObject findObject(final int objectId) {
        return (WorldObject)this.objects.get(objectId);
    }
    
    public Collection<WorldObject> getVisibleObjects() {
        return (Collection<WorldObject>)this.objects.values();
    }
    
    public Collection<Player> getPlayers() {
        return (Collection<Player>)this.players.values();
    }
    
    public void forEachPlayer(final Consumer<Player> action) {
        this.players.values().forEach(action);
    }
    
    public Player findPlayer(final String name) {
        return this.findPlayer(PlayerNameTable.getInstance().getIdByName(name));
    }
    
    public Player findPlayer(final int objectId) {
        return (Player)this.players.get(objectId);
    }
    
    public Pet findPet(final int ownerId) {
        return (Pet)this.pets.get(ownerId);
    }
    
    public Pet addPet(final int ownerId, final Pet pet) {
        return (Pet)this.pets.put(ownerId, (Object)pet);
    }
    
    public void removePet(final int ownerId) {
        this.pets.remove(ownerId);
    }
    
    public void addVisibleObject(final WorldObject object, final WorldRegion newRegion) {
        if (Objects.isNull(newRegion)) {
            return;
        }
        newRegion.addVisibleObject(object);
        if (newRegion.isActive()) {
            this.forEachVisibleObject(object, WorldObject.class, wo -> this.beAwareOfEachOther(object, wo));
        }
    }
    
    private void beAwareOfEachOther(final WorldObject object, final WorldObject wo) {
        this.describeObjectToOther(object, wo);
        this.describeObjectToOther(wo, object);
        if (GameUtils.isNpc(wo) && GameUtils.isCreature(object)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc)wo, (Creature)object, GameUtils.isSummon(object)), wo);
        }
        if (GameUtils.isNpc(object) && GameUtils.isCreature(wo)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcCreatureSee((Npc)object, (Creature)wo, GameUtils.isSummon(wo)), object);
        }
    }
    
    private void describeObjectToOther(final WorldObject object, final WorldObject wo) {
        if (!GameUtils.isPlayer(object) || !wo.isVisibleFor((Player)object)) {
            return;
        }
        wo.sendInfo((Player)object);
        if (GameUtils.isCreature(wo)) {
            final CreatureAI ai = ((Creature)wo).getAI();
            if (Objects.nonNull(ai)) {
                ai.describeStateToPlayer((Player)object);
                if (GameUtils.isMonster(wo)) {
                    ai.setActiveIfIdle();
                }
            }
        }
    }
    
    public void removeVisibleObject(final WorldObject object, final WorldRegion oldRegion) {
        if (Util.isAnyNull(new Object[] { object, oldRegion })) {
            return;
        }
        oldRegion.removeVisibleObject(object);
        oldRegion.forEachObjectInSurrounding(Creature.class, other -> this.forgetEachOther(object, other), other -> !object.equals(other));
    }
    
    public void switchRegionIfNeed(final WorldObject object) {
        final WorldRegion newRegion = this.getRegion(object);
        final WorldRegion oldRegion;
        if (Objects.nonNull(newRegion) && !newRegion.equals(oldRegion = object.getWorldRegion())) {
            object.setWorldRegion(newRegion);
            if (Objects.nonNull(oldRegion)) {
                oldRegion.removeVisibleObject(object);
            }
            newRegion.addVisibleObject(object);
            this.switchRegion(object, oldRegion, newRegion);
        }
    }
    
    private void switchRegion(final WorldObject object, final WorldRegion oldRegion, final WorldRegion newRegion) {
        for (final WorldRegion region : newRegion.surroundingRegions()) {
            if (!region.isSurroundingRegion(oldRegion)) {
                for (final WorldObject other : region.objects()) {
                    if (!other.equals(object) && Objects.equals(other.getInstanceWorld(), object.getInstanceWorld())) {
                        this.beAwareOfEachOther(object, other);
                    }
                }
            }
        }
        if (Objects.nonNull(oldRegion)) {
            for (final WorldRegion region : oldRegion.surroundingRegions()) {
                if (!region.isSurroundingRegion(oldRegion)) {
                    for (final WorldObject other : region.objects()) {
                        if (!other.equals(object)) {
                            this.forgetEachOther(object, other);
                        }
                    }
                }
            }
        }
    }
    
    private void forgetEachOther(final WorldObject object, final WorldObject other) {
        this.forgetObject(object, other);
        this.forgetObject(other, object);
    }
    
    private void forgetObject(final WorldObject object, final WorldObject wo) {
        if (GameUtils.isCreature(object)) {
            final Creature objectCreature = (Creature)object;
            final CreatureAI ai = objectCreature.getAI();
            if (Objects.nonNull(ai)) {
                ai.notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, wo);
            }
        }
    }
    
    public WorldObject findVisibleObject(final WorldObject reference, final int objectId) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return null;
        }
        return region.findObjectInSurrounding(reference, objectId, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange());
    }
    
    public void forEachCreature(final Consumer<Creature> action) {
        final Stream filter = this.objects.values().stream().filter(GameUtils::isCreature);
        final Class<Creature> obj = Creature.class;
        Objects.requireNonNull(obj);
        filter.map(obj::cast).forEach(action);
    }
    
    public <T extends WorldObject> T findAnyVisibleObject(final WorldObject reference, final Class<T> clazz, final int range, final boolean includeReference, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return (T)((includeReference && clazz.isInstance(reference)) ? ((T)clazz.cast(reference)) : null);
        }
        return region.findAnyObjectInSurrounding(clazz, (Predicate<T>)and(isVisibleInRange(reference, range, includeReference), (Predicate<T>)filter));
    }
    
    public <T extends WorldObject> T findFirstVisibleObject(final WorldObject reference, final Class<T> clazz, final int range, final boolean includeReference, final Predicate<T> filter, final Comparator<T> comparator) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return (T)((includeReference && clazz.isInstance(reference)) ? ((T)clazz.cast(reference)) : null);
        }
        return region.findFirstObjectInSurrounding(clazz, (Predicate<T>)and(isVisibleInRange(reference, range, includeReference), (Predicate<T>)filter), comparator);
    }
    
    public boolean hasVisiblePlayer(final WorldObject object) {
        final WorldRegion region = this.getRegion(object);
        return !Objects.isNull(region) && region.hasObjectInSurrounding(Player.class, isVisibleInRange(object, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange(), false));
    }
    
    public <T extends WorldObject> boolean hasAnyVisibleObjectInRange(final WorldObject reference, final Class<T> clazz, final int range, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        return !Objects.isNull(region) && region.hasObjectInSurrounding(clazz, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter));
    }
    
    public <T extends WorldObject> void forEachVisibleObject(final WorldObject reference, final Class<T> clazz, final Consumer<T> action) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.nonNull(region)) {
            region.forEachObjectInSurrounding(clazz, action, isVisible(reference));
        }
    }
    
    public <T extends WorldObject> void forEachVisibleObject(final WorldObject reference, final Class<T> clazz, final Consumer<T> action, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.nonNull(region)) {
            region.forEachObjectInSurrounding(clazz, action, (Predicate<T>)and(isVisible(reference), (Predicate<T>)filter));
        }
    }
    
    public <T extends WorldObject> List<T> getVisibleObjectsInRange(final WorldObject reference, final Class<T> clazz, final int range, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return Collections.emptyList();
        }
        return region.findAllObjectsInSurrounding(clazz, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter));
    }
    
    public void forEachPlayerInRange(final WorldObject reference, final int range, final Consumer<Player> action, final Predicate<Player> filter) {
        this.forEachVisibleObjectInRange(reference, Player.class, range, action, filter);
    }
    
    public <T extends WorldObject> void forEachVisibleObjectInRange(final WorldObject reference, final Class<T> clazz, final int range, final Consumer<T> action) {
        this.forEachVisibleObjectInRange(reference, clazz, range, action, o -> true);
    }
    
    public <T extends WorldObject> void forEachVisibleObjectInRange(final WorldObject reference, final Class<T> clazz, final int range, final Consumer<T> action, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return;
        }
        region.forEachObjectInSurrounding(clazz, action, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter));
    }
    
    public <T extends WorldObject> void forVisibleObjectsInRange(final WorldObject reference, final Class<T> clazz, final int range, final int maxObjects, final Predicate<T> filter, final Consumer<T> action) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return;
        }
        region.forEachObjectInSurroundingLimiting(clazz, maxObjects, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter), action);
    }
    
    public <T extends WorldObject> void forVisibleOrderedObjectsInRange(final WorldObject reference, final Class<T> clazz, final int range, final int maxObjects, final Predicate<T> filter, final Comparator<T> comparator, final Consumer<? super T> action) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return;
        }
        region.forEachOrderedObjectInSurrounding(clazz, maxObjects, comparator, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter), action);
    }
    
    public <T extends WorldObject> void forAnyVisibleObject(final WorldObject reference, final Class<T> clazz, final Consumer<T> action, final Predicate<T> filter) {
        this.forAnyVisibleObjectInRange(reference, clazz, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange(), action, filter);
    }
    
    public <T extends WorldObject> void forAnyVisibleObjectInRange(final WorldObject reference, final Class<T> clazz, final int range, final Consumer<T> action, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        if (Objects.isNull(region)) {
            return;
        }
        region.forAnyObjectInSurrounding(clazz, action, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter));
    }
    
    public <T extends WorldObject> boolean checkAnyVisibleObjectInRange(final WorldObject reference, final Class<T> clazz, final int range, final Predicate<T> filter) {
        final WorldRegion region = this.getRegion(reference);
        return !Objects.isNull(region) && region.hasObjectInSurrounding(clazz, (Predicate<T>)and(isVisibleInRange(reference, range, false), (Predicate<T>)filter));
    }
    
    public WorldRegion getRegion(final WorldObject object) {
        if (Objects.isNull(object)) {
            return null;
        }
        try {
            return this.regions[(object.getX() >> 11) + World.OFFSET_X][(object.getY() >> 11) + World.OFFSET_Y];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            this.disposeOutOfBoundsObject(object);
            return null;
        }
    }
    
    public WorldRegion getRegion(final int x, final int y) {
        try {
            return this.regions[(x >> 11) + World.OFFSET_X][(y >> 11) + World.OFFSET_Y];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            World.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), (x >> 11) + World.OFFSET_X, (y >> 11) + World.OFFSET_Y));
            return null;
        }
    }
    
    public synchronized void disposeOutOfBoundsObject(final WorldObject object) {
        if (GameUtils.isPlayer(object)) {
            final Player player = (Player)object;
            player.stopMove(player.getLastServerPosition());
        }
        else if (GameUtils.isSummon(object)) {
            final Summon summon = (Summon)object;
            summon.unSummon(summon.getOwner());
        }
        else if (Objects.nonNull(this.objects.remove(object.getObjectId()))) {
            if (GameUtils.isNpc(object)) {
                final Npc npc = (Npc)object;
                npc.deleteMe();
                World.LOGGER.warn("Deleting npc {} from invalid location X:{} Y:{} Z: {}", new Object[] { npc, object.getX(), object.getY(), object.getZ() });
                final Spawn spawn = npc.getSpawn();
                if (Objects.nonNull(spawn)) {
                    World.LOGGER.warn("Spawn location X:{} Y:{} Z:{} Heading:{}", new Object[] { spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading() });
                }
            }
            else if (GameUtils.isCreature(object)) {
                World.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIII)Ljava/lang/String;, object.getName(), object.getObjectId(), object.getX(), object.getY(), object.getZ()));
                ((Creature)object).deleteMe();
            }
            if (Objects.nonNull(object.getWorldRegion())) {
                object.getWorldRegion().removeVisibleObject(object);
            }
        }
    }
    
    public void incrementParty() {
        this.partyNumber.incrementAndGet();
    }
    
    public void decrementParty() {
        this.partyNumber.decrementAndGet();
    }
    
    public void incrementPartyMember() {
        this.memberInPartyNumber.incrementAndGet();
    }
    
    public void decrementPartyMember() {
        this.memberInPartyNumber.decrementAndGet();
    }
    
    public int getPartyCount() {
        return this.partyNumber.get();
    }
    
    public int getPartyMemberCount() {
        return this.memberInPartyNumber.get();
    }
    
    public static <T extends WorldObject> Predicate<T> and(final Predicate<T> p1, final Predicate<T> p2) {
        return p1.and(p2);
    }
    
    public static void init() {
        getInstance().initRegions();
        MapRegionManager.init();
        ZoneManager.init();
        GeoEngine.init();
        WorldTimeController.init();
        DoorDataManager.init();
        FenceDataManager.init();
    }
    
    private static <T extends WorldObject> Predicate<T> isVisible(final WorldObject reference) {
        return isVisible(reference, false);
    }
    
    private static <T extends WorldObject> Predicate<T> isVisible(final WorldObject reference, final boolean includeReference) {
        return object -> Objects.nonNull(object) && (includeReference || !object.equals(reference)) && Objects.equals(object.getInstanceWorld(), reference.getInstanceWorld());
    }
    
    private static <T extends WorldObject> Predicate<T> isVisibleInRange(final WorldObject reference, final int range, final boolean includeReference) {
        return object -> isVisible(reference, includeReference).test(object) && MathUtil.isInsideRadius3D(reference, object, range);
    }
    
    public static World getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)World.class);
        OFFSET_X = Math.abs(-144);
        OFFSET_Y = Math.abs(-128);
        REGIONS_X = 144 + World.OFFSET_X;
        REGIONS_Y = 144 + World.OFFSET_Y;
    }
    
    private static class Singleton
    {
        private static final World INSTANCE;
        
        static {
            INSTANCE = new World();
        }
    }
}
