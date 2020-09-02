// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2j.gameserver.handler.IItemHandler;
import java.util.Iterator;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.network.serverpackets.GMViewItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminCreateItem implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_itemcreate")) {
            AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
        }
        else if (command.startsWith("admin_create_item")) {
            try {
                final String val = command.substring(17);
                final StringTokenizer st = new StringTokenizer(val);
                if (st.countTokens() == 2) {
                    final String id = st.nextToken();
                    final int idval = Integer.parseInt(id);
                    final String num = st.nextToken();
                    final long numval = Long.parseLong(num);
                    this.createItem(activeChar, activeChar, idval, numval);
                }
                else if (st.countTokens() == 1) {
                    final String id = st.nextToken();
                    final int idval = Integer.parseInt(id);
                    this.createItem(activeChar, activeChar, idval, 1L);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //create_item <itemId> [amount]");
            }
            catch (NumberFormatException nfe) {
                BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
            }
            AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
        }
        else if (command.startsWith("admin_create_coin")) {
            try {
                final String val = command.substring(17);
                final StringTokenizer st = new StringTokenizer(val);
                if (st.countTokens() == 2) {
                    final String name = st.nextToken();
                    final int idval = this.getCoinId(name);
                    if (idval > 0) {
                        final String num = st.nextToken();
                        final long numval = Long.parseLong(num);
                        this.createItem(activeChar, activeChar, idval, numval);
                    }
                }
                else if (st.countTokens() == 1) {
                    final String name = st.nextToken();
                    final int idval = this.getCoinId(name);
                    this.createItem(activeChar, activeChar, idval, 1L);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //create_coin <name> [amount]");
            }
            catch (NumberFormatException nfe) {
                BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
            }
            AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
        }
        else if (command.startsWith("admin_give_item_target")) {
            try {
                final WorldObject target = activeChar.getTarget();
                if (!GameUtils.isPlayer(target)) {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid target.");
                    return false;
                }
                final String val2 = command.substring(22);
                final StringTokenizer st2 = new StringTokenizer(val2);
                if (st2.countTokens() == 2) {
                    final String id2 = st2.nextToken();
                    final int idval2 = Integer.parseInt(id2);
                    final String num2 = st2.nextToken();
                    final long numval2 = Long.parseLong(num2);
                    this.createItem(activeChar, (Player)target, idval2, numval2);
                }
                else if (st2.countTokens() == 1) {
                    final String id2 = st2.nextToken();
                    final int idval2 = Integer.parseInt(id2);
                    this.createItem(activeChar, (Player)target, idval2, 1L);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //give_item_target <itemId> [amount]");
            }
            catch (NumberFormatException nfe) {
                BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
            }
            AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
        }
        else if (command.startsWith("admin_give_item_to_all")) {
            final String val = command.substring(22);
            final StringTokenizer st = new StringTokenizer(val);
            int idval3 = 0;
            long numval3 = 0L;
            if (st.countTokens() == 2) {
                final String id3 = st.nextToken();
                idval3 = Integer.parseInt(id3);
                final String num3 = st.nextToken();
                numval3 = Long.parseLong(num3);
            }
            else if (st.countTokens() == 1) {
                final String id3 = st.nextToken();
                idval3 = Integer.parseInt(id3);
                numval3 = 1L;
            }
            int counter = 0;
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(idval3);
            if (template == null) {
                BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
                return false;
            }
            if (numval3 > 10L && !template.isStackable()) {
                BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
                return false;
            }
            for (final Player onlinePlayer : World.getInstance().getPlayers()) {
                if (activeChar != onlinePlayer && onlinePlayer.isOnline() && onlinePlayer.getClient() != null && !onlinePlayer.getClient().isDetached()) {
                    onlinePlayer.getInventory().addItem("Admin", idval3, numval3, onlinePlayer, (Object)activeChar);
                    onlinePlayer.sendMessage(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, numval3, template.getName()));
                    ++counter;
                }
            }
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, counter, template.getName()));
        }
        else if (command.startsWith("admin_delete_item")) {
            final String val = command.substring(18);
            final StringTokenizer st = new StringTokenizer(val);
            int idval3 = 0;
            long numval3 = 0L;
            if (st.countTokens() == 2) {
                final String id3 = st.nextToken();
                idval3 = Integer.parseInt(id3);
                final String num3 = st.nextToken();
                numval3 = Long.parseLong(num3);
            }
            else if (st.countTokens() == 1) {
                final String id3 = st.nextToken();
                idval3 = Integer.parseInt(id3);
                numval3 = 1L;
            }
            final Item item = (Item)World.getInstance().findObject(idval3);
            final int ownerId = item.getOwnerId();
            if (ownerId <= 0) {
                BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
                return false;
            }
            final Player player = World.getInstance().findPlayer(ownerId);
            if (player == null) {
                BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
                return false;
            }
            if (numval3 == 0L) {
                numval3 = item.getCount();
            }
            player.getInventory().destroyItem("AdminDelete", idval3, numval3, activeChar, (Object)null);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new GMViewItemList(1, player) });
            BuilderUtil.sendSysMessage(activeChar, "Item deleted.");
        }
        else if (command.startsWith("admin_use_item")) {
            final String val = command.substring(15);
            final int idval4 = Integer.parseInt(val);
            final Item item2 = (Item)World.getInstance().findObject(idval4);
            final int ownerId2 = item2.getOwnerId();
            if (ownerId2 <= 0) {
                BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
                return false;
            }
            final Player player2 = World.getInstance().findPlayer(ownerId2);
            if (player2 == null) {
                BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
                return false;
            }
            if (item2.isEquipable()) {
                player2.useEquippableItem(item2, false);
            }
            else {
                final IItemHandler ih = ItemHandler.getInstance().getHandler(item2.getEtcItem());
                if (ih != null) {
                    ih.useItem((Playable)player2, item2, false);
                }
            }
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new GMViewItemList(1, player2) });
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminCreateItem.ADMIN_COMMANDS;
    }
    
    private void createItem(final Player activeChar, final Player target, final int id, final long num) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(id);
        if (template == null) {
            BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
            return;
        }
        if (num > 10L && !template.isStackable()) {
            BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
            return;
        }
        target.getInventory().addItem("Admin", id, num, activeChar, (Object)null);
        if (activeChar != target) {
            target.sendMessage(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, num, template.getName()));
        }
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(JLjava/lang/String;ILjava/lang/String;)Ljava/lang/String;, num, template.getName(), id, target.getName()));
        target.sendPacket(new ServerPacket[] { (ServerPacket)new ExAdenaInvenCount(target) });
    }
    
    private int getCoinId(final String name) {
        int id;
        if (name.equalsIgnoreCase("adena")) {
            id = 57;
        }
        else if (name.equalsIgnoreCase("ancientadena")) {
            id = 5575;
        }
        else if (name.equalsIgnoreCase("festivaladena")) {
            id = 6673;
        }
        else if (name.equalsIgnoreCase("blueeva")) {
            id = 4355;
        }
        else if (name.equalsIgnoreCase("goldeinhasad")) {
            id = 4356;
        }
        else if (name.equalsIgnoreCase("silvershilen")) {
            id = 4357;
        }
        else if (name.equalsIgnoreCase("bloodypaagrio")) {
            id = 4358;
        }
        else if (name.equalsIgnoreCase("fantasyislecoin")) {
            id = 13067;
        }
        else {
            id = 0;
        }
        return id;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_itemcreate", "admin_create_item", "admin_create_coin", "admin_give_item_target", "admin_give_item_to_all", "admin_delete_item", "admin_use_item" };
    }
}
