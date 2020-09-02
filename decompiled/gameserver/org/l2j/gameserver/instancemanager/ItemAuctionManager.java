// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ItemAuctionManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, ItemAuctionInstance> _managerInstances;
    private final AtomicInteger _auctionIds;
    
    private ItemAuctionManager() {
        this._managerInstances = new HashMap<Integer, ItemAuctionInstance>();
        this._auctionIds = new AtomicInteger(1);
        if (!Config.ALT_ITEM_AUCTION_ENABLED) {
            ItemAuctionManager.LOGGER.info("Disabled by config.");
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement statement = con.createStatement();
                try {
                    final ResultSet rset = statement.executeQuery("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1");
                    try {
                        if (rset.next()) {
                            this._auctionIds.set(rset.getInt(1) + 1);
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (SQLException e) {
            ItemAuctionManager.LOGGER.error("Failed loading auctions.", (Throwable)e);
        }
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/ItemAuctions.xsd");
    }
    
    public static void deleteAuction(final int auctionId) {
        ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).deleteItemAuction(auctionId);
        ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).deleteItemAuctionBid(auctionId);
    }
    
    public void load() {
        this._managerInstances.clear();
        this.parseDatapackFile("data/ItemAuctions.xml");
        ItemAuctionManager.LOGGER.info("Loaded {} instance(s).", (Object)this._managerInstances.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        try {
            for (Node na = doc.getFirstChild(); na != null; na = na.getNextSibling()) {
                if ("list".equalsIgnoreCase(na.getNodeName())) {
                    for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling()) {
                        if ("instance".equalsIgnoreCase(nb.getNodeName())) {
                            final NamedNodeMap nab = nb.getAttributes();
                            final int instanceId = Integer.parseInt(nab.getNamedItem("id").getNodeValue());
                            if (this._managerInstances.containsKey(instanceId)) {
                                throw new Exception(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instanceId));
                            }
                            final ItemAuctionInstance instance = new ItemAuctionInstance(instanceId, this._auctionIds, nb);
                            this._managerInstances.put(instanceId, instance);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ItemAuctionManager.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
    }
    
    public final void shutdown() {
        for (final ItemAuctionInstance instance : this._managerInstances.values()) {
            instance.shutdown();
        }
    }
    
    public final ItemAuctionInstance getManagerInstance(final int instanceId) {
        return this._managerInstances.get(instanceId);
    }
    
    public final int getNextAuctionId() {
        return this._auctionIds.getAndIncrement();
    }
    
    public static ItemAuctionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemAuctionManager.class);
    }
    
    private static class Singleton
    {
        private static final ItemAuctionManager INSTANCE;
        
        static {
            INSTANCE = new ItemAuctionManager();
        }
    }
}
