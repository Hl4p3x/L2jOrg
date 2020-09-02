// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.Quest;

public abstract class AbstractNpcAI extends Quest
{
    public AbstractNpcAI() {
        super(-1);
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public void registerMobs(final int... mobs) {
        this.addAttackId(mobs);
        this.addKillId(mobs);
        this.addSpawnId(mobs);
        this.addSpellFinishedId(mobs);
        this.addSkillSeeId(mobs);
        this.addAggroRangeEnterId(mobs);
        this.addFactionCallId(mobs);
    }
    
    public void spawnMinions(final Npc npc, final String spawnName) {
        for (final MinionHolder is : npc.getParameters().getMinionList(spawnName)) {
            this.addMinion((Monster)npc, is.getId());
        }
    }
}
