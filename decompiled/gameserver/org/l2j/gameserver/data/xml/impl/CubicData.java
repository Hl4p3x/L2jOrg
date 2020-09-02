// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.cubic.conditions.HealthCondition;
import org.l2j.gameserver.model.cubic.conditions.RangeCondition;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;
import org.l2j.gameserver.model.cubic.conditions.HpCondition;
import org.l2j.gameserver.model.cubic.CubicSkill;
import java.util.Collections;
import org.l2j.gameserver.model.cubic.ICubicConditionHolder;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.templates.CubicTemplate;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class CubicData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, Map<Integer, CubicTemplate>> _cubics;
    
    private CubicData() {
        this._cubics = new HashMap<Integer, Map<Integer, CubicTemplate>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/cubics.xsd");
    }
    
    public void load() {
        this._cubics.clear();
        this.parseDatapackDirectory("data/stats/cubics", true);
        CubicData.LOGGER.info("Loaded {} cubics.", (Object)this._cubics.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final CubicTemplate template;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "cubic", cubicNode -> {
            new CubicTemplate(new StatsSet(this.parseAttributes(cubicNode)));
            this.parseTemplate(cubicNode, template);
        }));
    }
    
    private void parseTemplate(final Node cubicNode, final CubicTemplate template) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* cubicNode */
        //     2: invokedynamic   BootstrapMethod #1, test:()Ljava/util/function/Predicate;
        //     7: aload_0         /* this */
        //     8: aload_2         /* template */
        //     9: invokedynamic   BootstrapMethod #2, accept:(Lorg/l2j/gameserver/data/xml/impl/CubicData;Lorg/l2j/gameserver/model/actor/templates/CubicTemplate;)Ljava/util/function/Consumer;
        //    14: invokevirtual   org/l2j/gameserver/data/xml/impl/CubicData.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    17: aload_0         /* this */
        //    18: getfield        org/l2j/gameserver/data/xml/impl/CubicData._cubics:Ljava/util/Map;
        //    21: aload_2         /* template */
        //    22: invokevirtual   org/l2j/gameserver/model/actor/templates/CubicTemplate.getId:()I
        //    25: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    28: invokedynamic   BootstrapMethod #3, apply:()Ljava/util/function/Function;
        //    33: invokeinterface java/util/Map.computeIfAbsent:(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;
        //    38: checkcast       Ljava/util/Map;
        //    41: aload_2         /* template */
        //    42: invokevirtual   org/l2j/gameserver/model/actor/templates/CubicTemplate.getLevel:()I
        //    45: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    48: aload_2         /* template */
        //    49: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    54: pop            
        //    55: return         
        //    MethodParameters:
        //  Name       Flags  
        //  ---------  -----
        //  cubicNode  
        //  template   
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
    
    private void parseConditions(final Node cubicNode, final CubicTemplate template, final ICubicConditionHolder holder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* cubicNode */
        //     2: invokedynamic   BootstrapMethod #4, test:()Ljava/util/function/Predicate;
        //     7: aload_0         /* this */
        //     8: aload_3         /* holder */
        //     9: aload_2         /* template */
        //    10: invokedynamic   BootstrapMethod #5, accept:(Lorg/l2j/gameserver/data/xml/impl/CubicData;Lorg/l2j/gameserver/model/cubic/ICubicConditionHolder;Lorg/l2j/gameserver/model/actor/templates/CubicTemplate;)Ljava/util/function/Consumer;
        //    15: invokevirtual   org/l2j/gameserver/data/xml/impl/CubicData.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    18: return         
        //    MethodParameters:
        //  Name       Flags  
        //  ---------  -----
        //  cubicNode  
        //  template   
        //  holder     
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
    
    private void parseSkills(final Node cubicNode, final CubicTemplate template) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* cubicNode */
        //     2: ldc             "skill"
        //     4: aload_0         /* this */
        //     5: aload_1         /* cubicNode */
        //     6: aload_2         /* template */
        //     7: invokedynamic   BootstrapMethod #6, accept:(Lorg/l2j/gameserver/data/xml/impl/CubicData;Lorg/w3c/dom/Node;Lorg/l2j/gameserver/model/actor/templates/CubicTemplate;)Ljava/util/function/Consumer;
        //    12: invokevirtual   org/l2j/gameserver/data/xml/impl/CubicData.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
        //    15: return         
        //    MethodParameters:
        //  Name       Flags  
        //  ---------  -----
        //  cubicNode  
        //  template   
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:374)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
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
    
    public CubicTemplate getCubicTemplate(final int id, final int level) {
        return this._cubics.getOrDefault(id, Collections.emptyMap()).get(level);
    }
    
    public static CubicData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CubicData.class);
    }
    
    private static class Singleton
    {
        private static final CubicData INSTANCE;
        
        static {
            INSTANCE = new CubicData();
        }
    }
}
