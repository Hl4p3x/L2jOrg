// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.shop;

import org.slf4j.LoggerFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import org.l2j.gameserver.network.serverpackets.store.ExBRProductInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.ArrayList;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import java.util.List;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class L2Store extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final int VIP_GIFT_BASE_ID = 100000;
    private final IntMap<L2StoreProduct> primeItems;
    private final IntMap<L2StoreProduct> vipGifts;
    
    private L2Store() {
        this.primeItems = (IntMap<L2StoreProduct>)new HashIntMap(140);
        this.vipGifts = (IntMap<L2StoreProduct>)new HashIntMap(10);
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/shop/l2-store.xsd");
    }
    
    public void load() {
        this.primeItems.clear();
        this.parseDatapackFile("data/shop/l2-store.xml");
        L2Store.LOGGER.info("Loaded {} items", (Object)this.primeItems.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "product", (Consumer)this::parseProduct));
    }
    
    private void parseProduct(final Node productNode) {
        final List<L2StoreItem> items = this.parseItems(productNode);
        if (Util.isNullOrEmpty((Collection)items)) {
            return;
        }
        final L2StoreProduct product = this.createProduct(productNode, items);
        if (product.isVipGift()) {
            this.vipGifts.put(product.getId(), (Object)product);
        }
        else {
            this.primeItems.put(product.getId(), (Object)product);
        }
    }
    
    private List<L2StoreItem> parseItems(final Node productNode) {
        final List<L2StoreItem> items = new ArrayList<L2StoreItem>();
        for (Node b = productNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            if ("item".equalsIgnoreCase(b.getNodeName())) {
                final NamedNodeMap attrs = b.getAttributes();
                final int itemId = this.parseInt(attrs, "id");
                final int count = this.parseInt(attrs, "count");
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                if (Objects.isNull(item)) {
                    L2Store.LOGGER.error("Item template does not exists for itemId: {} in product id {}", (Object)itemId, (Object)productNode.getAttributes().getNamedItem("id"));
                }
                else {
                    items.add(new L2StoreItem(itemId, count, item.getWeight(), item.isTradeable()));
                }
            }
        }
        return items;
    }
    
    private L2StoreProduct createProduct(final Node productNode, final List<L2StoreItem> items) {
        final NamedNodeMap attrs = productNode.getAttributes();
        final L2StoreProduct product = new L2StoreProduct(this.parseInt(attrs, "id"), items);
        product.setCategory(this.parseByte(attrs, "category"));
        product.setPaymentType(this.parseByte(attrs, "payment-type"));
        product.setPrice(this.parseInt(attrs, "price"));
        product.setPanelType(this.parseByte(attrs, "panel-type"));
        product.setRecommended(this.parseByte(attrs, "recommended"));
        product.setStart(this.parseInt(attrs, "start-sale"));
        product.setEnd(this.parseInt(attrs, "end-sale"));
        product.setDaysOfWeek(this.parseByte(attrs, "days-of-week"));
        product.setStartHour(this.parseByte(attrs, "star-hour"));
        product.setStartMinute(this.parseByte(attrs, "start-minute"));
        product.setStopHour(this.parseByte(attrs, "stop-hour"));
        product.setStopMinute(this.parseByte(attrs, "stop-minute"));
        product.setStock(this.parseByte(attrs, "stock"));
        product.setMaxStock(this.parseByte(attrs, "max-stock"));
        product.setSalePercent(this.parseByte(attrs, "sale-percent"));
        product.setMinLevel(this.parseByte(attrs, "min-level"));
        product.setMaxLevel(this.parseByte(attrs, "max-level"));
        product.setMinBirthday(this.parseByte(attrs, "min-birthday"));
        product.setMaxBirthday(this.parseByte(attrs, "max-birthday"));
        product.setRestrictionAmount(this.parseByte(attrs, "restriction-amount"));
        product.setRestrictionPeriod((RestrictionPeriod)this.parseEnum(attrs, (Class)RestrictionPeriod.class, "restriction-period"));
        product.setAvailableCount(this.parseByte(attrs, "available-count"));
        product.setVipTier(this.parseByte(attrs, "vip-tier"));
        product.setSilverCoin(this.parseInt(attrs, "silver-coin"));
        product.setVipGift(this.parseBoolean(attrs, "is-vip-gift"));
        return product;
    }
    
    public void showProductInfo(final Player player, final int brId) {
        final L2StoreProduct item = (L2StoreProduct)this.primeItems.get(brId);
        if (player == null || item == null) {
            return;
        }
        player.sendPacket(new ExBRProductInfo(item, player));
    }
    
    public L2StoreProduct getItem(final int productId) {
        if (this.primeItems.containsKey(productId)) {
            return (L2StoreProduct)this.primeItems.get(productId);
        }
        return (L2StoreProduct)this.vipGifts.get(productId);
    }
    
    public L2StoreProduct getVipGiftOfTier(final byte tier) {
        return (L2StoreProduct)this.vipGifts.get(100000 + tier);
    }
    
    public IntMap<L2StoreProduct> getPrimeItems() {
        return this.primeItems;
    }
    
    public boolean canReceiveVipGift(final Player player) {
        return player.getVipTier() > 0 && !((L2StoreDAO)DatabaseAccess.getDAO((Class)L2StoreDAO.class)).hasBoughtAnyProductInRangeToday(player.getAccountName(), 100001, 100010);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static L2Store getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)L2Store.class);
    }
    
    private static class Singleton
    {
        private static final L2Store INSTANCE;
        
        static {
            INSTANCE = new L2Store();
        }
    }
}
