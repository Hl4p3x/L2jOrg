// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class PolymorphingAngel extends AbstractNpcAI
{
    private static final IntIntMap ANGELSPAWNS;
    
    private PolymorphingAngel() {
        this.addKillId((IntCollection)PolymorphingAngel.ANGELSPAWNS.keySet());
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final Attackable newNpc = (Attackable)addSpawn(PolymorphingAngel.ANGELSPAWNS.get(npc.getId()), (IPositionable)npc);
        newNpc.setRunning();
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new PolymorphingAngel();
    }
    
    static {
        (ANGELSPAWNS = (IntIntMap)new HashIntIntMap(5)).put(20830, 20859);
        PolymorphingAngel.ANGELSPAWNS.put(21067, 21068);
        PolymorphingAngel.ANGELSPAWNS.put(21062, 21063);
        PolymorphingAngel.ANGELSPAWNS.put(20831, 20860);
        PolymorphingAngel.ANGELSPAWNS.put(21070, 21071);
    }
}
