// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import java.util.Arrays;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.Condition;
import java.util.List;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.AbstractEffect;

public abstract class AbstractStatEffect extends AbstractEffect
{
    public final Stat addStat;
    public final Stat mulStat;
    public final double power;
    public final StatModifierType mode;
    public final List<Condition> conditions;
    
    public AbstractStatEffect(final StatsSet params, final Stat stat) {
        this(params, stat, stat);
    }
    
    public AbstractStatEffect(final StatsSet params, final Stat mulStat, final Stat addStat) {
        this.conditions = new ArrayList<Condition>();
        this.addStat = addStat;
        this.mulStat = mulStat;
        this.power = params.getDouble("power", 0.0);
        this.mode = (StatModifierType)params.getEnum("mode", (Class)StatModifierType.class, (Enum)StatModifierType.DIFF);
        if (params.contains("weapon-type")) {
            final int weaponTypesMask = Arrays.stream(params.getString("weapon-type").trim().split(" ")).mapToInt(w -> WeaponType.valueOf(w).mask()).reduce(0, (a, b) -> a | b);
            this.conditions.add((Condition)new ConditionUsingItemType(weaponTypesMask));
        }
        if (params.contains("armor-type")) {
            final int armorTypesMask = Arrays.stream(params.getString("armor-type").trim().split(" ")).mapToInt(armor -> ArmorType.valueOf(armor).mask()).reduce(0, (a, b) -> a | b);
            this.conditions.add((Condition)new ConditionUsingItemType(armorTypesMask));
        }
    }
    
    public void pump(final Creature effected, final Skill skill) {
        if (this.conditions.isEmpty() || this.conditions.stream().allMatch(cond -> cond.test(effected, effected, skill))) {
            switch (this.mode) {
                case DIFF: {
                    effected.getStats().mergeAdd(this.addStat, this.power);
                    break;
                }
                case PER: {
                    effected.getStats().mergeMul(this.mulStat, this.power);
                    break;
                }
            }
        }
    }
}
