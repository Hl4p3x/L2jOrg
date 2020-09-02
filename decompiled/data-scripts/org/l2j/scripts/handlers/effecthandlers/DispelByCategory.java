// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DispelByCategory extends AbstractEffect
{
    private final DispelSlotType category;
    private final int power;
    private final int max;
    
    private DispelByCategory(final StatsSet params) {
        this.category = (DispelSlotType)params.getEnum("category", (Class)DispelSlotType.class, (Enum)DispelSlotType.BUFF);
        this.power = params.getInt("power", 0);
        this.max = params.getInt("max", 0);
    }
    
    public EffectType getEffectType() {
        return EffectType.DISPEL;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return;
        }
        final List<BuffInfo> canceled = (List<BuffInfo>)Formulas.calcCancelStealEffects(effector, effected, skill, this.category, this.power, this.max);
        canceled.forEach(b -> effected.getEffectList().stopSkillEffects(true, b.getSkill()));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DispelByCategory(data);
        }
        
        public String effectName() {
            return "dispel-by-category";
        }
    }
}
