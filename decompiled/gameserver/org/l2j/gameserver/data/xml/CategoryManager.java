// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import io.github.joealisson.primitive.HashIntSet;
import java.util.Objects;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.EnumMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.CategoryType;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class CategoryManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<CategoryType, IntSet> categories;
    
    private CategoryManager() {
        this.categories = new EnumMap<CategoryType, IntSet>(CategoryType.class);
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/categories.xsd");
    }
    
    public void load() {
        this.categories.clear();
        this.parseDatapackFile("data/categories.xml");
        CategoryManager.LOGGER.info("Loaded {} Categories", (Object)this.categories.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attrs;
        final CategoryType categoryType;
        IntSet set;
        this.forEach((Node)doc, "list", list -> this.forEach(list, "category", category -> {
            attrs = category.getAttributes();
            categoryType = (CategoryType)this.parseEnum(attrs, (Class)CategoryType.class, "name", (Enum)null);
            if (Objects.isNull(categoryType)) {
                CategoryManager.LOGGER.warn("Can't find category by name: {}", (Object)attrs.getNamedItem("name").getNodeValue());
            }
            else {
                set = (IntSet)new HashIntSet();
                this.forEach(category, "id", idNode -> set.add(Integer.parseInt(idNode.getTextContent())));
                this.categories.put(categoryType, set);
            }
        }));
    }
    
    public boolean isInCategory(final CategoryType type, final int id) {
        final IntSet category = this.getCategoryByType(type);
        if (Objects.isNull(category)) {
            CategoryManager.LOGGER.warn("Can't find category type: {}", (Object)type);
            return false;
        }
        return category.contains(id);
    }
    
    public boolean isInCategory(final CategoryType type, final Creature creature) {
        final Player player;
        final int id = (creature instanceof Player && (player = (Player)creature) == creature) ? player.getClassId().getId() : creature.getId();
        final IntSet category = this.getCategoryByType(type);
        if (Objects.isNull(category)) {
            CategoryManager.LOGGER.warn("Can't find category type: {}", (Object)type);
            return false;
        }
        return category.contains(id);
    }
    
    public IntSet getCategoryByType(final CategoryType type) {
        return this.categories.get(type);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static CategoryManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CategoryManager.class);
    }
    
    private static class Singleton
    {
        private static final CategoryManager INSTANCE;
        
        static {
            INSTANCE = new CategoryManager();
        }
    }
}
