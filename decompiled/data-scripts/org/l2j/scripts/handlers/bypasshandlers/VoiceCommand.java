// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class VoiceCommand implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (command.length() > 7 && command.charAt(6) == '.') {
            final int endOfCommand = command.indexOf(" ", 7);
            String vc;
            String vparams;
            if (endOfCommand > 0) {
                vc = command.substring(7, endOfCommand).trim();
                vparams = command.substring(endOfCommand).trim();
            }
            else {
                vc = command.substring(7).trim();
                vparams = null;
            }
            if (vc.length() > 0) {
                final IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getHandler(vc);
                if (vch != null) {
                    return vch.useVoicedCommand(vc, player, vparams);
                }
            }
        }
        return false;
    }
    
    public String[] getBypassList() {
        return VoiceCommand.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "voice" };
    }
}
