// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.ensoul.ExShowEnsoulExtractionWindow;
import org.l2j.gameserver.network.serverpackets.ensoul.ExShowEnsoulWindow;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class EnsoulWindow implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (command.toLowerCase().startsWith(EnsoulWindow.COMMANDS[0])) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExShowEnsoulWindow.STATIC_PACKET });
            return true;
        }
        if (command.toLowerCase().startsWith(EnsoulWindow.COMMANDS[1])) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExShowEnsoulExtractionWindow.STATIC_PACKET });
            return true;
        }
        return false;
    }
    
    public String[] getBypassList() {
        return EnsoulWindow.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "show_ensoul_window", "show_extract_ensoul_window" };
    }
}
