// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.serverpackets.ShopPreviewList;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import java.util.StringTokenizer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Wear implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (!Config.ALLOW_WEAR) {
            return false;
        }
        try {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            if (st.countTokens() < 1) {
                return false;
            }
            showWearWindow(player, Integer.parseInt(st.nextToken()));
            return true;
        }
        catch (Exception e) {
            Wear.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
    }
    
    private static void showWearWindow(final Player player, final int val) {
        final ProductList buyList = BuyListData.getInstance().getBuyList(val);
        if (buyList == null) {
            Wear.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, val));
            player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
            return;
        }
        player.setInventoryBlockingStatus(true);
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ShopPreviewList(buyList, player.getAdena()) });
    }
    
    public String[] getBypassList() {
        return Wear.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "Wear" };
    }
}
