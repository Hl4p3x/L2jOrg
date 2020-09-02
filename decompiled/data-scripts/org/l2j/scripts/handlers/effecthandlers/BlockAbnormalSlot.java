// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Set;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockAbnormalSlot extends AbstractEffect
{
    private final Set<AbnormalType> blockAbnormalSlots;
    
    private BlockAbnormalSlot(final StatsSet params) {
        this.blockAbnormalSlots = Arrays.stream(params.getString("abnormals").split(" ")).map((Function<? super String, ?>)AbnormalType::valueOf).collect((Collector<? super Object, ?, Set<AbnormalType>>)Collectors.toSet());
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getEffectList().addBlockedAbnormalTypes((Set)this.blockAbnormalSlots);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getEffectList().removeBlockedAbnormalTypes((Set)this.blockAbnormalSlots);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new BlockAbnormalSlot(data);
        }
        
        public String effectName() {
            return "block-abnormal";
        }
    }
}
