// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class StealAbnormal extends AbstractEffect
{
    private final DispelSlotType slot;
    private final int rate;
    private final int power;
    
    private StealAbnormal(final StatsSet params) {
        this.slot = (DispelSlotType)params.getEnum("category", (Class)DispelSlotType.class, (Enum)DispelSlotType.BUFF);
        this.rate = params.getInt("rate", 0);
        this.power = params.getInt("power", 0);
    }
    
    public EffectType getEffectType() {
        return EffectType.STEAL_ABNORMAL;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected) && effector != effected) {
            final List<BuffInfo> toSteal = (List<BuffInfo>)Formulas.calcCancelStealEffects(effector, effected, skill, this.slot, this.rate, this.power);
            if (toSteal.isEmpty()) {
                return;
            }
            for (final BuffInfo infoToSteal : toSteal) {
                final BuffInfo stolen = new BuffInfo(effected, effector, infoToSteal.getSkill(), false, (Item)null, (Options)null);
                stolen.setAbnormalTime(infoToSteal.getTime());
                infoToSteal.getSkill().applyEffectScope(EffectScope.GENERAL, stolen, true, true);
                effected.getEffectList().remove(infoToSteal, true, true, true);
                effector.getEffectList().add(stolen);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StealAbnormal(data);
        }
        
        public String effectName() {
            return "steal-abnormal";
        }
    }
}
