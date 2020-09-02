// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.function.Function;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.Consumer;
import org.l2j.commons.threading.ThreadPool;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.WorldObject;
import io.github.joealisson.primitive.IntMap;

public final class WorldRegion
{
    private final IntMap<WorldObject> objects;
    private final Object taskLocker;
    private final int regionX;
    private final int regionY;
    private WorldRegion[] surroundingRegions;
    private ScheduledFuture<?> neighborsTask;
    private boolean active;
    private final AtomicInteger playersInside;
    
    WorldRegion(final int regionX, final int regionY) {
        this.objects = (IntMap<WorldObject>)new CHashIntMap();
        this.taskLocker = new Object();
        this.neighborsTask = null;
        this.playersInside = new AtomicInteger(0);
        this.regionX = regionX;
        this.regionY = regionY;
    }
    
    public void addVisibleObject(final WorldObject object) {
        if (Objects.isNull(object)) {
            return;
        }
        if (Objects.isNull(this.objects.put(object.getObjectId(), (Object)object)) && GameUtils.isPlayer(object)) {
            this.playersInside.getAndIncrement();
            if (!this.active && !Config.GRIDS_ALWAYS_ON) {
                this.startActivation();
            }
        }
    }
    
    private void startActivation() {
        this.setActive(true);
        this.startNeighborsTask(true, Config.GRID_NEIGHBOR_TURNON_TIME);
    }
    
    private void setActive(final boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        this.switchAI();
    }
    
    private void switchAI() {
        if (this.objects.isEmpty()) {
            return;
        }
        if (!this.active) {
            for (final WorldObject o : this.objects.values()) {
                if (GameUtils.isAttackable(o)) {
                    final Attackable mob = (Attackable)o;
                    mob.setTarget(null);
                    mob.stopMove(null);
                    mob.stopAllEffects();
                    mob.clearAggroList();
                    mob.getAttackByList().clear();
                    if (mob.hasAI()) {
                        mob.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                        mob.getAI().stopAITask();
                    }
                    RandomAnimationTaskManager.getInstance().remove(mob);
                }
                else {
                    if (!GameUtils.isNpc(o)) {
                        continue;
                    }
                    RandomAnimationTaskManager.getInstance().remove((Npc)o);
                }
            }
        }
        else {
            for (final WorldObject o : this.objects.values()) {
                if (GameUtils.isAttackable(o)) {
                    ((Attackable)o).getStatus().startHpMpRegeneration();
                }
                else {
                    if (!GameUtils.isNpc(o)) {
                        continue;
                    }
                    RandomAnimationTaskManager.getInstance().add((Npc)o);
                }
            }
        }
    }
    
    private void startNeighborsTask(final boolean activating, final int taskStartTime) {
        synchronized (this.taskLocker) {
            if (Objects.nonNull(this.neighborsTask)) {
                this.neighborsTask.cancel(true);
                this.neighborsTask = null;
            }
            this.neighborsTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new NeighborsTask(activating), (long)(1000 * taskStartTime));
        }
    }
    
    private void startDeactivation() {
        this.startNeighborsTask(false, Config.GRID_NEIGHBOR_TURNOFF_TIME);
    }
    
    void removeVisibleObject(final WorldObject object) {
        if (Objects.isNull(object) || this.objects.isEmpty()) {
            return;
        }
        if (Objects.nonNull(this.objects.remove(object.getObjectId())) && GameUtils.isPlayer(object)) {
            this.playersInside.getAndDecrement();
            if (this.areNeighborsEmpty() && !Config.GRIDS_ALWAYS_ON) {
                this.startDeactivation();
            }
        }
    }
    
    private boolean areNeighborsEmpty() {
        for (final WorldRegion region : this.surroundingRegions) {
            if (region.isActive() && region.playersInside.get() > 0) {
                return false;
            }
        }
        return true;
    }
    
     <T extends WorldObject> void forEachObjectInSurrounding(final Class<T> clazz, final Consumer<T> action, final Predicate<T> filter) {
        for (final WorldRegion region : this.surroundingRegions) {
            for (final WorldObject object : region.objects.values()) {
                final T casted;
                if (clazz.isInstance(object) && filter.test(casted = clazz.cast(object))) {
                    action.accept(casted);
                }
            }
        }
    }
    
     <T extends WorldObject> void forEachObjectInSurroundingLimiting(final Class<T> clazz, final int limit, final Predicate<T> filter, final Consumer<T> action) {
        int accepted = 0;
        for (final WorldRegion region : this.surroundingRegions) {
            for (final WorldObject object : region.objects.values()) {
                final T casted;
                if (clazz.isInstance(object) && filter.test(casted = clazz.cast(object))) {
                    action.accept(casted);
                    if (++accepted > limit) {
                        return;
                    }
                    continue;
                }
            }
        }
    }
    
     <T extends WorldObject> void forEachOrderedObjectInSurrounding(final Class<T> clazz, final int maxObjects, final Comparator<T> comparator, final Predicate<T> filter, final Consumer<? super T> action) {
        this.filteredSurroundingObjects(clazz, filter).sorted(comparator).limit(maxObjects).forEach(action);
    }
    
    private <T extends WorldObject> Stream<T> filteredSurroundingObjects(final Class<T> clazz, final Predicate<T> filter) {
        final Stream<Object> filter2 = Arrays.stream(this.surroundingRegions).flatMap(r -> r.objects.values().stream()).filter(o -> applyInstanceFilter(o, clazz, filter));
        Objects.requireNonNull(clazz);
        return (Stream<T>)filter2.map((Function<? super Object, ?>)clazz::cast);
    }
    
     <T extends WorldObject> void forAnyObjectInSurrounding(final Class<T> clazz, final Consumer<T> action, final Predicate<T> filter) {
        for (final WorldRegion region : this.surroundingRegions) {
            for (final WorldObject object : region.objects.values()) {
                if (applyInstanceFilter(object, clazz, filter)) {
                    action.accept(clazz.cast(object));
                    return;
                }
            }
        }
    }
    
    WorldObject findObjectInSurrounding(final WorldObject reference, final int objectId, final int range) {
        final WorldRegion[] surroundingRegions = this.surroundingRegions;
        final int length = surroundingRegions.length;
        int i = 0;
        while (i < length) {
            final WorldRegion region = surroundingRegions[i];
            final WorldObject object;
            if (Objects.nonNull(object = region.getObject(objectId))) {
                if (!MathUtil.isInsideRadius3D(reference, object, range)) {
                    return null;
                }
                return object;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
     <T extends WorldObject> List<T> findAllObjectsInSurrounding(final Class<T> clazz, final Predicate<T> filter) {
        return this.filteredSurroundingObjects(clazz, filter).collect((Collector<? super T, ?, List<T>>)Collectors.toList());
    }
    
     <T extends WorldObject> T findAnyObjectInSurrounding(final Class<T> clazz, final Predicate<T> filter) {
        for (final WorldRegion region : this.surroundingRegions) {
            for (final WorldObject object : region.objects.values()) {
                if (applyInstanceFilter(object, clazz, filter)) {
                    return clazz.cast(object);
                }
            }
        }
        return null;
    }
    
     <T extends WorldObject> T findFirstObjectInSurrounding(final Class<T> clazz, final Predicate<T> filter, final Comparator<T> comparator) {
        return this.filteredSurroundingObjects(clazz, filter).min(comparator).orElse(null);
    }
    
     <T extends WorldObject> boolean hasObjectInSurrounding(final Class<T> clazz, final Predicate<T> filter) {
        for (final WorldRegion region : this.surroundingRegions) {
            for (final WorldObject object : region.objects.values()) {
                if (applyInstanceFilter(object, clazz, filter)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    WorldObject getObject(final int objectId) {
        return (WorldObject)this.objects.get(objectId);
    }
    
    void setSurroundingRegions(final WorldRegion[] regions) {
        this.surroundingRegions = regions;
        for (int i = 0; i < this.surroundingRegions.length; ++i) {
            if (this.surroundingRegions[i] == this) {
                final WorldRegion first = this.surroundingRegions[0];
                this.surroundingRegions[0] = this;
                this.surroundingRegions[i] = first;
            }
        }
    }
    
    public boolean isInSurroundingRegion(final WorldObject object) {
        final WorldRegion objectRegion;
        return !Objects.isNull(object) && !Objects.isNull(objectRegion = object.getWorldRegion()) && this.isSurroundingRegion(objectRegion);
    }
    
    boolean isSurroundingRegion(final WorldRegion region) {
        return Objects.nonNull(region) && Math.abs(this.regionX - region.regionX) <= 1 && Math.abs(this.regionY - region.regionY) <= 1;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    private static <T extends WorldObject> boolean applyInstanceFilter(final WorldObject object, final Class<T> clazz, final Predicate<T> filter) {
        return clazz.isInstance(object) && filter.test(clazz.cast(object));
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.regionX, this.regionY);
    }
    
    WorldRegion[] surroundingRegions() {
        return this.surroundingRegions;
    }
    
    Collection<WorldObject> objects() {
        return (Collection<WorldObject>)this.objects.values();
    }
    
    private class NeighborsTask implements Runnable
    {
        private final boolean isActivating;
        
        NeighborsTask(final boolean isActivating) {
            this.isActivating = isActivating;
        }
        
        @Override
        public void run() {
            for (final WorldRegion region : WorldRegion.this.surroundingRegions) {
                if (this.isActivating || region.areNeighborsEmpty()) {
                    region.setActive(this.isActivating);
                }
            }
        }
    }
}
