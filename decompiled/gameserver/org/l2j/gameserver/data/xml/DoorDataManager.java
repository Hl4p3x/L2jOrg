// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntSet;
import java.util.ArrayList;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.w3c.dom.NamedNodeMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.commons.xml.XmlReader;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import java.util.HashMap;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Door;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class DoorDataManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<String, IntSet> groups;
    private final IntMap<Door> doors;
    private final IntMap<StatsSet> templates;
    private final IntMap<List<Door>> regions;
    
    private DoorDataManager() {
        this.groups = new HashMap<String, IntSet>();
        this.doors = (IntMap<Door>)new HashIntMap();
        this.templates = (IntMap<StatsSet>)new HashIntMap();
        this.regions = (IntMap<List<Door>>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/doors.xsd");
    }
    
    public void load() {
        this.doors.clear();
        this.groups.clear();
        this.regions.clear();
        this.parseDatapackFile("data/doors.xml");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "door", doorNode -> this.spawnDoor(this.parseDoor(doorNode))));
        DoorDataManager.LOGGER.info("Loaded {} Door Templates for {} regions.", (Object)this.doors.size(), (Object)this.regions.size());
    }
    
    public StatsSet parseDoor(final Node doorNode) {
        final StatsSet params = new StatsSet(this.parseAttributes(doorNode));
        params.set("baseHpMax", 1);
        final NamedNodeMap attrs;
        final StatsSet set;
        AtomicInteger count;
        NamedNodeMap nodeAttrs;
        final StatsSet set2;
        final AtomicInteger atomicInteger;
        int i;
        Node att;
        this.forEach(doorNode, x$0 -> XmlReader.isNode(x$0), innerDoorNode -> {
            attrs = innerDoorNode.getAttributes();
            if (innerDoorNode.getNodeName().equals("nodes")) {
                set.set("nodeZ", this.parseInt(attrs, "nodeZ"));
                count = new AtomicInteger();
                this.forEach(innerDoorNode, x$0 -> XmlReader.isNode(x$0), nodes -> {
                    nodeAttrs = nodes.getAttributes();
                    if ("node".equals(nodes.getNodeName())) {
                        set2.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, atomicInteger.get()), this.parseInt(nodeAttrs, "x"));
                        set2.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, atomicInteger.getAndIncrement()), this.parseInt(nodeAttrs, "y"));
                    }
                });
            }
            else if (attrs != null) {
                for (i = 0; i < attrs.getLength(); ++i) {
                    att = attrs.item(i);
                    set.set(att.getNodeName(), att.getNodeValue());
                }
            }
            return;
        });
        this.applyCollisions(params);
        return params;
    }
    
    private void applyCollisions(final StatsSet set) {
        if (set.contains("nodeX_0") && set.contains("nodeY_0") && set.contains("nodeX_1") && set.contains("nodeX_1")) {
            final int height = set.getInt("height", 150);
            final int nodeX = set.getInt("nodeX_0");
            final int nodeY = set.getInt("nodeY_0");
            final int posX = set.getInt("nodeX_1");
            final int posY = set.getInt("nodeX_1");
            int collisionRadius = Math.min(Math.abs(nodeX - posX), Math.abs(nodeY - posY));
            if (collisionRadius < 20) {
                collisionRadius = 20;
            }
            set.set("collision_radius", collisionRadius);
            set.set("collision_height", height);
        }
    }
    
    private void spawnDoor(final StatsSet set) {
        final DoorTemplate template = new DoorTemplate(set);
        final Door door = this.spawnDoor(template, null);
        this.templates.put(door.getId(), (Object)set);
        this.doors.put(door.getId(), (Object)door);
        ((List)this.regions.computeIfAbsent(MapRegionManager.getInstance().getMapRegionLocId(door), key -> new ArrayList())).add(door);
    }
    
    public Door spawnDoor(final DoorTemplate template, final Instance instance) {
        final Door door = new Door(template);
        door.setCurrentHp(door.getMaxHp());
        if (instance != null) {
            door.setInstance(instance);
        }
        door.spawnMe(template.getX(), template.getY(), template.getZ());
        if (template.getGroupName() != null) {
            this.groups.computeIfAbsent(door.getGroupName(), key -> new HashIntSet()).add(door.getId());
        }
        return door;
    }
    
    public StatsSet getDoorTemplate(final int doorId) {
        return (StatsSet)this.templates.get(doorId);
    }
    
    public Door getDoor(final int doorId) {
        return (Door)this.doors.get(doorId);
    }
    
    public IntSet getDoorsByGroup(final String groupName) {
        return this.groups.getOrDefault(groupName, Containers.emptyIntSet());
    }
    
    public Collection<Door> getDoors() {
        return (Collection<Door>)this.doors.values();
    }
    
    public boolean checkIfDoorsBetween(final int x, final int y, final int z, final int tx, final int ty, final int tz, final Instance instance, final boolean doubleFaceCheck) {
        final Collection<Door> allDoors = (Collection<Door>)((instance != null) ? instance.getDoors() : this.regions.get(MapRegionManager.getInstance().getMapRegionLocId(x, y)));
        if (allDoors == null) {
            return false;
        }
        for (final Door doorInst : allDoors) {
            if (!doorInst.isDead() && !doorInst.isOpen() && doorInst.checkCollision()) {
                if (doorInst.getX(0) == 0) {
                    continue;
                }
                boolean intersectFace = false;
                for (int i = 0; i < 4; ++i) {
                    final int j = (i + 1 < 4) ? (i + 1) : 0;
                    final int denominator = (ty - y) * (doorInst.getX(i) - doorInst.getX(j)) - (tx - x) * (doorInst.getY(i) - doorInst.getY(j));
                    if (denominator != 0) {
                        final float multiplier1 = ((doorInst.getX(j) - doorInst.getX(i)) * (y - doorInst.getY(i)) - (doorInst.getY(j) - doorInst.getY(i)) * (x - doorInst.getX(i))) / (float)denominator;
                        final float multiplier2 = ((tx - x) * (y - doorInst.getY(i)) - (ty - y) * (x - doorInst.getX(i))) / (float)denominator;
                        if (multiplier1 >= 0.0f && multiplier1 <= 1.0f && multiplier2 >= 0.0f && multiplier2 <= 1.0f) {
                            final int intersectZ = Math.round(z + multiplier1 * (tz - z));
                            if (intersectZ > doorInst.getZMin() && intersectZ < doorInst.getZMax()) {
                                if (!doubleFaceCheck || intersectFace) {
                                    return true;
                                }
                                intersectFace = true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static DoorDataManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)DoorDataManager.class);
    }
    
    private static class Singleton
    {
        protected static final DoorDataManager INSTANCE;
        
        static {
            INSTANCE = new DoorDataManager();
        }
    }
}
