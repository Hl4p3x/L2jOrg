// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminTransform implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_transform_menu")) {
            AdminHtml.showAdminHtml(activeChar, "transform.htm");
            return true;
        }
        if (command.startsWith("admin_untransform")) {
            final WorldObject obj = (WorldObject)((activeChar.getTarget() == null) ? activeChar : activeChar.getTarget());
            if (!GameUtils.isCreature(obj) || !((Creature)obj).isTransformed()) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            ((Creature)obj).stopTransformation(true);
        }
        else if (command.startsWith("admin_transform")) {
            final WorldObject obj = activeChar.getTarget();
            if (!GameUtils.isPlayer(obj)) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final Player player = obj.getActingPlayer();
            if (activeChar.isSitting()) {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
                return false;
            }
            if (player.isTransformed()) {
                if (!command.contains(" ")) {
                    player.untransform();
                    return true;
                }
                activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                return false;
            }
            else {
                if (player.isInWater()) {
                    activeChar.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
                    return false;
                }
                if (player.isFlyingMounted() || player.isMounted()) {
                    activeChar.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET);
                    return false;
                }
                final String[] parts = command.split(" ");
                if (parts.length != 2 || !Util.isDigit(parts[1])) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //transform <id>");
                    return false;
                }
                final int id = Integer.parseInt(parts[1]);
                if (!player.transform(id, true)) {
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
                    return false;
                }
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminTransform.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_transform", "admin_untransform", "admin_transform_menu" };
    }
}
