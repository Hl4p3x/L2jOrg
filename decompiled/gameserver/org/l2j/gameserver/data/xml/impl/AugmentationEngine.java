// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.xml.XmlReader;
import java.util.Map;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.StatsSet;
import java.util.function.Function;
import org.l2j.gameserver.handler.EffectHandler;
import java.util.Objects;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.options.Options;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.EffectParser;

public class AugmentationEngine extends EffectParser
{
    private static final Logger LOGGER;
    private final IntMap<Options> augmentations;
    
    private AugmentationEngine() {
        this.augmentations = (IntMap<Options>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/augmentation/options/options.xsd");
    }
    
    public synchronized void load() {
        this.augmentations.clear();
        this.parseDatapackDirectory("data/augmentation/options", false);
        AugmentationEngine.LOGGER.info("Loaded {} Augmentations Options.", (Object)this.augmentations.size());
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
        //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/data/xml/impl/AugmentationEngine;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/data/xml/impl/AugmentationEngine.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
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
    
    private void parseSkills(final Options option, final Node innerNode) {
        final NamedNodeMap attr = innerNode.getAttributes();
        final SkillHolder skill = new SkillHolder(this.parseInt(attr, "id"), this.parseInt(attr, "level"));
        final OptionsSkillType type = (OptionsSkillType)this.parseEnum(attr, (Class)OptionsSkillType.class, "type");
        switch (type) {
            case ACTIVE: {
                option.addActiveSkill(skill);
                break;
            }
            case PASSIVE: {
                option.addPassiveSkill(skill);
                break;
            }
            default: {
                option.addActivationSkill(new OptionsSkillHolder(skill, this.parseDouble(attr, "chance"), type));
                break;
            }
        }
    }
    
    private void parseEffects(Node node, final Options option) {
        final Node child = node.getFirstChild();
        while (Objects.nonNull(node)) {
            String effectName;
            if ("effect".equals(child.getNodeName())) {
                effectName = this.parseString(child.getAttributes(), "name");
            }
            else {
                effectName = child.getNodeName();
            }
            final Function<StatsSet, AbstractEffect> factory = EffectHandler.getInstance().getHandlerFactory(effectName);
            if (Objects.isNull(factory)) {
                AugmentationEngine.LOGGER.error("could not parse options' {} effect {}", (Object)option, (Object)effectName);
            }
            else {
                option.addEffect(this.createEffect(factory, child));
            }
            node = node.getNextSibling();
        }
    }
    
    private AbstractEffect createEffect(final Function<StatsSet, AbstractEffect> factory, final Node node) {
        final StatsSet statsSet = new StatsSet(this.parseAttributes(node));
        StatsSet stats = null;
        if (node.hasChildNodes()) {
            final IntMap<StatsSet> levelInfo = this.parseEffectChildNodes(node, 1, 1, statsSet);
            if (!levelInfo.isEmpty()) {
                stats = (StatsSet)levelInfo.get(1);
                stats.merge(statsSet);
            }
        }
        return factory.apply(Objects.requireNonNullElse(stats, statsSet));
    }
    
    public Options getOptions(final int id) {
        return (Options)this.augmentations.get(id);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static AugmentationEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AugmentationEngine.class);
    }
    
    private static class Singleton
    {
        private static final AugmentationEngine INSTANCE;
        
        static {
            INSTANCE = new AugmentationEngine();
        }
    }
}
