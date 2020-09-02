// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.w3c.dom.NamedNodeMap;
import java.util.List;
import java.util.Objects;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class PrimeShopData extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final int VIP_GIFT_BASE_ID = 100000;
    private final IntMap<PrimeShopProduct> primeItems;
    private final IntMap<PrimeShopProduct> vipGifts;
    
    private PrimeShopData() {
        this.primeItems = (IntMap<PrimeShopProduct>)new HashIntMap(140);
        this.vipGifts = (IntMap<PrimeShopProduct>)new HashIntMap(10);
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/primeShop.xsd");
    }
    
    public void load() {
        this.primeItems.clear();
        this.parseDatapackFile("data/primeShop.xml");
        PrimeShopData.LOGGER.info("Loaded {} items", (Object)this.primeItems.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "product", (Consumer)this::parseProduct));
    }
    
    private void parseProduct(final Node productNode) {
        final List<PrimeShopItem> items = new ArrayList<PrimeShopItem>();
        for (Node b = productNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            if ("item".equalsIgnoreCase(b.getNodeName())) {
                final NamedNodeMap attrs = b.getAttributes();
                final int itemId = this.parseInteger(attrs, "id");
                final int count = this.parseInteger(attrs, "count");
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                if (Objects.isNull(item)) {
                    PrimeShopData.LOGGER.error("Item template does not exists for itemId: {} in product id {}", (Object)itemId, (Object)productNode.getAttributes().getNamedItem("id"));
                    return;
                }
                items.add(new PrimeShopItem(itemId, count, item.getWeight(), (int)(item.isTradeable() ? 1 : 0)));
            }
        }
        final NamedNodeMap attrs2 = productNode.getAttributes();
        final PrimeShopProduct product = new PrimeShopProduct(this.parseInteger(attrs2, "id"), items);
        product.setCategory(this.parseByte(attrs2, "category"));
        product.setPaymentType(this.parseByte(attrs2, "paymentType"));
        product.setPrice(this.parseInteger(attrs2, "price"));
        product.setPanelType(this.parseByte(attrs2, "panelType"));
        product.setRecommended(this.parseByte(attrs2, "recommended"));
        product.setStart(this.parseInteger(attrs2, "startSale"));
        product.setEnd(this.parseInteger(attrs2, "endSale"));
        product.setDaysOfWeek(this.parseByte(attrs2, "dayOfWeek"));
        product.setStartHour(this.parseByte(attrs2, "startHour"));
        product.setStartMinute(this.parseByte(attrs2, "startMinute"));
        product.setStopHour(this.parseByte(attrs2, "stopHour"));
        product.setStopMinute(this.parseByte(attrs2, "stopMinute"));
        product.setStock(this.parseByte(attrs2, "stock"));
        product.setMaxStock(this.parseByte(attrs2, "maxStock"));
        product.setSalePercent(this.parseByte(attrs2, "salePercent"));
        product.setMinLevel(this.parseByte(attrs2, "minLevel"));
        product.setMaxLevel(this.parseByte(attrs2, "maxLevel"));
        product.setMinBirthday(this.parseByte(attrs2, "minBirthday"));
        product.setMaxBirthday(this.parseByte(attrs2, "maxBirthday"));
        product.setRestrictionDay(this.parseByte(attrs2, "restrictionDay"));
        product.setAvailableCount(this.parseByte(attrs2, "availableCount"));
        product.setVipTier(this.parseByte(attrs2, "vipTier"));
        product.setSilverCoin(this.parseInteger(attrs2, "silverCoin"));
        product.setVipGift(this.parseBoolean(attrs2, "isVipGift"));
        if (product.isVipGift()) {
            this.vipGifts.put(product.getId(), (Object)product);
        }
        else {
            this.primeItems.put(product.getId(), (Object)product);
        }
    }
    
    public void showProductInfo(final Player player, final int brId) {
        final PrimeShopProduct item = (PrimeShopProduct)this.primeItems.get(brId);
        if (player == null || item == null) {
            return;
        }
        player.sendPacket(new ExBRProductInfo(item, player));
    }
    
    public PrimeShopProduct getItem(final int productId) {
        if (this.primeItems.containsKey(productId)) {
            return (PrimeShopProduct)this.primeItems.get(productId);
        }
        return (PrimeShopProduct)this.vipGifts.get(productId);
    }
    
    public PrimeShopProduct getVipGiftOfTier(final byte tier) {
        return (PrimeShopProduct)this.vipGifts.get(100000 + tier);
    }
    
    public IntMap<PrimeShopProduct> getPrimeItems() {
        return this.primeItems;
    }
    
    public static PrimeShopData getInstance() {
        return Singleton.INSTANCE;
    }
    
    public boolean canReceiveVipGift(final Player player) {
        return player.getVipTier() > 0 && !((PrimeShopDAO)DatabaseAccess.getDAO((Class)PrimeShopDAO.class)).hasBougthAnyItemInRangeToday(player.getObjectId(), 100001, 100010);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PrimeShopData.class);
    }
    
    private static class Singleton
    {
        private static final PrimeShopData INSTANCE;
        
        static {
            INSTANCE = new PrimeShopData();
        }
    }
}
