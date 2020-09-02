// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class SeeThroughSilentMove extends AbstractNpcAI
{
    private static final int[] MONSTERS;
    
    private SeeThroughSilentMove() {
        this.addSpawnId(SeeThroughSilentMove.MONSTERS);
    }
    
    public String onSpawn(final Npc npc) {
        if (GameUtils.isAttackable((WorldObject)npc)) {
            ((Attackable)npc).setSeeThroughSilentMove(true);
        }
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new SeeThroughSilentMove();
    }
    
    static {
        MONSTERS = new int[] { 20142, 18002, 29009, 29010, 29011, 29012, 29013 };
    }
}
