// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TargetCancel extends AbstractEffect
{
    private final int power;
    
    private TargetCancel(final StatsSet params) {
        this.power = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability((double)this.power, effector, effected, skill);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.setTarget((WorldObject)null);
        effected.abortAttack();
        effected.abortCast();
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[] { effector });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TargetCancel(data);
        }
        
        public String effectName() {
            return "TargetCancel";
        }
    }
}
