// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminCamera implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (!GameUtils.isCreature(activeChar.getTarget())) {
            activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
            return false;
        }
        final Creature target = (Creature)activeChar.getTarget();
        final String[] com = command.split(" ");
        final String s = com[0];
        switch (s) {
            case "admin_cam": {
                if (com.length != 12) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //cam force angle1 angle2 time range duration relYaw relPitch isWide relAngle");
                    return false;
                }
                AbstractScript.specialCamera(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]), Integer.parseInt(com[10]));
                break;
            }
            case "admin_camex": {
                if (com.length != 10) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //camex force angle1 angle2 time duration relYaw relPitch isWide relAngle");
                    return false;
                }
                AbstractScript.specialCameraEx(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]));
                break;
            }
            case "admin_cam3": {
                if (com.length != 12) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //cam3 force angle1 angle2 time range duration relYaw relPitch isWide relAngle unk");
                    return false;
                }
                AbstractScript.specialCamera3(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]), Integer.parseInt(com[10]), Integer.parseInt(com[11]));
                break;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminCamera.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_cam", "admin_camex", "admin_cam3" };
    }
}
