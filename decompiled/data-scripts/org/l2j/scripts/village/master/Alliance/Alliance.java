// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.Alliance;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Alliance extends AbstractNpcAI
{
    private static final int[] NPCS;
    
    private Alliance() {
        this.addStartNpc(Alliance.NPCS);
        this.addTalkId(Alliance.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (!"9001-01.htm".equals(event) && player.getClan() == null) {
            return "9001-04.htm";
        }
        return event;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        return "9001-01.htm";
    }
    
    public static Alliance provider() {
        return new Alliance();
    }
    
    static {
        NPCS = new int[] { 30026, 30031, 30037, 30066, 30070, 30109, 30115, 30120, 30154, 30174, 30175, 30176, 30187, 30191, 30195, 30288, 30289, 30290, 30297, 30358, 30373, 30462, 30474, 30498, 30499, 30500, 30503, 30504, 30505, 30508, 30511, 30512, 30513, 30520, 30525, 30565, 30594, 30595, 30676, 30677, 30681, 30685, 30687, 30689, 30694, 30699, 30704, 30845, 30847, 30849, 30854, 30857, 30862, 30865, 30894, 30897, 30900, 30905, 30910, 30913 };
    }
}
