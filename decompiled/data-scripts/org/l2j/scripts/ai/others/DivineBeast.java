// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DivineBeast extends AbstractNpcAI
{
    private static final int DIVINE_BEAST = 14870;
    private static final int TRANSFORMATION_ID = 258;
    private static final int CHECK_TIME = 2000;
    
    private DivineBeast() {
        this.addSummonSpawnId(new int[] { 14870 });
    }
    
    public void onSummonSpawn(final Summon summon) {
        this.startQuestTimer("VALIDATE_TRANSFORMATION", 2000L, (Npc)null, summon.getActingPlayer(), true);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (player == null || !player.hasServitors()) {
            this.cancelQuestTimer(event, npc, player);
        }
        else if (player.getTransformationId() != 258) {
            this.cancelQuestTimer(event, npc, player);
            player.getServitors().values().forEach(summon -> summon.unSummon(player));
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new DivineBeast();
    }
}
