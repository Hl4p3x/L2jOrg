// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.skills.SkillCaster;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class CallSkill extends AbstractEffect
{
    private final SkillHolder skill;
    
    private CallSkill(final StatsSet params) {
        this.skill = new SkillHolder(params.getInt("skill"), params.getInt("power", 1));
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        Skill triggerSkill = null;
        if (this.skill.getLevel() == 0) {
            final int knownLevel = effector.getSkillLevel(this.skill.getSkillId());
            if (knownLevel > 0) {
                triggerSkill = SkillEngine.getInstance().getSkill(this.skill.getSkillId(), knownLevel);
            }
            else {
                CallSkill.LOGGER.warn("Player {} called unknown skill {} triggered by {} CallSkill.", new Object[] { effector, this.skill, skill });
            }
        }
        else {
            triggerSkill = this.skill.getSkill();
        }
        if (Objects.nonNull(triggerSkill)) {
            SkillCaster.triggerCast(effector, effected, triggerSkill);
        }
        else {
            CallSkill.LOGGER.warn("Skill not found effect called from {}", (Object)skill);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new CallSkill(data);
        }
        
        public String effectName() {
            return "call-skill";
        }
    }
}
