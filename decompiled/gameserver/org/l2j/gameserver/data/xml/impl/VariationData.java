// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import io.github.joealisson.primitive.Containers;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Node;
import java.util.List;
import org.l2j.gameserver.model.options.OptionDataGroup;
import org.l2j.gameserver.model.options.Options;
import java.util.HashMap;
import org.l2j.gameserver.model.options.OptionDataCategory;
import java.util.ArrayList;
import org.l2j.gameserver.model.options.VariationWeaponType;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.options.Variation;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class VariationData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<Variation> variations;
    private final IntMap<IntMap<VariationFee>> fees;
    
    private VariationData() {
        this.variations = (IntMap<Variation>)new HashIntMap();
        this.fees = (IntMap<IntMap<VariationFee>>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/augmentation/Variations.xsd");
    }
    
    public void load() {
        this.variations.clear();
        this.fees.clear();
        this.parseDatapackFile("data/augmentation/Variations.xml");
        VariationData.LOGGER.info("Loaded {} Variations.", (Object)this.variations.size());
        VariationData.LOGGER.info("Loaded {} Fees.", (Object)this.fees.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final int mineralId;
        final Variation variation;
        final String weaponTypeString;
        final VariationWeaponType weaponType;
        final int order;
        final List<OptionDataCategory> sets;
        final double chance;
        final Map<Options, Double> options;
        final double optionChance;
        final int optionId;
        final Options opt;
        final Map<Options, Double> map;
        final double optionChance2;
        final int fromId;
        final int toId;
        int id;
        Options op;
        final Map<Options, Double> map2;
        final List<OptionDataCategory> list;
        final Variation variation2;
        final HashMap<Integer, List<Integer>> itemGroups;
        final int id2;
        final List<Integer> items;
        final int itemId;
        final List<Integer> list2;
        final Map<Integer, List<Integer>> map3;
        final int itemGroupId;
        final Map<K, List<Integer>> map4;
        final List<Integer> itemGroup;
        final int itemId2;
        final int itemCount;
        final int cancelFee;
        final VariationFee fee;
        final IntMap<VariationFee> feeByMinerals;
        final int mId;
        final IntMap intMap;
        final Object o;
        final int fromId2;
        int toId2;
        int id3;
        final IntMap intMap2;
        final Object o2;
        final Iterator<Integer> iterator;
        int item;
        IntMap<VariationFee> fees;
        this.forEach((Node)doc, "list", listNode -> {
            this.forEach(listNode, "variations", variationsNode -> this.forEach(variationsNode, "variation", variationNode -> {
                mineralId = this.parseInteger(variationNode.getAttributes(), "mineralId");
                if (ItemEngine.getInstance().getTemplate(mineralId) == null) {
                    VariationData.LOGGER.warn("Mineral with item id {}  was not found.", (Object)mineralId);
                }
                variation = new Variation(mineralId);
                this.forEach(variationNode, "optionGroup", groupNode -> {
                    weaponTypeString = this.parseString(groupNode.getAttributes(), "weaponType").toUpperCase();
                    weaponType = VariationWeaponType.valueOf(weaponTypeString);
                    order = this.parseInteger(groupNode.getAttributes(), "order");
                    sets = new ArrayList<OptionDataCategory>();
                    this.forEach(groupNode, "optionCategory", categoryNode -> {
                        chance = this.parseDouble(categoryNode.getAttributes(), "chance");
                        options = new HashMap<Options, Double>();
                        this.forEach(categoryNode, "option", optionNode -> {
                            optionChance = this.parseDouble(optionNode.getAttributes(), "chance");
                            optionId = this.parseInteger(optionNode.getAttributes(), "id");
                            opt = AugmentationEngine.getInstance().getOptions(optionId);
                            if (opt == null) {
                                VariationData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, optionId));
                                return;
                            }
                            else {
                                map.put(opt, optionChance);
                                return;
                            }
                        });
                        this.forEach(categoryNode, "optionRange", optionNode -> {
                            optionChance2 = this.parseDouble(optionNode.getAttributes(), "chance");
                            fromId = this.parseInteger(optionNode.getAttributes(), "from");
                            toId = this.parseInteger(optionNode.getAttributes(), "to");
                            id = fromId;
                            while (id <= toId) {
                                op = AugmentationEngine.getInstance().getOptions(id);
                                if (op == null) {
                                    VariationData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                }
                                else {
                                    map2.put(op, optionChance2);
                                    ++id;
                                }
                            }
                            return;
                        });
                        list.add(new OptionDataCategory(options, chance));
                        return;
                    });
                    variation2.setEffectGroup(weaponType, order, new OptionDataGroup(sets));
                    return;
                });
                this.variations.put(mineralId, (Object)variation);
            }));
            itemGroups = new HashMap<Integer, List<Integer>>();
            this.forEach(listNode, "itemGroups", variationsNode -> this.forEach(variationsNode, "itemGroup", variationNode -> {
                id2 = this.parseInteger(variationNode.getAttributes(), "id");
                items = new ArrayList<Integer>();
                this.forEach(variationNode, "item", itemNode -> {
                    itemId = this.parseInteger(itemNode.getAttributes(), "id");
                    if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                        VariationData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId));
                    }
                    list2.add(itemId);
                    return;
                });
                map3.put(id2, items);
            }));
            this.forEach(listNode, "fees", variationNode -> this.forEach(variationNode, "fee", feeNode -> {
                itemGroupId = this.parseInteger(feeNode.getAttributes(), "itemGroup");
                itemGroup = map4.get(itemGroupId);
                itemId2 = this.parseInteger(feeNode.getAttributes(), "itemId");
                itemCount = this.parseInteger(feeNode.getAttributes(), "itemCount");
                cancelFee = this.parseInteger(feeNode.getAttributes(), "cancelFee");
                if (ItemEngine.getInstance().getTemplate(itemId2) == null) {
                    VariationData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId2));
                }
                fee = new VariationFee(itemId2, itemCount, cancelFee);
                feeByMinerals = (IntMap<VariationFee>)new HashIntMap();
                this.forEach(feeNode, "mineral", mineralNode -> {
                    mId = this.parseInteger(mineralNode.getAttributes(), "id");
                    intMap.put(mId, o);
                    return;
                });
                this.forEach(feeNode, "mineralRange", mineralNode -> {
                    fromId2 = this.parseInteger(mineralNode.getAttributes(), "from");
                    for (toId2 = this.parseInteger(mineralNode.getAttributes(), "to"), id3 = fromId2; id3 <= toId2; ++id3) {
                        intMap2.put(id3, o2);
                    }
                    return;
                });
                itemGroup.iterator();
                while (iterator.hasNext()) {
                    item = iterator.next();
                    fees = (IntMap<VariationFee>)this.fees.computeIfAbsent(item, k -> new HashIntMap());
                    fees.putAll((IntMap)feeByMinerals);
                }
            }));
        });
    }
    
    public VariationInstance generateRandomVariation(final Variation variation, final Item targetItem) {
        final VariationWeaponType weaponType = (targetItem.getWeaponItem() != null && targetItem.getWeaponItem().isMagicWeapon()) ? VariationWeaponType.MAGE : VariationWeaponType.WARRIOR;
        return this.generateRandomVariation(variation, weaponType);
    }
    
    private VariationInstance generateRandomVariation(final Variation variation, final VariationWeaponType weaponType) {
        final Options option1 = variation.getRandomEffect(weaponType, 0);
        final Options option2 = variation.getRandomEffect(weaponType, 1);
        return (option1 != null && option2 != null) ? new VariationInstance(variation.getMineralId(), option1, option2) : null;
    }
    
    public final Variation getVariation(final int mineralId) {
        return (Variation)this.variations.get(mineralId);
    }
    
    public final VariationFee getFee(final int itemId, final int mineralId) {
        return (VariationFee)((IntMap)this.fees.getOrDefault(itemId, (Object)Containers.emptyIntMap())).get(mineralId);
    }
    
    public final long getCancelFee(final int itemId, final int mineralId) {
        final IntMap<VariationFee> fees = (IntMap<VariationFee>)this.fees.get(itemId);
        if (fees == null) {
            return -1L;
        }
        VariationFee fee = (VariationFee)fees.get(mineralId);
        if (fee == null) {
            VariationData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, itemId, mineralId));
            fee = fees.values().iterator().next();
            if (fee == null) {
                return -1L;
            }
        }
        return fee.getCancelFee();
    }
    
    public final boolean hasFeeData(final int itemId) {
        final IntMap<VariationFee> itemFees = (IntMap<VariationFee>)this.fees.get(itemId);
        return itemFees != null && !itemFees.isEmpty();
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static VariationData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)VariationData.class);
    }
    
    private static class Singleton
    {
        protected static final VariationData INSTANCE;
        
        static {
            INSTANCE = new VariationData();
        }
    }
}
