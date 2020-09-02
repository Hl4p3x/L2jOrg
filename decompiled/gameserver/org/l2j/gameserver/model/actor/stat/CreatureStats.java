// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.Collection;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.EffectList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.util.GameUtils;
import java.util.stream.Stream;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.Config;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.EnumMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.l2j.gameserver.model.stats.TraitType;
import java.util.Set;
import org.l2j.gameserver.model.stats.StatsHolder;
import java.util.Deque;
import org.l2j.gameserver.enums.Position;
import java.util.Stack;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Map;
import org.l2j.gameserver.model.actor.Creature;

public class CreatureStats
{
    private final Creature creature;
    private final Map<Stat, Double> statsAdd;
    private final Map<Stat, Double> statsMul;
    private final Map<Stat, Map<MoveType, Double>> _moveTypeStats;
    private final Map<SkillType, Double> reuseStat;
    private final Map<SkillType, Double> mpConsumeStat;
    private final Map<SkillType, Stack<Double>> skillEvasionStat;
    private final Map<Stat, Map<Position, Double>> _positionStats;
    private final Deque<StatsHolder> _additionalAdd;
    private final Deque<StatsHolder> _additionalMul;
    private final Map<Stat, Double> _fixedValue;
    private final float[] _attackTraitValues;
    private final float[] _defenceTraitValues;
    private final Set<TraitType> _attackTraits;
    private final Set<TraitType> _defenceTraits;
    private final Set<TraitType> _invulnerableTraits;
    private final ReentrantReadWriteLock _lock;
    private long _exp;
    private long _sp;
    private byte _level;
    private int _maxBuffCount;
    private double _vampiricSum;
    private double _attackSpeedMultiplier;
    private double _mAttackSpeedMultiplier;
    
    public CreatureStats(final Creature activeChar) {
        this.statsAdd = new EnumMap<Stat, Double>(Stat.class);
        this.statsMul = new EnumMap<Stat, Double>(Stat.class);
        this._moveTypeStats = new ConcurrentHashMap<Stat, Map<MoveType, Double>>();
        this.reuseStat = Collections.synchronizedMap(new EnumMap<SkillType, Double>(SkillType.class));
        this.mpConsumeStat = Collections.synchronizedMap(new EnumMap<SkillType, Double>(SkillType.class));
        this.skillEvasionStat = Collections.synchronizedMap(new EnumMap<SkillType, Stack<Double>>(SkillType.class));
        this._positionStats = new ConcurrentHashMap<Stat, Map<Position, Double>>();
        this._additionalAdd = new ConcurrentLinkedDeque<StatsHolder>();
        this._additionalMul = new ConcurrentLinkedDeque<StatsHolder>();
        this._fixedValue = new ConcurrentHashMap<Stat, Double>();
        this._attackTraitValues = new float[TraitType.values().length];
        this._defenceTraitValues = new float[TraitType.values().length];
        this._attackTraits = EnumSet.noneOf(TraitType.class);
        this._defenceTraits = EnumSet.noneOf(TraitType.class);
        this._invulnerableTraits = EnumSet.noneOf(TraitType.class);
        this._lock = new ReentrantReadWriteLock();
        this._exp = 0L;
        this._sp = 0L;
        this._level = 1;
        this._maxBuffCount = Config.BUFFS_MAX_AMOUNT;
        this._vampiricSum = 0.0;
        this._attackSpeedMultiplier = 1.0;
        this._mAttackSpeedMultiplier = 1.0;
        this.creature = activeChar;
        for (int i = 0; i < TraitType.values().length; ++i) {
            this._attackTraitValues[i] = 1.0f;
            this._defenceTraitValues[i] = 0.0f;
        }
    }
    
    public int getAccuracy() {
        return (int)this.getValue(Stat.ACCURACY);
    }
    
    public int getMagicAccuracy() {
        return (int)this.getValue(Stat.ACCURACY_MAGIC);
    }
    
    public Creature getCreature() {
        return this.creature;
    }
    
    public final double getAttackSpeedMultiplier() {
        return this._attackSpeedMultiplier;
    }
    
    public final double getMAttackSpeedMultiplier() {
        return this._mAttackSpeedMultiplier;
    }
    
    public final int getCON() {
        return (int)this.getValue(Stat.STAT_CON);
    }
    
    public final double getCriticalDmg(final double init) {
        return this.getValue(Stat.CRITICAL_DAMAGE, init);
    }
    
    public int getCriticalHit() {
        return (int)this.getValue(Stat.CRITICAL_RATE);
    }
    
    public final int getDEX() {
        return (int)this.getValue(Stat.STAT_DEX);
    }
    
    public int getEvasionRate() {
        return (int)this.getValue(Stat.EVASION_RATE);
    }
    
    public int getMagicEvasionRate() {
        return (int)this.getValue(Stat.MAGIC_EVASION_RATE);
    }
    
    public long getExp() {
        return this._exp;
    }
    
    public void setExp(final long value) {
        this._exp = value;
    }
    
    public int getINT() {
        return (int)this.getValue(Stat.STAT_INT);
    }
    
    public byte getLevel() {
        return this._level;
    }
    
    public void setLevel(final byte value) {
        this._level = value;
    }
    
    public final int getMagicalAttackRange(final Skill skill) {
        if (skill != null) {
            return (int)this.getValue(Stat.MAGIC_ATTACK_RANGE, skill.getCastRange());
        }
        return this.creature.getTemplate().getBaseAttackRange();
    }
    
    public int getMaxCp() {
        return (int)this.getValue(Stat.MAX_CP);
    }
    
    public int getMaxRecoverableCp() {
        return (int)this.getValue(Stat.MAX_RECOVERABLE_CP, this.getMaxCp());
    }
    
    public int getMaxHp() {
        return (int)this.getValue(Stat.MAX_HP);
    }
    
    public int getMaxRecoverableHp() {
        return (int)this.getValue(Stat.MAX_RECOVERABLE_HP, this.getMaxHp());
    }
    
    public int getMaxMp() {
        return (int)this.getValue(Stat.MAX_MP);
    }
    
    public int getMaxRecoverableMp() {
        return (int)this.getValue(Stat.MAX_RECOVERABLE_MP, this.getMaxMp());
    }
    
    public int getMAtk() {
        return (int)this.getValue(Stat.MAGIC_ATTACK);
    }
    
    public int getMAtkSpd() {
        return (int)this.getValue(Stat.MAGIC_ATTACK_SPEED);
    }
    
    public final int getMCriticalHit() {
        return (int)this.getValue(Stat.MAGIC_CRITICAL_RATE);
    }
    
    public int getMDef() {
        return (int)this.getValue(Stat.MAGICAL_DEFENCE);
    }
    
    public final int getMEN() {
        return (int)this.getValue(Stat.STAT_MEN);
    }
    
    public double getMovementSpeedMultiplier() {
        double baseSpeed;
        if (this.creature.isInsideZone(ZoneType.WATER)) {
            baseSpeed = this.creature.getTemplate().getBaseValue(this.creature.isRunning() ? Stat.SWIM_RUN_SPEED : Stat.SWIM_WALK_SPEED, 1.0);
        }
        else {
            baseSpeed = this.creature.getTemplate().getBaseValue(this.creature.isRunning() ? Stat.RUN_SPEED : Stat.WALK_SPEED, 1.0);
        }
        return this.getMoveSpeed() * (1.0 / baseSpeed);
    }
    
    public double getRunSpeed() {
        return this.getValue(this.creature.isInsideZone(ZoneType.WATER) ? Stat.SWIM_RUN_SPEED : Stat.RUN_SPEED);
    }
    
    public double getWalkSpeed() {
        return this.getValue(this.creature.isInsideZone(ZoneType.WATER) ? Stat.SWIM_WALK_SPEED : Stat.WALK_SPEED);
    }
    
    public double getSwimRunSpeed() {
        return this.getValue(Stat.SWIM_RUN_SPEED);
    }
    
    public double getSwimWalkSpeed() {
        return this.getValue(Stat.SWIM_WALK_SPEED);
    }
    
    public double getMoveSpeed() {
        if (this.creature.isInsideZone(ZoneType.WATER)) {
            return this.creature.isRunning() ? this.getSwimRunSpeed() : this.getSwimWalkSpeed();
        }
        return this.creature.isRunning() ? this.getRunSpeed() : this.getWalkSpeed();
    }
    
    public int getPAtk() {
        return (int)this.getValue(Stat.PHYSICAL_ATTACK);
    }
    
    public int getPAtkSpd() {
        return (int)this.getValue(Stat.PHYSICAL_ATTACK_SPEED);
    }
    
    public int getPDef() {
        return (int)this.getValue(Stat.PHYSICAL_DEFENCE);
    }
    
    public final int getPhysicalAttackRange() {
        return (int)this.getValue(Stat.PHYSICAL_ATTACK_RANGE);
    }
    
    public int getPhysicalAttackRadius() {
        return 40;
    }
    
    public int getPhysicalAttackAngle() {
        return 240;
    }
    
    public final double getWeaponReuseModifier() {
        return this.getValue(Stat.ATK_REUSE, 1.0);
    }
    
    public final int getShldDef() {
        return (int)this.getValue(Stat.SHIELD_DEFENCE);
    }
    
    public long getSp() {
        return this._sp;
    }
    
    public void setSp(final long value) {
        this._sp = value;
    }
    
    public final int getSTR() {
        return (int)this.getValue(Stat.STAT_STR);
    }
    
    public final int getWIT() {
        return (int)this.getValue(Stat.STAT_WIT);
    }
    
    public final int getMpConsume(final Skill skill) {
        if (skill == null) {
            return 1;
        }
        double mpConsume = skill.getMpConsume();
        final double nextDanceMpCost = Math.ceil(skill.getMpConsume() / 2.0);
        if (skill.isDance() && Config.DANCE_CONSUME_ADDITIONAL_MP && this.creature != null && this.creature.getDanceCount() > 0) {
            mpConsume += this.creature.getDanceCount() * nextDanceMpCost;
        }
        return (int)(mpConsume * this.getMpConsumeTypeValue(skill.getSkillType()));
    }
    
    public final int getMpInitialConsume(final Skill skill) {
        if (skill == null) {
            return 1;
        }
        return skill.getMpInitialConsume();
    }
    
    public AttributeType getAttackElement() {
        final Item weaponInstance = this.creature.getActiveWeaponInstance();
        if (weaponInstance != null && weaponInstance.getAttackAttributeType() != AttributeType.NONE) {
            return weaponInstance.getAttackAttributeType();
        }
        int tempVal = 0;
        final int[] stats = { this.getAttackElementValue(AttributeType.FIRE), this.getAttackElementValue(AttributeType.WATER), this.getAttackElementValue(AttributeType.WIND), this.getAttackElementValue(AttributeType.EARTH), this.getAttackElementValue(AttributeType.HOLY), this.getAttackElementValue(AttributeType.DARK) };
        AttributeType returnVal = AttributeType.NONE;
        for (byte x = 0; x < stats.length; ++x) {
            if (stats[x] > tempVal) {
                returnVal = AttributeType.findByClientId(x);
                tempVal = stats[x];
            }
        }
        return returnVal;
    }
    
    public int getAttackElementValue(final AttributeType attackAttribute) {
        switch (attackAttribute) {
            case FIRE: {
                return (int)this.getValue(Stat.FIRE_POWER);
            }
            case WATER: {
                return (int)this.getValue(Stat.WATER_POWER);
            }
            case WIND: {
                return (int)this.getValue(Stat.WIND_POWER);
            }
            case EARTH: {
                return (int)this.getValue(Stat.EARTH_POWER);
            }
            case HOLY: {
                return (int)this.getValue(Stat.HOLY_POWER);
            }
            case DARK: {
                return (int)this.getValue(Stat.DARK_POWER);
            }
            default: {
                return 0;
            }
        }
    }
    
    public int getDefenseElementValue(final AttributeType defenseAttribute) {
        switch (defenseAttribute) {
            case FIRE: {
                return (int)this.getValue(Stat.FIRE_RES);
            }
            case WATER: {
                return (int)this.getValue(Stat.WATER_RES);
            }
            case WIND: {
                return (int)this.getValue(Stat.WIND_RES);
            }
            case EARTH: {
                return (int)this.getValue(Stat.EARTH_RES);
            }
            case HOLY: {
                return (int)this.getValue(Stat.HOLY_RES);
            }
            case DARK: {
                return (int)this.getValue(Stat.DARK_RES);
            }
            default: {
                return (int)this.getValue(Stat.BASE_ATTRIBUTE_RES);
            }
        }
    }
    
    public void mergeAttackTrait(final TraitType traitType, final float value) {
        this._lock.readLock().lock();
        try {
            final float[] attackTraitValues = this._attackTraitValues;
            final int ordinal = traitType.ordinal();
            attackTraitValues[ordinal] += value;
            this._attackTraits.add(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public void removeAttackTrait(final TraitType traitType, final float value) {
        this._lock.readLock().lock();
        try {
            final float[] attackTraitValues = this._attackTraitValues;
            final int ordinal = traitType.ordinal();
            attackTraitValues[ordinal] -= value;
            if (this._attackTraitValues[traitType.ordinal()] == 1.0f) {
                this._attackTraits.remove(traitType);
            }
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public float getAttackTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            return this._attackTraitValues[traitType.ordinal()];
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public boolean hasAttackTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            return this._attackTraits.contains(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public void mergeDefenceTrait(final TraitType traitType, final float value) {
        this._lock.readLock().lock();
        try {
            final float[] defenceTraitValues = this._defenceTraitValues;
            final int ordinal = traitType.ordinal();
            defenceTraitValues[ordinal] += value;
            this._defenceTraits.add(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public void removeDefenceTrait(final TraitType traitType, final float value) {
        this._lock.readLock().lock();
        try {
            final float[] defenceTraitValues = this._defenceTraitValues;
            final int ordinal = traitType.ordinal();
            defenceTraitValues[ordinal] -= value;
            if (this._defenceTraitValues[traitType.ordinal()] == 0.0f) {
                this._defenceTraits.remove(traitType);
            }
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public float getDefenceTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            return this._defenceTraitValues[traitType.ordinal()];
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public boolean hasDefenceTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            return this._defenceTraits.contains(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public void mergeInvulnerableTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            this._invulnerableTraits.add(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public void removeInvulnerableTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            this._invulnerableTraits.remove(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public boolean isInvulnerableTrait(final TraitType traitType) {
        this._lock.readLock().lock();
        try {
            return this._invulnerableTraits.contains(traitType);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public int getMaxBuffCount() {
        return this._maxBuffCount;
    }
    
    public void setMaxBuffCount(final int buffCount) {
        this._maxBuffCount = buffCount;
    }
    
    public void mergeAdd(final Stat stat, final double val) {
        final Map<Stat, Double> statsAdd = this.statsAdd;
        final Double value = val;
        Objects.requireNonNull(stat);
        statsAdd.merge(stat, value, stat::functionAdd);
    }
    
    public void mergeMul(final Stat stat, final double val) {
        final Map<Stat, Double> statsMul = this.statsMul;
        final Double value = val;
        Objects.requireNonNull(stat);
        statsMul.merge(stat, value, stat::functionMul);
    }
    
    public double getAdd(final Stat stat) {
        return this.getAdd(stat, 0.0);
    }
    
    public double getAdd(final Stat stat, final double defaultValue) {
        this._lock.readLock().lock();
        try {
            return this.statsAdd.getOrDefault(stat, defaultValue);
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public double getMul(final Stat stat) {
        return this.getMul(stat, 1.0);
    }
    
    public double getMul(final Stat stat, final double defaultValue) {
        this._lock.readLock().lock();
        try {
            if (this.statsMul.containsKey(stat)) {
                return this.statsMul.get(stat) / 100.0 + 1.0;
            }
            return defaultValue;
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public double getValue(final Stat stat, final double baseValue) {
        final Double fixedValue = this._fixedValue.get(stat);
        return (fixedValue != null) ? fixedValue : stat.finalize(this.creature, Optional.of(baseValue));
    }
    
    public double getValue(final Stat stat) {
        final Double fixedValue = this._fixedValue.get(stat);
        return (fixedValue != null) ? fixedValue : stat.finalize(this.creature, Optional.empty());
    }
    
    protected void resetStats() {
        this.statsAdd.clear();
        this.statsMul.clear();
        this._vampiricSum = 0.0;
    }
    
    public final void recalculateStats(final boolean broadcast) {
        final Map<Stat, Double> oldAdds = broadcast ? new EnumMap<Stat, Double>(this.statsAdd) : Collections.emptyMap();
        final Map<Stat, Double> oldMuls = broadcast ? new EnumMap<Stat, Double>(this.statsMul) : Collections.emptyMap();
        this._lock.writeLock().lock();
        try {
            this.resetStats();
            final EffectList effectList = this.creature.getEffectList();
            final Stream<BuffInfo> passives = effectList.getPassives().stream().filter(BuffInfo::isInUse).filter(info -> info.getSkill().checkConditions(SkillConditionScope.PASSIVE, this.creature, this.creature));
            final Stream<BuffInfo> options = effectList.getOptions().stream().filter(BuffInfo::isInUse);
            final Stream<BuffInfo> effectsStream = Stream.concat((Stream<? extends BuffInfo>)effectList.getEffects().stream().filter(BuffInfo::isInUse), Stream.concat((Stream<? extends BuffInfo>)passives, (Stream<? extends BuffInfo>)options));
            effectsStream.forEach(info -> info.getEffects().stream().filter(effect -> this.canActivate(info, effect)).forEach(effect -> effect.pump(info.getEffected(), info.getSkill())));
            if (GameUtils.isSummon(this.creature) && Util.falseIfNullOrElse((Object)this.creature.getActingPlayer(), player -> player.hasAbnormalType(AbnormalType.ABILITY_CHANGE))) {
                this.creature.getActingPlayer().getEffectList().getEffects().stream().filter(BuffInfo::isInUse).filter(info -> info.isAbnormalType(AbnormalType.ABILITY_CHANGE)).forEach(info -> info.getEffects().stream().filter(effect -> this.canActivate(info, effect)).forEach(effect -> effect.pump(this.creature, info.getSkill())));
            }
            this._additionalAdd.stream().filter(holder -> holder.verifyCondition(this.creature)).forEach(holder -> this.mergeAdd(holder.getStat(), holder.getValue()));
            this._additionalMul.stream().filter(holder -> holder.verifyCondition(this.creature)).forEach(holder -> this.mergeMul(holder.getStat(), holder.getValue()));
            this._attackSpeedMultiplier = Formulas.calcAtkSpdMultiplier(this.creature);
            this._mAttackSpeedMultiplier = Formulas.calcMAtkSpdMultiplier(this.creature);
        }
        finally {
            this._lock.writeLock().unlock();
        }
        this.onRecalculateStats(broadcast);
        if (broadcast) {
            final Set<Stat> modified = Stat.stream().filter(stat -> this.isStatChanged(oldAdds, oldMuls, stat)).collect((Collector<? super Stat, ?, Set<Stat>>)Collectors.toSet());
            this.creature.broadcastModifiedStats(modified);
        }
    }
    
    protected boolean isStatChanged(final Map<Stat, Double> oldAdds, final Map<Stat, Double> oldMuls, final Stat stat) {
        return !Objects.equals(this.statsAdd.get(stat), oldAdds.get(stat)) || !Objects.equals(this.statsMul.get(stat), oldMuls.get(stat));
    }
    
    private boolean canActivate(final BuffInfo info, final AbstractEffect effect) {
        return effect.canStart(info.getEffector(), info.getEffected(), info.getSkill()) && effect.canPump(info.getEffector(), info.getEffected(), info.getSkill());
    }
    
    protected void onRecalculateStats(final boolean broadcast) {
        if (this.creature.getCurrentCp() > this.getMaxCp()) {
            this.creature.setCurrentCp(this.getMaxCp());
        }
        if (this.creature.getCurrentHp() > this.getMaxHp()) {
            this.creature.setCurrentHp(this.getMaxHp());
        }
        if (this.creature.getCurrentMp() > this.getMaxMp()) {
            this.creature.setCurrentMp(this.getMaxMp());
        }
    }
    
    public double getPositionTypeValue(final Stat stat, final Position position) {
        return this._positionStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(position, 1.0);
    }
    
    public void mergePositionTypeValue(final Stat stat, final Position position, final double value, final BiFunction<? super Double, ? super Double, ? extends Double> func) {
        this._positionStats.computeIfAbsent(stat, key -> new ConcurrentHashMap()).merge(position, value, func);
    }
    
    public double getMoveTypeValue(final Stat stat, final MoveType type) {
        return this._moveTypeStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(type, 0.0);
    }
    
    public void mergeMoveTypeValue(final Stat stat, final MoveType type, final double value) {
        this._moveTypeStats.computeIfAbsent(stat, key -> new ConcurrentHashMap()).merge(type, value, MathUtil::add);
    }
    
    public double getReuseTypeValue(final SkillType magicType) {
        return this.reuseStat.getOrDefault(magicType, 1.0);
    }
    
    public void mergeReuseTypeValue(final SkillType magicType, final double value, final BiFunction<? super Double, ? super Double, ? extends Double> func) {
        this.reuseStat.merge(magicType, value, func);
    }
    
    public double getMpConsumeTypeValue(final SkillType magicType) {
        return this.mpConsumeStat.getOrDefault(magicType, 1.0);
    }
    
    public void mergeMpConsumeTypeValue(final SkillType magicType, final double value, final BiFunction<? super Double, ? super Double, ? extends Double> func) {
        this.mpConsumeStat.merge(magicType, value, func);
    }
    
    public double getSkillEvasionTypeValue(final SkillType magicType) {
        final Stack<Double> stack = this.skillEvasionStat.get(magicType);
        return Util.isNullOrEmpty((Collection)stack) ? 0.0 : stack.pop();
    }
    
    public void addSkillEvasionTypeValue(final SkillType magicType, final double value) {
        this.skillEvasionStat.computeIfAbsent(magicType, k -> new Stack()).add(value);
    }
    
    public void removeSkillEvasionTypeValue(final SkillType magicType, final double value) {
        this.skillEvasionStat.computeIfPresent(magicType, (k, v) -> {
            v.remove(value);
            return v.isEmpty() ? null : v;
        });
    }
    
    public void addToVampiricSum(final double sum) {
        this._vampiricSum += sum;
    }
    
    public double getVampiricSum() {
        this._lock.readLock().lock();
        try {
            return this._vampiricSum;
        }
        finally {
            this._lock.readLock().unlock();
        }
    }
    
    public int getReuseTime(final Skill skill) {
        return (skill.isStaticReuse() || skill.isStatic()) ? skill.getReuseDelay() : ((int)(skill.getReuseDelay() * this.getReuseTypeValue(skill.getSkillType())));
    }
    
    public boolean addAdditionalStat(final Stat stat, final double value, final BiPredicate<Creature, StatsHolder> condition) {
        return this._additionalAdd.add(new StatsHolder(stat, value, condition));
    }
    
    public boolean addAdditionalStat(final Stat stat, final double value) {
        return this._additionalAdd.add(new StatsHolder(stat, value));
    }
    
    public boolean removeAddAdditionalStat(final Stat stat, final double value) {
        final Iterator<StatsHolder> it = this._additionalAdd.iterator();
        while (it.hasNext()) {
            final StatsHolder holder = it.next();
            if (holder.getStat() == stat && holder.getValue() == value) {
                it.remove();
                return true;
            }
        }
        return false;
    }
    
    public boolean mulAdditionalStat(final Stat stat, final double value, final BiPredicate<Creature, StatsHolder> condition) {
        return this._additionalMul.add(new StatsHolder(stat, value, condition));
    }
    
    public boolean mulAdditionalStat(final Stat stat, final double value) {
        return this._additionalMul.add(new StatsHolder(stat, value));
    }
    
    public boolean removeMulAdditionalStat(final Stat stat, final double value) {
        final Iterator<StatsHolder> it = this._additionalMul.iterator();
        while (it.hasNext()) {
            final StatsHolder holder = it.next();
            if (holder.getStat() == stat && holder.getValue() == value) {
                it.remove();
                return true;
            }
        }
        return false;
    }
    
    public boolean addFixedValue(final Stat stat, final Double value) {
        return this._fixedValue.put(stat, value) == null;
    }
    
    public boolean removeFixedValue(final Stat stat) {
        return this._fixedValue.remove(stat) != null;
    }
}
