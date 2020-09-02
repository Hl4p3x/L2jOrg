// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DragonValley;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CaveMaiden extends AbstractNpcAI
{
    private static final int CAVEMAIDEN = 20134;
    private static final int CAVEKEEPER = 20246;
    private static final int BANSHEE = 20412;
    
    private CaveMaiden() {
        this.addKillId(new int[] { 20134, 20246 });
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (getRandom(100) < 20) {
            final Npc spawnBanshee = addSpawn(20412, (IPositionable)npc, false, 300000L);
            final Playable attacker = (Playable)(isSummon ? ((Playable)killer.getServitors().values().stream().findFirst().orElse((Playable)killer.getPet())) : killer);
            this.addAttackPlayerDesire(spawnBanshee, attacker);
            npc.deleteMe();
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new CaveMaiden();
    }
}
