// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.AligatorIsland;

import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Nos extends AbstractNpcAI
{
    private final int MONSTER_CHANCE_SPAWN = 70;
    private final int NOS = 20793;
    private final int CROKIAN = 20804;
    private final int MONSTER_DESPAWN_DELAY = 300000;
    
    private Nos() {
        this.addAggroRangeEnterId(new int[] { 20804 });
    }
    
    public String onAggroRangeEnter(final Npc npc, final Player player, final boolean isSummon) {
        if (Rnd.get(100) <= 70) {
            addSpawn(20793, (IPositionable)npc, false, 300000L);
            addSpawn(20793, (IPositionable)npc, false, 300000L);
            addSpawn(20793, (IPositionable)npc, false, 300000L);
        }
        return super.onAggroRangeEnter(npc, player, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Nos();
    }
}
