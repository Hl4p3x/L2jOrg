// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import org.l2j.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Augment implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        try {
            switch (Integer.parseInt(command.substring(8, 9).trim())) {
                case 1: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)ExShowVariationMakeWindow.STATIC_PACKET });
                    return true;
                }
                case 2: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)ExShowVariationCancelWindow.STATIC_PACKET });
                    return true;
                }
            }
        }
        catch (Exception e) {
            Augment.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        return false;
    }
    
    public String[] getBypassList() {
        return Augment.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "Augment" };
    }
}
