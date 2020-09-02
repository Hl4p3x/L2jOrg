// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class EnergyAttack extends AbstractEffect
{
    private final double power;
    private final int chargeConsume;
    private final int criticalChance;
    private final boolean ignoreShieldDefence;
    private final boolean overHit;
    private final double pDefMod;
    
    private EnergyAttack(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.criticalChance = params.getInt("critical-chance", 0);
        this.ignoreShieldDefence = params.getBoolean("ignore-shield", false);
        this.overHit = params.getBoolean("over-hit", false);
        this.chargeConsume = params.getInt("consume-charges", 0);
        this.pDefMod = params.getDouble("physical-defense-mod", 1.0);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill);
    }
    
    public EffectType getEffectType() {
        return EffectType.PHYSICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector)) {
            return;
        }
        final Player attacker = effector.getActingPlayer();
        final int charge = Math.min(this.chargeConsume, attacker.getCharges());
        if (!attacker.decreaseCharges(charge)) {
            attacker.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill) });
            return;
        }
        if (this.overHit && GameUtils.isAttackable((WorldObject)effected)) {
            ((Attackable)effected).overhitEnabled(true);
        }
        double defence = effected.getPDef() * this.pDefMod;
        if (!this.ignoreShieldDefence) {
            final byte shield = Formulas.calcShldUse((Creature)attacker, effected);
            switch (shield) {
                case 1: {
                    defence += effected.getShldDef();
                    break;
                }
                case 2: {
                    defence = -1.0;
                    break;
                }
            }
        }
        double damage = 1.0;
        final boolean critical = Formulas.calcCrit((double)this.criticalChance, (Creature)attacker, effected, skill);
        if (defence != -1.0) {
            final double weaponTraitMod = Formulas.calcWeaponTraitBonus((Creature)attacker, effected);
            final double generalTraitMod = Formulas.calcGeneralTraitBonus((Creature)attacker, effected, skill.getTrait(), true);
            final double weaknessMod = Formulas.calcWeaknessBonus((Creature)attacker, effected, skill.getTrait());
            final double attributeMod = Formulas.calcAttributeBonus((Creature)attacker, effected, skill);
            final double pvpPveMod = Formulas.calculatePvpPveBonus((Creature)attacker, effected, skill, true);
            final double energyChargesBoost = 1.0 + charge * 0.1;
            final double critMod = critical ? Formulas.calcCritDamage((Creature)attacker, effected, skill) : 1.0;
            final double ssmod = skill.useSoulShot() ? attacker.chargedShotBonus(ShotType.SOULSHOTS) : 1.0;
            final double baseMod = 77.0 * (attacker.getPAtk() * attacker.getLevelMod() + effector.getStats().getValue(Stat.PHYSICAL_SKILL_POWER, this.power)) / defence;
            damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * energyChargesBoost * pvpPveMod;
        }
        effector.doAttack(damage, effected, skill, false, false, critical, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new EnergyAttack(data);
        }
        
        public String effectName() {
            return "energy-attack";
        }
    }
}
