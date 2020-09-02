// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.enums.AttributeType;
import java.util.Objects;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminElement implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        InventorySlot armorType = null;
        if (command.startsWith("admin_setlh")) {
            armorType = InventorySlot.HEAD;
        }
        else if (command.startsWith("admin_setlc")) {
            armorType = InventorySlot.CHEST;
        }
        else if (command.startsWith("admin_setlg")) {
            armorType = InventorySlot.GLOVES;
        }
        else if (command.startsWith("admin_setlb")) {
            armorType = InventorySlot.FEET;
        }
        else if (command.startsWith("admin_setll")) {
            armorType = InventorySlot.LEGS;
        }
        else if (command.startsWith("admin_setlw")) {
            armorType = InventorySlot.RIGHT_HAND;
        }
        else if (command.startsWith("admin_setls")) {
            armorType = InventorySlot.LEFT_HAND;
        }
        if (Objects.nonNull(armorType)) {
            try {
                final String[] args = command.split(" ");
                final AttributeType type = AttributeType.findByName(args[1]);
                final int value = Integer.parseInt(args[2]);
                if (type == null || value < 0 || value > 450) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element> <value>[0-450]");
                    return false;
                }
                this.setElement(activeChar, type, value, armorType);
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element>[0-5] <value>[0-450]");
                return false;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminElement.ADMIN_COMMANDS;
    }
    
    private void setElement(final Player activeChar, final AttributeType type, final int value, final InventorySlot armorType) {
        WorldObject target = activeChar.getTarget();
        if (target == null) {
            target = (WorldObject)activeChar;
        }
        Player player = null;
        if (GameUtils.isPlayer(target)) {
            player = (Player)target;
            Item itemInstance = null;
            final Item parmorInstance = player.getInventory().getPaperdollItem(armorType);
            if (parmorInstance != null && parmorInstance.getLocationSlot() == armorType.getId()) {
                itemInstance = parmorInstance;
            }
            if (itemInstance != null) {
                final AttributeHolder element = itemInstance.getAttribute(type);
                String old;
                if (element == null) {
                    old = "None";
                }
                else {
                    old = element.toString();
                }
                player.getInventory().unEquipItemInSlot(armorType);
                if (type == AttributeType.NONE) {
                    itemInstance.clearAllAttributes();
                }
                else if (value < 1) {
                    itemInstance.clearAttribute(type);
                }
                else {
                    itemInstance.setAttribute(new AttributeHolder(type, value), true);
                }
                player.getInventory().equipItem(itemInstance);
                String current;
                if (itemInstance.getAttributes() == null) {
                    current = "None";
                }
                else {
                    current = itemInstance.getAttribute(type).toString();
                }
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addModifiedItem(itemInstance);
                player.sendInventoryUpdate(iu);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), itemInstance.getTemplate().getName(), old, current));
                if (player != activeChar) {
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, activeChar.getName(), itemInstance.getTemplate().getName(), old, current));
                }
            }
            return;
        }
        activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_setlh", "admin_setlc", "admin_setll", "admin_setlg", "admin_setlb", "admin_setlw", "admin_setls" };
    }
}
