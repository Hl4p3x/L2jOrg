// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.RecipeStat;
import org.l2j.gameserver.model.Recipe;
import java.util.ArrayList;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.RecipeList;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class RecipeData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, RecipeList> _recipes;
    
    private RecipeData() {
        this._recipes = new HashMap<Integer, RecipeList>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/Recipes.xsd");
    }
    
    public void load() {
        this._recipes.clear();
        this.parseDatapackFile("data/Recipes.xml");
        RecipeData.LOGGER.info("Loaded {} recipes.", (Object)this._recipes.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final List<Recipe> recipePartList = new ArrayList<Recipe>();
        final List<RecipeStat> recipeStatUseList = new ArrayList<RecipeStat>();
        final List<RecipeStat> recipeAltStatChangeList = new ArrayList<RecipeStat>();
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
            Label_1095:
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        recipePartList.clear();
                        recipeStatUseList.clear();
                        recipeAltStatChangeList.clear();
                        final NamedNodeMap attrs = d.getAttributes();
                        int id = -1;
                        boolean haveRare = false;
                        final StatsSet set = new StatsSet();
                        Node att = attrs.getNamedItem("id");
                        if (att == null) {
                            RecipeData.LOGGER.error(": Missing id for recipe item, skipping");
                        }
                        else {
                            id = Integer.parseInt(att.getNodeValue());
                            set.set("id", id);
                            att = attrs.getNamedItem("recipeId");
                            if (att == null) {
                                RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                            }
                            else {
                                set.set("recipeId", Integer.parseInt(att.getNodeValue()));
                                att = attrs.getNamedItem("name");
                                if (att == null) {
                                    RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                }
                                else {
                                    set.set("recipeName", att.getNodeValue());
                                    att = attrs.getNamedItem("craftLevel");
                                    if (att == null) {
                                        RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                    }
                                    else {
                                        set.set("craftLevel", Integer.parseInt(att.getNodeValue()));
                                        att = attrs.getNamedItem("type");
                                        if (att == null) {
                                            RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                        }
                                        else {
                                            set.set("isDwarvenRecipe", att.getNodeValue().equalsIgnoreCase("dwarven"));
                                            att = attrs.getNamedItem("successRate");
                                            if (att == null) {
                                                RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                            }
                                            else {
                                                set.set("successRate", Integer.parseInt(att.getNodeValue()));
                                                for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                                                    if ("statUse".equalsIgnoreCase(c.getNodeName())) {
                                                        final String statName = c.getAttributes().getNamedItem("name").getNodeValue();
                                                        final int value = Integer.parseInt(c.getAttributes().getNamedItem("value").getNodeValue());
                                                        try {
                                                            recipeStatUseList.add(new RecipeStat(statName, value));
                                                        }
                                                        catch (Exception e) {
                                                            RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                                            continue Label_1095;
                                                        }
                                                    }
                                                    else if ("altStatChange".equalsIgnoreCase(c.getNodeName())) {
                                                        final String statName = c.getAttributes().getNamedItem("name").getNodeValue();
                                                        final int value = Integer.parseInt(c.getAttributes().getNamedItem("value").getNodeValue());
                                                        try {
                                                            recipeAltStatChangeList.add(new RecipeStat(statName, value));
                                                        }
                                                        catch (Exception e) {
                                                            RecipeData.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                                                            continue Label_1095;
                                                        }
                                                    }
                                                    else if ("ingredient".equalsIgnoreCase(c.getNodeName())) {
                                                        final int ingId = Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue());
                                                        final int ingCount = Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue());
                                                        recipePartList.add(new Recipe(ingId, ingCount));
                                                    }
                                                    else if ("production".equalsIgnoreCase(c.getNodeName())) {
                                                        set.set("itemId", Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue()));
                                                        set.set("count", Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue()));
                                                    }
                                                    else if ("productionRare".equalsIgnoreCase(c.getNodeName())) {
                                                        set.set("rareItemId", Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue()));
                                                        set.set("rareCount", Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue()));
                                                        set.set("rarity", Integer.parseInt(c.getAttributes().getNamedItem("rarity").getNodeValue()));
                                                        haveRare = true;
                                                    }
                                                }
                                                final RecipeList recipeList = new RecipeList(set, haveRare);
                                                for (final Recipe recipePart : recipePartList) {
                                                    recipeList.addRecipe(recipePart);
                                                }
                                                for (final RecipeStat recipeStatUse : recipeStatUseList) {
                                                    recipeList.addStatUse(recipeStatUse);
                                                }
                                                for (final RecipeStat recipeAltStatChange : recipeAltStatChangeList) {
                                                    recipeList.addAltStatChange(recipeAltStatChange);
                                                }
                                                this._recipes.put(id, recipeList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public RecipeList getRecipeList(final int listId) {
        return this._recipes.get(listId);
    }
    
    public RecipeList getRecipeByItemId(final int itemId) {
        for (final RecipeList find : this._recipes.values()) {
            if (find.getRecipeId() == itemId) {
                return find;
            }
        }
        return null;
    }
    
    public int[] getAllItemIds() {
        final int[] idList = new int[this._recipes.size()];
        int i = 0;
        for (final RecipeList rec : this._recipes.values()) {
            idList[i++] = rec.getRecipeId();
        }
        return idList;
    }
    
    public RecipeList getValidRecipeList(final Player player, final int id) {
        final RecipeList recipeList = this._recipes.get(id);
        if (recipeList == null || recipeList.getRecipes().length == 0) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
            player.setIsCrafting(false);
            return null;
        }
        return recipeList;
    }
    
    public static RecipeData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RecipeData.class);
    }
    
    private static class Singleton
    {
        private static final RecipeData INSTANCE;
        
        static {
            INSTANCE = new RecipeData();
        }
    }
}
