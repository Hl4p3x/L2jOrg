// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminDestroyItems implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final PlayerInventory inventory = activeChar.getInventory();
        final InventoryUpdate iu = new InventoryUpdate();
        for (final Item item : inventory.getItems()) {
            if (item.isEquipped() && !command.contains("all")) {
                continue;
            }
            iu.addRemovedItem(item);
            inventory.destroyItem("Admin Destroy", item, activeChar, (Object)null);
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)iu });
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminDestroyItems.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_destroy_items", "admin_destroy_all_items", "admin_destroyitems", "admin_destroyallitems" };
    }
}
