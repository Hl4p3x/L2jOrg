// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.stream.Stream;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.world.zone.ZoneArea;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.l2j.gameserver.world.zone.form.ZonePolygonArea;
import java.util.function.ToIntFunction;
import java.util.ArrayList;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.holders.MinionHolder;
import java.util.Collections;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ITerritorized;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import java.util.function.Predicate;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.LinkedList;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class SpawnsData extends GameXmlReader
{
    protected static final Logger LOGGER;
    private final List<SpawnTemplate> spawns;
    
    private SpawnsData() {
        this.spawns = new LinkedList<SpawnTemplate>();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/spawns.xsd");
    }
    
    public void spawnByName(final String spawnName) {
        this.spawns.parallelStream().filter(spawnTemplate -> spawnTemplate.getName() != null && spawnTemplate.getName().equals(spawnName)).forEach(template -> {
            template.spawn(spawnTemplate -> spawnTemplate.getName() != null && spawnTemplate.getName().equals(spawnName), null);
            template.notifyActivate();
        });
    }
    
    public void deSpawnByName(final String spawnName) {
        this.spawns.parallelStream().filter(spawnTemplate -> spawnTemplate.getName() != null && spawnTemplate.getName().equals(spawnName)).forEach(template -> {
            template.despawn(spawnTemplate -> spawnTemplate.getName() != null && spawnTemplate.getName().equals(spawnName));
            template.notifyActivate();
        });
    }
    
    public void spawnAll() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }
        SpawnsData.LOGGER.info("Initializing spawns...");
        this.spawns.parallelStream().filter(SpawnTemplate::isSpawningByDefault).forEach(template -> {
            template.spawnAll(null);
            template.notifyActivate();
            return;
        });
        SpawnsData.LOGGER.info("All spawns has been initialized!");
    }
    
    public void load() {
        this.parseDatapackDirectory("data/spawns", true);
        SpawnsData.LOGGER.info("Loaded spawns");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "spawn", spawnNode -> {
            try {
                this.parseSpawn(spawnNode, f, this.spawns);
            }
            catch (Exception e) {
                SpawnsData.LOGGER.warn("Error while processing spawn in file {}", (Object)f.getAbsolutePath(), (Object)e);
            }
        }));
    }
    
    public List<SpawnTemplate> getSpawns() {
        return this.spawns;
    }
    
    public List<NpcSpawnTemplate> getNpcSpawns(final Predicate<NpcSpawnTemplate> condition) {
        return this.spawns.stream().flatMap(template -> template.getGroups().stream()).flatMap(group -> group.getSpawns().stream()).filter((Predicate<? super Object>)condition).collect((Collector<? super Object, ?, List<NpcSpawnTemplate>>)Collectors.toList());
    }
    
    public void parseSpawn(final Node spawnsNode, final File file, final List<SpawnTemplate> spawns) {
        final SpawnTemplate spawnTemplate = new SpawnTemplate(new StatsSet(this.parseAttributes(spawnsNode)), file.getAbsolutePath());
        SpawnGroup defaultGroup = null;
        for (Node innerNode = spawnsNode.getFirstChild(); innerNode != null; innerNode = innerNode.getNextSibling()) {
            if ("territories".equalsIgnoreCase(innerNode.getNodeName())) {
                this.parseTerritories(innerNode, file.getName(), spawnTemplate);
            }
            else if ("group".equalsIgnoreCase(innerNode.getNodeName())) {
                this.parseGroup(innerNode, spawnTemplate);
            }
            else if ("npc".equalsIgnoreCase(innerNode.getNodeName())) {
                if (Objects.isNull(defaultGroup)) {
                    defaultGroup = new SpawnGroup(StatsSet.EMPTY_STATSET);
                }
                this.parseNpc(innerNode, spawnTemplate, defaultGroup);
            }
            else if ("parameters".equalsIgnoreCase(innerNode.getNodeName())) {
                this.parseParameters(spawnsNode, spawnTemplate);
            }
        }
        if (defaultGroup != null) {
            spawnTemplate.addGroup(defaultGroup);
        }
        spawns.add(spawnTemplate);
    }
    
    private void parseTerritories(final Node innerNode, final String fileName, final ITerritorized spawnTemplate) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* innerNode */
        //     2: invokedynamic   BootstrapMethod #9, test:()Ljava/util/function/Predicate;
        //     7: aload_0         /* this */
        //     8: aload_2         /* fileName */
        //     9: aload_3         /* spawnTemplate */
        //    10: invokedynamic   BootstrapMethod #10, accept:(Lorg/l2j/gameserver/data/xml/impl/SpawnsData;Ljava/lang/String;Lorg/l2j/gameserver/model/interfaces/ITerritorized;)Ljava/util/function/Consumer;
        //    15: invokevirtual   org/l2j/gameserver/data/xml/impl/SpawnsData.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    18: return         
        //    MethodParameters:
        //  Name           Flags  
        //  -------------  -----
        //  innerNode      
        //  fileName       
        //  spawnTemplate  
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
    
    private void parseGroup(final Node n, final SpawnTemplate spawnTemplate) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: new             Lorg/l2j/gameserver/model/StatsSet;
        //     7: dup            
        //     8: aload_0         /* this */
        //     9: aload_1         /* n */
        //    10: invokevirtual   org/l2j/gameserver/data/xml/impl/SpawnsData.parseAttributes:(Lorg/w3c/dom/Node;)Ljava/util/Map;
        //    13: invokespecial   org/l2j/gameserver/model/StatsSet.<init>:(Ljava/util/Map;)V
        //    16: invokespecial   org/l2j/gameserver/model/spawns/SpawnGroup.<init>:(Lorg/l2j/gameserver/model/StatsSet;)V
        //    19: astore_3        /* group */
        //    20: aload_0         /* this */
        //    21: aload_1         /* n */
        //    22: invokedynamic   BootstrapMethod #11, test:()Ljava/util/function/Predicate;
        //    27: aload_0         /* this */
        //    28: aload_2         /* spawnTemplate */
        //    29: aload_3         /* group */
        //    30: invokedynamic   BootstrapMethod #12, accept:(Lorg/l2j/gameserver/data/xml/impl/SpawnsData;Lorg/l2j/gameserver/model/spawns/SpawnTemplate;Lorg/l2j/gameserver/model/spawns/SpawnGroup;)Ljava/util/function/Consumer;
        //    35: invokevirtual   org/l2j/gameserver/data/xml/impl/SpawnsData.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    38: aload_2         /* spawnTemplate */
        //    39: aload_3         /* group */
        //    40: invokevirtual   org/l2j/gameserver/model/spawns/SpawnTemplate.addGroup:(Lorg/l2j/gameserver/model/spawns/SpawnGroup;)V
        //    43: return         
        //    MethodParameters:
        //  Name           Flags  
        //  -------------  -----
        //  n              
        //  spawnTemplate  
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
    
    private void parseNpc(final Node n, final SpawnTemplate spawnTemplate, final SpawnGroup group) {
        final NpcSpawnTemplate npcTemplate = new NpcSpawnTemplate(spawnTemplate, group, new StatsSet(this.parseAttributes(n)));
        final NpcTemplate template = NpcData.getInstance().getTemplate(npcTemplate.getId());
        if (Objects.isNull(template)) {
            SpawnsData.LOGGER.warn("Requested spawn for non existing npc: {} in file: {}", (Object)npcTemplate.getId(), (Object)spawnTemplate.getFilePath());
            return;
        }
        if (template.isType("Servitor") || template.isType("Pet")) {
            SpawnsData.LOGGER.warn("Requested spawn for {} {} ({}) file: {}", new Object[] { template.getType(), template.getName(), template.getId(), spawnTemplate.getFilePath() });
            return;
        }
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("parameters".equalsIgnoreCase(d.getNodeName())) {
                this.parseParameters(d, npcTemplate);
            }
            else if ("minions".equalsIgnoreCase(d.getNodeName())) {
                this.parseMinions(d, npcTemplate);
            }
            else if ("locations".equalsIgnoreCase(d.getNodeName())) {
                this.parseLocations(d, npcTemplate);
            }
        }
        group.addSpawn(npcTemplate);
    }
    
    private void parseLocations(final Node n, final NpcSpawnTemplate npcTemplate) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("location".equalsIgnoreCase(d.getNodeName())) {
                final int x = this.parseInt(d.getAttributes(), "x");
                final int y = this.parseInt(d.getAttributes(), "y");
                final int z = this.parseInt(d.getAttributes(), "z");
                final int heading = this.parseInt(d.getAttributes(), "heading", 0);
                final double chance = this.parseDouble(d.getAttributes(), "chance");
                npcTemplate.addSpawnLocation(new ChanceLocation(x, y, z, heading, chance));
            }
        }
    }
    
    private void parseParameters(final Node n, final IParameterized<StatsSet> npcTemplate) {
        final Map<String, Object> params = this.parseParameters(n);
        npcTemplate.setParameters(params.isEmpty() ? StatsSet.EMPTY_STATSET : new StatsSet(Collections.unmodifiableMap((Map<? extends String, ?>)params)));
    }
    
    private void parseMinions(final Node n, final NpcSpawnTemplate npcTemplate) {
        final MinionHolder minion;
        this.forEach(n, "minion", minionNode -> {
            new MinionHolder(new StatsSet(this.parseAttributes(minionNode)));
            npcTemplate.addMinion(minion);
        });
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static SpawnsData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SpawnsData.class);
    }
    
    private static class Singleton
    {
        private static final SpawnsData INSTANCE;
        
        static {
            INSTANCE = new SpawnsData();
        }
    }
}
