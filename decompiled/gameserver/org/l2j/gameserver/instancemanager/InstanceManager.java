// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import io.github.joealisson.primitive.maps.impl.CHashIntLongMap;
import java.util.Iterator;
import io.github.joealisson.primitive.pair.IntLong;
import io.github.joealisson.primitive.Containers;
import java.sql.ResultSet;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.InstanceDAO;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.gameserver.enums.InstanceRemoveBuffType;
import java.time.DayOfWeek;
import org.l2j.gameserver.model.holders.InstanceReenterTimeHolder;
import org.l2j.gameserver.enums.InstanceReenterType;
import java.lang.reflect.Constructor;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.conditions.Condition;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.Objects;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.maps.IntLongMap;
import org.l2j.gameserver.model.instancezone.Instance;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class InstanceManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final InstanceTemplate DEFAULT_TEMPLATE;
    private final IntMap<InstanceTemplate> instanceTemplates;
    private final IntMap<Instance> instanceWorlds;
    private final IntMap<IntLongMap> playerInstanceTimes;
    private int currentInstanceId;
    
    private InstanceManager() {
        this.instanceTemplates = (IntMap<InstanceTemplate>)new HashIntMap();
        this.instanceWorlds = (IntMap<Instance>)new CHashIntMap();
        this.playerInstanceTimes = (IntMap<IntLongMap>)new CHashIntMap();
        this.currentInstanceId = 0;
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/instance.xsd");
    }
    
    public void load() {
        this.instanceTemplates.clear();
        this.parseDatapackDirectory("data/instances", true);
        InstanceManager.LOGGER.info("Loaded {} instance templates.", (Object)this.instanceTemplates.size());
        this.playerInstanceTimes.clear();
        this.restoreInstanceTimes();
        InstanceManager.LOGGER.info("Loaded instance reenter times for {} players.", (Object)this.playerInstanceTimes.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node node = doc.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            if ("instance".equals(node.getNodeName())) {
                this.parseInstanceTemplate(node, f);
            }
        }
    }
    
    private void parseInstanceTemplate(final Node instanceNode, final File file) {
        final NamedNodeMap attrs = instanceNode.getAttributes();
        final int id = this.parseInt(attrs, "id");
        if (this.instanceTemplates.containsKey(id)) {
            InstanceManager.LOGGER.warn("Instance template with ID {} already exists", (Object)id);
            return;
        }
        final InstanceTemplate template = new InstanceTemplate(id, this.parseString(attrs, "name"), this.parseInt(attrs, "maxWorlds", -1));
        for (Node innerNode = instanceNode.getFirstChild(); Objects.nonNull(innerNode); innerNode = innerNode.getNextSibling()) {
            final String nodeName = innerNode.getNodeName();
            switch (nodeName) {
                case "time": {
                    this.parseTimes(template, innerNode);
                    break;
                }
                case "misc": {
                    this.parseMisc(template, innerNode);
                    break;
                }
                case "rates": {
                    this.parseRates(template, innerNode);
                    break;
                }
                case "locations": {
                    this.parseLocations(template, innerNode);
                    break;
                }
                case "spawnlist": {
                    this.parseSpawns(file, template, innerNode);
                    break;
                }
                case "doorlist": {
                    this.parseDoors(template, innerNode);
                    break;
                }
                case "removeBuffs": {
                    this.parseRemoveBuffs(template, innerNode);
                    break;
                }
                case "reenter": {
                    this.parseReenter(template, innerNode);
                    break;
                }
                case "parameters": {
                    template.setParameters(this.parseParameters(innerNode));
                    break;
                }
                case "conditions": {
                    this.parseConditions(id, template, innerNode);
                    break;
                }
            }
        }
        this.instanceTemplates.put(id, (Object)template);
    }
    
    private void parseConditions(final int id, final InstanceTemplate template, final Node innerNode) {
        final List<Condition> conditions = new ArrayList<Condition>();
        for (Node conditionNode = innerNode.getFirstChild(); conditionNode != null; conditionNode = conditionNode.getNextSibling()) {
            if (conditionNode.getNodeName().equals("condition")) {
                final NamedNodeMap attrs = conditionNode.getAttributes();
                final String type = this.parseString(attrs, "type");
                final boolean onlyLeader = this.parseBoolean(attrs, "onlyLeader", false);
                final boolean showMessageAndHtml = this.parseBoolean(attrs, "showMessageAndHtml", false);
                StatsSet params = null;
                for (Node f = conditionNode.getFirstChild(); f != null; f = f.getNextSibling()) {
                    if (f.getNodeName().equals("param")) {
                        if (params == null) {
                            params = new StatsSet();
                        }
                        params.set(this.parseString(f.getAttributes(), "name"), this.parseString(f.getAttributes(), "value"));
                    }
                }
                if (params == null) {
                    params = StatsSet.EMPTY_STATSET;
                }
                try {
                    final Class<?> clazz = Class.forName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, type));
                    final Constructor<?> constructor = clazz.getConstructor(InstanceTemplate.class, StatsSet.class, Boolean.TYPE, Boolean.TYPE);
                    conditions.add((Condition)constructor.newInstance(template, params, onlyLeader, showMessageAndHtml));
                }
                catch (Exception ex) {
                    InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, type, template.getName(), id));
                }
            }
        }
        template.setConditions(conditions);
    }
    
    private void parseReenter(final InstanceTemplate template, final Node innerNode) {
        final InstanceReenterType type = (InstanceReenterType)this.parseEnum(innerNode.getAttributes(), (Class)InstanceReenterType.class, "apply", (Enum)InstanceReenterType.NONE);
        final List<InstanceReenterTimeHolder> data = new ArrayList<InstanceReenterTimeHolder>();
        for (Node e = innerNode.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e.getNodeName().equals("reset")) {
                final NamedNodeMap attrs = e.getAttributes();
                final int time = this.parseInt(attrs, "time", -1);
                if (time > 0) {
                    data.add(new InstanceReenterTimeHolder(time));
                }
                else {
                    final DayOfWeek day = (DayOfWeek)this.parseEnum(attrs, (Class)DayOfWeek.class, "day");
                    final int hour = this.parseInt(attrs, "hour", -1);
                    final int minute = this.parseInt(attrs, "minute", -1);
                    data.add(new InstanceReenterTimeHolder(day, hour, minute));
                }
            }
        }
        template.setReenterData(type, data);
    }
    
    private void parseRemoveBuffs(final InstanceTemplate template, final Node innerNode) {
        final InstanceRemoveBuffType removeBuffType = (InstanceRemoveBuffType)this.parseEnum(innerNode.getAttributes(), (Class)InstanceRemoveBuffType.class, "type");
        final IntSet exceptionBuffList = (IntSet)new HashIntSet();
        for (Node e = innerNode.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e.getNodeName().equals("skill")) {
                exceptionBuffList.add(this.parseInt(e.getAttributes(), "id"));
            }
        }
        template.setRemoveBuff(removeBuffType, exceptionBuffList);
    }
    
    private void parseSpawns(final File file, final InstanceTemplate template, final Node innerNode) {
        final List<SpawnTemplate> spawns = new ArrayList<SpawnTemplate>();
        SpawnsData.getInstance().parseSpawn(innerNode, file, spawns);
        template.addSpawns(spawns);
    }
    
    private void parseRates(final InstanceTemplate template, final Node innerNode) {
        final NamedNodeMap attrs = innerNode.getAttributes();
        template.setExpRate(this.parseFloat(attrs, "exp", Float.valueOf(Config.RATE_INSTANCE_XP)));
        template.setSPRate(this.parseFloat(attrs, "sp", Float.valueOf(Config.RATE_INSTANCE_SP)));
        template.setExpPartyRate(this.parseFloat(attrs, "partyExp", Float.valueOf(Config.RATE_INSTANCE_PARTY_XP)));
        template.setSPPartyRate(this.parseFloat(attrs, "partySp", Float.valueOf(Config.RATE_INSTANCE_PARTY_SP)));
    }
    
    private void parseMisc(final InstanceTemplate template, final Node innerNode) {
        final NamedNodeMap attrs = innerNode.getAttributes();
        template.allowPlayerSummon(this.parseBoolean(attrs, "allowPlayerSummon"));
        template.setIsPvP(this.parseBoolean(attrs, "isPvP"));
    }
    
    private void parseTimes(final InstanceTemplate template, final Node innerNode) {
        final NamedNodeMap attrs = innerNode.getAttributes();
        template.setDuration(this.parseInt(attrs, "duration", -1));
        template.setEmptyDestroyTime(this.parseInt(attrs, "empty", -1));
        template.setEjectTime(this.parseInt(attrs, "eject", -1));
    }
    
    private void parseDoors(final InstanceTemplate template, final Node innerNode) {
        for (Node doorNode = innerNode.getFirstChild(); doorNode != null; doorNode = doorNode.getNextSibling()) {
            if (doorNode.getNodeName().equals("door")) {
                final StatsSet parsedSet = DoorDataManager.getInstance().parseDoor(doorNode);
                final StatsSet mergedSet = new StatsSet();
                final int doorId = parsedSet.getInt("id");
                final StatsSet templateSet = DoorDataManager.getInstance().getDoorTemplate(doorId);
                if (templateSet != null) {
                    mergedSet.merge(templateSet);
                }
                else {
                    InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;I)Ljava/lang/String;, doorId, template.getName(), template.getId()));
                }
                mergedSet.merge(parsedSet);
                try {
                    template.addDoor(doorId, new DoorTemplate(mergedSet));
                }
                catch (Exception e) {
                    InstanceManager.LOGGER.warn("Cannot initialize template for door: {}, instance: {}", new Object[] { doorId, template, e });
                }
            }
        }
    }
    
    private void parseLocations(final InstanceTemplate template, final Node innerNode) {
        for (Node locationsNode = innerNode.getFirstChild(); Objects.nonNull(locationsNode); locationsNode = locationsNode.getNextSibling()) {
            final InstanceTeleportType type = (InstanceTeleportType)this.parseEnum(locationsNode.getAttributes(), (Class)InstanceTeleportType.class, "type");
            final String nodeName = locationsNode.getNodeName();
            switch (nodeName) {
                case "enter": {
                    template.setEnterLocation(type, this.parseLocations(locationsNode));
                    break;
                }
                case "exit": {
                    if (type.equals(InstanceTeleportType.ORIGIN)) {
                        template.setExitLocation(type, null);
                        break;
                    }
                    final List<Location> locations = this.parseLocations(locationsNode);
                    if (locations.isEmpty()) {
                        InstanceManager.LOGGER.warn("Missing exit location data for instance {}!", (Object)template);
                        break;
                    }
                    template.setExitLocation(type, locations);
                    break;
                }
            }
        }
    }
    
    private List<Location> parseLocations(final Node locationsNode) {
        final List<Location> locations = new ArrayList<Location>();
        for (Node locationNode = locationsNode.getFirstChild(); Objects.nonNull(locationNode); locationNode = locationNode.getNextSibling()) {
            locations.add(this.parseLocation(locationNode));
        }
        return locations;
    }
    
    public Instance createInstance() {
        return new Instance(this.getNewInstanceId(), InstanceManager.DEFAULT_TEMPLATE, null);
    }
    
    public Instance createInstance(final InstanceTemplate template, final Player player) {
        return (template != null) ? new Instance(this.getNewInstanceId(), template, player) : null;
    }
    
    public Instance createInstance(final int id, final Player player) {
        if (!this.instanceTemplates.containsKey(id)) {
            InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
            return null;
        }
        return new Instance(this.getNewInstanceId(), (InstanceTemplate)this.instanceTemplates.get(id), player);
    }
    
    public Instance getInstance(final int instanceId) {
        return (Instance)this.instanceWorlds.get(instanceId);
    }
    
    public Collection<Instance> getInstances() {
        return (Collection<Instance>)this.instanceWorlds.values();
    }
    
    public Instance getPlayerInstance(final Player player, final boolean isInside) {
        return this.instanceWorlds.values().stream().filter(i -> isInside ? i.containsPlayer(player) : i.isAllowed(player)).findFirst().orElse(null);
    }
    
    private synchronized int getNewInstanceId() {
        do {
            if (this.currentInstanceId == Integer.MAX_VALUE) {
                this.currentInstanceId = 0;
            }
            ++this.currentInstanceId;
        } while (this.instanceWorlds.containsKey(this.currentInstanceId));
        return this.currentInstanceId;
    }
    
    public void register(final Instance instance) {
        final int instanceId = instance.getId();
        if (!this.instanceWorlds.containsKey(instanceId)) {
            this.instanceWorlds.put(instanceId, (Object)instance);
        }
    }
    
    public void unregister(final int instanceId) {
        if (this.instanceWorlds.containsKey(instanceId)) {
            this.instanceWorlds.remove(instanceId);
        }
    }
    
    public String getInstanceName(final int templateId) {
        return ((InstanceTemplate)this.instanceTemplates.get(templateId)).getName();
    }
    
    private void restoreInstanceTimes() {
        ((InstanceDAO)DatabaseAccess.getDAO((Class)InstanceDAO.class)).findAllInstancesTime(this::addInstancesTime);
    }
    
    private void addInstancesTime(final ResultSet rs) {
        try {
            final long currentTime = System.currentTimeMillis();
            while (rs.next()) {
                final long time = rs.getLong("time");
                if (time > currentTime) {
                    final int charId = rs.getInt("charId");
                    final int instanceId = rs.getInt("instanceId");
                    this.setReenterPenalty(charId, instanceId, time);
                }
            }
        }
        catch (Exception e) {
            InstanceManager.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public IntLongMap getAllInstanceTimes(final Player player) {
        final IntLongMap instanceTimes = (IntLongMap)this.playerInstanceTimes.get(player.getObjectId());
        if (instanceTimes == null || instanceTimes.isEmpty()) {
            return Containers.EMPTY_INT_LONG_MAP;
        }
        final IntSet invalidPenalty = (IntSet)new HashIntSet(instanceTimes.size());
        for (final IntLong entry : instanceTimes.entrySet()) {
            if (entry.getValue() <= System.currentTimeMillis()) {
                invalidPenalty.add(entry.getKey());
            }
        }
        if (!invalidPenalty.isEmpty()) {
            ((InstanceDAO)DatabaseAccess.getDAO((Class)InstanceDAO.class)).deleteInstanceTime(player.getObjectId(), invalidPenalty);
        }
        return instanceTimes;
    }
    
    public void setReenterPenalty(final int objectId, final int id, final long time) {
        ((IntLongMap)this.playerInstanceTimes.computeIfAbsent(objectId, k -> new CHashIntLongMap())).put(id, time);
    }
    
    public long getInstanceTime(final Player player, final int id) {
        final IntLongMap playerData = (IntLongMap)this.playerInstanceTimes.get(player.getObjectId());
        if (playerData == null || !playerData.containsKey(id)) {
            return -1L;
        }
        final long time = playerData.get(id);
        if (time <= System.currentTimeMillis()) {
            this.deleteInstanceTime(player, id);
            return -1L;
        }
        return time;
    }
    
    public void deleteInstanceTime(final Player player, final int id) {
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteInstanceTime(player.getObjectId(), id);
        ((IntLongMap)this.playerInstanceTimes.get(player.getObjectId())).remove(id);
    }
    
    public InstanceTemplate getInstanceTemplate(final int id) {
        return (InstanceTemplate)this.instanceTemplates.get(id);
    }
    
    public Collection<InstanceTemplate> getInstanceTemplates() {
        return (Collection<InstanceTemplate>)this.instanceTemplates.values();
    }
    
    public long getWorldCount(final int templateId) {
        return this.instanceWorlds.values().stream().filter(i -> i.getTemplateId() == templateId).count();
    }
    
    public List<Instance> getInstances(final int templateId) {
        return this.instanceWorlds.values().stream().filter(i -> i.getTemplateId() == templateId).collect((Collector<? super Object, ?, List<Instance>>)Collectors.toList());
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static InstanceManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)InstanceManager.class);
        DEFAULT_TEMPLATE = new InstanceTemplate();
    }
    
    private static class Singleton
    {
        private static final InstanceManager INSTANCE;
        
        static {
            INSTANCE = new InstanceManager();
        }
    }
}
