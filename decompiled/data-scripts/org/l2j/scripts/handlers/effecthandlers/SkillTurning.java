// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SkillTurning extends AbstractEffect
{
    private final int power;
    
    private SkillTurning(final StatsSet params) {
        this.power = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Rnd.chance(this.power);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected == effector || effected.isRaid()) {
            return;
        }
        effected.breakCast();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SkillTurning(data);
        }
        
        public String effectName() {
            return "SkillTurning";
        }
    }
}
