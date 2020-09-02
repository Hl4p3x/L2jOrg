// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.HashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.clanhallauction.ClanHallAuction;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;

public class ClanHallAuctionManager extends AbstractEventManager<AbstractEvent>
{
    private static final Logger LOGGER;
    private static final Map<Integer, ClanHallAuction> AUCTIONS;
    
    private ClanHallAuctionManager() {
    }
    
    private void onEventStart() {
        ClanHallAuctionManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        ClanHallAuctionManager.AUCTIONS.clear();
        ClanHallManager.getInstance().getFreeAuctionableHall().forEach(c -> ClanHallAuctionManager.AUCTIONS.put(c.getId(), new ClanHallAuction(c.getId())));
    }
    
    private void onEventEnd() {
        ClanHallAuctionManager.AUCTIONS.values().forEach(ClanHallAuction::finalizeAuctions);
        ClanHallAuctionManager.AUCTIONS.clear();
        ClanHallAuctionManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
    }
    
    @Override
    public void onInitialized() {
    }
    
    public ClanHallAuction getClanHallAuctionById(final int clanHallId) {
        return ClanHallAuctionManager.AUCTIONS.get(clanHallId);
    }
    
    public ClanHallAuction getClanHallAuctionByClan(final Clan clan) {
        return ClanHallAuctionManager.AUCTIONS.values().stream().filter(a -> a.getBids().containsKey(clan.getId())).findFirst().orElse(null);
    }
    
    public boolean checkForClanBid(final int clanHallId, final Clan clan) {
        return ClanHallAuctionManager.AUCTIONS.entrySet().stream().filter(a -> a.getKey() != clanHallId).anyMatch(a -> a.getValue().getBids().containsKey(clan.getId()));
    }
    
    public static ClanHallAuctionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanHallAuctionManager.class);
        AUCTIONS = new HashMap<Integer, ClanHallAuction>();
    }
    
    private static class Singleton
    {
        private static final ClanHallAuctionManager INSTANCE;
        
        static {
            INSTANCE = new ClanHallAuctionManager();
        }
    }
}
