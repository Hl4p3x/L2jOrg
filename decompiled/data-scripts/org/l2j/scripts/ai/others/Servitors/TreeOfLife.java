// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Servitors;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class TreeOfLife extends AbstractNpcAI
{
    private static final int[] TREE_OF_LIFE;
    
    private TreeOfLife() {
        this.addSummonSpawnId(TreeOfLife.TREE_OF_LIFE);
    }
    
    public void onSummonSpawn(final Summon summon) {
        this.getTimers().addTimer((Object)"HEAL", 3000L, (Npc)null, summon.getOwner());
    }
    
    public void onTimerEvent(final String event, final StatsSet params, final Npc npc, final Player player) {
        if (player != null) {
            final Summon summon = player.getFirstServitor();
            if (event.equals("HEAL") && summon != null && Util.contains(TreeOfLife.TREE_OF_LIFE, summon.getId())) {
                summon.doCast(summon.getTemplate().getParameters().getSkillHolder("s_tree_heal").getSkill(), (Item)null, false, false);
                this.getTimers().addTimer((Object)"HEAL", 8000L, (Npc)null, player);
            }
        }
    }
    
    public static AbstractNpcAI provider() {
        return new TreeOfLife();
    }
    
    static {
        TREE_OF_LIFE = new int[] { 14933, 14943, 15010, 15011, 15154 };
    }
}
