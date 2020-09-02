// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.item.instance.Item;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.options.EnchantOptions;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class EnchantItemOptionsData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<IntMap<EnchantOptions>> data;
    
    private EnchantItemOptionsData() {
        this.data = (IntMap<IntMap<EnchantOptions>>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/EnchantItemOptions.xsd");
    }
    
    public synchronized void load() {
        this.data.clear();
        this.parseDatapackFile("data/EnchantItemOptions.xml");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attr;
        final EnchantOptions option;
        byte i;
        this.forEach((Node)doc, "list", list -> this.forEach(list, "item", itemNode -> this.forEach(itemNode, "options", optionsNode -> {
            attr = optionsNode.getAttributes();
            option = new EnchantOptions(this.parseInt(attr, "level"));
            for (i = 0; i < 3; ++i) {
                option.setOption(i, this.parseInt(attr, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i + 1)));
            }
            ((IntMap)this.data.computeIfAbsent(this.parseInt(itemNode.getAttributes(), "id"), id -> new HashIntMap())).put(option.getLevel(), (Object)option);
        })));
        EnchantItemOptionsData.LOGGER.info("Loaded {} Option Items.", (Object)this.data.size());
    }
    
    public EnchantOptions getOptions(final int itemId, final int enchantLevel) {
        if (!this.data.containsKey(itemId) || !((IntMap)this.data.get(itemId)).containsKey(enchantLevel)) {
            return null;
        }
        return (EnchantOptions)((IntMap)this.data.get(itemId)).get(enchantLevel);
    }
    
    public EnchantOptions getOptions(final Item item) {
        return (EnchantOptions)Util.computeIfNonNull((Object)item, i -> this.getOptions(i.getId(), i.getEnchantLevel()));
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static EnchantItemOptionsData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EnchantItemOptionsData.class);
    }
    
    private static class Singleton
    {
        protected static final EnchantItemOptionsData INSTANCE;
        
        static {
            INSTANCE = new EnchantItemOptionsData();
        }
    }
}
