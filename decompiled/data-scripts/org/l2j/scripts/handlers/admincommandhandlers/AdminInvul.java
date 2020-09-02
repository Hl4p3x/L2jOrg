// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminInvul implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_invul")) {
            this.handleInvul(activeChar);
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.equals("admin_undying")) {
            this.handleUndying(activeChar, (Creature)activeChar);
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.equals("admin_setinvul")) {
            final WorldObject target = activeChar.getTarget();
            if (GameUtils.isPlayer(target)) {
                this.handleInvul((Player)target);
            }
        }
        else if (command.equals("admin_setundying")) {
            final WorldObject target = activeChar.getTarget();
            if (GameUtils.isCreature(target)) {
                this.handleUndying(activeChar, (Creature)target);
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminInvul.ADMIN_COMMANDS;
    }
    
    private void handleInvul(final Player activeChar) {
        String text;
        if (activeChar.isInvul()) {
            activeChar.setIsInvul(false);
            text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName());
        }
        else {
            activeChar.setIsInvul(true);
            text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName());
        }
        BuilderUtil.sendSysMessage(activeChar, text);
    }
    
    private void handleUndying(final Player activeChar, final Creature target) {
        String text;
        if (target.isUndying()) {
            target.setUndying(false);
            text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName());
        }
        else {
            target.setUndying(true);
            text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName());
        }
        BuilderUtil.sendSysMessage(activeChar, text);
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_invul", "admin_setinvul", "admin_undying", "admin_setundying" };
    }
}
