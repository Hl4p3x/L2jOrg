// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminTargetSay implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_targetsay")) {
            try {
                final WorldObject obj = activeChar.getTarget();
                if (obj instanceof StaticWorldObject || !GameUtils.isCreature(obj)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                final String message = command.substring(16);
                final Creature target = (Creature)obj;
                target.broadcastPacket((ServerPacket)new CreatureSay(target.getObjectId(), GameUtils.isPlayer((WorldObject)target) ? ChatType.GENERAL : ChatType.NPC_GENERAL, target.getName(), message));
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //targetsay <text>");
                return false;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminTargetSay.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_targetsay" };
    }
}
