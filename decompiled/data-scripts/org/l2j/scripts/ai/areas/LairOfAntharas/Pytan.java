// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.LairOfAntharas;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Pytan extends AbstractNpcAI
{
    private static final int PYTAN = 20761;
    private static final int KNORIKS = 20405;
    
    private Pytan() {
        this.addKillId(20761);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (getRandom(100) < 5) {
            final Npc spawnBanshee = addSpawn(20405, (IPositionable)npc, false, 300000L);
            final Playable attacker = (Playable)(isSummon ? ((Playable)killer.getServitors().values().stream().findFirst().orElse((Playable)killer.getPet())) : killer);
            this.addAttackPlayerDesire(spawnBanshee, attacker);
            npc.deleteMe();
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Pytan();
    }
}
