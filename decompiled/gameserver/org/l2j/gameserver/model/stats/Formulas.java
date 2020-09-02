// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.enums.BasicProperty;
import java.util.Iterator;
import org.l2j.gameserver.data.xml.impl.KarmaData;
import java.util.ArrayList;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.List;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.data.xml.impl.HitConditionBonusData;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;

public final class Formulas
{
    public static final byte SHIELD_DEFENSE_FAILED = 0;
    public static final byte SHIELD_DEFENSE_SUCCEED = 1;
    public static final byte SHIELD_DEFENSE_PERFECT_BLOCK = 2;
    public static final int SKILL_LAUNCH_TIME = 500;
    private static final int HP_REGENERATE_PERIOD = 3000;
    private static final byte MELEE_ATTACK_RANGE = 40;
    
    public static int getRegeneratePeriod(final Creature cha) {
        return GameUtils.isDoor(cha) ? 300000 : 3000;
    }
    
    public static double calcBlowDamage(final Creature attacker, final Creature target, final Skill skill, final double power) {
        double defence = target.getPDef();
        switch (calcShldUse(attacker, target)) {
            case 1: {
                defence += target.getShldDef();
                break;
            }
            case 2: {
                return 1.0;
            }
        }
        final Position position = Position.getPosition(attacker, target);
        final double criticalMod = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE, 1.0);
        final double criticalPositionMod = attacker.getStats().getPositionTypeValue(Stat.CRITICAL_DAMAGE, position);
        final double criticalVulnMod = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE, 1.0);
        final double criticalAddMod = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_ADD, 0.0);
        final double criticalAddVuln = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_ADD, 0.0);
        final double weaponTraitMod = calcWeaponTraitBonus(attacker, target);
        final double generalTraitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
        final double weaknessMod = calcWeaknessBonus(attacker, target, skill.getTrait());
        final double attributeMod = calcAttributeBonus(attacker, target, skill);
        final double randomMod = attacker.getRandomDamageMultiplier();
        final double pvpPveMod = calculatePvpPveBonus(attacker, target, skill, true);
        final double ssmod = attacker.chargedShotBonus(ShotType.SOULSHOTS);
        final double cdMult = criticalMod * ((criticalPositionMod - 1.0) / 2.0 + 1.0) * ((criticalVulnMod - 1.0) / 2.0 + 1.0);
        final double cdPatk = criticalAddMod + criticalAddVuln;
        final double positionMod = (position == Position.BACK) ? 0.2 : ((position == Position.SIDE) ? 0.05 : 0.0);
        final double baseMod = 77.0 * ((power + attacker.getPAtk()) * 0.666 + positionMod * (power + attacker.getPAtk()) * randomMod + 6.0 * cdPatk) / defence;
        return baseMod * ssmod * cdMult * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * randomMod * pvpPveMod;
    }
    
    public static double calcMagicDam(final Creature attacker, final Creature target, final Skill skill, final double mAtk, final double power, final double mDef, final boolean mcrit) {
        final double shotsBonus = skill.useSpiritShot() ? attacker.chargedShotBonus(ShotType.SPIRITSHOTS) : 1.0;
        final double critMod = mcrit ? calcCritDamage(attacker, target, skill) : 1.0;
        final double generalTraitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
        final double weaknessMod = calcWeaknessBonus(attacker, target, skill.getTrait());
        final double attributeMod = calcAttributeBonus(attacker, target, skill);
        final double randomMod = attacker.getRandomDamageMultiplier();
        final double pvpPveMod = calculatePvpPveBonus(attacker, target, skill, mcrit);
        double damage = 77.0 * attacker.getStats().getValue(Stat.MAGICAL_SKILL_POWER, power) * Math.sqrt(mAtk) / mDef * shotsBonus;
        if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill)) {
            if (GameUtils.isPlayer(attacker)) {
                if (calcMagicSuccess(attacker, target, skill)) {
                    if (skill.hasAnyEffectType(EffectType.HP_DRAIN)) {
                        attacker.sendPacket(SystemMessageId.DRAIN_WAS_ONLY_50_SUCCESSFUL);
                    }
                    else {
                        attacker.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
                    }
                    damage /= 2.0;
                }
                else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2);
                    sm.addString(target.getName());
                    sm.addSkillName(skill);
                    attacker.sendPacket(sm);
                    damage = 1.0;
                }
            }
            if (GameUtils.isPlayer(target)) {
                final SystemMessage sm = skill.hasAnyEffectType(EffectType.HP_DRAIN) ? SystemMessage.getSystemMessage(SystemMessageId.YOU_RESISTED_C1_S_DRAIN) : SystemMessage.getSystemMessage(SystemMessageId.YOU_RESISTED_C1_S_MAGIC);
                sm.addString(attacker.getName());
                target.sendPacket(sm);
            }
        }
        return damage * critMod * generalTraitMod * weaknessMod * attributeMod * randomMod * pvpPveMod;
    }
    
    public static boolean calcCrit(double rate, final Creature activeChar, final Creature target, final Skill skill) {
        if (skill == null) {
            final double criticalRateMod = (target.getStats().getValue(Stat.DEFENCE_CRITICAL_RATE, rate) + target.getStats().getValue(Stat.DEFENCE_CRITICAL_RATE_ADD, 0.0)) / 10.0;
            final double criticalLocBonus = calcCriticalPositionBonus(activeChar, target);
            final double criticalHeightBonus = calcCriticalHeightBonus(activeChar, target);
            rate = criticalLocBonus * criticalRateMod * criticalHeightBonus;
            if (activeChar.getLevel() >= 78 || target.getLevel() >= 78) {
                rate += Math.sqrt(activeChar.getLevel()) * (activeChar.getLevel() - target.getLevel()) * 0.125;
            }
            rate = CommonUtil.constrain(rate, 3.0, 97.0);
            return rate > Rnd.get(100);
        }
        if (!skill.isMagic()) {
            final byte skillCritRateStat = (byte)activeChar.getStats().getValue(Stat.STAT_BONUS_SKILL_CRITICAL);
            double statBonus;
            if (skillCritRateStat >= 0 && skillCritRateStat < BaseStats.values().length) {
                statBonus = BaseStats.values()[skillCritRateStat].calcBonus(activeChar);
            }
            else {
                statBonus = BaseStats.STR.calcBonus(activeChar);
            }
            final double rateBonus = activeChar.getStats().getValue(Stat.CRITICAL_RATE_SKILL, 1.0);
            final double finalRate = rate * statBonus * rateBonus * 10.0;
            return finalRate > Rnd.get(1000);
        }
        rate += activeChar.getStats().getValue(Stat.MAGIC_CRITICAL_RATE);
        if (target == null || !skill.isBad()) {
            return Math.min(rate, 320.0) > Rnd.get(1000);
        }
        double finalRate2 = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_RATE, rate) + target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_RATE_ADD, 0.0);
        if (activeChar.getLevel() >= 78 && target.getLevel() >= 78) {
            finalRate2 += Math.sqrt(activeChar.getLevel()) + (activeChar.getLevel() - target.getLevel()) / 25.0;
            return Math.min(finalRate2, 320.0) > Rnd.get(1000);
        }
        return Math.min(finalRate2, 200.0) > Rnd.get(1000);
    }
    
    public static double calcCriticalPositionBonus(final Creature activeChar, final Creature target) {
        switch (Position.getPosition(activeChar, target)) {
            case SIDE: {
                return 1.1 * activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.SIDE);
            }
            case BACK: {
                return 1.3 * activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.BACK);
            }
            default: {
                return activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.FRONT);
            }
        }
    }
    
    public static double calcCriticalHeightBonus(final ILocational from, final ILocational target) {
        return (CommonUtil.constrain(from.getZ() - target.getZ(), -25, 25) * 4 / 5 + 10) / 100 + 1;
    }
    
    public static double calcCritDamage(final Creature attacker, final Creature target, final Skill skill) {
        double criticalDamage;
        double defenceCriticalDamage;
        if (skill != null) {
            if (skill.isMagic()) {
                criticalDamage = attacker.getStats().getValue(Stat.MAGIC_CRITICAL_DAMAGE, 1.0);
                defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_DAMAGE, 1.0);
            }
            else {
                criticalDamage = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_SKILL, 1.0);
                defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_SKILL, 1.0);
            }
        }
        else {
            criticalDamage = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE, 1.0) * attacker.getStats().getPositionTypeValue(Stat.CRITICAL_DAMAGE, Position.getPosition(attacker, target));
            defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE, 1.0);
        }
        return Math.max(1.0, 2.0 + (criticalDamage - defenceCriticalDamage) / (75.0 + Math.min(defenceCriticalDamage, criticalDamage)));
    }
    
    public static double calcCritDamageAdd(final Creature attacker, final Creature target, final Skill skill) {
        double criticalDamageAdd;
        double defenceCriticalDamageAdd;
        if (skill != null) {
            if (skill.isMagic()) {
                criticalDamageAdd = attacker.getStats().getValue(Stat.MAGIC_CRITICAL_DAMAGE_ADD, 0.0);
                defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD, 0.0);
            }
            else {
                criticalDamageAdd = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_SKILL_ADD, 0.0);
                defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_SKILL_ADD, 0.0);
            }
        }
        else {
            criticalDamageAdd = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_ADD, 0.0);
            defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_ADD, 0.0);
        }
        return criticalDamageAdd + defenceCriticalDamageAdd;
    }
    
    public static boolean calcAtkBreak(final Creature target, final double dmg) {
        if (target.isChanneling()) {
            return false;
        }
        double init = 0.0;
        if (Config.ALT_GAME_CANCEL_CAST && target.isCastingNow(SkillCaster::canAbortCast)) {
            init = 15.0;
        }
        if (Config.ALT_GAME_CANCEL_BOW && target.isAttackingNow()) {
            final Weapon wpn = target.getActiveWeaponItem();
            if (wpn != null && wpn.getItemType() == WeaponType.BOW) {
                init = 15.0;
            }
        }
        if (target.isRaid() || target.isHpBlocked() || init <= 0.0) {
            return false;
        }
        init += Math.sqrt(13.0 * dmg);
        init -= BaseStats.MEN.calcBonus(target) * 100.0 - 100.0;
        double rate = target.getStats().getValue(Stat.ATTACK_CANCEL, init);
        rate = Math.max(Math.min(rate, 99.0), 1.0);
        return Rnd.get(100) < rate;
    }
    
    public static int calcAtkSpd(final Creature attacker, final Skill skill, final double skillTime) {
        if (skill.isMagic()) {
            return (int)(skillTime / attacker.getMAtkSpd() * 333.0);
        }
        return (int)(skillTime / attacker.getPAtkSpd() * 300.0);
    }
    
    public static double calcAtkSpdMultiplier(final Creature creature) {
        final double armorBonus = 1.0;
        final double dexBonus = BaseStats.DEX.calcBonus(creature);
        final double weaponAttackSpeed = Stat.weaponBaseValue(creature, Stat.PHYSICAL_ATTACK_SPEED) / armorBonus;
        final double attackSpeedPerBonus = creature.getStats().getMul(Stat.PHYSICAL_ATTACK_SPEED);
        final double attackSpeedDiffBonus = creature.getStats().getAdd(Stat.PHYSICAL_ATTACK_SPEED);
        return dexBonus * (weaponAttackSpeed / 333.0) * attackSpeedPerBonus + attackSpeedDiffBonus / 333.0;
    }
    
    public static double calcMAtkSpdMultiplier(final Creature creature) {
        final double armorBonus = 1.0;
        final double witBonus = BaseStats.WIT.calcBonus(creature);
        final double castingSpeedPerBonus = creature.getStats().getMul(Stat.MAGIC_ATTACK_SPEED);
        final double castingSpeedDiffBonus = creature.getStats().getAdd(Stat.MAGIC_ATTACK_SPEED);
        return 1.0 * witBonus * castingSpeedPerBonus + castingSpeedDiffBonus / 333.0;
    }
    
    public static double calcSkillTimeFactor(final Creature creature, final Skill skill) {
        if (skill.isChanneling() || skill.getSkillType() == SkillType.STATIC) {
            return 1.0;
        }
        double factor = 0.0;
        if (skill.getSkillType() == SkillType.MAGIC) {
            final double spiritshotHitTime = creature.isChargedShot(ShotType.SPIRITSHOTS) ? 0.4 : 0.0;
            factor = creature.getStats().getMAttackSpeedMultiplier() + creature.getStats().getMAttackSpeedMultiplier() * spiritshotHitTime;
        }
        else {
            factor = creature.getAttackSpeedMultiplier();
        }
        if (GameUtils.isNpc(creature)) {
            final double npcFactor = ((Npc)creature).getTemplate().getHitTimeFactorSkill();
            if (npcFactor > 0.0) {
                factor /= npcFactor;
            }
        }
        return Math.max(0.01, factor);
    }
    
    public static double calcSkillCancelTime(final Creature creature, final Skill skill) {
        return Math.max(skill.getHitCancelTime() * 1000.0 / calcSkillTimeFactor(creature, skill), 500.0);
    }
    
    public static boolean calcHitMiss(final Creature attacker, final Creature target) {
        int chance = (80 + 2 * (attacker.getAccuracy() - target.getEvasionRate())) * 10;
        chance *= (int)HitConditionBonusData.getInstance().getConditionBonus(attacker, target);
        chance = Math.max(chance, 200);
        chance = Math.min(chance, 980);
        return chance < Rnd.get(1000);
    }
    
    public static byte calcShldUse(final Creature attacker, final Creature target, final boolean sendSysMsg) {
        final ItemTemplate item = target.getSecondaryWeaponItem();
        if (!(item instanceof Armor) || ((Armor)item).getItemType() == ArmorType.SIGIL) {
            return 0;
        }
        double shldRate = target.getStats().getValue(Stat.SHIELD_DEFENCE_RATE) * BaseStats.DEX.calcBonus(target);
        if (attacker.getAttackType().isRanged()) {
            shldRate *= 1.3;
        }
        final int degreeside = target.isAffected(EffectFlag.PHYSICAL_SHIELD_ANGLE_ALL) ? 360 : 120;
        if (degreeside < 360 && Math.abs(target.calculateDirectionTo(attacker) - MathUtil.convertHeadingToDegree(target.getHeading())) > degreeside / 2.0) {
            return 0;
        }
        byte shldSuccess = 0;
        if (shldRate > Rnd.get(100)) {
            if (100.0 - 2.0 * BaseStats.DEX.calcBonus(target) < Rnd.get(100)) {
                shldSuccess = 2;
            }
            else {
                shldSuccess = 1;
            }
        }
        if (sendSysMsg && GameUtils.isPlayer(target)) {
            final Player enemy = target.getActingPlayer();
            switch (shldSuccess) {
                case 1: {
                    enemy.sendPacket(SystemMessageId.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                    break;
                }
                case 2: {
                    enemy.sendPacket(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                    break;
                }
            }
        }
        return shldSuccess;
    }
    
    public static byte calcShldUse(final Creature attacker, final Creature target) {
        return calcShldUse(attacker, target, true);
    }
    
    public static boolean calcMagicAffected(final Creature actor, final Creature target, final Skill skill) {
        double defence = 0.0;
        if (skill.isActive() && skill.isBad()) {
            defence = target.getMDef();
        }
        final double attack = 2 * actor.getMAtk() * calcGeneralTraitBonus(actor, target, skill.getTrait(), false);
        double d = (attack - defence) / (attack + defence);
        if (skill.isDebuff() && target.getAbnormalShieldBlocks() > 0) {
            target.decrementAbnormalShieldBlocks();
            return false;
        }
        d += 0.5 * Rnd.nextGaussian();
        return d > 0.0;
    }
    
    public static double calcLvlBonusMod(final Creature attacker, final Creature target, final Skill skill) {
        final int attackerLvl = (skill.getMagicLevel() > 0) ? skill.getMagicLevel() : attacker.getLevel();
        final double skillLvlBonusRateMod = 1.0 + skill.getLevelBonusRate() / 100.0;
        final double lvlMod = 1.0 + (attackerLvl - target.getLevel()) / 100.0;
        return skillLvlBonusRateMod * lvlMod;
    }
    
    public static boolean calcEffectSuccess(final Creature attacker, final Creature target, final Skill skill) {
        if (GameUtils.isDoor(target) || target instanceof SiegeFlag || target instanceof StaticWorldObject) {
            return false;
        }
        if (skill.isDebuff()) {
            boolean resisted = target.isCastingNow(s -> s.getSkill().getAbnormalResists().contains(skill.getAbnormalType()));
            if (!resisted && target.getAbnormalShieldBlocks() > 0) {
                target.decrementAbnormalShieldBlocks();
                resisted = true;
            }
            if (!resisted) {
                final double sphericBarrierRange = target.getStats().getValue(Stat.SPHERIC_BARRIER_RANGE, 0.0);
                if (sphericBarrierRange > 0.0) {
                    resisted = !MathUtil.isInsideRadius3D(attacker, target, (int)sphericBarrierRange);
                }
            }
            if (resisted) {
                attacker.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2).addString(target.getName())).addSkillName(skill));
                return false;
            }
        }
        final int activateRate = skill.getActivateRate();
        if (activateRate <= 0 || activateRate >= 100) {
            return true;
        }
        int magicLevel = skill.getMagicLevel();
        if (magicLevel <= -1) {
            magicLevel = target.getLevel() + 3;
        }
        final double targetBasicProperty = getAbnormalResist(skill.getBasicProperty(), target);
        final double baseMod = (magicLevel - target.getLevel() + 3) * skill.getLevelBonusRate() + activateRate + 30.0 - targetBasicProperty;
        final double elementMod = calcAttributeBonus(attacker, target, skill);
        final double traitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), false);
        final double basicPropertyResist = getBasicPropertyResistBonus(skill.getBasicProperty(), target);
        final double buffDebuffMod = skill.isDebuff() ? (2.0 - target.getStats().getValue(Stat.RESIST_ABNORMAL_DEBUFF, 1.0)) : 1.0;
        final double rate = baseMod * elementMod * traitMod * buffDebuffMod;
        final double finalRate = (traitMod > 0.0) ? (CommonUtil.constrain(rate, (double)skill.getMinChance(), (double)skill.getMaxChance()) * basicPropertyResist) : 0.0;
        if (!Rnd.chance(finalRate)) {
            if (target != attacker) {
                attacker.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2).addString(target.getName())).addSkillName(skill));
            }
            return false;
        }
        return true;
    }
    
    public static boolean calcCubicSkillSuccess(final CubicInstance attacker, final Creature target, final Skill skill, final byte shld) {
        if (skill.isDebuff()) {
            if (skill.getActivateRate() == -1) {
                return true;
            }
            if (target.getAbnormalShieldBlocks() > 0) {
                target.decrementAbnormalShieldBlocks();
                return false;
            }
        }
        if (shld == 2) {
            return false;
        }
        if (calcBuffDebuffReflection(target, skill)) {
            return false;
        }
        final double targetBasicProperty = getAbnormalResist(skill.getBasicProperty(), target);
        final double baseRate = skill.getActivateRate();
        final double statMod = 1.0 + targetBasicProperty / 100.0;
        double rate = baseRate / statMod;
        final double resMod = calcGeneralTraitBonus(attacker.getOwner(), target, skill.getTrait(), false);
        rate *= resMod;
        final double lvlBonusMod = calcLvlBonusMod(attacker.getOwner(), target, skill);
        rate *= lvlBonusMod;
        final double elementMod = calcAttributeBonus(attacker.getOwner(), target, skill);
        rate *= elementMod;
        final double basicPropertyResist = getBasicPropertyResistBonus(skill.getBasicProperty(), target);
        final double finalRate = CommonUtil.constrain(rate, (double)skill.getMinChance(), (double)skill.getMaxChance()) * basicPropertyResist;
        return Rnd.get(100) < finalRate;
    }
    
    public static boolean calcMagicSuccess(final Creature attacker, final Creature target, final Skill skill) {
        final int mAccDiff = attacker.getMagicAccuracy() - target.getMagicEvasionRate();
        int mAccModifier = 100;
        if (mAccDiff > -20) {
            mAccModifier = 2;
        }
        else if (mAccDiff > -25) {
            mAccModifier = 30;
        }
        else if (mAccDiff > -30) {
            mAccModifier = 60;
        }
        else if (mAccDiff > -35) {
            mAccModifier = 90;
        }
        float targetModifier = 1.0f;
        if (GameUtils.isAttackable(target) && !target.isRaid() && !target.isRaidMinion() && target.getLevel() >= Config.MIN_NPC_LVL_MAGIC_PENALTY && attacker.getActingPlayer() != null && target.getLevel() - attacker.getActingPlayer().getLevel() >= 3) {
            final int lvlDiff = target.getLevel() - attacker.getActingPlayer().getLevel() - 2;
            if (lvlDiff >= Config.NPC_SKILL_CHANCE_PENALTY.size()) {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(Config.NPC_SKILL_CHANCE_PENALTY.size() - 1);
            }
            else {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(lvlDiff);
            }
        }
        final double resModifier = target.getStats().getValue(Stat.MAGIC_SUCCESS_RES, 1.0);
        final int rate = 100 - Math.round((float)(mAccModifier * targetModifier * resModifier));
        return Rnd.get(100) < rate;
    }
    
    public static double calcManaDam(final Creature attacker, final Creature target, final Skill skill, final double power, final boolean mcrit, final double critLimit) {
        double mAtk = attacker.getMAtk();
        double mDef = target.getMDef();
        final double mp = target.getMaxMp();
        switch (calcShldUse(attacker, target)) {
            case 1: {
                mDef += target.getShldDef();
                break;
            }
            case 2: {
                return 1.0;
            }
        }
        mAtk *= (skill.useSpiritShot() ? attacker.chargedShotBonus(ShotType.SPIRITSHOTS) : 1.0);
        double damage = Math.sqrt(mAtk) * power * (mp / 97.0) / mDef;
        damage *= calcGeneralTraitBonus(attacker, target, skill.getTrait(), false);
        damage *= calculatePvpPveBonus(attacker, target, skill, mcrit);
        if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill)) {
            if (GameUtils.isPlayer(attacker)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2_S_MAGIC);
                sm.addString(target.getName());
                sm.addString(attacker.getName());
                attacker.sendPacket(sm);
                damage /= 2.0;
            }
            if (GameUtils.isPlayer(target)) {
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.C1_WEAKLY_RESISTED_C2_S_MAGIC);
                sm2.addString(target.getName());
                sm2.addString(attacker.getName());
                target.sendPacket(sm2);
            }
        }
        if (mcrit) {
            damage *= 3.0;
            damage = Math.min(damage, critLimit);
            attacker.sendPacket(SystemMessageId.M_CRITICAL);
        }
        return damage;
    }
    
    public static double calculateSkillResurrectRestorePercent(final double baseRestorePercent, final Creature caster) {
        if (baseRestorePercent == 0.0 || baseRestorePercent == 100.0) {
            return baseRestorePercent;
        }
        double restorePercent = baseRestorePercent * BaseStats.WIT.calcBonus(caster);
        if (restorePercent - baseRestorePercent > 20.0) {
            restorePercent += 20.0;
        }
        restorePercent = Math.max(restorePercent, baseRestorePercent);
        restorePercent = Math.min(restorePercent, 90.0);
        return restorePercent;
    }
    
    public static boolean calcPhysicalSkillEvasion(final Creature activeChar, final Creature target, final Skill skill) {
        if (Rnd.get(100) < target.getStats().getSkillEvasionTypeValue(skill.getSkillType())) {
            if (GameUtils.isPlayer(activeChar)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DODGED_THE_ATTACK);
                sm.addString(target.getName());
                activeChar.getActingPlayer().sendPacket(sm);
            }
            if (GameUtils.isPlayer(target)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DODGED_C1_S_ATTACK);
                sm.addString(activeChar.getName());
                target.getActingPlayer().sendPacket(sm);
            }
            return true;
        }
        return false;
    }
    
    public static boolean calcSkillMastery(final Creature actor, final Skill skill) {
        if (!GameUtils.isPlayer(actor)) {
            return false;
        }
        final int val = (int)actor.getStats().getAdd(Stat.SKILL_CRITICAL, -1.0);
        if (val == -1) {
            return false;
        }
        final double chance = BaseStats.values()[val].calcBonus(actor) * actor.getStats().getValue(Stat.SKILL_CRITICAL_PROBABILITY, 1.0);
        return Rnd.nextDouble() * 100.0 < chance;
    }
    
    public static double calcAttributeBonus(final Creature attacker, final Creature target, final Skill skill) {
        int attack_attribute;
        int defence_attribute;
        if (skill != null && skill.getAttributeType() != AttributeType.NONE) {
            attack_attribute = attacker.getAttackElementValue(skill.getAttributeType()) + skill.getAttributeValue();
            defence_attribute = target.getDefenseElementValue(skill.getAttributeType());
        }
        else {
            attack_attribute = attacker.getAttackElementValue(attacker.getAttackElement());
            defence_attribute = target.getDefenseElementValue(attacker.getAttackElement());
        }
        final int diff = attack_attribute - defence_attribute;
        if (diff > 0) {
            return Math.min(1.025 + Math.sqrt(Math.pow(diff, 3.0) / 2.0) * 1.0E-4, 1.25);
        }
        if (diff < 0) {
            return Math.max(0.975 - Math.sqrt(Math.pow(-diff, 3.0) / 2.0) * 1.0E-4, 0.75);
        }
        return 1.0;
    }
    
    public static void calcCounterAttack(final Creature attacker, final Creature target, final Skill skill, final boolean crit) {
        if (skill.isMagic() || skill.getCastRange() > 40) {
            return;
        }
        final double chance = target.getStats().getValue(Stat.VENGEANCE_SKILL_PHYSICAL_DAMAGE, 0.0);
        if (Rnd.get(100) < chance) {
            if (GameUtils.isPlayer(target)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_COUNTERED_C1_S_ATTACK);
                sm.addString(attacker.getName());
                target.sendPacket(sm);
            }
            if (GameUtils.isPlayer(attacker)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_PERFORMING_A_COUNTERATTACK);
                sm.addString(target.getName());
                attacker.sendPacket(sm);
            }
            double counterdmg = target.getPAtk() * 873.0 / attacker.getPDef();
            counterdmg *= calcWeaponTraitBonus(attacker, target);
            counterdmg *= calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
            counterdmg *= calcAttributeBonus(attacker, target, skill);
            attacker.reduceCurrentHp(counterdmg, target, skill, DamageInfo.DamageType.ATTACK);
        }
    }
    
    public static boolean calcBuffDebuffReflection(final Creature target, final Skill skill) {
        return skill.isDebuff() && skill.getActivateRate() != -1 && target.getStats().getValue(skill.isMagic() ? Stat.REFLECT_SKILL_MAGIC : Stat.REFLECT_SKILL_PHYSIC, 0.0) > Rnd.get(100);
    }
    
    public static double calcFallDam(final Creature cha, final int fallHeight) {
        if (!Config.ENABLE_FALLING_DAMAGE || fallHeight < 0) {
            return 0.0;
        }
        return cha.getStats().getValue(Stat.FALL, fallHeight * cha.getMaxHp() / 1000.0);
    }
    
    public static boolean calcBlowSuccess(final Creature activeChar, final Creature target, final Skill skill, final double chanceBoost) {
        if (skill.getActivateRate() == -1) {
            return true;
        }
        final Weapon weapon = activeChar.getActiveWeaponItem();
        final double weaponCritical = (weapon != null) ? weapon.getStats(Stat.CRITICAL_RATE, activeChar.getTemplate().getBaseCritRate()) : activeChar.getTemplate().getBaseCritRate();
        final double dexBonus = BaseStats.DEX.calcBonus(activeChar);
        final double critHeightBonus = calcCriticalHeightBonus(activeChar, target);
        final double criticalPosition = calcCriticalPositionBonus(activeChar, target);
        final double chanceBoostMod = (100.0 + chanceBoost) / 100.0;
        final double blowRateMod = activeChar.getStats().getValue(Stat.BLOW_RATE, 1.0);
        final double rate = criticalPosition * critHeightBonus * weaponCritical * chanceBoostMod * blowRateMod * dexBonus;
        return Rnd.get(100) < Math.min(rate, 80.0);
    }
    
    public static List<BuffInfo> calcCancelStealEffects(final Creature activeChar, final Creature target, final Skill skill, final DispelSlotType slot, final int rate, final int max) {
        final List<BuffInfo> canceled = new ArrayList<BuffInfo>(max);
        switch (slot) {
            case BUFF: {
                final int cancelMagicLvl = skill.getMagicLevel();
                final List<BuffInfo> dances = target.getEffectList().getDances();
                for (int i = dances.size() - 1; i >= 0; --i) {
                    final BuffInfo info = dances.get(i);
                    if (info.getSkill().canBeStolen()) {
                        if (rate >= 100 || calcCancelSuccess(info, cancelMagicLvl, rate, skill, target)) {
                            canceled.add(info);
                            if (canceled.size() >= max) {
                                break;
                            }
                        }
                    }
                }
                if (canceled.size() < max) {
                    final List<BuffInfo> buffs = target.getEffectList().getBuffs();
                    for (int j = buffs.size() - 1; j >= 0; --j) {
                        final BuffInfo info2 = buffs.get(j);
                        if (info2.getSkill().canBeStolen()) {
                            if (rate >= 100 || calcCancelSuccess(info2, cancelMagicLvl, rate, skill, target)) {
                                canceled.add(info2);
                                if (canceled.size() >= max) {
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            }
            case DEBUFF: {
                final List<BuffInfo> debuffs = target.getEffectList().getDebuffs();
                for (int k = debuffs.size() - 1; k >= 0; --k) {
                    final BuffInfo info3 = debuffs.get(k);
                    if (info3.getSkill().canBeDispelled() && Rnd.get(100) <= rate) {
                        canceled.add(info3);
                        if (canceled.size() >= max) {
                            break;
                        }
                    }
                }
                break;
            }
        }
        return canceled;
    }
    
    public static boolean calcCancelSuccess(final BuffInfo info, final int cancelMagicLvl, final int rate, final Skill skill, final Creature target) {
        final double resist = 2.0 - target.getStats().getValue(Stat.RESIST_DISPEL_BUFF, 1.0);
        final int chance = (int)(rate + (cancelMagicLvl - info.getSkill().getMagicLevel()) * 2 + info.getAbnormalTime() / 120 * resist);
        return Rnd.get(100) < CommonUtil.constrain(chance, 25, 75);
    }
    
    public static int calcEffectAbnormalTime(final Creature caster, final Creature target, final Skill skill) {
        int time = (skill == null || skill.isPassive() || skill.isToggle()) ? -1 : skill.getAbnormalTime();
        if (skill != null && calcSkillMastery(caster, skill)) {
            time *= 2;
        }
        return time;
    }
    
    public static boolean calcProbability(final double baseChance, final Creature attacker, final Creature target, final Skill skill) {
        if (Double.isNaN(baseChance)) {
            return calcGeneralTraitBonus(attacker, target, skill.getTrait(), true) > 0.0;
        }
        return Rnd.get(100) < (skill.getMagicLevel() + baseChance - target.getLevel() - getAbnormalResist(skill.getBasicProperty(), target)) * calcAttributeBonus(attacker, target, skill) * calcGeneralTraitBonus(attacker, target, skill.getTrait(), false);
    }
    
    public static int calculateKarmaLost(final Player player, double finalExp) {
        final double karmaLooseMul = KarmaData.getInstance().getMultiplier(player.getLevel());
        if (finalExp > 0.0) {
            finalExp /= Config.RATE_KARMA_LOST;
        }
        return (int)(Math.abs(finalExp) / karmaLooseMul / 30.0);
    }
    
    public static int calculateKarmaGain(final int pkCount, final boolean isSummon) {
        int result = 43200;
        if (isSummon) {
            result = (int)((pkCount * 0.375 + 1.0) * 60.0 * 4.0) - 150;
            if (result > 10800) {
                return 10800;
            }
        }
        if (pkCount < 99) {
            result = (int)((pkCount * 0.5 + 1.0) * 60.0 * 12.0);
        }
        else if (pkCount < 180) {
            result = (int)((pkCount * 0.125 + 37.75) * 60.0 * 12.0);
        }
        return result;
    }
    
    public static double calcGeneralTraitBonus(final Creature attacker, final Creature target, final TraitType traitType, final boolean ignoreResistance) {
        if (traitType == TraitType.NONE) {
            return 1.0;
        }
        if (target.getStats().isInvulnerableTrait(traitType)) {
            return 0.0;
        }
        switch (traitType.getType()) {
            case 2: {
                if (!attacker.getStats().hasAttackTrait(traitType) || !target.getStats().hasDefenceTrait(traitType)) {
                    return 1.0;
                }
                break;
            }
            case 3: {
                if (ignoreResistance) {
                    return 1.0;
                }
                break;
            }
            default: {
                return 1.0;
            }
        }
        return Math.max(attacker.getStats().getAttackTrait(traitType) - target.getStats().getDefenceTrait(traitType), 0.05);
    }
    
    public static double calcWeaknessBonus(final Creature attacker, final Creature target, final TraitType traitType) {
        double result = 1.0;
        for (final TraitType trait : TraitType.getAllWeakness()) {
            if (traitType != trait && target.getStats().hasDefenceTrait(trait) && attacker.getStats().hasAttackTrait(trait) && !target.getStats().isInvulnerableTrait(traitType)) {
                result *= Math.max(attacker.getStats().getAttackTrait(trait) - target.getStats().getDefenceTrait(trait), 0.05);
            }
        }
        return result;
    }
    
    public static double calcWeaponTraitBonus(final Creature attacker, final Creature target) {
        return Math.max(0.0, 1.0 - target.getStats().getDefenceTrait(attacker.getAttackType().getTraitType()));
    }
    
    public static double calcAttackTraitBonus(final Creature attacker, final Creature target) {
        final double weaponTraitBonus = calcWeaponTraitBonus(attacker, target);
        if (weaponTraitBonus == 0.0) {
            return 0.0;
        }
        double weaknessBonus = 1.0;
        for (final TraitType traitType : TraitType.values()) {
            if (traitType.getType() == 2) {
                weaknessBonus *= calcGeneralTraitBonus(attacker, target, traitType, true);
                if (weaknessBonus == 0.0) {
                    return 0.0;
                }
            }
        }
        return Math.max(weaponTraitBonus * weaknessBonus, 0.05);
    }
    
    public static double getBasicPropertyResistBonus(final BasicProperty basicProperty, final Creature target) {
        if (basicProperty == BasicProperty.NONE || !target.hasBasicPropertyResist()) {
            return 1.0;
        }
        final BasicPropertyResist resist = target.getBasicPropertyResist(basicProperty);
        switch (resist.getResistLevel()) {
            case 0: {
                return 1.0;
            }
            case 1: {
                return 0.6;
            }
            case 2: {
                return 0.3;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    public static double calcAutoAttackDamage(final Creature attacker, final Creature target, final byte shld, final boolean crit, final boolean ss) {
        double defence = target.getPDef();
        switch (shld) {
            case 1: {
                defence += target.getShldDef();
                break;
            }
            case 2: {
                return 1.0;
            }
        }
        final Weapon weapon = attacker.getActiveWeaponItem();
        final boolean isRanged = weapon != null && weapon.getItemType().isRanged();
        final double cAtk = crit ? calcCritDamage(attacker, target, null) : 1.0;
        final double cAtkAdd = crit ? calcCritDamageAdd(attacker, target, null) : 0.0;
        final double critMod = crit ? (isRanged ? 0.5 : 1.0) : 0.0;
        final double ssBonus = ss ? attacker.chargedShotBonus(ShotType.SOULSHOTS) : 1.0;
        final double random_damage = attacker.getRandomDamageMultiplier();
        final double proxBonus = (attacker.isInFrontOf(target) ? 0.0 : (attacker.isBehind(target) ? 0.2 : 0.05)) * attacker.getPAtk();
        double attack = attacker.getPAtk() * random_damage + proxBonus;
        attack = (attack * cAtk * ssBonus + cAtkAdd) * critMod * (isRanged ? 154 : 77) + attack * (1.0 - critMod) * ssBonus * (isRanged ? 154 : 77);
        double damage = attack / defence;
        damage *= calcAttackTraitBonus(attacker, target);
        damage *= calcAttributeBonus(attacker, target, null);
        damage *= calculatePvpPveBonus(attacker, target, null, crit);
        damage = Math.max(0.0, damage);
        return damage;
    }
    
    public static double getAbnormalResist(final BasicProperty basicProperty, final Creature target) {
        switch (basicProperty) {
            case PHYSIC: {
                return target.getStats().getValue(Stat.ABNORMAL_RESIST_PHYSICAL);
            }
            case MAGIC: {
                return target.getStats().getValue(Stat.ABNORMAL_RESIST_MAGICAL);
            }
            default: {
                return 0.0;
            }
        }
    }
    
    public static double calcPveDamagePenalty(final Creature attacker, final Creature target, final Skill skill, final boolean crit) {
        if (!GameUtils.isAttackable(target) || target.getLevel() < Config.MIN_NPC_LVL_DMG_PENALTY || attacker.getActingPlayer() == null || target.getLevel() - attacker.getActingPlayer().getLevel() <= 1) {
            return 1.0;
        }
        final int lvlDiff = target.getLevel() - attacker.getActingPlayer().getLevel() - 1;
        if (skill != null) {
            return Config.NPC_SKILL_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_SKILL_DMG_PENALTY.size() - 1));
        }
        if (crit) {
            return Config.NPC_CRIT_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_CRIT_DMG_PENALTY.size() - 1));
        }
        return Config.NPC_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_DMG_PENALTY.size() - 1));
    }
    
    public static boolean calcStunBreak(final Creature activeChar) {
        return Config.ALT_GAME_STUN_BREAK && activeChar.hasBlockActions() && Rnd.get(14) == 0 && activeChar.getEffectList().hasAbnormalType(AbnormalType.STUN, info -> info.getTime() <= info.getSkill().getAbnormalTime());
    }
    
    public static boolean calcRealTargetBreak() {
        return Rnd.chance(3);
    }
    
    public static int calculateTimeBetweenAttacks(final int attackSpeed) {
        return Math.max(50, 500000 / attackSpeed);
    }
    
    public static int calculateTimeToHit(final int totalAttackTime, final WeaponType attackType, final boolean twoHanded, final boolean secondHit) {
        switch (attackType) {
            case BOW:
            case CROSSBOW:
            case TWO_HAND_CROSSBOW: {
                return (int)(totalAttackTime * 0.95);
            }
            case DUAL_BLUNT:
            case DUAL_DAGGER:
            case DUAL:
            case FIST: {
                if (secondHit) {
                    return (int)(totalAttackTime * 0.6);
                }
                return (int)(totalAttackTime * 0.2726);
            }
            default: {
                if (twoHanded) {
                    return (int)(totalAttackTime * 0.735);
                }
                return (int)(totalAttackTime * 0.644);
            }
        }
    }
    
    public static int calculateReuseTime(final Creature activeChar, final Weapon weapon) {
        if (weapon == null) {
            return 0;
        }
        final WeaponType defaultAttackType = weapon.getItemType();
        final WeaponType weaponType = activeChar.getTransformation().map(transform -> transform.getBaseAttackType(activeChar, defaultAttackType)).orElse(defaultAttackType);
        int reuse = weapon.getReuseDelay();
        if (reuse == 0 || !weaponType.isRanged()) {
            return 0;
        }
        reuse *= (int)activeChar.getStats().getWeaponReuseModifier();
        final double atkSpd = activeChar.getStats().getPAtkSpd();
        return (int)((500000 + 333 * reuse) / atkSpd);
    }
    
    public static double calculatePvpPveBonus(final Creature attacker, final Creature target, final Skill skill, final boolean crit) {
        if (GameUtils.isPlayable(attacker) && GameUtils.isPlayable(target)) {
            double pvpAttack;
            double pvpDefense;
            if (skill != null) {
                if (skill.isMagic()) {
                    pvpAttack = attacker.getStats().getValue(Stat.PVP_MAGICAL_SKILL_DAMAGE, 1.0);
                    pvpDefense = target.getStats().getValue(Stat.PVP_MAGICAL_SKILL_DEFENCE, 1.0);
                }
                else {
                    pvpAttack = attacker.getStats().getValue(Stat.PVP_PHYSICAL_SKILL_DAMAGE, 1.0);
                    pvpDefense = target.getStats().getValue(Stat.PVP_PHYSICAL_SKILL_DEFENCE, 1.0);
                }
            }
            else {
                pvpAttack = attacker.getStats().getValue(Stat.PVP_PHYSICAL_ATTACK_DAMAGE, 1.0);
                pvpDefense = target.getStats().getValue(Stat.PVP_PHYSICAL_ATTACK_DEFENCE, 1.0);
            }
            return Math.max(0.05, 1.0 + (pvpAttack - pvpDefense));
        }
        if (GameUtils.isAttackable(target) || GameUtils.isAttackable(attacker)) {
            final double pvePenalty = calcPveDamagePenalty(attacker, target, skill, crit);
            double pveAttack;
            double pveDefense;
            double pveRaidDefense;
            double pveRaidAttack;
            if (skill != null) {
                if (skill.isMagic()) {
                    pveAttack = attacker.getStats().getValue(Stat.PVE_MAGICAL_SKILL_DAMAGE, 1.0);
                    pveDefense = target.getStats().getValue(Stat.PVE_MAGICAL_SKILL_DEFENCE, 1.0);
                    pveRaidDefense = (attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_MAGICAL_SKILL_DEFENCE, 1.0) : 1.0);
                    pveRaidAttack = (attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_MAGICAL_SKILL_DAMAGE, 1.0) : 1.0);
                }
                else {
                    pveAttack = attacker.getStats().getValue(Stat.PVE_PHYSICAL_SKILL_DAMAGE, 1.0);
                    pveDefense = target.getStats().getValue(Stat.PVE_PHYSICAL_SKILL_DEFENCE, 1.0);
                    pveRaidAttack = (attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_PHYSICAL_SKILL_DAMAGE, 1.0) : 1.0);
                    pveRaidDefense = (attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_PHYSICAL_SKILL_DEFENCE, 1.0) : 1.0);
                }
            }
            else {
                pveAttack = attacker.getStats().getValue(Stat.PVE_PHYSICAL_ATTACK_DAMAGE, 1.0);
                pveDefense = target.getStats().getValue(Stat.PVE_PHYSICAL_ATTACK_DEFENCE, 1.0);
                pveRaidAttack = (attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_PHYSICAL_ATTACK_DAMAGE, 1.0) : 1.0);
                pveRaidDefense = (attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_PHYSICAL_ATTACK_DEFENCE, 1.0) : 1.0);
            }
            return Math.max(0.05, (1.0 + (pveAttack * pveRaidAttack - pveDefense * pveRaidDefense)) * pvePenalty);
        }
        return 1.0;
    }
    
    public static double calcSpiritElementalDamage(final Creature attacker, final Creature target, final double baseDamage) {
        if (!GameUtils.isPlayer(attacker)) {
            return 0.0;
        }
        final Player attackerPlayer = attacker.getActingPlayer();
        final ElementalType type = ElementalType.of(attackerPlayer.getActiveElementalSpiritType());
        final int elementalDamage = 0;
        if (ElementalType.NONE == type) {
            return elementalDamage;
        }
        final double critRate = attackerPlayer.getElementalSpiritCritRate();
        final boolean isCrit = Math.min(critRate * 10.0, 380.0) > Rnd.get(1000);
        final double critDamage = attackerPlayer.getElementalSpiritCritDamage();
        final double attack = attackerPlayer.getActiveElementalSpiritAttack() - target.getElementalSpiritDefenseOf(type) + Rnd.get(-2, 6);
        if (GameUtils.isPlayer(target)) {
            return calcSpiritElementalPvPDamage(attack, critDamage, isCrit, baseDamage);
        }
        return calcSpiritElementalPvEDamage(type, target.getElementalSpiritType(), attack, critDamage, isCrit, baseDamage);
    }
    
    private static double calcSpiritElementalPvPDamage(final double attack, final double critDamage, final boolean isCrit, final double baseDamage) {
        double damage = Math.min(Math.max(0.0, (attack * 1.3 + baseDamage * 0.03 * attack) / Math.log(Math.max(attack, 5.0))), 2295.0);
        if (isCrit) {
            damage *= 1.0 + (Rnd.get(13, 20) + critDamage) / 100.0;
        }
        return damage;
    }
    
    private static double calcSpiritElementalPvEDamage(final ElementalType attackerType, final ElementalType targetType, final double attack, final double critDamage, final boolean isCrit, final double baseDamage) {
        double damage = Math.abs(attack * 0.8);
        double bonus;
        if (attackerType.isSuperior(targetType)) {
            damage *= 1.3;
            bonus = 1.3;
        }
        else if (targetType == attackerType) {
            bonus = 1.1;
        }
        else {
            damage *= 1.1;
            bonus = 1.1;
        }
        if (isCrit) {
            damage += Math.abs((40.0 + (9.2 + attack * 0.048) * critDamage) * bonus + Rnd.get(-10, 30));
        }
        return (damage + baseDamage) * bonus / Math.log(20.0 + baseDamage + damage);
    }
}
