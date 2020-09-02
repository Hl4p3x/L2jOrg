// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.item.auction.ItemAuction;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.network.serverpackets.ExItemAuctionInfoPacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Date;
import java.util.StringTokenizer;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.handler.IBypassHandler;

public class ItemAuctionLink implements IBypassHandler
{
    private static final SimpleDateFormat fmt;
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (!Config.ALT_ITEM_AUCTION_ENABLED) {
            player.sendPacket(SystemMessageId.IT_IS_NOT_AN_AUCTION_PERIOD);
            return true;
        }
        final ItemAuctionInstance au = ItemAuctionManager.getInstance().getManagerInstance(target.getId());
        if (au == null) {
            return false;
        }
        try {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            if (!st.hasMoreTokens()) {
                return false;
            }
            final String cmd = st.nextToken();
            if ("show".equalsIgnoreCase(cmd)) {
                if (!player.getFloodProtectors().getItemAuction().tryPerformAction("RequestInfoItemAuction")) {
                    return false;
                }
                if (player.isItemAuctionPolling()) {
                    return false;
                }
                final ItemAuction currentAuction = au.getCurrentAuction();
                final ItemAuction nextAuction = au.getNextAuction();
                if (currentAuction == null) {
                    player.sendPacket(SystemMessageId.IT_IS_NOT_AN_AUCTION_PERIOD);
                    if (nextAuction != null) {
                        player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ItemAuctionLink.fmt.format(new Date(nextAuction.getStartingTime()))));
                    }
                    return true;
                }
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExItemAuctionInfoPacket(false, currentAuction, nextAuction) });
            }
            else {
                if (!"cancel".equalsIgnoreCase(cmd)) {
                    return false;
                }
                final ItemAuction[] auctions = au.getAuctionsByBidder(player.getObjectId());
                boolean returned = false;
                for (final ItemAuction auction : auctions) {
                    if (auction.cancelBid(player)) {
                        returned = true;
                    }
                }
                if (!returned) {
                    player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                }
            }
        }
        catch (Exception e) {
            ItemAuctionLink.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        return true;
    }
    
    public String[] getBypassList() {
        return ItemAuctionLink.COMMANDS;
    }
    
    static {
        fmt = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        COMMANDS = new String[] { "ItemAuction" };
    }
}
