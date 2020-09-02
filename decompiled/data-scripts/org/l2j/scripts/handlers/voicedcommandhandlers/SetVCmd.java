// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class SetVCmd implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        if (command.equals("set")) {
            final WorldObject target = activeChar.getTarget();
            if (!GameUtils.isPlayer(target)) {
                return false;
            }
            final Player player = activeChar.getTarget().getActingPlayer();
            if (activeChar.getClan() == null || player.getClan() == null || activeChar.getClan().getId() != player.getClan().getId()) {
                return false;
            }
            if (params.startsWith("privileges")) {
                final String val = params.substring(11);
                if (!Util.isInteger(val)) {
                    return false;
                }
                final int n = Integer.parseInt(val);
                if (activeChar.getClanPrivileges().getBitmask() <= n || !activeChar.isClanLeader()) {
                    return false;
                }
                player.getClanPrivileges().setBitmask(n);
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n, activeChar.getName()));
            }
            else if (params.startsWith("title")) {}
        }
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return SetVCmd.VOICED_COMMANDS;
    }
    
    static {
        VOICED_COMMANDS = new String[] { "set name", "set home", "set group" };
    }
}
