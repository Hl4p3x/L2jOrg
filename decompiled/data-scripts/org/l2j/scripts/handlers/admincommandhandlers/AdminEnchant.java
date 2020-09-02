// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import java.util.Objects;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminEnchant implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player player) {
        final StringTokenizer tokens = new StringTokenizer(command);
        if (tokens.hasMoreTokens()) {
            final String nextToken = tokens.nextToken();
            InventorySlot inventorySlot = null;
            switch (nextToken) {
                case "admin_seteh": {
                    inventorySlot = InventorySlot.HEAD;
                    break;
                }
                case "admin_setec": {
                    inventorySlot = InventorySlot.CHEST;
                    break;
                }
                case "admin_seteg": {
                    inventorySlot = InventorySlot.GLOVES;
                    break;
                }
                case "admin_seteb": {
                    inventorySlot = InventorySlot.FEET;
                    break;
                }
                case "admin_setel": {
                    inventorySlot = InventorySlot.LEGS;
                    break;
                }
                case "admin_setew": {
                    inventorySlot = InventorySlot.RIGHT_HAND;
                    break;
                }
                case "admin_setes": {
                    inventorySlot = InventorySlot.LEFT_HAND;
                    break;
                }
                case "admin_setle": {
                    inventorySlot = InventorySlot.LEFT_EAR;
                    break;
                }
                case "admin_setre": {
                    inventorySlot = InventorySlot.RIGHT_EAR;
                    break;
                }
                case "admin_setlf": {
                    inventorySlot = InventorySlot.LEFT_FINGER;
                    break;
                }
                case "admin_setrf": {
                    inventorySlot = InventorySlot.RIGHT_FINGER;
                    break;
                }
                case "admin_seten": {
                    inventorySlot = InventorySlot.NECK;
                    break;
                }
                case "admin_setun": {
                    inventorySlot = InventorySlot.PENDANT;
                    break;
                }
                case "admin_setba": {
                    inventorySlot = InventorySlot.CLOAK;
                    break;
                }
                case "admin_setbe": {
                    inventorySlot = InventorySlot.BELT;
                    break;
                }
                default: {
                    inventorySlot = null;
                    break;
                }
            }
            final InventorySlot itemSlot = inventorySlot;
            if (Objects.nonNull(itemSlot) && tokens.hasMoreTokens()) {
                final int enchant = Util.parseNextInt(tokens, -1);
                if (enchant < 0 || enchant > 32767) {
                    BuilderUtil.sendSysMessage(player, "You must set the enchant level to be between 0 and 32767.");
                }
                else {
                    this.setEnchant(player, enchant, itemSlot);
                }
            }
            else {
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command));
            }
        }
        this.showMainPage(player);
        return true;
    }
    
    private void setEnchant(final Player player, final int ench, final InventorySlot itemSlot) {
        final Player target = Objects.nonNull(player.getTarget()) ? player.getTarget().getActingPlayer() : player;
        if (Objects.isNull(target)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        Item itemInstance = null;
        final Item item = target.getInventory().getPaperdollItem(itemSlot);
        if (Objects.nonNull(item) && item.getLocationSlot() == itemSlot.getId()) {
            itemInstance = item;
        }
        if (Objects.nonNull(itemInstance)) {
            final int curEnchant = itemInstance.getEnchantLevel();
            target.getInventory().unEquipItemInSlot(itemSlot);
            itemInstance.setEnchantLevel(ench);
            target.getInventory().equipItem(itemInstance);
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(itemInstance);
            target.sendInventoryUpdate(iu);
            target.broadcastUserInfo();
            BuilderUtil.sendSysMessage(player, String.format("Changed enchantment of %s's %s from %d  to %d.", target.getName(), itemInstance.getName(), curEnchant, ench));
            target.sendMessage(String.format("Admin has changed the enchantment of your %s from %d to %d.", itemInstance.getName(), curEnchant, ench));
        }
    }
    
    private void showMainPage(final Player activeChar) {
        AdminHtml.showAdminHtml(activeChar, "enchant.htm");
    }
    
    public String[] getAdminCommandList() {
        return AdminEnchant.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_seteh", "admin_setec", "admin_seteg", "admin_setel", "admin_seteb", "admin_setew", "admin_setes", "admin_setle", "admin_setre", "admin_setlf", "admin_setrf", "admin_seten", "admin_setun", "admin_setba", "admin_setbe", "admin_enchant" };
    }
}
