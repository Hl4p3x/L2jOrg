// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.templates;

import java.util.Collections;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.EnumMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Map;
import org.l2j.gameserver.model.events.ListenersContainer;

public class CreatureTemplate extends ListenersContainer
{
    protected final Map<Stat, Double> _baseValues;
    private WeaponType _baseAttackType;
    private int _collisionRadius;
    private double _fCollisionRadius;
    private int _collisionHeight;
    private double _fCollisionHeight;
    private Race _race;
    
    public CreatureTemplate(final StatsSet set) {
        this._baseValues = new EnumMap<Stat, Double>(Stat.class);
        this.set(set);
    }
    
    public void set(final StatsSet set) {
        this._baseValues.put(Stat.STAT_STR, set.getDouble("baseSTR", 0.0));
        this._baseValues.put(Stat.STAT_CON, set.getDouble("baseCON", 0.0));
        this._baseValues.put(Stat.STAT_DEX, set.getDouble("baseDEX", 0.0));
        this._baseValues.put(Stat.STAT_INT, set.getDouble("baseINT", 0.0));
        this._baseValues.put(Stat.STAT_WIT, set.getDouble("baseWIT", 0.0));
        this._baseValues.put(Stat.STAT_MEN, set.getDouble("baseMEN", 0.0));
        this._baseValues.put(Stat.MAX_HP, set.getDouble("baseHpMax", 0.0));
        this._baseValues.put(Stat.MAX_MP, set.getDouble("baseMpMax", 0.0));
        this._baseValues.put(Stat.MAX_CP, set.getDouble("baseCpMax", 0.0));
        this._baseValues.put(Stat.REGENERATE_HP_RATE, set.getDouble("baseHpReg", 0.0));
        this._baseValues.put(Stat.REGENERATE_MP_RATE, set.getDouble("baseMpReg", 0.0));
        this._baseValues.put(Stat.REGENERATE_CP_RATE, set.getDouble("baseCpReg", 0.0));
        this._baseValues.put(Stat.PHYSICAL_ATTACK, set.getDouble("basePAtk", 0.0));
        this._baseValues.put(Stat.MAGIC_ATTACK, set.getDouble("baseMAtk", 0.0));
        this._baseValues.put(Stat.PHYSICAL_DEFENCE, set.getDouble("basePDef", 0.0));
        this._baseValues.put(Stat.MAGICAL_DEFENCE, set.getDouble("baseMDef", 0.0));
        this._baseValues.put(Stat.PHYSICAL_ATTACK_SPEED, set.getDouble("basePAtkSpd", 300.0));
        this._baseValues.put(Stat.MAGIC_ATTACK_SPEED, set.getDouble("baseMAtkSpd", 333.0));
        this._baseValues.put(Stat.SHIELD_DEFENCE, set.getDouble("baseShldDef", 0.0));
        this._baseValues.put(Stat.PHYSICAL_ATTACK_RANGE, set.getDouble("baseAtkRange", 40.0));
        this._baseValues.put(Stat.RANDOM_DAMAGE, set.getDouble("baseRndDam", 0.0));
        this._baseValues.put(Stat.SHIELD_DEFENCE_RATE, set.getDouble("baseShldRate", 0.0));
        this._baseValues.put(Stat.CRITICAL_RATE, set.getDouble("baseCritRate", 4.0));
        this._baseValues.put(Stat.MAGIC_CRITICAL_RATE, set.getDouble("baseMCritRate", 0.0));
        this._baseValues.put(Stat.BREATH, set.getDouble("baseBreath", 100.0));
        this._baseValues.put(Stat.FIRE_POWER, set.getDouble("baseFire", 0.0));
        this._baseValues.put(Stat.WIND_POWER, set.getDouble("baseWind", 0.0));
        this._baseValues.put(Stat.WATER_POWER, set.getDouble("baseWater", 0.0));
        this._baseValues.put(Stat.EARTH_POWER, set.getDouble("baseEarth", 0.0));
        this._baseValues.put(Stat.HOLY_POWER, set.getDouble("baseHoly", 0.0));
        this._baseValues.put(Stat.DARK_POWER, set.getDouble("baseDark", 0.0));
        this._baseValues.put(Stat.FIRE_RES, set.getDouble("baseFireRes", 0.0));
        this._baseValues.put(Stat.WIND_RES, set.getDouble("baseWindRes", 0.0));
        this._baseValues.put(Stat.WATER_RES, set.getDouble("baseWaterRes", 0.0));
        this._baseValues.put(Stat.EARTH_RES, set.getDouble("baseEarthRes", 0.0));
        this._baseValues.put(Stat.HOLY_RES, set.getDouble("baseHolyRes", 0.0));
        this._baseValues.put(Stat.DARK_RES, set.getDouble("baseDarkRes", 0.0));
        this._baseValues.put(Stat.BASE_ATTRIBUTE_RES, set.getDouble("baseElementRes", 0.0));
        this._fCollisionHeight = set.getDouble("collision_height", 0.0);
        this._fCollisionRadius = set.getDouble("collision_radius", 0.0);
        this._collisionRadius = (int)this._fCollisionRadius;
        this._collisionHeight = (int)this._fCollisionHeight;
        this._baseValues.put(Stat.RUN_SPEED, set.getDouble("baseRunSpd", 120.0));
        this._baseValues.put(Stat.WALK_SPEED, set.getDouble("baseWalkSpd", 50.0));
        this._baseValues.put(Stat.SWIM_RUN_SPEED, set.getDouble("baseSwimRunSpd", 120.0));
        this._baseValues.put(Stat.SWIM_WALK_SPEED, set.getDouble("baseSwimWalkSpd", 50.0));
        this._baseValues.put(Stat.FLY_RUN_SPEED, set.getDouble("baseFlyRunSpd", 120.0));
        this._baseValues.put(Stat.FLY_WALK_SPEED, set.getDouble("baseFlyWalkSpd", 50.0));
        this._baseAttackType = set.getEnum("baseAtkType", WeaponType.class, WeaponType.FIST);
        this._baseValues.put(Stat.ABNORMAL_RESIST_PHYSICAL, set.getDouble("physicalAbnormalResist", 10.0));
        this._baseValues.put(Stat.ABNORMAL_RESIST_MAGICAL, set.getDouble("magicAbnormalResist", 10.0));
    }
    
    public int getBaseSTR() {
        return this._baseValues.getOrDefault(Stat.STAT_STR, 0.0).intValue();
    }
    
    public int getBaseCON() {
        return this._baseValues.getOrDefault(Stat.STAT_CON, 0.0).intValue();
    }
    
    public int getBaseDEX() {
        return this._baseValues.getOrDefault(Stat.STAT_DEX, 0.0).intValue();
    }
    
    public int getBaseINT() {
        return this._baseValues.getOrDefault(Stat.STAT_INT, 0.0).intValue();
    }
    
    public int getBaseWIT() {
        return this._baseValues.getOrDefault(Stat.STAT_WIT, 0.0).intValue();
    }
    
    public int getBaseMEN() {
        return this._baseValues.getOrDefault(Stat.STAT_MEN, 0.0).intValue();
    }
    
    public float getBaseHpMax() {
        return this._baseValues.getOrDefault(Stat.MAX_HP, 0.0).floatValue();
    }
    
    public float getBaseCpMax() {
        return this._baseValues.getOrDefault(Stat.MAX_CP, 0.0).floatValue();
    }
    
    public float getBaseMpMax() {
        return this._baseValues.getOrDefault(Stat.MAX_MP, 0.0).floatValue();
    }
    
    public float getBaseHpReg() {
        return this._baseValues.getOrDefault(Stat.REGENERATE_HP_RATE, 0.0).floatValue();
    }
    
    public float getBaseMpReg() {
        return this._baseValues.getOrDefault(Stat.REGENERATE_MP_RATE, 0.0).floatValue();
    }
    
    public int getBaseFire() {
        return this._baseValues.getOrDefault(Stat.FIRE_POWER, 0.0).intValue();
    }
    
    public int getBaseWind() {
        return this._baseValues.getOrDefault(Stat.WIND_POWER, 0.0).intValue();
    }
    
    public int getBaseWater() {
        return this._baseValues.getOrDefault(Stat.WATER_POWER, 0.0).intValue();
    }
    
    public int getBaseEarth() {
        return this._baseValues.getOrDefault(Stat.EARTH_POWER, 0.0).intValue();
    }
    
    public int getBaseHoly() {
        return this._baseValues.getOrDefault(Stat.HOLY_POWER, 0.0).intValue();
    }
    
    public int getBaseDark() {
        return this._baseValues.getOrDefault(Stat.DARK_POWER, 0.0).intValue();
    }
    
    public double getBaseFireRes() {
        return this._baseValues.getOrDefault(Stat.FIRE_RES, 0.0);
    }
    
    public double getBaseWindRes() {
        return this._baseValues.getOrDefault(Stat.WIND_RES, 0.0);
    }
    
    public double getBaseWaterRes() {
        return this._baseValues.getOrDefault(Stat.WATER_RES, 0.0);
    }
    
    public double getBaseEarthRes() {
        return this._baseValues.getOrDefault(Stat.EARTH_RES, 0.0);
    }
    
    public double getBaseHolyRes() {
        return this._baseValues.getOrDefault(Stat.HOLY_RES, 0.0);
    }
    
    public double getBaseDarkRes() {
        return this._baseValues.getOrDefault(Stat.DARK_RES, 0.0);
    }
    
    public double getBaseElementRes() {
        return this._baseValues.getOrDefault(Stat.BASE_ATTRIBUTE_RES, 0.0);
    }
    
    public int getBasePAtk() {
        return this._baseValues.getOrDefault(Stat.PHYSICAL_ATTACK, 0.0).intValue();
    }
    
    public int getBaseMAtk() {
        return this._baseValues.getOrDefault(Stat.MAGIC_ATTACK, 0.0).intValue();
    }
    
    public int getBasePDef() {
        return this._baseValues.getOrDefault(Stat.PHYSICAL_DEFENCE, 0.0).intValue();
    }
    
    public int getBaseMDef() {
        return this._baseValues.getOrDefault(Stat.MAGICAL_DEFENCE, 0.0).intValue();
    }
    
    public int getBasePAtkSpd() {
        return this._baseValues.getOrDefault(Stat.PHYSICAL_ATTACK_SPEED, 0.0).intValue();
    }
    
    public int getBaseMAtkSpd() {
        return this._baseValues.getOrDefault(Stat.MAGIC_ATTACK_SPEED, 0.0).intValue();
    }
    
    public int getRandomDamage() {
        return this._baseValues.getOrDefault(Stat.RANDOM_DAMAGE, 0.0).intValue();
    }
    
    public int getBaseShldDef() {
        return this._baseValues.getOrDefault(Stat.SHIELD_DEFENCE, 0.0).intValue();
    }
    
    public int getBaseShldRate() {
        return this._baseValues.getOrDefault(Stat.SHIELD_DEFENCE_RATE, 0.0).intValue();
    }
    
    public int getBaseCritRate() {
        return this._baseValues.getOrDefault(Stat.CRITICAL_RATE, 0.0).intValue();
    }
    
    public int getBaseMCritRate() {
        return this._baseValues.getOrDefault(Stat.MAGIC_CRITICAL_RATE, 0.0).intValue();
    }
    
    public int getBaseBreath() {
        return this._baseValues.getOrDefault(Stat.BREATH, 0.0).intValue();
    }
    
    public int getBaseAbnormalResistPhysical() {
        return this._baseValues.getOrDefault(Stat.ABNORMAL_RESIST_PHYSICAL, 0.0).intValue();
    }
    
    public int getBaseAbnormalResistMagical() {
        return this._baseValues.getOrDefault(Stat.ABNORMAL_RESIST_MAGICAL, 0.0).intValue();
    }
    
    public int getCollisionRadius() {
        return this._collisionRadius;
    }
    
    public int getCollisionHeight() {
        return this._collisionHeight;
    }
    
    public double getfCollisionRadius() {
        return this._fCollisionRadius;
    }
    
    public double getfCollisionHeight() {
        return this._fCollisionHeight;
    }
    
    public WeaponType getBaseAttackType() {
        return this._baseAttackType;
    }
    
    public void setBaseAttackType(final WeaponType type) {
        this._baseAttackType = type;
    }
    
    public int getBaseAttackRange() {
        return this._baseValues.getOrDefault(Stat.PHYSICAL_ATTACK_RANGE, 0.0).intValue();
    }
    
    public Map<Integer, Skill> getSkills() {
        return Collections.emptyMap();
    }
    
    public Race getRace() {
        return this._race;
    }
    
    public void setRace(final Race race) {
        this._race = race;
    }
    
    public double getBaseValue(final Stat stat, final double defaultValue) {
        return this._baseValues.getOrDefault(stat, defaultValue);
    }
}
