// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TalkingIsland;

import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Roxxy extends AbstractNpcAI
{
    private static final int ROXXY = 30006;
    
    private Roxxy() {
        this.addSpawnId(new int[] { 30006 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("TEXT_SPAM") && npc != null) {
            npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SPEAK_WITH_ME_ABOUT_TRAVELING_AROUND_ADEN, 1000);
            this.startQuestTimer("TEXT_SPAM", (long)getRandom(10000, 30000), npc, (Player)null, false);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSpawn(final Npc npc) {
        this.startQuestTimer("TEXT_SPAM", 10000L, npc, (Player)null, false);
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new Roxxy();
    }
}
