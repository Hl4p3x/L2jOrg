// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class NonLethalableNpcs extends AbstractNpcAI
{
    private static final int[] NPCS;
    
    public NonLethalableNpcs() {
        this.addSpawnId(NonLethalableNpcs.NPCS);
    }
    
    public String onSpawn(final Npc npc) {
        npc.setLethalable(false);
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new NonLethalableNpcs();
    }
    
    static {
        NPCS = new int[] { 35062 };
    }
}
