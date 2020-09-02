// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class ChatLink implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        int val = 0;
        try {
            val = Integer.parseInt(command.substring(5));
        }
        catch (Exception ex) {}
        final Npc npc = (Npc)target;
        if (val == 0 && npc.hasListener(EventType.ON_NPC_FIRST_TALK)) {
            EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnNpcFirstTalk(npc, player), new ListenersContainer[] { (ListenersContainer)npc });
        }
        else {
            npc.showChatWindow(player, val);
        }
        return false;
    }
    
    public String[] getBypassList() {
        return ChatLink.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "Chat" };
    }
}
