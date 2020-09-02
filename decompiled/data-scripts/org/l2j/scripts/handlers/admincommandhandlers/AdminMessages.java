// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminMessages implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_msg ")) {
            try {
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(Integer.parseInt(command.substring(10).trim())) });
                return true;
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Command format: //msg <SYSTEM_MSG_ID>");
                return false;
            }
        }
        if (command.startsWith("admin_msgx ")) {
            final String[] tokens = command.split(" ");
            if (tokens.length <= 2 || !Util.isDigit(tokens[1])) {
                BuilderUtil.sendSysMessage(activeChar, "Command format: //msgx <SYSTEM_MSG_ID> [item:Id] [skill:Id] [npc:Id] [zone:x,y,x] [castle:Id] [str:'text']");
                return false;
            }
            final SystemMessage sm = SystemMessage.getSystemMessage(Integer.parseInt(tokens[1]));
            int lastPos = 0;
            for (int i = 2; i < tokens.length; ++i) {
                try {
                    final String val = tokens[i];
                    if (val.startsWith("item:")) {
                        sm.addItemName(Integer.parseInt(val.substring(5)));
                    }
                    else if (val.startsWith("skill:")) {
                        sm.addSkillName(Integer.parseInt(val.substring(6)));
                    }
                    else if (val.startsWith("npc:")) {
                        sm.addNpcName(Integer.parseInt(val.substring(4)));
                    }
                    else if (val.startsWith("zone:")) {
                        final int x = Integer.parseInt(val.substring(5, val.indexOf(",")));
                        final int y = Integer.parseInt(val.substring(val.indexOf(",") + 1, val.lastIndexOf(",")));
                        final int z = Integer.parseInt(val.substring(val.lastIndexOf(",") + 1, val.length()));
                        sm.addZoneName(x, y, z);
                    }
                    else if (val.startsWith("castle:")) {
                        sm.addCastleId(Integer.parseInt(val.substring(7)));
                    }
                    else if (val.startsWith("str:")) {
                        final int pos = command.indexOf("'", lastPos + 1);
                        lastPos = command.indexOf("'", pos + 1);
                        sm.addString(command.substring(pos + 1, lastPos));
                    }
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                }
            }
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        }
        return false;
    }
    
    public String[] getAdminCommandList() {
        return AdminMessages.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_msg", "admin_msgx" };
    }
}
