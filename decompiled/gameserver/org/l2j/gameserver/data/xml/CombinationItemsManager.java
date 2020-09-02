// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.combination.CombinationItemReward;
import org.l2j.gameserver.model.item.combination.CombinationItemType;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Node;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.ArrayList;
import org.l2j.gameserver.model.item.combination.CombinationItem;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class CombinationItemsManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final List<CombinationItem> items;
    
    private CombinationItemsManager() {
        this.items = new ArrayList<CombinationItem>();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/combination-items.xsd");
    }
    
    public synchronized void load() {
        this.items.clear();
        this.parseDatapackFile("data/combination-items.xml");
        CombinationItemsManager.LOGGER.info("Loaded {} combinations", (Object)this.items.size());
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
        //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/data/xml/CombinationItemsManager;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/data/xml/CombinationItemsManager.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
        //    13: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  doc   
        //  f     
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
    
    public CombinationItem getItemsBySlots(final int firstSlot, final int secondSlot) {
        return this.items.stream().filter(item -> item.getItemOne() == firstSlot && item.getItemTwo() == secondSlot).findFirst().orElse(null);
    }
    
    public List<CombinationItem> getItemsByFirstSlot(final int id) {
        return this.items.stream().filter(item -> item.getItemOne() == id).collect((Collector<? super Object, ?, List<CombinationItem>>)Collectors.toList());
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static CombinationItemsManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CombinationItemsManager.class);
    }
    
    private static class Singleton
    {
        private static final CombinationItemsManager INSTANCE;
        
        static {
            INSTANCE = new CombinationItemsManager();
        }
    }
}
