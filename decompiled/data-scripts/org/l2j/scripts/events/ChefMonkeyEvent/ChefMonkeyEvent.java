// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.ChefMonkeyEvent;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public final class ChefMonkeyEvent extends LongTimeEvent
{
    private static final int CHEF_MONKEY = 34292;
    
    private ChefMonkeyEvent() {
        this.addStartNpc(34292);
        this.addFirstTalkId(34292);
        this.addTalkId(34292);
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return "34292-01.htm";
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new ChefMonkeyEvent();
    }
}
