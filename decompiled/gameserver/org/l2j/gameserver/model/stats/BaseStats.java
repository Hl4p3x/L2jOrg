// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.gameserver.util.GameXmlReader;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Creature;
import java.util.NoSuchElementException;

public enum BaseStats
{
    STR(Stat.STAT_STR), 
    INT(Stat.STAT_INT), 
    DEX(Stat.STAT_DEX), 
    WIT(Stat.STAT_WIT), 
    CON(Stat.STAT_CON), 
    MEN(Stat.STAT_MEN);
    
    public static final int MAX_STAT_VALUE = 201;
    private static final BaseStats[] CACHE;
    private final double[] bonus;
    private final Stat stat;
    private int enhancementSkillId;
    private int enhancementFirstLevel;
    private int enhancementSecondLevel;
    private int enhancementThirdLevel;
    
    private BaseStats(final Stat stat) {
        this.bonus = new double[201];
        this.stat = stat;
    }
    
    public static BaseStats valueOf(final Stat stat) {
        for (final BaseStats baseStat : BaseStats.CACHE) {
            if (baseStat.getStat() == stat) {
                return baseStat;
            }
        }
        throw new NoSuchElementException(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/stats/Stat;)Ljava/lang/String;, stat));
    }
    
    public Stat getStat() {
        return this.stat;
    }
    
    public int calcValue(final Creature creature) {
        if (Objects.nonNull(creature)) {
            return (int)creature.getStats().getValue(this.stat);
        }
        return 0;
    }
    
    public double calcBonus(final Creature creature) {
        if (!Objects.nonNull(creature)) {
            return 1.0;
        }
        final int value = this.calcValue(creature);
        if (value < 1) {
            return 1.0;
        }
        return this.bonus[value];
    }
    
    void setValue(final int index, final double value) {
        this.bonus[index] = value;
    }
    
    public double getValue(final int index) {
        return this.bonus[index];
    }
    
    public int getEnhancementSkillId() {
        return this.enhancementSkillId;
    }
    
    public int getEnhancementSkillLevel(final double value) {
        if (value >= this.enhancementThirdLevel) {
            return 3;
        }
        if (value >= this.enhancementSecondLevel) {
            return 2;
        }
        if (value >= this.enhancementFirstLevel) {
            return 1;
        }
        return 0;
    }
    
    static {
        CACHE = values();
        new GameXmlReader() {
            protected Path getSchemaFilePath() {
                return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/statBonus.xsd");
            }
            
            public void load() {
                this.parseDatapackFile("data/stats/statBonus.xml");
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
                //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/model/stats/BaseStats$1;)Ljava/util/function/Consumer;
                //    10: invokevirtual   org/l2j/gameserver/model/stats/BaseStats$1.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
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
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1164)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:1009)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
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
            
            private void parseStatBonus(final BaseStats baseStat, final Node bonusNode) {
                final int index;
                final double bonus;
                this.forEach(bonusNode, "value", statValue -> {
                    index = this.parseInt(statValue.getAttributes(), "level");
                    bonus = Double.parseDouble(statValue.getTextContent());
                    baseStat.setValue(index, bonus);
                });
            }
            
            private void parseStatEnhancement(final BaseStats baseStat, final Node node) {
                final NamedNodeMap attr = node.getAttributes();
                baseStat.enhancementSkillId = this.parseInt(attr, "skill-id");
                baseStat.enhancementFirstLevel = this.parseInt(attr, "first-level");
                baseStat.enhancementSecondLevel = this.parseInt(attr, "second-level");
                baseStat.enhancementThirdLevel = this.parseInt(attr, "third-level");
            }
        }.load();
    }
}
