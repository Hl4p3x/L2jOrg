// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminShop implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_buy")) {
            try {
                this.handleBuyRequest(activeChar, command.substring(10));
            }
            catch (IndexOutOfBoundsException e2) {
                BuilderUtil.sendSysMessage(activeChar, "Please specify buylist.");
            }
        }
        else if (command.equals("admin_gmshop")) {
            AdminHtml.showAdminHtml(activeChar, "gmshops.htm");
        }
        else if (command.startsWith("admin_multisell")) {
            try {
                final int listId = Integer.parseInt(command.substring(16).trim());
                MultisellData.getInstance().separateAndSend(listId, activeChar, (Npc)null, false);
            }
            catch (NumberFormatException | IndexOutOfBoundsException ex3) {
                final RuntimeException ex;
                final RuntimeException e = ex;
                BuilderUtil.sendSysMessage(activeChar, "Please specify multisell list ID.");
            }
        }
        else if (command.toLowerCase().startsWith("admin_exc_multisell")) {
            try {
                final int listId = Integer.parseInt(command.substring(20).trim());
                MultisellData.getInstance().separateAndSend(listId, activeChar, (Npc)null, true);
            }
            catch (NumberFormatException | IndexOutOfBoundsException ex4) {
                final RuntimeException ex2;
                final RuntimeException e = ex2;
                BuilderUtil.sendSysMessage(activeChar, "Please specify multisell list ID.");
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminShop.ADMIN_COMMANDS;
    }
    
    private void handleBuyRequest(final Player activeChar, final String command) {
        int val = -1;
        try {
            val = Integer.parseInt(command);
        }
        catch (Exception e) {
            AdminShop.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command));
        }
        final ProductList buyList = BuyListData.getInstance().getBuyList(val);
        if (buyList != null) {
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new BuyList(buyList, activeChar, 0.0) });
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExBuySellList(activeChar, false) });
        }
        else {
            AdminShop.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, val));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminShop.class);
        ADMIN_COMMANDS = new String[] { "admin_buy", "admin_gmshop", "admin_multisell", "admin_exc_multisell" };
    }
}
