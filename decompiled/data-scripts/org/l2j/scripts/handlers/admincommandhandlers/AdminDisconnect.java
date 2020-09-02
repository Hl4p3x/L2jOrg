// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminDisconnect implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_character_disconnect")) {
            this.disconnectCharacter(activeChar);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminDisconnect.ADMIN_COMMANDS;
    }
    
    private void disconnectCharacter(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (GameUtils.isPlayer(target)) {
            player = (Player)target;
            if (player == activeChar) {
                BuilderUtil.sendSysMessage(activeChar, "You cannot logout your own character.");
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                Disconnection.of(player).defaultSequence(false);
            }
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_character_disconnect" };
    }
}
