// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.TeleportType;
import org.w3c.dom.Node;
import java.util.HashMap;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import java.util.Map;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class TeleportersData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<Map<String, TeleportHolder>> teleporters;
    
    private TeleportersData() {
        this.teleporters = (IntMap<Map<String, TeleportHolder>>)new HashIntMap();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/teleporters/teleporterData.xsd");
    }
    
    public void load() {
        this.teleporters.clear();
        this.parseDatapackDirectory("data/teleporters", true);
        TeleportersData.LOGGER.info("Loaded: {} npc teleporters.", (Object)this.teleporters.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* doc */
        //     2: ldc             "list"
        //     4: aload_0         /* this */
        //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/data/xml/impl/TeleportersData;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/data/xml/impl/TeleportersData.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
        //    13: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  doc   
        //  f     
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
    
    private void parseNpcs(final HashMap<String, TeleportHolder> teleportList, final Node node) {
        this.forEach(node, "npc", npcNode -> this.registerTeleportList(this.parseInteger(npcNode.getAttributes(), "id"), teleportList));
    }
    
    private void parseTeleport(final HashMap<String, TeleportHolder> teleportList, final int npcId, final Node node) {
        final NamedNodeMap nodeAttrs = node.getAttributes();
        final TeleportType type = (TeleportType)this.parseEnum(nodeAttrs, (Class)TeleportType.class, "type");
        final String name = this.parseString(nodeAttrs, "name", type.name());
        final TeleportHolder holder = new TeleportHolder(name, type);
        this.forEach(node, "location", location -> holder.registerLocation(new StatsSet(this.parseAttributes(location))));
        if (Objects.nonNull(teleportList.putIfAbsent(name, holder))) {
            TeleportersData.LOGGER.warn("Duplicate teleport list ({}) has been found for NPC: {}", (Object)name, (Object)npcId);
        }
    }
    
    private void registerTeleportList(final int npcId, final Map<String, TeleportHolder> teleList) {
        this.teleporters.put(npcId, (Object)teleList);
    }
    
    public TeleportHolder getHolder(final int npcId, final String listName) {
        return ((Map)this.teleporters.getOrDefault(npcId, (Object)Collections.emptyMap())).get(listName);
    }
    
    public static TeleportersData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TeleportersData.class);
    }
    
    private static class Singleton
    {
        private static final TeleportersData INSTANCE;
        
        static {
            INSTANCE = new TeleportersData();
        }
    }
}
