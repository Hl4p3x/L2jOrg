// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Objects;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.w3c.dom.NodeList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class LCoinShopData extends GameXmlReader
{
    private static final Logger LOGGER;
    private IntMap<LCoinShopProductInfo> productInfos;
    
    public LCoinShopData() {
        this.productInfos = (IntMap<LCoinShopProductInfo>)new HashIntMap();
        this.load();
    }
    
    public LCoinShopProductInfo getProductInfo(final int id) {
        return (LCoinShopProductInfo)this.productInfos.get(id);
    }
    
    public IntMap<LCoinShopProductInfo> getProductInfos() {
        return this.productInfos;
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/LCoinShop.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/LCoinShop.xml");
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "product", (Consumer)this::parseProduct));
    }
    
    private void parseProduct(final Node productNode) {
        final NamedNodeMap attributes = productNode.getAttributes();
        final Integer id = this.parseInteger(attributes, "id");
        final LCoinShopProductInfo.Category category = (LCoinShopProductInfo.Category)this.parseEnum(attributes, (Class)LCoinShopProductInfo.Category.class, "category", (Enum)LCoinShopProductInfo.Category.Equip);
        final Integer limitPerDay = this.parseInteger(attributes, "limitPerDay", Integer.valueOf(0));
        final Integer minLevel = this.parseInteger(attributes, "minLevel", Integer.valueOf(1));
        final Boolean isEvent = this.parseBoolean(attributes, "isEvent", Boolean.valueOf(false));
        final Integer remainServerItemAmount = this.parseInteger(attributes, "remainServerItemAmount", Integer.valueOf(-1));
        final List<ItemHolder> ingredients = new ArrayList<ItemHolder>();
        ItemHolder production = null;
        final NodeList list = productNode.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            final Node targetNode = list.item(i);
            final ItemHolder holder = this.parseItemInfo(targetNode);
            if (holder == null) {
                return;
            }
            if ("ingredient".equalsIgnoreCase(targetNode.getNodeName())) {
                ingredients.add(holder);
            }
            else {
                production = holder;
            }
        }
        if (ingredients.isEmpty() || production == null) {
            LCoinShopData.LOGGER.error("Incorrect configuration product id {}", (Object)id);
            return;
        }
        if (this.productInfos.put((int)id, (Object)new LCoinShopProductInfo(id, category, limitPerDay, minLevel, isEvent, ingredients, production, remainServerItemAmount)) != null) {
            LCoinShopData.LOGGER.warn("Duplicate product id {}", (Object)id);
        }
    }
    
    private ItemHolder parseItemInfo(final Node itemInfoNode) {
        final NamedNodeMap attributes = itemInfoNode.getAttributes();
        final Integer itemId = this.parseInteger(attributes, "id");
        final Integer count = this.parseInteger(attributes, "count");
        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (Objects.isNull(item)) {
            LCoinShopData.LOGGER.error("Item template does not exists for itemId: {} in product id {}", (Object)itemId, (Object)itemInfoNode.getAttributes().getNamedItem("id"));
            return null;
        }
        return new ItemHolder(itemId, count);
    }
    
    public static LCoinShopData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)LCoinShopData.class);
    }
    
    private static class Singleton
    {
        private static final LCoinShopData INSTANCE;
        
        static {
            INSTANCE = new LCoinShopData();
        }
    }
}
