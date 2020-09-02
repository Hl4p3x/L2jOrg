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

public class ResistDispelByCategory extends AbstractEffect
{
    private final DispelSlotType slot;
    private final double power;
    
    private ResistDispelByCategory(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.slot = (DispelSlotType)params.getEnum("category", (Class)DispelSlotType.class, (Enum)DispelSlotType.BUFF);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        if (this.slot == DispelSlotType.BUFF) {
            effected.getStats().mergeMul(Stat.RESIST_DISPEL_BUFF, this.power);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ResistDispelByCategory(data);
        }
        
        public String effectName() {
            return "resist-dispel-by-category";
        }
    }
}
