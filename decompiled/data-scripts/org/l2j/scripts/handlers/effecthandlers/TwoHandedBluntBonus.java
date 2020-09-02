// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.conditions.ConditionUsingSlotType;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class TwoHandedBluntBonus extends AbstractEffect
{
    private static final Condition weaponTypeCondition;
    private static final Condition slotCondition;
    private final double pAtkAmount;
    private final StatModifierType pAtkmode;
    private final double accuracyAmount;
    private final StatModifierType accuracyMode;
    
    private TwoHandedBluntBonus(final StatsSet params) {
        this.pAtkAmount = params.getDouble("power", 0.0);
        this.pAtkmode = (StatModifierType)params.getEnum("mode", (Class)StatModifierType.class, (Enum)StatModifierType.DIFF);
        this.accuracyAmount = params.getDouble("accuracy", 0.0);
        this.accuracyMode = (StatModifierType)params.getEnum("accuracy-mode", (Class)StatModifierType.class, (Enum)StatModifierType.DIFF);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        if (TwoHandedBluntBonus.weaponTypeCondition.test(effected, effected, skill) && TwoHandedBluntBonus.slotCondition.test(effected, effected, skill)) {
            switch (this.pAtkmode) {
                case DIFF: {
                    effected.getStats().mergeAdd(Stat.PHYSICAL_ATTACK, this.pAtkAmount);
                    break;
                }
                case PER: {
                    effected.getStats().mergeMul(Stat.PHYSICAL_ATTACK, this.pAtkAmount);
                    break;
                }
            }
            switch (this.accuracyMode) {
                case DIFF: {
                    effected.getStats().mergeAdd(Stat.ACCURACY, this.accuracyAmount);
                    break;
                }
                case PER: {
                    effected.getStats().mergeMul(Stat.ACCURACY, this.accuracyAmount);
                    break;
                }
            }
        }
    }
    
    static {
        weaponTypeCondition = (Condition)new ConditionUsingItemType(WeaponType.BLUNT.mask());
        slotCondition = (Condition)new ConditionUsingSlotType(BodyPart.TWO_HAND.getId());
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TwoHandedBluntBonus(data);
        }
        
        public String effectName() {
            return "two-hand-blunt-bonus";
        }
    }
}
