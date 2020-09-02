// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminChangeAccessLevel implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player gm) {
        final String[] parts = command.split(" ");
        if (parts.length == 2) {
            try {
                final int lvl = Integer.parseInt(parts[1]);
                final WorldObject target = gm.getTarget();
                if (!GameUtils.isPlayer(target)) {
                    gm.sendPacket(SystemMessageId.INVALID_TARGET);
                }
                else {
                    this.onlineChange(gm, (Player)target, lvl);
                }
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(gm, "Usage: //changelvl <target_new_level> | <player_name> <new_level>");
            }
        }
        else if (parts.length == 3) {
            final String name = parts[1];
            final int level = Integer.parseInt(parts[2]);
            final Player player = World.getInstance().findPlayer(name);
            if (player != null) {
                this.onlineChange(gm, player, level);
            }
            else if (((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateAccessLevelByName(name, level)) {
                BuilderUtil.sendSysMessage(gm, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, level));
            }
            else {
                BuilderUtil.sendSysMessage(gm, "Character not found or access level unaltered.");
            }
        }
        return true;
    }
    
    private void onlineChange(final Player activeChar, final Player player, final int lvl) {
        if (lvl >= 0) {
            final AccessLevel acccessLevel = AdminData.getInstance().getAccessLevel(lvl);
            if (acccessLevel != null) {
                player.setAccessLevel(lvl, true, true);
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, acccessLevel.getName(), acccessLevel.getLevel()));
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, player.getName(), acccessLevel.getName(), acccessLevel.getLevel()));
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, lvl));
            }
        }
        else {
            player.setAccessLevel(lvl, false, true);
            player.sendMessage("Your character has been banned. Bye.");
            Disconnection.of(player).defaultSequence(false);
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminChangeAccessLevel.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_changelvl" };
    }
}
