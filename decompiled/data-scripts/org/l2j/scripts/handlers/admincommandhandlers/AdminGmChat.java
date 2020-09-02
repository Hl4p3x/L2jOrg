// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminGmChat implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_gmchat")) {
            this.handleGmChat(command, activeChar);
        }
        else if (command.startsWith("admin_snoop")) {
            this.snoop(command, activeChar);
        }
        if (command.startsWith("admin_gmchat_menu")) {
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        return true;
    }
    
    private void snoop(final String command, final Player activeChar) {
        WorldObject target = null;
        if (command.length() > 12) {
            target = (WorldObject)World.getInstance().findPlayer(command.substring(12));
        }
        if (target == null) {
            target = activeChar.getTarget();
        }
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.SELECT_TARGET);
            return;
        }
        if (!GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = (Player)target;
        player.addSnooper(activeChar);
        activeChar.addSnooped(player);
    }
    
    public String[] getAdminCommandList() {
        return AdminGmChat.ADMIN_COMMANDS;
    }
    
    private void handleGmChat(final String command, final Player activeChar) {
        try {
            int offset = 0;
            if (command.startsWith("admin_gmchat_menu")) {
                offset = 18;
            }
            else {
                offset = 13;
            }
            final String text = command.substring(offset);
            final CreatureSay cs = new CreatureSay(0, ChatType.ALLIANCE, activeChar.getName(), text);
            AdminData.getInstance().broadcastToGMs((ServerPacket)cs);
        }
        catch (StringIndexOutOfBoundsException ex) {}
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_gmchat", "admin_snoop", "admin_gmchat_menu" };
    }
}
