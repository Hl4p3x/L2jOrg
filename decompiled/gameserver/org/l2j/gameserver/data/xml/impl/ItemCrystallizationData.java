// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.Collection;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.Objects;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import java.util.EnumMap;
import org.l2j.gameserver.model.holders.CrystallizationDataHolder;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import java.util.List;
import org.l2j.gameserver.enums.CrystallizationType;
import org.l2j.gameserver.model.item.type.CrystalType;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ItemCrystallizationData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<CrystalType, Map<CrystallizationType, List<ItemChanceHolder>>> crystallizationTemplates;
    private final IntMap<CrystallizationDataHolder> items;
    
    private ItemCrystallizationData() {
        this.crystallizationTemplates = new EnumMap<CrystalType, Map<CrystallizationType, List<ItemChanceHolder>>>(CrystalType.class);
        this.items = (IntMap<CrystallizationDataHolder>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/crystallizable-items.xsd");
    }
    
    public void load() {
        this.crystallizationTemplates.clear();
        CrystalType.forEach(c -> this.crystallizationTemplates.put(c, new EnumMap<CrystallizationType, List<ItemChanceHolder>>(CrystallizationType.class)));
        this.items.clear();
        this.parseDatapackFile("data/crystallizable-items.xml");
        ItemCrystallizationData.LOGGER.info("Loaded {} crystallization templates.", (Object)this.crystallizationTemplates.size());
        ItemCrystallizationData.LOGGER.info("Loaded {} pre-defined crystallizable items.", (Object)this.items.size());
        this.generateCrystallizationData();
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
        //     5: invokedynamic   BootstrapMethod #1, accept:(Lorg/l2j/gameserver/data/xml/impl/ItemCrystallizationData;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/data/xml/impl/ItemCrystallizationData.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
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
    
    private void parseTemplate(final Node node) {
        final NamedNodeMap attr;
        final CrystalType crystalType;
        final CrystallizationType crystallizationType;
        this.forEach(node, "template", templateNode -> {
            attr = templateNode.getAttributes();
            crystalType = (CrystalType)this.parseEnum(attr, (Class)CrystalType.class, "crystalType");
            crystallizationType = (CrystallizationType)this.parseEnum(attr, (Class)CrystallizationType.class, "crystallizationType");
            this.crystallizationTemplates.get(crystalType).put(crystallizationType, this.parseRewards(templateNode));
        });
    }
    
    private List<ItemChanceHolder> parseRewards(final Node templateNode) {
        final List<ItemChanceHolder> crystallizeRewards = new ArrayList<ItemChanceHolder>();
        final NamedNodeMap attrs;
        final int itemId;
        final long itemCount;
        final double itemChance;
        final List<ItemChanceHolder> list;
        this.forEach(templateNode, "item", itemNode -> {
            attrs = itemNode.getAttributes();
            itemId = this.parseInt(attrs, "id");
            itemCount = this.parseLong(attrs, "count");
            itemChance = this.parseDouble(attrs, "chance");
            list.add(new ItemChanceHolder(itemId, itemChance, itemCount));
            return;
        });
        return crystallizeRewards;
    }
    
    private void parseItem(final Node node) {
        final int id;
        this.forEach(node, "item", itemNode -> {
            id = this.parseInt(itemNode.getAttributes(), "id");
            this.items.put(id, (Object)new CrystallizationDataHolder(id, this.parseRewards(itemNode)));
        });
    }
    
    private List<ItemChanceHolder> calculateCrystallizeRewards(final ItemTemplate item, final List<ItemChanceHolder> crystallizeRewards) {
        if (Objects.isNull(crystallizeRewards)) {
            return null;
        }
        final List<ItemChanceHolder> rewards = new ArrayList<ItemChanceHolder>();
        for (final ItemChanceHolder reward : crystallizeRewards) {
            double chance = reward.getChance() * item.getCrystalCount();
            long count = reward.getCount();
            if (chance > 100.0) {
                final double countMul = Math.ceil(chance / 100.0);
                chance /= countMul;
                count *= (long)countMul;
            }
            rewards.add(new ItemChanceHolder(reward.getId(), chance, count));
        }
        return rewards;
    }
    
    private void generateCrystallizationData() {
        final int previousCount = this.items.size();
        if (this.crystallizationTemplates.values().stream().flatMap(c -> c.values().stream()).anyMatch(Predicate.not(List::isEmpty))) {
            for (final ItemTemplate item : ItemEngine.getInstance().getAllItems()) {
                if ((GameUtils.isWeapon(item) || GameUtils.isArmor(item)) && item.isCrystallizable() && !this.items.containsKey(item.getId())) {
                    final List<ItemChanceHolder> holder = this.crystallizationTemplates.get(item.getCrystalType()).get(GameUtils.isWeapon(item) ? CrystallizationType.WEAPON : CrystallizationType.ARMOR);
                    if (!Objects.nonNull(holder)) {
                        continue;
                    }
                    this.items.put(item.getId(), (Object)new CrystallizationDataHolder(item.getId(), this.calculateCrystallizeRewards(item, holder)));
                }
            }
        }
        ItemCrystallizationData.LOGGER.atInfo().addArgument(() -> this.items.size() - previousCount).log("Generated {} crystallizable items from templates.");
    }
    
    private CrystallizationDataHolder getCrystallizationData(final int itemId) {
        return (CrystallizationDataHolder)this.items.get(itemId);
    }
    
    public List<ItemChanceHolder> getCrystallizationRewards(final Item item) {
        final List<ItemChanceHolder> result = new ArrayList<ItemChanceHolder>();
        final CrystallizationDataHolder data = this.getCrystallizationData(item.getId());
        if (Objects.nonNull(data)) {
            if (data.getItems().stream().noneMatch(i -> i.getId() == item.getTemplate().getCrystalItemId())) {
                result.add(new ItemChanceHolder(item.getTemplate().getCrystalItemId(), 100.0, item.getCrystalCount()));
            }
            result.addAll(data.getItems());
        }
        else {
            result.add(new ItemChanceHolder(item.getTemplate().getCrystalItemId(), 100.0, item.getCrystalCount()));
        }
        return result;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ItemCrystallizationData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemCrystallizationData.class);
    }
    
    private static class Singleton
    {
        private static final ItemCrystallizationData INSTANCE;
        
        static {
            INSTANCE = new ItemCrystallizationData();
        }
    }
}
