// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import java.util.function.Predicate;
import java.util.Collections;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.ArrayList;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import java.util.function.Consumer;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.ConcurrentHashMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.instance.Fence;
import java.util.List;
import org.l2j.gameserver.world.WorldRegion;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class FenceDataManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final int MAX_Z_DIFF = 100;
    private final Map<WorldRegion, List<Fence>> regions;
    private final IntMap<Fence> fences;
    
    private FenceDataManager() {
        this.regions = new ConcurrentHashMap<WorldRegion, List<Fence>>();
        this.fences = (IntMap<Fence>)new CHashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/FenceData.xsd");
    }
    
    public void load() {
        if (!this.fences.isEmpty()) {
            this.fences.values().forEach(this::removeFence);
        }
        this.parseDatapackFile("data/FenceData.xml");
        FenceDataManager.LOGGER.info("Loaded {} Fences", (Object)this.fences.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "fence", (Consumer)this::spawnFence));
    }
    
    private void spawnFence(final Node fenceNode) {
        final StatsSet set = new StatsSet(this.parseAttributes(fenceNode));
        this.spawnFence(set.getInt("x"), set.getInt("y"), set.getInt("z"), set.getString("name"), set.getInt("width"), set.getInt("length"), set.getInt("height"), 0, set.getEnum("state", FenceState.class, FenceState.CLOSED));
    }
    
    public Fence spawnFence(final int x, final int y, final int z, final int width, final int length, final int height, final int instanceId, final FenceState state) {
        return this.spawnFence(x, y, z, null, width, length, height, instanceId, state);
    }
    
    private Fence spawnFence(final int x, final int y, final int z, final String name, final int width, final int length, final int height, final int instanceId, final FenceState state) {
        final Fence fence = new Fence(x, y, name, width, length, height, state);
        if (instanceId > 0) {
            fence.setInstanceById(instanceId);
        }
        fence.spawnMe(x, y, z);
        this.addFence(fence);
        return fence;
    }
    
    private void addFence(final Fence fence) {
        this.fences.put(fence.getObjectId(), (Object)fence);
        this.regions.computeIfAbsent(World.getInstance().getRegion(fence), key -> new ArrayList()).add(fence);
    }
    
    public void removeFence(final Fence fence) {
        this.fences.remove(fence.getObjectId());
        final List<Fence> fencesInRegion = this.regions.get(World.getInstance().getRegion(fence));
        if (fencesInRegion != null) {
            fencesInRegion.remove(fence);
        }
    }
    
    public IntMap<Fence> getFences() {
        return this.fences;
    }
    
    public boolean checkIfFenceBetween(final int x, final int y, final int z, final int tx, final int ty, final int tz, final Instance instance) {
        int instanceId;
        int xMin;
        int xMax;
        int yMin;
        int yMax;
        final Predicate<Fence> filter = fence -> {
            if (!fence.getState().isGeodataEnabled()) {
                return false;
            }
            else {
                instanceId = ((instance == null) ? 0 : instance.getId());
                if (fence.getInstanceId() != instanceId) {
                    return false;
                }
                else {
                    xMin = fence.getXMin();
                    xMax = fence.getXMax();
                    yMin = fence.getYMin();
                    yMax = fence.getYMax();
                    if (x < xMin && tx < xMin) {
                        return false;
                    }
                    else if (x > xMax && tx > xMax) {
                        return false;
                    }
                    else if (y < yMin && ty < yMin) {
                        return false;
                    }
                    else if (y > yMax && ty > yMax) {
                        return false;
                    }
                    else if (x > xMin && tx > xMin && x < xMax && tx < xMax && y > yMin && ty > yMin && y < yMax && ty < yMax) {
                        return false;
                    }
                    else if (this.crossLinePart(xMin, yMin, xMax, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax) || this.crossLinePart(xMax, yMin, xMax, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || this.crossLinePart(xMax, yMax, xMin, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || this.crossLinePart(xMin, yMax, xMin, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax)) {
                        return z > fence.getZ() - 100 && z < fence.getZ() + 100;
                    }
                    else {
                        return false;
                    }
                }
            }
        };
        final WorldRegion region = World.getInstance().getRegion(x, y);
        return region != null && this.regions.getOrDefault(region, Collections.emptyList()).stream().anyMatch((Predicate<? super Object>)filter);
    }
    
    private boolean crossLinePart(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4, final double xMin, final double yMin, final double xMax, final double yMax) {
        final double[] result = this.intersection(x1, y1, x2, y2, x3, y3, x4, y4);
        if (result == null) {
            return false;
        }
        final double xCross = result[0];
        final double yCross = result[1];
        return (xCross <= xMax && xCross >= xMin) || (yCross <= yMax && yCross >= yMin);
    }
    
    private double[] intersection(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4) {
        final double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0.0) {
            return null;
        }
        final double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        final double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
        return new double[] { xi, yi };
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static FenceDataManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FenceDataManager.class);
    }
    
    private static class Singleton
    {
        private static final FenceDataManager INSTANCE;
        
        static {
            INSTANCE = new FenceDataManager();
        }
    }
}
