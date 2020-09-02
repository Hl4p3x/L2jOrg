// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Spawns;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class NoRandomActivity extends AbstractNpcAI
{
    private NoRandomActivity() {
    }
    
    public void onSpawnNpc(final SpawnTemplate template, final SpawnGroup group, final Npc npc) {
        npc.setRandomAnimation(npc.getParameters().getBoolean("disableRandomAnimation", false));
        npc.setRandomWalking(npc.getParameters().getBoolean("disableRandomWalk", false));
        if (npc.getSpawn() != null) {
            npc.getSpawn().setRandomWalking(!npc.getParameters().getBoolean("disableRandomWalk", false));
        }
    }
    
    public static AbstractNpcAI provider() {
        return new NoRandomActivity();
    }
}
