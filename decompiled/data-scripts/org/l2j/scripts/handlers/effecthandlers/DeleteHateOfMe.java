// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DeleteHateOfMe extends AbstractEffect
{
    private final int power;
    
    private DeleteHateOfMe(final StatsSet params) {
        this.power = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability((double)this.power, effector, effected, skill);
    }
    
    public EffectType getEffectType() {
        return EffectType.HATE;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isAttackable((WorldObject)effected)) {
            return;
        }
        final Attackable target = (Attackable)effected;
        target.stopHating(effector);
        target.setWalking();
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DeleteHateOfMe(data);
        }
        
        public String effectName() {
            return "DeleteHateOfMe";
        }
    }
}
