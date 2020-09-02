// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

import org.slf4j.LoggerFactory;
import java.util.stream.IntStream;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Collection;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.world.zone.form.ZoneCubeArea;
import io.github.joealisson.primitive.IntList;
import org.l2j.gameserver.world.zone.form.ZonePolygonArea;
import io.github.joealisson.primitive.ArrayIntList;
import org.l2j.gameserver.world.zone.type.SpawnZone;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.world.zone.form.ZoneCylinderArea;
import org.l2j.gameserver.model.Location;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.NamedNodeMap;
import java.lang.reflect.Constructor;
import org.l2j.commons.util.Util;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.OptionalInt;
import java.util.HashMap;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.List;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import io.github.joealisson.primitive.IntMap;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ZoneManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final Map<String, AbstractZoneSettings> SETTINGS;
    private static final int SHIFT_BY = 15;
    private static final int OFFSET_X;
    private static final int OFFSET_Y;
    private final Map<Class<? extends Zone>, IntMap<? extends Zone>> classZones;
    private final Map<String, SpawnTerritory> spawnTerritories;
    private final ZoneRegion[][] zoneRegions;
    private int lastDynamicId;
    private List<Item> _debugItems;
    
    private ZoneManager() {
        this.classZones = new HashMap<Class<? extends Zone>, IntMap<? extends Zone>>();
        this.spawnTerritories = new HashMap<String, SpawnTerritory>();
        this.lastDynamicId = 300000;
        final int regionsX = 9 + ZoneManager.OFFSET_X + 1;
        final int regionsY = 9 + ZoneManager.OFFSET_Y + 1;
        this.zoneRegions = new ZoneRegion[regionsX][regionsY];
        for (int x = 0; x < regionsX; ++x) {
            for (int y = 0; y < regionsY; ++y) {
                this.zoneRegions[x][y] = new ZoneRegion();
            }
        }
        ZoneManager.LOGGER.info("Zone Region Grid set up: {} by {}", (Object)regionsX, (Object)regionsY);
    }
    
    public final void load() {
        this.classZones.clear();
        this.spawnTerritories.clear();
        this.parseDatapackDirectory("data/zones", true);
        ZoneManager.LOGGER.info("Loaded {} zone classes and {} zones.", (Object)this.classZones.size(), (Object)this.getSize());
        ZoneManager.LOGGER.info("Loaded {}  NPC spawn territories.", (Object)this.spawnTerritories.size());
        final OptionalInt maxId = this.classZones.values().stream().flatMapToInt(map -> map.keySet().stream()).filter(value -> value < 300000).max();
        maxId.ifPresent(id -> ZoneManager.LOGGER.info("Last static id: {}", (Object)id));
        this.releaseResources();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/zones/zones.xsd");
    }
    
    public void reload() {
        this.unload();
        this.load();
        World.getInstance().forEachCreature(creature -> creature.revalidateZone(true));
        ZoneManager.SETTINGS.clear();
    }
    
    public void unload() {
        this.classZones.values().stream().flatMap(map -> map.values().stream()).filter(z -> Objects.nonNull(z.getSettings())).forEach(z -> ZoneManager.SETTINGS.put(z.getName(), z.getSettings()));
        Arrays.stream(this.zoneRegions).flatMap((Function<? super ZoneRegion[], ? extends Stream<?>>)Arrays::stream).forEach(r -> r.getZones().clear());
        ZoneManager.LOGGER.info("Removed zones in regions.");
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName()) && this.parseBoolean(n.getAttributes(), "enabled")) {
                this.forEach(n, "zone", zone -> this.parseZone(zone, f.getAbsolutePath()));
            }
        }
    }
    
    private void parseZone(final Node zoneNode, final String file) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     6: astore_3        /* attributes */
        //     7: aload_0         /* this */
        //     8: aload_3         /* attributes */
        //     9: ldc             "type"
        //    11: invokevirtual   org/l2j/gameserver/world/zone/ZoneManager.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    14: astore          type
        //    16: aload           type
        //    18: invokestatic    java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
        //    21: astore          zoneClass
        //    23: ldc             Lorg/l2j/gameserver/world/zone/type/SpawnTerritory;.class
        //    25: aload           zoneClass
        //    27: invokevirtual   java/lang/Class.isAssignableFrom:(Ljava/lang/Class;)Z
        //    30: ifeq            40
        //    33: aload_0         /* this */
        //    34: aload_1         /* zoneNode */
        //    35: aload_2         /* file */
        //    36: invokevirtual   org/l2j/gameserver/world/zone/ZoneManager.addTerritory:(Lorg/w3c/dom/Node;Ljava/lang/String;)V
        //    39: return         
        //    40: ldc_w           Lorg/l2j/gameserver/world/zone/Zone;.class
        //    43: aload           zoneClass
        //    45: invokevirtual   java/lang/Class.isAssignableFrom:(Ljava/lang/Class;)Z
        //    48: ifne            66
        //    51: getstatic       org/l2j/gameserver/world/zone/ZoneManager.LOGGER:Lorg/slf4j/Logger;
        //    54: ldc_w           "The zone type: {} in file: {} is not subclass of Zone Class"
        //    57: aload           type
        //    59: aload_2         /* file */
        //    60: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //    65: return         
        //    66: aload_0         /* this */
        //    67: aload_1         /* zoneNode */
        //    68: aload           zoneClass
        //    70: aload_2         /* file */
        //    71: invokevirtual   org/l2j/gameserver/world/zone/ZoneManager.addZone:(Lorg/w3c/dom/Node;Ljava/lang/Class;Ljava/lang/String;)V
        //    74: goto            134
        //    77: astore          e
        //    79: getstatic       org/l2j/gameserver/world/zone/ZoneManager.LOGGER:Lorg/slf4j/Logger;
        //    82: ldc_w           "No such zone type: {} in file: {}"
        //    85: aload           type
        //    87: aload_2         /* file */
        //    88: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //    93: goto            134
        //    96: astore          e
        //    98: getstatic       org/l2j/gameserver/world/zone/ZoneManager.LOGGER:Lorg/slf4j/Logger;
        //   101: ldc_w           "The type: {} in file: {} must have a public constructor with a int parameter"
        //   104: aload           type
        //   106: aload_2         /* file */
        //   107: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   112: goto            134
        //   115: astore          e
        //   117: getstatic       org/l2j/gameserver/world/zone/ZoneManager.LOGGER:Lorg/slf4j/Logger;
        //   120: ldc_w           "There is a invalid Zone in file {}: {}"
        //   123: aload_2         /* file */
        //   124: aload           e
        //   126: invokevirtual   org/l2j/gameserver/world/zone/InvalidZoneException.getMessage:()Ljava/lang/String;
        //   129: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   134: return         
        //    MethodParameters:
        //  Name      Flags  
        //  --------  -----
        //  zoneNode  
        //  file      
        //    StackMapTable: 00 06 FE 00 28 07 01 33 07 00 D4 07 00 F5 19 FF 00 0A 00 05 07 00 0B 07 00 CE 07 00 D4 07 01 33 07 00 D4 00 01 07 01 0F 52 07 03 32 52 07 01 1D 12
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                
        //  -----  -----  -----  -----  ----------------------------------------------------
        //  16     39     77     96     Ljava/lang/ClassNotFoundException;
        //  40     65     77     96     Ljava/lang/ClassNotFoundException;
        //  66     74     77     96     Ljava/lang/ClassNotFoundException;
        //  16     39     96     115    Ljava/lang/NoSuchMethodException;
        //  16     39     96     115    Ljava/lang/IllegalAccessException;
        //  16     39     96     115    Ljava/lang/InstantiationException;
        //  16     39     96     115    Ljava/lang/reflect/InvocationTargetException;
        //  40     65     96     115    Ljava/lang/NoSuchMethodException;
        //  40     65     96     115    Ljava/lang/IllegalAccessException;
        //  40     65     96     115    Ljava/lang/InstantiationException;
        //  40     65     96     115    Ljava/lang/reflect/InvocationTargetException;
        //  66     74     96     115    Ljava/lang/NoSuchMethodException;
        //  66     74     96     115    Ljava/lang/IllegalAccessException;
        //  66     74     96     115    Ljava/lang/InstantiationException;
        //  66     74     96     115    Ljava/lang/reflect/InvocationTargetException;
        //  16     39     115    134    Lorg/l2j/gameserver/world/zone/InvalidZoneException;
        //  40     65     115    134    Lorg/l2j/gameserver/world/zone/InvalidZoneException;
        //  66     74     115    134    Lorg/l2j/gameserver/world/zone/InvalidZoneException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0040:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void addZone(final Node zoneNode, final Class<?> zoneClass, final String file) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, InvalidZoneException {
        final Constructor<? extends Zone> constructor = zoneClass.asSubclass(Zone.class).getConstructor(Integer.TYPE);
        final NamedNodeMap attributes = zoneNode.getAttributes();
        Integer zoneId = (Integer)Util.computeIfNonNull((Object)attributes.getNamedItem("id"), (Function)this::parseInt);
        if (Objects.isNull(zoneId)) {
            zoneId = this.lastDynamicId++;
        }
        final Zone zone = (Zone)constructor.newInstance(zoneId);
        zone.setName(this.parseString(attributes, "name"));
        this.parseZoneProperties(zoneNode, zone);
        if (Objects.isNull(zone.getArea())) {
            throw new InvalidZoneException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/world/zone/Zone;)Ljava/lang/String;, zone));
        }
        if (Objects.nonNull(this.addZone(zoneId, zone))) {
            ZoneManager.LOGGER.warn("Zone ({}) from file: {} overrides previous definition.", (Object)zone, (Object)file);
        }
        this.registerIntoWorldRegion(zone);
    }
    
    public int addCylinderZone(final Class<?> zoneClass, final String zoneName, final Location coords, final int radius) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<? extends Zone> constructor = zoneClass.asSubclass(Zone.class).getConstructor(Integer.TYPE);
        final int zoneId = this.lastDynamicId++;
        final ZoneArea area = new ZoneCylinderArea(coords.getX(), coords.getY(), coords.getZ() - 100, coords.getZ() + 100, radius);
        final Zone zone = (Zone)constructor.newInstance(zoneId);
        zone.setName(zoneName);
        zone.setArea(area);
        if (Objects.isNull(zone.getArea())) {
            ZoneManager.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/world/zone/Zone;)Ljava/lang/String;, zone));
        }
        if (Objects.nonNull(this.addZone(zoneId, zone))) {
            ZoneManager.LOGGER.warn("Zone ({}) overrides previous definition.", (Object)zone);
        }
        this.registerIntoWorldRegion(zone);
        return zoneId;
    }
    
    private void registerIntoWorldRegion(final Zone zone) {
        for (int x = 0; x < this.zoneRegions.length; ++x) {
            for (int y = 0; y < this.zoneRegions[x].length; ++y) {
                final int ax = x - ZoneManager.OFFSET_X << 15;
                final int bx = x + 1 - ZoneManager.OFFSET_X << 15;
                final int ay = y - ZoneManager.OFFSET_Y << 15;
                final int by = y + 1 - ZoneManager.OFFSET_Y << 15;
                if (zone.getArea().intersectsRectangle(ax, bx, ay, by)) {
                    this.zoneRegions[x][y].getZones().put(zone.getId(), (Object)zone);
                }
            }
        }
    }
    
    private void parseZoneProperties(final Node zoneNode, final Zone zone) throws InvalidZoneException {
        for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            final NamedNodeMap attr = node.getAttributes();
            final String nodeName = node.getNodeName();
            switch (nodeName) {
                case "polygon": {
                    zone.setArea(this.parsePolygon(node));
                    break;
                }
                case "cube": {
                    zone.setArea(this.parseCube(node));
                    break;
                }
                case "cylinder": {
                    zone.setArea(this.parseCylinder(node));
                    break;
                }
                case "property": {
                    zone.setParameter(this.parseString(attr, "name"), this.parseString(attr, "value"));
                    break;
                }
                case "spawn": {
                    this.parseSpawn(zone, attr);
                    break;
                }
                case "respawn": {
                    this.parseRespawn(zone, attr);
                    break;
                }
            }
        }
    }
    
    private void parseRespawn(final Zone zone, final NamedNodeMap attr) {
        if (zone instanceof RespawnZone) {
            final String race = this.parseString(attr, "race");
            final String point = this.parseString(attr, "region");
            ((RespawnZone)zone).addRaceRespawnPoint(race, point);
        }
    }
    
    private void parseSpawn(final Zone zone, final NamedNodeMap attr) {
        if (zone instanceof SpawnZone) {
            final int x = this.parseInt(attr, "x");
            final int y = this.parseInt(attr, "y");
            final int z = this.parseInt(attr, "z");
            final String type = this.parseString(attr, "type");
            ((SpawnZone)zone).parseLoc(x, y, z, type);
        }
    }
    
    private void addTerritory(final Node zoneNode, final String file) throws InvalidZoneException {
        final String name = this.parseString(zoneNode.getAttributes(), "name");
        if (Util.isNullOrEmpty((CharSequence)name)) {
            ZoneManager.LOGGER.warn("Missing name for SpawnTerritory in file: {}, skipping zone", (Object)file);
        }
        else if (this.spawnTerritories.containsKey(name)) {
            ZoneManager.LOGGER.warn("Spawn Territory Name {} already used for another zone, check file: {}. Skipping zone", (Object)name, (Object)file);
        }
        else {
            ZoneArea area = null;
            for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                final String nodeName = node.getNodeName();
                ZoneArea zoneArea = null;
                switch (nodeName) {
                    case "polygon": {
                        zoneArea = this.parsePolygon(node);
                        break;
                    }
                    case "cube": {
                        zoneArea = this.parseCube(node);
                        break;
                    }
                    case "cylinder": {
                        zoneArea = this.parseCylinder(node);
                        break;
                    }
                    default: {
                        zoneArea = null;
                        break;
                    }
                }
                area = zoneArea;
                if (Objects.nonNull(area)) {
                    this.spawnTerritories.put(name, new SpawnTerritory(name, area));
                    break;
                }
            }
            if (Objects.isNull(area)) {
                ZoneManager.LOGGER.warn("There is no defined area to Spawn Territory {} on file {}", (Object)name, (Object)file);
            }
        }
    }
    
    private ZoneArea parsePolygon(final Node polygonNode) throws InvalidZoneException {
        final IntList xPoints = (IntList)new ArrayIntList();
        final IntList yPoints = (IntList)new ArrayIntList();
        for (Node node = polygonNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                final NamedNodeMap attr = node.getAttributes();
                xPoints.add(this.parseInt(attr, "x"));
                yPoints.add(this.parseInt(attr, "y"));
            }
        }
        if (xPoints.size() < 3) {
            throw new InvalidZoneException("The Zone with Polygon form must have at least 3 points");
        }
        final NamedNodeMap attributes = polygonNode.getAttributes();
        final int minZ = this.parseInt(attributes, "min-z");
        final int maxZ = this.parseInt(attributes, "max-z");
        return new ZonePolygonArea(xPoints.toArray(int[]::new), yPoints.toArray(int[]::new), minZ, maxZ);
    }
    
    private ZoneArea parseCylinder(final Node zoneNode) throws InvalidZoneException {
        final NamedNodeMap attributes = zoneNode.getAttributes();
        final int radius = this.parseInt(attributes, "radius");
        if (radius <= 0) {
            throw new InvalidZoneException("The Zone with Cylinder form must have a radius");
        }
        for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                final NamedNodeMap attr = node.getAttributes();
                final int x = this.parseInt(attr, "x");
                final int y = this.parseInt(attr, "y");
                final int minZ = this.parseInt(attributes, "min-z");
                final int maxZ = this.parseInt(attributes, "max-z");
                return new ZoneCylinderArea(x, y, minZ, maxZ, radius);
            }
        }
        throw new InvalidZoneException("The Zone with Cylinder form must have 1 point");
    }
    
    private ZoneArea parseCube(final Node cubeNode) throws InvalidZoneException {
        final NamedNodeMap attributes = cubeNode.getAttributes();
        final int[] points = new int[4];
        int point = 0;
        for (Node node = cubeNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                final NamedNodeMap attr = node.getAttributes();
                final int x = this.parseInt(attr, "x");
                final int y = this.parseInt(attr, "y");
                points[point++] = x;
                points[point++] = y;
                if (point > 3) {
                    final int minZ = this.parseInt(attributes, "min-z");
                    final int maxZ = this.parseInt(attributes, "max-z");
                    return new ZoneCubeArea(points[0], points[2], points[1], points[3], minZ, maxZ);
                }
            }
        }
        throw new InvalidZoneException("The Zone with Cube Form must have 2 points");
    }
    
    public int getSize() {
        return this.classZones.values().stream().mapToInt(IntMap::size).sum();
    }
    
    private <T extends Zone> T addZone(final int id, final T zone) {
        return (T)this.classZones.computeIfAbsent(zone.getClass(), k -> new HashIntMap()).put(id, (Object)zone);
    }
    
    public <T extends Zone> Collection<T> getAllZones(final Class<T> zoneType) {
        return (Collection<T>)this.classZones.get(zoneType).values();
    }
    
    public Zone getZoneById(final int id) {
        return this.classZones.values().stream().filter(m -> m.containsKey(id)).map(m -> (Zone)m.get(id)).findAny().orElse(null);
    }
    
    public Zone getZoneByName(final String name) {
        return this.classZones.values().stream().flatMap(m -> m.values().stream()).filter(z -> Objects.equals(name, z.getName())).findAny().orElse(null);
    }
    
    public <T extends Zone> T getZoneById(final int id, final Class<T> zoneType) {
        return (T)this.classZones.get(zoneType).get(id);
    }
    
    public List<Zone> getZones(final ILocational locational) {
        return this.getZones(locational.getX(), locational.getY(), locational.getZ());
    }
    
    public <T extends Zone> T getZone(final ILocational locational, final Class<T> type) {
        return (T)(Objects.isNull(locational) ? null : this.getZone(locational.getX(), locational.getY(), locational.getZ(), type));
    }
    
    public List<Zone> getZones(final int x, final int y) {
        final ZoneRegion region = this.getRegion(x, y);
        return (List<Zone>)(Objects.isNull(region) ? Collections.emptyList() : region.getZones().values().stream().filter(z -> z.isInsideZone(x, y)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
    }
    
    public List<Zone> getZones(final int x, final int y, final int z) {
        final ZoneRegion region = this.getRegion(x, y);
        return (List<Zone>)(Objects.isNull(region) ? Collections.emptyList() : region.getZones().values().stream().filter(zone -> zone.isInsideZone(x, y, z)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
    }
    
    private <T extends Zone> T getZone(final int x, final int y, final int z, final Class<T> type) {
        final ZoneRegion region = this.getRegion(x, y);
        Zone zone2;
        if (Objects.isNull(region)) {
            zone2 = null;
        }
        else {
            final Stream<Object> filter = region.getZones().values().stream().filter(zone -> type.isInstance(zone) && zone.isInsideZone(x, y, z));
            Objects.requireNonNull(type);
            zone2 = filter.map((Function<? super Object, ?>)type::cast).findFirst().orElse(null);
        }
        return (T)zone2;
    }
    
    public SpawnTerritory getSpawnTerritory(final String name) {
        return this.spawnTerritories.get(name);
    }
    
    public List<SpawnTerritory> getSpawnTerritories(final WorldObject object) {
        return this.spawnTerritories.values().stream().filter(t -> t.isInsideZone(object.getX(), object.getY(), object.getZ())).collect((Collector<? super SpawnTerritory, ?, List<SpawnTerritory>>)Collectors.toList());
    }
    
    public final OlympiadStadiumZone getOlympiadStadium(final Creature creature) {
        OlympiadStadiumZone olympiadStadiumZone;
        if (Objects.isNull(creature)) {
            olympiadStadiumZone = null;
        }
        else {
            final Stream<Object> filter = this.getZones(creature).stream().filter(z -> z instanceof OlympiadStadiumZone && z.isCreatureInZone(creature));
            final Class<OlympiadStadiumZone> obj = OlympiadStadiumZone.class;
            Objects.requireNonNull(obj);
            olympiadStadiumZone = filter.map((Function<? super Object, ?>)obj::cast).findAny().orElse(null);
        }
        return olympiadStadiumZone;
    }
    
    List<Item> getDebugItems() {
        if (this._debugItems == null) {
            this._debugItems = new ArrayList<Item>();
        }
        return this._debugItems;
    }
    
    public void clearDebugItems() {
        if (this._debugItems != null) {
            final Iterator<Item> it = this._debugItems.iterator();
            while (it.hasNext()) {
                final Item item = it.next();
                if (item != null) {
                    item.decayMe();
                }
                it.remove();
            }
        }
    }
    
    public ZoneRegion getRegion(final int x, final int y) {
        try {
            return this.zoneRegions[(x >> 15) + ZoneManager.OFFSET_X][(y >> 15) + ZoneManager.OFFSET_Y];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            ZoneManager.LOGGER.warn("Incorrect zone region X: {} Y: {} for coordinates x: {} y:{}", new Object[] { (x >> 15) + ZoneManager.OFFSET_X, (y >> 15) + ZoneManager.OFFSET_Y, x, y });
            return null;
        }
    }
    
    public ZoneRegion getRegion(final ILocational point) {
        return this.getRegion(point.getX(), point.getY());
    }
    
    public static AbstractZoneSettings getSettings(final String name) {
        return ZoneManager.SETTINGS.get(name);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ZoneManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ZoneManager.class);
        SETTINGS = new HashMap<String, AbstractZoneSettings>();
        OFFSET_X = Math.abs(-9);
        OFFSET_Y = Math.abs(-8);
    }
    
    private static class Singleton
    {
        private static final ZoneManager INSTANCE;
        
        static {
            INSTANCE = new ZoneManager();
        }
    }
}
