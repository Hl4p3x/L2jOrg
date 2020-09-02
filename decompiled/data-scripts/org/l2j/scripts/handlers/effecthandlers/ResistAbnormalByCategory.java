// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ResistAbnormalByCategory extends AbstractEffect
{
    private final DispelSlotType category;
    private final double power;
    
    private ResistAbnormalByCategory(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.category = (DispelSlotType)params.getEnum("category", (Class)DispelSlotType.class, (Enum)DispelSlotType.DEBUFF);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        if (this.category == DispelSlotType.DEBUFF) {
            effected.getStats().mergeMul(Stat.RESIST_ABNORMAL_DEBUFF, this.power);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ResistAbnormalByCategory(data);
        }
        
        public String effectName() {
            return "resist-abnormal-by-category";
        }
    }
}
