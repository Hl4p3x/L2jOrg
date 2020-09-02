// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Multisell implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        try {
            if (command.toLowerCase().startsWith(Multisell.COMMANDS[0])) {
                final int listId = Integer.parseInt(command.substring(9).trim());
                MultisellData.getInstance().separateAndSend(listId, player, (Npc)target, false);
                return true;
            }
            if (command.toLowerCase().startsWith(Multisell.COMMANDS[1])) {
                final int listId = Integer.parseInt(command.substring(13).trim());
                MultisellData.getInstance().separateAndSend(listId, player, (Npc)target, true);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            Multisell.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
    }
    
    public String[] getBypassList() {
        return Multisell.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "multisell", "exc_multisell" };
    }
}
