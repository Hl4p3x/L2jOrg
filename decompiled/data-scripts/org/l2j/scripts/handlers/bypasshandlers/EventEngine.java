// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class EventEngine implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        try {
            if (command.equalsIgnoreCase("event_participate")) {
                Event.registerPlayer(player);
                return true;
            }
            if (command.equalsIgnoreCase("event_unregister")) {
                Event.removeAndResetPlayer(player);
                return true;
            }
        }
        catch (Exception e) {
            EventEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        return false;
    }
    
    public String[] getBypassList() {
        return EventEngine.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "event_participate", "event_unregister" };
    }
}
