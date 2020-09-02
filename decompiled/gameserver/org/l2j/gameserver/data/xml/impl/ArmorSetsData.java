// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.stream.IntStream;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import org.l2j.gameserver.model.ArmorSet;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ArmorSetsData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, ArmorSet> _armorSets;
    private final Map<Integer, List<ArmorSet>> _armorSetItems;
    
    private ArmorSetsData() {
        this._armorSets = new HashMap<Integer, ArmorSet>();
        this._armorSetItems = new HashMap<Integer, List<ArmorSet>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/armorSets.xsd");
    }
    
    public void load() {
        this._armorSets.clear();
        this.parseDatapackDirectory("data/stats/armorsets", false);
        ArmorSetsData.LOGGER.info("Loaded {} Armor sets.", (Object)this._armorSets.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node setNode = n.getFirstChild(); setNode != null; setNode = setNode.getNextSibling()) {
                    if ("set".equalsIgnoreCase(setNode.getNodeName())) {
                        final int id = this.parseInt(setNode.getAttributes(), "id");
                        final int minimumPieces = this.parseInt(setNode.getAttributes(), "minimumPieces", 0);
                        final boolean isVisual = this.parseBoolean(setNode.getAttributes(), "visual", false);
                        final ArmorSet set = new ArmorSet(id, minimumPieces, isVisual);
                        if (this._armorSets.putIfAbsent(id, set) != null) {
                            ArmorSetsData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, id, f.getName()));
                        }
                        for (Node innerSetNode = setNode.getFirstChild(); innerSetNode != null; innerSetNode = innerSetNode.getNextSibling()) {
                            final String nodeName = innerSetNode.getNodeName();
                            switch (nodeName) {
                                case "requiredItems": {
                                    final NamedNodeMap attrs;
                                    final int itemId;
                                    final ItemTemplate item;
                                    final ArmorSet set2;
                                    this.forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node -> {
                                        attrs = node.getAttributes();
                                        itemId = this.parseInt(attrs, "id");
                                        item = ItemEngine.getInstance().getTemplate(itemId);
                                        if (item == null) {
                                            ArmorSetsData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, itemId, f.getName()));
                                        }
                                        else if (!set2.addRequiredItem(itemId)) {
                                            ArmorSetsData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/ItemTemplate;Ljava/lang/String;)Ljava/lang/String;, item, f.getName()));
                                        }
                                        return;
                                    });
                                    break;
                                }
                                case "optionalItems": {
                                    final NamedNodeMap attrs2;
                                    final int itemId2;
                                    final ItemTemplate item2;
                                    final ArmorSet set3;
                                    this.forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node -> {
                                        attrs2 = node.getAttributes();
                                        itemId2 = this.parseInt(attrs2, "id");
                                        item2 = ItemEngine.getInstance().getTemplate(itemId2);
                                        if (item2 == null) {
                                            ArmorSetsData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, itemId2, f.getName()));
                                        }
                                        else if (!set3.addOptionalItem(itemId2)) {
                                            ArmorSetsData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/ItemTemplate;Ljava/lang/String;)Ljava/lang/String;, item2, f.getName()));
                                        }
                                        return;
                                    });
                                    break;
                                }
                                case "skills": {
                                    final NamedNodeMap attrs3;
                                    final int skillId;
                                    final int skillLevel;
                                    final ArmorSet set4;
                                    final int minPieces;
                                    final int minEnchant;
                                    final boolean isOptional;
                                    final int artifactSlotMask;
                                    final int artifactBookSlot;
                                    this.forEach(innerSetNode, b -> "skill".equals(b.getNodeName()), node -> {
                                        attrs3 = node.getAttributes();
                                        skillId = this.parseInt(attrs3, "id");
                                        skillLevel = this.parseInt(attrs3, "level");
                                        minPieces = this.parseInt(attrs3, "minimumPieces", set4.getMinimumPieces());
                                        minEnchant = this.parseInt(attrs3, "minimumEnchant", 0);
                                        isOptional = this.parseBoolean(attrs3, "optional", false);
                                        artifactSlotMask = this.parseInt(attrs3, "slotMask", 0);
                                        artifactBookSlot = this.parseInt(attrs3, "bookSlot", 0);
                                        set4.addSkill(new ArmorsetSkillHolder(skillId, skillLevel, minPieces, minEnchant, isOptional, artifactSlotMask, artifactBookSlot));
                                        return;
                                    });
                                    break;
                                }
                                case "stats": {
                                    final NamedNodeMap attrs4;
                                    final ArmorSet set5;
                                    this.forEach(innerSetNode, b -> "stat".equals(b.getNodeName()), node -> {
                                        attrs4 = node.getAttributes();
                                        set5.addStatsBonus((BaseStats)this.parseEnum(attrs4, (Class)BaseStats.class, "type"), this.parseInt(attrs4, "val"));
                                        return;
                                    });
                                    break;
                                }
                            }
                        }
                        IntStream.concat(set.getRequiredItems().stream(), set.getOptionalItems().stream()).forEach(itemHolder -> this._armorSetItems.computeIfAbsent(itemHolder, key -> new ArrayList()).add(set));
                    }
                }
            }
        }
    }
    
    public ArmorSet getSet(final int setId) {
        return this._armorSets.get(setId);
    }
    
    public List<ArmorSet> getSets(final int itemId) {
        return this._armorSetItems.getOrDefault(itemId, Collections.emptyList());
    }
    
    public static ArmorSetsData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ArmorSetsData.class);
    }
    
    private static class Singleton
    {
        protected static final ArmorSetsData INSTANCE;
        
        static {
            INSTANCE = new ArmorSetsData();
        }
    }
}
