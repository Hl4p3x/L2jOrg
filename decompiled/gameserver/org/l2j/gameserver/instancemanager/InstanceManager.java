// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.enums.InstanceTeleportType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import io.github.joealisson.primitive.maps.impl.CHashIntLongMap;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.Objects;
import io.github.joealisson.primitive.pair.IntLong;
import java.util.ArrayList;
import io.github.joealisson.primitive.Containers;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Node;
import org.l2j.commons.xml.XmlReader;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.maps.IntLongMap;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class InstanceManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final String DELETE_INSTANCE_TIME = "DELETE FROM character_instance_time WHERE charId=? AND instanceId=?";
    private final IntMap<InstanceTemplate> instanceTemplates;
    private final IntMap<Instance> instanceWorlds;
    private final IntMap<IntLongMap> playerInstanceTimes;
    private int currentInstanceId;
    
    private InstanceManager() {
        this.instanceTemplates = (IntMap<InstanceTemplate>)new HashIntMap();
        this.instanceWorlds = (IntMap<Instance>)new CHashIntMap();
        this.playerInstanceTimes = (IntMap<IntLongMap>)new CHashIntMap();
        this.currentInstanceId = 0;
        this.load();
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
        this.forEach((Node)doc, x$0 -> XmlReader.isNode(x$0), listNode -> {
            if ("instance".equals(listNode.getNodeName())) {
                this.parseInstanceTemplate(listNode, f);
            }
        });
    }
    
    private void parseInstanceTemplate(final Node instanceNode, final File file) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* instanceNode */
        //     2: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     7: ldc             "id"
        //     9: invokevirtual   org/l2j/gameserver/instancemanager/InstanceManager.parseInteger:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/Integer;
        //    12: invokevirtual   java/lang/Integer.intValue:()I
        //    15: istore_3        /* id */
        //    16: aload_0         /* this */
        //    17: getfield        org/l2j/gameserver/instancemanager/InstanceManager.instanceTemplates:Lio/github/joealisson/primitive/IntMap;
        //    20: iload_3         /* id */
        //    21: invokeinterface io/github/joealisson/primitive/IntMap.containsKey:(I)Z
        //    26: ifeq            44
        //    29: getstatic       org/l2j/gameserver/instancemanager/InstanceManager.LOGGER:Lorg/slf4j/Logger;
        //    32: iload_3         /* id */
        //    33: invokedynamic   BootstrapMethod #2, makeConcatWithConstants:(I)Ljava/lang/String;
        //    38: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
        //    43: return         
        //    44: new             Lorg/l2j/gameserver/model/instancezone/InstanceTemplate;
        //    47: dup            
        //    48: new             Lorg/l2j/gameserver/model/StatsSet;
        //    51: dup            
        //    52: aload_0         /* this */
        //    53: aload_1         /* instanceNode */
        //    54: invokevirtual   org/l2j/gameserver/instancemanager/InstanceManager.parseAttributes:(Lorg/w3c/dom/Node;)Ljava/util/Map;
        //    57: invokespecial   org/l2j/gameserver/model/StatsSet.<init>:(Ljava/util/Map;)V
        //    60: invokespecial   org/l2j/gameserver/model/instancezone/InstanceTemplate.<init>:(Lorg/l2j/gameserver/model/StatsSet;)V
        //    63: astore          template
        //    65: aload_0         /* this */
        //    66: aload_1         /* instanceNode */
        //    67: invokedynamic   BootstrapMethod #3, test:()Ljava/util/function/Predicate;
        //    72: aload_0         /* this */
        //    73: aload           template
        //    75: aload_2         /* file */
        //    76: iload_3         /* id */
        //    77: invokedynamic   BootstrapMethod #4, accept:(Lorg/l2j/gameserver/instancemanager/InstanceManager;Lorg/l2j/gameserver/model/instancezone/InstanceTemplate;Ljava/io/File;I)Ljava/util/function/Consumer;
        //    82: invokevirtual   org/l2j/gameserver/instancemanager/InstanceManager.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    85: aload_0         /* this */
        //    86: getfield        org/l2j/gameserver/instancemanager/InstanceManager.instanceTemplates:Lio/github/joealisson/primitive/IntMap;
        //    89: iload_3         /* id */
        //    90: aload           template
        //    92: invokeinterface io/github/joealisson/primitive/IntMap.put:(ILjava/lang/Object;)Ljava/lang/Object;
        //    97: pop            
        //    98: return         
        //    MethodParameters:
        //  Name          Flags  
        //  ------------  -----
        //  instanceNode  
        //  file          
        //    StackMapTable: 00 01 FC 00 2C 01
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
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
    
    public Instance createInstance() {
        return new Instance(this.getNewInstanceId(), new InstanceTemplate(StatsSet.EMPTY_STATSET), null);
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
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement ps = con.createStatement();
                try {
                    final ResultSet rs = ps.executeQuery("SELECT * FROM character_instance_time ORDER BY charId");
                    try {
                        while (rs.next()) {
                            final long time = rs.getLong("time");
                            if (time > System.currentTimeMillis()) {
                                final int charId = rs.getInt("charId");
                                final int instanceId = rs.getInt("instanceId");
                                this.setReenterPenalty(charId, instanceId, time);
                            }
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
    }
    
    public IntLongMap getAllInstanceTimes(final Player player) {
        final IntLongMap instanceTimes = (IntLongMap)this.playerInstanceTimes.get(player.getObjectId());
        if (instanceTimes == null || instanceTimes.isEmpty()) {
            return Containers.EMPTY_INT_LONG_MAP;
        }
        final List<Integer> invalidPenalty = new ArrayList<Integer>(instanceTimes.size());
        for (final IntLong entry : instanceTimes.entrySet()) {
            if (entry.getValue() <= System.currentTimeMillis()) {
                invalidPenalty.add(entry.getKey());
            }
        }
        if (!invalidPenalty.isEmpty()) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement ps = con.prepareStatement("DELETE FROM character_instance_time WHERE charId=? AND instanceId=?");
                    try {
                        for (final Integer id : invalidPenalty) {
                            ps.setInt(1, player.getObjectId());
                            ps.setInt(2, id);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        final List<Integer> list = invalidPenalty;
                        final IntLongMap obj = instanceTimes;
                        Objects.requireNonNull(obj);
                        list.forEach(obj::remove);
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (Throwable t) {
                        if (ps != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            }
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
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM character_instance_time WHERE charId=? AND instanceId=?");
                try {
                    ps.setInt(1, player.getObjectId());
                    ps.setInt(2, id);
                    ps.execute();
                    if (this.playerInstanceTimes.get(player.getObjectId()) != null) {
                        ((IntLongMap)this.playerInstanceTimes.get(player.getObjectId())).remove(id);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            InstanceManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
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
    
    public static InstanceManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)InstanceManager.class);
    }
    
    private static class Singleton
    {
        private static final InstanceManager INSTANCE;
        
        static {
            INSTANCE = new InstanceManager();
        }
    }
}
