// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.network.serverpackets.MultiSellList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.IntSet;
import java.util.List;
import org.w3c.dom.Node;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.MultisellEntryHolder;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.holders.MultisellListHolder;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class MultisellData extends GameXmlReader
{
    public static final int PAGE_SIZE = 40;
    private static final Logger LOGGER;
    private final Map<Integer, MultisellListHolder> _multisells;
    
    private MultisellData() {
        this._multisells = new HashMap<Integer, MultisellListHolder>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/multisell.xsd");
    }
    
    public void load() {
        this._multisells.clear();
        this.parseDatapackDirectory("data/multisell", false);
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).loadCustomMultisell()) {
            this.parseDatapackDirectory("data/multisell/custom", false);
        }
        MultisellData.LOGGER.info("Loaded {} multisell lists.", (Object)this._multisells.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        try {
            final StatsSet set;
            final int listId;
            final ArrayList entries;
            List<ItemChanceHolder> ingredients;
            List<ItemChanceHolder> products;
            MultisellEntryHolder entry;
            Node d;
            int id;
            long count;
            byte enchantmentLevel;
            Boolean maintainIngredient;
            ItemChanceHolder ingredient;
            final Object o;
            int id2;
            long count2;
            double chance;
            byte enchantmentLevel2;
            ItemChanceHolder product;
            double totalChance;
            final List<MultisellEntryHolder> list;
            IntSet allowNpc;
            final StatsSet set2;
            this.forEach((Node)doc, "list", listNode -> {
                set = new StatsSet(this.parseAttributes(listNode));
                listId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
                entries = new ArrayList<MultisellEntryHolder>(listNode.getChildNodes().getLength());
                this.forEach(listNode, itemNode -> {
                    if ("item".equalsIgnoreCase(itemNode.getNodeName())) {
                        ingredients = new ArrayList<ItemChanceHolder>(1);
                        products = new ArrayList<ItemChanceHolder>(1);
                        entry = new MultisellEntryHolder(ingredients, products);
                        for (d = itemNode.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("ingredient".equalsIgnoreCase(d.getNodeName())) {
                                id = this.parseInt(d.getAttributes(), "id");
                                count = this.parseLong(d.getAttributes(), "count");
                                enchantmentLevel = this.parseByte(d.getAttributes(), "enchantmentLevel", (byte)0);
                                maintainIngredient = this.parseBoolean(d.getAttributes(), "maintainIngredient", (boolean)(0 != 0));
                                ingredient = new ItemChanceHolder(id, 0.0, count, enchantmentLevel, maintainIngredient);
                                if (this.itemExists(ingredient)) {
                                    ingredients.add(ingredient);
                                }
                                else {
                                    MultisellData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(IJI)Ljava/lang/String;, ingredient.getId(), ingredient.getCount(), o));
                                }
                            }
                            else if ("production".equalsIgnoreCase(d.getNodeName())) {
                                id2 = this.parseInt(d.getAttributes(), "id");
                                count2 = this.parseLong(d.getAttributes(), "count");
                                chance = this.parseDouble(d.getAttributes(), "chance", Double.NaN);
                                enchantmentLevel2 = this.parseByte(d.getAttributes(), "enchantmentLevel", (byte)0);
                                product = new ItemChanceHolder(id2, chance, count2, enchantmentLevel2);
                                if (this.itemExists(product)) {
                                    if ((!Double.isNaN(chance) && chance < 0.0) || chance > 100.0) {
                                        MultisellData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(IJDI)Ljava/lang/String;, product.getId(), product.getCount(), chance, o));
                                    }
                                    else {
                                        products.add(product);
                                    }
                                }
                                else {
                                    MultisellData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(IJI)Ljava/lang/String;, product.getId(), product.getCount(), o));
                                }
                            }
                        }
                        totalChance = products.stream().filter(i -> !Double.isNaN(i.getChance())).mapToDouble(ItemChanceHolder::getChance).sum();
                        if (totalChance > 100.0) {
                            MultisellData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(DII)Ljava/lang/String;, totalChance, o, list.size()));
                        }
                        list.add(entry);
                    }
                    else if ("npcs".equalsIgnoreCase(itemNode.getNodeName())) {
                        allowNpc = (IntSet)new HashIntSet(itemNode.getChildNodes().getLength());
                        this.forEach(itemNode, n -> "npc".equalsIgnoreCase(n.getNodeName()), n -> allowNpc.add(Integer.parseInt(n.getTextContent())));
                        set2.set("allowNpc", allowNpc);
                    }
                    return;
                });
                set.set("listId", listId);
                set.set("entries", entries);
                this._multisells.put(listId, new MultisellListHolder(set));
            });
        }
        catch (Exception e) {
            MultisellData.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/io/File;)Ljava/lang/String;, f), (Throwable)e);
        }
    }
    
    public final void separateAndSend(final int listId, final Player player, final Npc npc, final boolean inventoryOnly, double ingredientMultiplier, double productMultiplier) {
        final MultisellListHolder template = this._multisells.get(listId);
        if (template == null) {
            MultisellData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;I)Ljava/lang/String;, listId, player.getName(), (npc != null) ? npc.getId() : 0));
            return;
        }
        if (!template.isNpcAllowed(-1) && (Objects.isNull(npc) || !template.isNpcAllowed(npc.getId()))) {
            if (!player.isGM()) {
                MultisellData.LOGGER.warn("Player {} attempted to open multisell {} from npc {} which is not allowed!", new Object[] { player, listId, npc });
                return;
            }
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, listId));
        }
        ingredientMultiplier = (Double.isNaN(ingredientMultiplier) ? template.getIngredientMultiplier() : ingredientMultiplier);
        productMultiplier = (Double.isNaN(productMultiplier) ? template.getProductMultiplier() : productMultiplier);
        final PreparedMultisellListHolder list = new PreparedMultisellListHolder(template, inventoryOnly, player.getInventory(), npc, ingredientMultiplier, productMultiplier);
        int index = 0;
        do {
            player.sendPacket(new MultiSellList(list, index));
            index += 40;
        } while (index < list.getEntries().size());
        player.setMultiSell(list);
    }
    
    public final void separateAndSend(final int listId, final Player player, final Npc npc, final boolean inventoryOnly) {
        this.separateAndSend(listId, player, npc, inventoryOnly, Double.NaN, Double.NaN);
    }
    
    private boolean itemExists(final ItemHolder holder) {
        final SpecialItemType specialItem = SpecialItemType.getByClientId(holder.getId());
        return specialItem != null || Objects.nonNull(ItemEngine.getInstance().getTemplate(holder.getId()));
    }
    
    public static MultisellData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MultisellData.class);
    }
    
    private static class Singleton
    {
        private static final MultisellData INSTANCE;
        
        static {
            INSTANCE = new MultisellData();
        }
    }
}
