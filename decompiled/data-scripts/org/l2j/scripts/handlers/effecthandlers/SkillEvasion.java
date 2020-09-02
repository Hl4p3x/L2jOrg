// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class SkillEvasion extends AbstractEffect
{
    private final SkillType magicType;
    private final double power;
    
    private SkillEvasion(final StatsSet params) {
        this.magicType = (SkillType)params.getEnum("type", (Class)SkillType.class);
        this.power = params.getDouble("power", 0.0);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getStats().addSkillEvasionTypeValue(this.magicType, this.power);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getStats().removeSkillEvasionTypeValue(this.magicType, this.power);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SkillEvasion(data);
        }
        
        public String effectName() {
            return "skill-evasion";
        }
    }
}
