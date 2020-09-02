// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.model.stats.finalizers.ShotsBonusFinalizer;
import org.l2j.gameserver.model.stats.finalizers.RandomDamageFinalizer;
import org.l2j.gameserver.model.stats.finalizers.VampiricChanceFinalizer;
import org.l2j.gameserver.model.stats.finalizers.AttributeFinalizer;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.stats.finalizers.BaseStatsFinalizer;
import org.l2j.gameserver.model.stats.finalizers.SpeedFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PRangeFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MEvasionRateFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PEvasionRateFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MAccuracyFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PAccuracyFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MCritRateFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PCriticalRateFinalizer;
import org.l2j.gameserver.model.stats.finalizers.ShieldDefenceRateFinalizer;
import org.l2j.gameserver.model.stats.finalizers.ShieldDefenceFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MAttackSpeedFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PAttackSpeedFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MAttackFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PAttackFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MDefenseFinalizer;
import org.l2j.gameserver.model.stats.finalizers.PDefenseFinalizer;
import org.l2j.gameserver.model.stats.finalizers.RegenMPFinalizer;
import org.l2j.gameserver.model.stats.finalizers.RegenCPFinalizer;
import org.l2j.gameserver.model.stats.finalizers.RegenHPFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MaxCpFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MaxMpFinalizer;
import org.l2j.gameserver.model.stats.finalizers.MaxHpFinalizer;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import java.util.stream.Stream;
import org.l2j.gameserver.util.MathUtil;
import java.util.function.BiFunction;
import java.util.EnumSet;

public enum Stat
{
    MAX_HP((IStatsFunction)new MaxHpFinalizer()), 
    MAX_MP((IStatsFunction)new MaxMpFinalizer()), 
    MAX_CP((IStatsFunction)new MaxCpFinalizer()), 
    MAX_RECOVERABLE_HP, 
    MAX_RECOVERABLE_MP, 
    MAX_RECOVERABLE_CP, 
    REGENERATE_HP_RATE((IStatsFunction)new RegenHPFinalizer()), 
    REGENERATE_CP_RATE((IStatsFunction)new RegenCPFinalizer()), 
    REGENERATE_MP_RATE((IStatsFunction)new RegenMPFinalizer()), 
    ADDITIONAL_POTION_HP, 
    ADDITIONAL_POTION_MP, 
    ADDITIONAL_POTION_CP, 
    MANA_CHARGE, 
    HEAL_RECEIVE, 
    HEAL_POWER, 
    PHYSICAL_DEFENCE((IStatsFunction)new PDefenseFinalizer()), 
    MAGICAL_DEFENCE((IStatsFunction)new MDefenseFinalizer()), 
    PHYSICAL_ATTACK((IStatsFunction)new PAttackFinalizer()), 
    MAGIC_ATTACK((IStatsFunction)new MAttackFinalizer()), 
    PHYSICAL_ATTACK_SPEED((IStatsFunction)new PAttackSpeedFinalizer()), 
    MAGIC_ATTACK_SPEED((IStatsFunction)new MAttackSpeedFinalizer()), 
    ATK_REUSE, 
    SHIELD_DEFENCE((IStatsFunction)new ShieldDefenceFinalizer()), 
    CRITICAL_DAMAGE, 
    CRITICAL_DAMAGE_ADD, 
    HATE_ATTACK, 
    REAR_DAMAGE_RATE, 
    DAMAGE_IMMOBILIZED, 
    DAMAGE_TAKEN_IMMOBILIZED, 
    PVP_PHYSICAL_ATTACK_DAMAGE, 
    PVP_MAGICAL_SKILL_DAMAGE, 
    PVP_PHYSICAL_SKILL_DAMAGE, 
    PVP_PHYSICAL_ATTACK_DEFENCE, 
    PVP_MAGICAL_SKILL_DEFENCE, 
    PVP_PHYSICAL_SKILL_DEFENCE, 
    PVE_PHYSICAL_ATTACK_DAMAGE, 
    PVE_PHYSICAL_SKILL_DAMAGE, 
    PVE_MAGICAL_SKILL_DAMAGE, 
    PVE_PHYSICAL_ATTACK_DEFENCE, 
    PVE_PHYSICAL_SKILL_DEFENCE, 
    PVE_MAGICAL_SKILL_DEFENCE, 
    PVE_RAID_PHYSICAL_ATTACK_DEFENCE, 
    PVE_RAID_PHYSICAL_SKILL_DEFENCE, 
    PVE_RAID_MAGICAL_SKILL_DEFENCE, 
    PVE_RAID_PHYSICAL_ATTACK_DAMAGE, 
    PVE_RAID_PHYSICAL_SKILL_DAMAGE, 
    PVE_RAID_MAGICAL_SKILL_DAMAGE, 
    PVP_DAMAGE_TAKEN, 
    PVE_DAMAGE_TAKEN, 
    PVE_DAMAGE_TAKEN_MONSTER, 
    PVE_DAMAGE_TAKEN_RAID, 
    DAMAGE_TAKEN, 
    MAGIC_CRITICAL_DAMAGE, 
    PHYSICAL_SKILL_POWER, 
    MAGICAL_SKILL_POWER, 
    CRITICAL_DAMAGE_SKILL, 
    CRITICAL_DAMAGE_SKILL_ADD, 
    MAGIC_CRITICAL_DAMAGE_ADD, 
    SHIELD_DEFENCE_RATE((IStatsFunction)new ShieldDefenceRateFinalizer()), 
    CRITICAL_RATE((IStatsFunction)new PCriticalRateFinalizer()), 
    CRITICAL_RATE_SKILL, 
    MAGIC_CRITICAL_RATE((IStatsFunction)new MCritRateFinalizer()), 
    BLOW_RATE, 
    DEFENCE_CRITICAL_RATE, 
    DEFENCE_CRITICAL_RATE_ADD, 
    DEFENCE_MAGIC_CRITICAL_RATE, 
    DEFENCE_MAGIC_CRITICAL_RATE_ADD, 
    DEFENCE_CRITICAL_DAMAGE, 
    DEFENCE_MAGIC_CRITICAL_DAMAGE, 
    DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD, 
    DEFENCE_CRITICAL_DAMAGE_ADD, 
    DEFENCE_CRITICAL_DAMAGE_SKILL, 
    DEFENCE_CRITICAL_DAMAGE_SKILL_ADD, 
    INSTANT_KILL_RESIST, 
    EXPSP_RATE, 
    BONUS_EXP, 
    BONUS_SP, 
    BONUS_DROP_AMOUNT, 
    BONUS_DROP_RATE, 
    BONUS_SPOIL_RATE, 
    ATTACK_CANCEL, 
    BONUS_L2COIN_DROP_RATE, 
    ACCURACY((IStatsFunction)new PAccuracyFinalizer()), 
    ACCURACY_MAGIC((IStatsFunction)new MAccuracyFinalizer()), 
    EVASION_RATE((IStatsFunction)new PEvasionRateFinalizer()), 
    MAGIC_EVASION_RATE((IStatsFunction)new MEvasionRateFinalizer()), 
    PHYSICAL_ATTACK_RANGE((IStatsFunction)new PRangeFinalizer()), 
    MAGIC_ATTACK_RANGE, 
    ATTACK_COUNT_MAX, 
    PHYSICAL_POLEARM_TARGET_SINGLE, 
    HIT_AT_NIGHT, 
    SPEED, 
    RUN_SPEED((IStatsFunction)new SpeedFinalizer()), 
    WALK_SPEED((IStatsFunction)new SpeedFinalizer()), 
    SWIM_RUN_SPEED((IStatsFunction)new SpeedFinalizer()), 
    SWIM_WALK_SPEED((IStatsFunction)new SpeedFinalizer()), 
    FLY_RUN_SPEED((IStatsFunction)new SpeedFinalizer()), 
    FLY_WALK_SPEED((IStatsFunction)new SpeedFinalizer()), 
    STAT_STR((IStatsFunction)new BaseStatsFinalizer()), 
    STAT_CON((IStatsFunction)new BaseStatsFinalizer()), 
    STAT_DEX((IStatsFunction)new BaseStatsFinalizer()), 
    STAT_INT((IStatsFunction)new BaseStatsFinalizer()), 
    STAT_WIT((IStatsFunction)new BaseStatsFinalizer()), 
    STAT_MEN((IStatsFunction)new BaseStatsFinalizer()), 
    BREATH, 
    FALL, 
    FISHING_EXP_SP_BONUS, 
    DAMAGE_ZONE_VULN, 
    RESIST_DISPEL_BUFF, 
    RESIST_ABNORMAL_DEBUFF, 
    FIRE_RES((IStatsFunction)new AttributeFinalizer(AttributeType.FIRE, false)), 
    WIND_RES((IStatsFunction)new AttributeFinalizer(AttributeType.WIND, false)), 
    WATER_RES((IStatsFunction)new AttributeFinalizer(AttributeType.WATER, false)), 
    EARTH_RES((IStatsFunction)new AttributeFinalizer(AttributeType.EARTH, false)), 
    HOLY_RES((IStatsFunction)new AttributeFinalizer(AttributeType.HOLY, false)), 
    DARK_RES((IStatsFunction)new AttributeFinalizer(AttributeType.DARK, false)), 
    BASE_ATTRIBUTE_RES, 
    MAGIC_SUCCESS_RES, 
    ABNORMAL_RESIST_PHYSICAL, 
    ABNORMAL_RESIST_MAGICAL, 
    REAL_DAMAGE_RESIST, 
    FIRE_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.FIRE, true)), 
    WATER_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.WATER, true)), 
    WIND_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.WIND, true)), 
    EARTH_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.EARTH, true)), 
    HOLY_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.HOLY, true)), 
    DARK_POWER((IStatsFunction)new AttributeFinalizer(AttributeType.DARK, true)), 
    REFLECT_DAMAGE_PERCENT, 
    REFLECT_DAMAGE_PERCENT_DEFENSE, 
    REFLECT_SKILL_MAGIC, 
    REFLECT_SKILL_PHYSIC, 
    VENGEANCE_SKILL_MAGIC_DAMAGE, 
    VENGEANCE_SKILL_PHYSICAL_DAMAGE, 
    ABSORB_DAMAGE_PERCENT, 
    ABSORB_DAMAGE_CHANCE((IStatsFunction)new VampiricChanceFinalizer()), 
    ABSORB_DAMAGE_DEFENCE, 
    TRANSFER_DAMAGE_SUMMON_PERCENT, 
    MANA_SHIELD_PERCENT, 
    TRANSFER_DAMAGE_TO_PLAYER, 
    ABSORB_MANA_DAMAGE_PERCENT, 
    WEIGHT_LIMIT, 
    WEIGHT_PENALTY, 
    INVENTORY_NORMAL, 
    STORAGE_PRIVATE, 
    TRADE_SELL, 
    TRADE_BUY, 
    RECIPE_DWARVEN, 
    RECIPE_COMMON, 
    CRAFT_RATE_MASTER, 
    CRAFT_RATE_CRITICAL, 
    SKILL_CRITICAL, 
    SKILL_CRITICAL_PROBABILITY, 
    VITALITY_CONSUME_RATE, 
    VITALITY_EXP_RATE, 
    MAX_SOULS, 
    REDUCE_EXP_LOST_BY_PVP, 
    REDUCE_EXP_LOST_BY_MOB, 
    REDUCE_EXP_LOST_BY_RAID, 
    REDUCE_DEATH_PENALTY_BY_PVP, 
    REDUCE_DEATH_PENALTY_BY_MOB, 
    REDUCE_DEATH_PENALTY_BY_RAID, 
    BROOCH_JEWELS, 
    AGATHION_SLOTS, 
    ARTIFACT_SLOTS, 
    MAX_SUMMON_POINTS, 
    MAX_CUBIC, 
    SPHERIC_BARRIER_RANGE, 
    DEBUFF_BLOCK, 
    RANDOM_DAMAGE((IStatsFunction)new RandomDamageFinalizer()), 
    DAMAGE_LIMIT, 
    MAX_MOMENTUM, 
    STAT_BONUS_SKILL_CRITICAL, 
    STAT_BONUS_SPEED, 
    SOUL_SHOTS_BONUS((IStatsFunction)new ShotsBonusFinalizer()), 
    SPIRIT_SHOTS_BONUS((IStatsFunction)new ShotsBonusFinalizer()), 
    WORLD_CHAT_POINTS, 
    ENCHANT_RATE_BONUS, 
    ATTACK_DAMAGE, 
    ELEMENTAL_SPIRIT_BONUS_XP, 
    ELEMENTAL_SPIRIT_FIRE_ATTACK, 
    ELEMENTAL_SPIRIT_FIRE_DEFENSE, 
    ELEMENTAL_SPIRIT_WATER_ATTACK, 
    ELEMENTAL_SPIRIT_WATER_DEFENSE, 
    ELEMENTAL_SPIRIT_WIND_ATTACK, 
    ELEMENTAL_SPIRIT_WIND_DEFENSE, 
    ELEMENTAL_SPIRIT_EARTH_ATTACK, 
    ELEMENTAL_SPIRIT_EARTH_DEFENSE, 
    ELEMENTAL_SPIRIT_CRITICAL_RATE, 
    ELEMENTAL_SPIRIT_CRITICAL_DAMAGE;
    
    private static final EnumSet<Stat> CACHE;
    private final IStatsFunction _valueFinalizer;
    private final BiFunction<Double, Double, Double> _addFunction;
    private final BiFunction<Double, Double, Double> _mulFunction;
    private boolean hasDefaultFinalizer;
    
    private Stat() {
        this(Stat::defaultValue, MathUtil::add, MathUtil::add);
        this.hasDefaultFinalizer = true;
    }
    
    private Stat(final IStatsFunction valueFinalizer) {
        this(valueFinalizer, MathUtil::add, MathUtil::add);
    }
    
    private Stat(final IStatsFunction valueFinalizer, final BiFunction<Double, Double, Double> addFunction, final BiFunction<Double, Double, Double> mulFunction) {
        this._valueFinalizer = valueFinalizer;
        this._addFunction = addFunction;
        this._mulFunction = mulFunction;
    }
    
    public static Stream<Stat> stream() {
        return Stat.CACHE.stream();
    }
    
    public static double weaponBaseValue(final Creature creature, final Stat stat) {
        return stat._valueFinalizer.calcWeaponBaseValue(creature, stat);
    }
    
    public static double defaultValue(final Creature creature, final Optional<Double> base, final Stat stat) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return base.map(aDouble -> defaultValue(creature, stat, aDouble)).orElseGet(() -> mul * (add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType())));
    }
    
    public static double defaultValue(final Creature creature, final Stat stat, final double baseValue) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return baseValue * mul + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }
    
    public Double finalize(final Creature creature, final Optional<Double> baseValue) {
        try {
            return this._valueFinalizer.calc(creature, baseValue, this);
        }
        catch (Exception e) {
            return defaultValue(creature, baseValue, this);
        }
    }
    
    public double functionAdd(final double oldValue, final double value) {
        return this._addFunction.apply(oldValue, value);
    }
    
    public double functionMul(final double oldValue, final double value) {
        return this._mulFunction.apply(oldValue, value);
    }
    
    public boolean hasDefaultFinalizer() {
        return this.hasDefaultFinalizer;
    }
    
    static {
        CACHE = EnumSet.allOf(Stat.class);
    }
}
