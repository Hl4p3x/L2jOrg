// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.stats.Stat;
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

public final class PhysicalAttackHpLink extends AbstractEffect
{
    private final double power;
    private final double criticalChance;
    private final boolean overHit;
    
    private PhysicalAttackHpLink(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.criticalChance = params.getDouble("critical-chance", 0.0);
        this.overHit = params.getBoolean("over-hit", false);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill);
    }
    
    public EffectType getEffectType() {
        return EffectType.PHYSICAL_ATTACK_HP_LINK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector.isAlikeDead()) {
            return;
        }
        if (this.overHit && GameUtils.isAttackable((WorldObject)effected)) {
            ((Attackable)effected).overhitEnabled(true);
        }
        final double attack = effector.getPAtk();
        double defence = effected.getPDef();
        switch (Formulas.calcShldUse(effector, effected)) {
            case 1: {
                defence += effected.getShldDef();
                break;
            }
            case 2: {
                defence = -1.0;
                break;
            }
        }
        double damage = 1.0;
        final boolean critical = Formulas.calcCrit(this.criticalChance, effector, effected, skill);
        if (defence != -1.0) {
            final double weaponTraitMod = Formulas.calcWeaponTraitBonus(effector, effected);
            final double generalTraitMod = Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), true);
            final double weaknessMod = Formulas.calcWeaknessBonus(effector, effected, skill.getTrait());
            final double attributeMod = Formulas.calcAttributeBonus(effector, effected, skill);
            final double pvpPveMod = Formulas.calculatePvpPveBonus(effector, effected, skill, true);
            final double randomMod = effector.getRandomDamageMultiplier();
            final double weaponMod = effector.getAttackType().isRanged() ? 70.0 : 77.0;
            final double power = effector.getStats().getValue(Stat.PHYSICAL_SKILL_POWER, this.power);
            final double rangedBonus = effector.getAttackType().isRanged() ? (attack + power) : 0.0;
            final double critMod = critical ? Formulas.calcCritDamage(effector, effected, skill) : 1.0;
            final double ssmod = skill.useSoulShot() ? effector.chargedShotBonus(ShotType.SOULSHOTS) : 1.0;
            final double baseMod = weaponMod * (attack * effector.getLevelMod() + power + rangedBonus) / defence;
            damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * pvpPveMod * randomMod;
            damage *= -(effector.getCurrentHp() * 2.0 / effector.getMaxHp()) + 2.0;
        }
        effector.doAttack(damage, effected, skill, false, false, critical, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new PhysicalAttackHpLink(data);
        }
        
        public String effectName() {
            return "physical-attack-hp-link";
        }
    }
}
