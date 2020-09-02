// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.xml.XmlReader;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import java.util.EnumMap;
import org.l2j.gameserver.model.ensoul.EnsoulStone;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.ensoul.EnsoulFee;
import org.l2j.gameserver.model.item.type.CrystalType;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class EnsoulData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<CrystalType, EnsoulFee> ensoulFees;
    private final IntMap<EnsoulOption> ensoulOptions;
    private final IntMap<EnsoulStone> ensoulStones;
    
    private EnsoulData() {
        this.ensoulFees = new EnumMap<CrystalType, EnsoulFee>(CrystalType.class);
        this.ensoulOptions = (IntMap<EnsoulOption>)new HashIntMap();
        this.ensoulStones = (IntMap<EnsoulStone>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/ensoulStones.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/stats/ensoulStones.xml");
        EnsoulData.LOGGER.info("Loaded {} fees", (Object)this.ensoulFees.size());
        EnsoulData.LOGGER.info("Loaded {} options", (Object)this.ensoulOptions.size());
        EnsoulData.LOGGER.info("Loaded {} stones", (Object)this.ensoulStones.size());
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
        //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/data/xml/impl/EnsoulData;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/data/xml/impl/EnsoulData.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
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
    
    private void parseFees(final Node ensoulNode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* ensoulNode */
        //     2: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     7: ldc             Lorg/l2j/gameserver/model/item/type/CrystalType;.class
        //     9: ldc             "crystalType"
        //    11: invokevirtual   org/l2j/gameserver/data/xml/impl/EnsoulData.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //    14: checkcast       Lorg/l2j/gameserver/model/item/type/CrystalType;
        //    17: astore_2        /* type */
        //    18: new             Lorg/l2j/gameserver/model/ensoul/EnsoulFee;
        //    21: dup            
        //    22: aload_2         /* type */
        //    23: invokespecial   org/l2j/gameserver/model/ensoul/EnsoulFee.<init>:(Lorg/l2j/gameserver/model/item/type/CrystalType;)V
        //    26: astore_3        /* fee */
        //    27: aload_0         /* this */
        //    28: aload_1         /* ensoulNode */
        //    29: invokedynamic   BootstrapMethod #1, test:()Ljava/util/function/Predicate;
        //    34: aload_0         /* this */
        //    35: aload_3         /* fee */
        //    36: invokedynamic   BootstrapMethod #2, accept:(Lorg/l2j/gameserver/data/xml/impl/EnsoulData;Lorg/l2j/gameserver/model/ensoul/EnsoulFee;)Ljava/util/function/Consumer;
        //    41: invokevirtual   org/l2j/gameserver/data/xml/impl/EnsoulData.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    44: return         
        //    MethodParameters:
        //  Name        Flags  
        //  ----------  -----
        //  ensoulNode  
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
    
    private void parseFee(final Node ensoulNode, final EnsoulFee fee, final int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.setEnsoul(index, new ItemHolder(this.parseInt(attrs, "itemId"), this.parseInteger(attrs, "count")));
        this.ensoulFees.put(fee.getCrystalType(), fee);
    }
    
    private void parseReFee(final Node ensoulNode, final EnsoulFee fee, final int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.setResoul(index, new ItemHolder(this.parseInt(attrs, "itemId"), this.parseInt(attrs, "count")));
    }
    
    private void parseRemove(final Node ensoulNode, final EnsoulFee fee) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.addRemovalFee(new ItemHolder(this.parseInt(attrs, "itemId"), this.parseInt(attrs, "count")));
    }
    
    private void parseOptions(final Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = this.parseInt(attrs, "id");
        final String name = this.parseString(attrs, "name");
        final String desc = this.parseString(attrs, "desc");
        final int skillId = this.parseInt(attrs, "skillId");
        final int skillLevel = this.parseInt(attrs, "skillLevel");
        final EnsoulOption option = new EnsoulOption(id, name, desc, skillId, skillLevel);
        this.ensoulOptions.put(option.getId(), (Object)option);
    }
    
    private void parseStones(final Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final EnsoulStone stone = new EnsoulStone(this.parseInt(attrs, "id"), this.parseInt(attrs, "slotType"));
        this.forEach(ensoulNode, "option", optionNode -> stone.addOption(this.parseInteger(optionNode.getAttributes(), "id")));
        this.ensoulStones.put(stone.getId(), (Object)stone);
    }
    
    public ItemHolder getEnsoulFee(final CrystalType type, final int index) {
        return (ItemHolder)Util.computeIfNonNull((Object)this.ensoulFees.get(type), e -> e.getEnsoul(index));
    }
    
    public ItemHolder getResoulFee(final CrystalType type, final int index) {
        return (ItemHolder)Util.computeIfNonNull((Object)this.ensoulFees.get(type), e -> e.getResoul(index));
    }
    
    public Collection<ItemHolder> getRemovalFee(final CrystalType type) {
        final EnsoulFee fee = this.ensoulFees.get(type);
        return (fee != null) ? fee.getRemovalFee() : Collections.emptyList();
    }
    
    public EnsoulOption getOption(final int id) {
        return (EnsoulOption)this.ensoulOptions.get(id);
    }
    
    public EnsoulStone getStone(final int id) {
        return (EnsoulStone)this.ensoulStones.get(id);
    }
    
    public int getStone(final int type, final int optionId) {
        for (final EnsoulStone stone : this.ensoulStones.values()) {
            if (stone.getSlotType() == type) {
                for (final int id : stone.getOptions()) {
                    if (id == optionId) {
                        return stone.getId();
                    }
                }
            }
        }
        return 0;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static EnsoulData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EnsoulData.class);
    }
    
    private static class Singleton
    {
        private static final EnsoulData INSTANCE;
        
        static {
            INSTANCE = new EnsoulData();
        }
    }
}
