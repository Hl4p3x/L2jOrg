// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.stream.Stream;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Set;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MagicalAttackByAbnormalSlot extends AbstractEffect
{
    private final double power;
    private final Set<AbnormalType> abnormals;
    
    private MagicalAttackByAbnormalSlot(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.abnormals = Arrays.stream(params.getString("abnormals", "").split(" ")).map((Function<? super String, ?>)AbnormalType::valueOf).collect((Collector<? super Object, ?, Set<AbnormalType>>)Collectors.toSet());
    }
    
    public EffectType getEffectType() {
        return EffectType.MAGICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!effector.isAlikeDead()) {
            final Stream<Object> stream = this.abnormals.stream();
            Objects.requireNonNull(effected);
            if (!stream.noneMatch((Predicate<? super Object>)effected::hasAbnormalType)) {
                if (GameUtils.isPlayer((WorldObject)effected) && effected.getActingPlayer().isFakeDeath()) {
                    effected.stopFakeDeath(true);
                }
                final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
                final double damage = Formulas.calcMagicDam(effector, effected, skill, (double)effector.getMAtk(), this.power, (double)effected.getMDef(), mcrit);
                effector.doAttack(damage, effected, skill, false, false, mcrit, false);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicalAttackByAbnormalSlot(data);
        }
        
        public String effectName() {
            return "magical-attack-by-abnormal";
        }
    }
}
