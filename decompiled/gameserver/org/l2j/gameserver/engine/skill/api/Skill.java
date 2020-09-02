// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.commons.util.Rnd;
import java.util.function.Predicate;
import java.util.AbstractCollection;
import java.util.ArrayList;
import org.l2j.commons.util.Util;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Collection;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.stats.BasicPropertyResist;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import java.util.function.Consumer;
import java.util.LinkedList;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.MountEnabledSkillList;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.Config;
import java.util.Collections;
import java.util.Objects;
import java.util.EnumMap;
import org.l2j.gameserver.engine.skill.SkillAutoUseType;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.model.skills.SkillBuffType;
import java.util.Set;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.EnumSet;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.EffectScope;
import java.util.List;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import java.util.Map;
import org.l2j.gameserver.model.skills.SkillOperateType;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public final class Skill implements IIdentifiable, Cloneable
{
    private static final Logger LOGGER;
    private final int id;
    private final String name;
    private final SkillOperateType operateType;
    private final SkillType type;
    private final boolean debuff;
    private final int maxLevel;
    public Map<SkillConditionScope, List<SkillCondition>> conditions;
    public Map<EffectScope, List<AbstractEffect>> effects;
    private TraitType traitType;
    private AbnormalType abnormalType;
    private AbnormalType subordinationAbnormalType;
    private EnumSet<AbnormalVisualEffect> abnormalVisualEffect;
    private boolean isAbnormalInstant;
    private int abnormalLvl;
    private int abnormalTime;
    private int activateRate;
    private AttributeType attributeType;
    private int attributeValue;
    private String icon;
    private int level;
    private int displayId;
    private int castRange;
    private int effectRange;
    private int coolTime;
    private int hitTime;
    private double hitCancelTime;
    private int reuseDelayGroup;
    private boolean staticReuse;
    private long reuseHashCode;
    private int reuseDelay;
    private int magicLevel;
    private int effectPoint;
    private int levelBonusRate;
    private double magicCriticalRate;
    private int minChance;
    private int maxChance;
    private TargetType targetType;
    public AffectScope affectScope;
    private AffectObject affectObject;
    private int affectRange;
    public int affectMin;
    public int affectRandom;
    private int manaConsume;
    private int manaInitialConsume;
    private int hpConsume;
    private int soulMaxConsume;
    private int chargeConsume;
    private int itemConsumeId;
    private int itemConsumeCount;
    private boolean removedOnAnyActionExceptMove;
    private boolean removedOnDamage;
    private boolean blockedInOlympiad;
    private boolean stayAfterDeath;
    private boolean isTriggeredSkill;
    private boolean isSuicideAttack;
    private boolean canBeDispelled;
    private boolean excludedFromCheck;
    private boolean withoutAction;
    private int channelingSkillId;
    private long channelingStart;
    private long channelingTickInterval;
    private int mpPerChanneling;
    private boolean canCastWhileDisabled;
    private boolean isSharedWithSummon;
    private boolean deleteAbnormalOnLeave;
    private boolean irreplacableBuff;
    private boolean blockActionUseSkill;
    private Set<AbnormalType> abnormalResists;
    private SkillBuffType buffType;
    private BasicProperty basicProperty;
    private NextActionType nextAction;
    private SkillAutoUseType skillAutoUseType;
    private int fanStartAngle;
    private int fanRadius;
    private int fanAngle;
    private volatile long effectsMask;
    private boolean useCustomTime;
    private boolean useCustomDelay;
    
    Skill(final int id, final String name, final int maxLevel, final boolean debuff, final SkillOperateType action, final SkillType type) {
        this.conditions = new EnumMap<SkillConditionScope, List<SkillCondition>>(SkillConditionScope.class);
        this.effects = new EnumMap<EffectScope, List<AbstractEffect>>(EffectScope.class);
        this.traitType = TraitType.NONE;
        this.abnormalType = AbnormalType.NONE;
        this.subordinationAbnormalType = AbnormalType.NONE;
        this.attributeType = AttributeType.NONE;
        this.reuseDelayGroup = -1;
        this.nextAction = NextActionType.NONE;
        this.effectsMask = -1L;
        this.id = id;
        this.level = 1;
        this.name = name;
        this.maxLevel = maxLevel;
        this.debuff = debuff;
        this.operateType = action;
        this.type = type;
    }
    
    void computeSkillAttributes() {
        this.buffType = (this.isTriggeredSkill ? SkillBuffType.TRIGGER : (this.isToggle() ? SkillBuffType.TOGGLE : (this.isDance() ? SkillBuffType.DANCE : (this.debuff ? SkillBuffType.DEBUFF : (this.isHealingPotionSkill() ? SkillBuffType.NONE : SkillBuffType.BUFF)))));
        if (Objects.isNull(this.abnormalResists)) {
            this.abnormalResists = Collections.emptySet();
        }
        if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(this.id)) {
            this.useCustomDelay = true;
            this.reuseDelay = Config.SKILL_REUSE_LIST.get(this.id);
        }
        if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(this.id)) {
            this.useCustomTime = true;
            this.abnormalTime = Config.SKILL_DURATION_LIST.get(this.id);
        }
        this.reuseHashCode = SkillEngine.skillHashCode((this.reuseDelayGroup > 0) ? this.reuseDelayGroup : this.id, this.level);
        this.minChance = Config.MIN_ABNORMAL_STATE_SUCCESS_RATE;
        this.maxChance = Config.MAX_ABNORMAL_STATE_SUCCESS_RATE;
    }
    
    public boolean checkCondition(final Creature activeChar, final WorldObject object) {
        if (activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.GM_SKILL_RESTRICTION) {
            return true;
        }
        if (GameUtils.isPlayer(activeChar) && activeChar.getActingPlayer().isMounted() && this.isBad() && !MountEnabledSkillList.contains(this.id)) {
            activeChar.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this.id));
            return false;
        }
        if (!this.checkConditions(SkillConditionScope.GENERAL, activeChar, object) || !this.checkConditions(SkillConditionScope.TARGET, activeChar, object)) {
            if (activeChar != object || !this.isBad()) {
                activeChar.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this.id));
            }
            return false;
        }
        return true;
    }
    
    public boolean checkConditions(final SkillConditionScope skillConditionScope, final Creature caster, final WorldObject target) {
        return this.conditions.getOrDefault(skillConditionScope, Collections.emptyList()).stream().allMatch(c -> c.canUse(caster, this, target));
    }
    
    public WorldObject getTarget(final Creature activeChar, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        return this.getTarget(activeChar, activeChar.getTarget(), forceUse, dontMove, sendMessage);
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject seletedTarget, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        final ITargetTypeHandler handler = TargetHandler.getInstance().getHandler((Enum<TargetType>)this.targetType);
        if (Objects.nonNull(handler)) {
            try {
                return handler.getTarget(activeChar, seletedTarget, this, forceUse, dontMove, sendMessage);
            }
            catch (Exception e) {
                Skill.LOGGER.error("Could not execute target handler {} on skill {}", new Object[] { handler, this, e });
            }
        }
        if (activeChar.isGM()) {
            activeChar.sendMessage(String.format("Target type %s of skill %s is not currently handled.", this.targetType, this));
        }
        return null;
    }
    
    public List<WorldObject> getTargetsAffected(final Creature activeChar, final WorldObject target) {
        if (Objects.isNull(target)) {
            return Collections.emptyList();
        }
        final IAffectScopeHandler handler = AffectScopeHandler.getInstance().getHandler((Enum<AffectScope>)this.affectScope);
        if (Objects.nonNull(handler)) {
            try {
                final List<WorldObject> result = new LinkedList<WorldObject>();
                final IAffectScopeHandler affectScopeHandler = handler;
                final List<WorldObject> obj = result;
                Objects.requireNonNull((LinkedList)obj);
                affectScopeHandler.forEachAffected(activeChar, target, this, obj::add);
                return result;
            }
            catch (Exception e) {
                Skill.LOGGER.error("Could not execute affect scope handler {} of Skill {}", new Object[] { handler, this, e });
            }
        }
        if (activeChar.isGM()) {
            activeChar.sendMessage(String.format("Target affect scope %s of skill %s is not currently handled.", this.affectScope, this));
        }
        return Collections.emptyList();
    }
    
    public void forEachTargetAffected(final Creature activeChar, final WorldObject target, final Consumer<? super WorldObject> action) {
        if (Objects.isNull(target)) {
            return;
        }
        final IAffectScopeHandler handler = AffectScopeHandler.getInstance().getHandler((Enum<AffectScope>)this.affectScope);
        if (Objects.nonNull(handler)) {
            try {
                handler.forEachAffected(activeChar, target, this, action);
            }
            catch (Exception e) {
                Skill.LOGGER.warn("Could not execute affect scope handler {} of skill {}", new Object[] { handler, this, e });
            }
        }
        else if (activeChar.isGM()) {
            activeChar.sendMessage(String.format("Target affect scope %s of skill %s is not currently handled.", this.affectScope, this));
        }
    }
    
    public void applyEffectScope(final EffectScope effectScope, final BuffInfo info, final boolean applyInstantEffects, final boolean addContinuousEffects) {
        if (Objects.nonNull(effectScope) && this.hasEffects(effectScope)) {
            for (final AbstractEffect effect : this.getEffects(effectScope)) {
                if (effect.isInstant()) {
                    if (!applyInstantEffects || !effect.calcSuccess(info.getEffector(), info.getEffected(), info.getSkill())) {
                        continue;
                    }
                    effect.instant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                }
                else {
                    if (!addContinuousEffects) {
                        continue;
                    }
                    if (applyInstantEffects) {
                        effect.continuousInstant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    }
                    if (effect.canStart(info.getEffector(), info.getEffected(), info.getSkill())) {
                        info.addEffect(effect);
                    }
                    if (!GameUtils.isPlayer(info.getEffected()) || info.getSkill().isBad()) {
                        continue;
                    }
                    info.getEffected().getActingPlayer().getStatus().startHpMpRegeneration();
                }
            }
        }
    }
    
    public void applyEffects(final Creature effector, final Creature effected) {
        this.applyEffects(effector, effected, false, false, true, 0, null);
    }
    
    private void applyEffects(final Creature effector, final Creature effected, final Item item) {
        this.applyEffects(effector, effected, false, false, true, 0, item);
    }
    
    public void applyEffects(final Creature effector, final Creature effected, final boolean instant, final int abnormalTime) {
        this.applyEffects(effector, effected, false, false, instant, abnormalTime, null);
    }
    
    public void applyEffects(final Creature effector, final Creature effected, final boolean self, final boolean passive, final boolean instant, final int abnormalTime, final Item item) {
        if (Objects.isNull(effected)) {
            return;
        }
        if (effected.isIgnoringSkillEffects(this.id, this.level)) {
            return;
        }
        boolean addContinuousEffects = !passive && (this.operateType.isToggle() || (this.operateType.isContinuous() && Formulas.calcEffectSuccess(effector, effected, this)));
        if (!self && !passive) {
            final BuffInfo info = new BuffInfo(effector, effected, this, !instant, item, null);
            if (addContinuousEffects && abnormalTime > 0) {
                info.setAbnormalTime(abnormalTime);
            }
            this.applyEffectScope(EffectScope.GENERAL, info, instant, addContinuousEffects);
            final EffectScope pvpOrPveEffectScope = (GameUtils.isPlayable(effector) && GameUtils.isAttackable(effected)) ? EffectScope.PVE : ((GameUtils.isPlayable(effector) && GameUtils.isPlayable(effected)) ? EffectScope.PVP : null);
            this.applyEffectScope(pvpOrPveEffectScope, info, instant, addContinuousEffects);
            if (addContinuousEffects) {
                final BuffInfo existingInfo = this.operateType.isAura() ? effected.getEffectList().getBuffInfoBySkillId(this.id) : null;
                if (existingInfo != null) {
                    existingInfo.resetAbnormalTime(info.getAbnormalTime());
                }
                else {
                    effected.getEffectList().add(info);
                }
                if (this.debuff && this.basicProperty != BasicProperty.NONE && effected.hasBasicPropertyResist()) {
                    final BasicPropertyResist resist = effected.getBasicPropertyResist(this.basicProperty);
                    resist.increaseResistLevel();
                }
            }
            if (this.isSharedWithSummon && GameUtils.isPlayer(effected) && effected.hasServitors() && !this.isTransformation() && addContinuousEffects && this.isContinuous() && !this.debuff) {
                effected.getServitors().values().forEach(s -> this.applyEffects(effector, s, instant, 0));
            }
        }
        if (self) {
            addContinuousEffects = (!passive && (this.operateType.isToggle() || (this.operateType.isSelfContinuous() && Formulas.calcEffectSuccess(effector, effector, this))));
            final BuffInfo info = new BuffInfo(effector, effector, this, !instant, item, null);
            if (addContinuousEffects && abnormalTime > 0) {
                info.setAbnormalTime(abnormalTime);
            }
            this.applyEffectScope(EffectScope.SELF, info, instant, addContinuousEffects);
            if (addContinuousEffects) {
                final BuffInfo existingInfo2 = this.operateType.isAura() ? effector.getEffectList().getBuffInfoBySkillId(this.id) : null;
                if (existingInfo2 != null) {
                    existingInfo2.resetAbnormalTime(info.getAbnormalTime());
                }
                else {
                    info.getEffector().getEffectList().add(info);
                }
            }
            if (addContinuousEffects && this.isSharedWithSummon && GameUtils.isPlayer(info.getEffected()) && this.isContinuous() && !this.debuff && info.getEffected().hasServitors()) {
                info.getEffected().getServitors().values().forEach(s -> this.applyEffects(effector, s, false, 0));
            }
        }
        if (passive && this.checkConditions(SkillConditionScope.PASSIVE, effector, effector)) {
            final BuffInfo info = new BuffInfo(effector, effector, this, true, item, null);
            this.applyEffectScope(EffectScope.GENERAL, info, false, true);
            effector.getEffectList().add(info);
        }
    }
    
    public void applyChannelingEffects(final Creature effector, final Creature effected) {
        if (Objects.isNull(effected)) {
            return;
        }
        final BuffInfo info = new BuffInfo(effector, effected, this, false, null, null);
        this.applyEffectScope(EffectScope.CHANNELING, info, true, true);
    }
    
    public void activateSkill(final Creature caster, final WorldObject... targets) {
        this.activateSkill(caster, null, targets);
    }
    
    public void activateSkill(final Creature caster, final Item item, final WorldObject... targets) {
        this.activateSkill(caster, null, item, targets);
    }
    
    public void activateSkill(final CubicInstance cubic, final WorldObject... targets) {
        this.activateSkill(cubic.getOwner(), cubic, null, targets);
    }
    
    private void activateSkill(final Creature caster, final CubicInstance cubic, final Item item, final WorldObject... targets) {
        for (final WorldObject targetObject : targets) {
            if (GameUtils.isCreature(targetObject)) {
                final Creature target = (Creature)targetObject;
                if (Formulas.calcBuffDebuffReflection(target, this)) {
                    this.applyEffects(target, caster, false, 0);
                    final BuffInfo info = new BuffInfo(caster, target, this, false, item, null);
                    this.applyEffectScope(EffectScope.GENERAL, info, true, false);
                    final EffectScope pvpOrPveEffectScope = (GameUtils.isPlayable(caster) && GameUtils.isAttackable(target)) ? EffectScope.PVE : ((GameUtils.isPlayable(caster) && GameUtils.isPlayable(target)) ? EffectScope.PVP : null);
                    this.applyEffectScope(pvpOrPveEffectScope, info, true, false);
                }
                else {
                    this.applyEffects(caster, target, item);
                }
            }
        }
        if (this.hasEffects(EffectScope.SELF)) {
            if (caster.isAffectedBySkill(this.id)) {
                caster.stopSkillEffects(true, this.id);
            }
            this.applyEffects(caster, caster, true, false, true, 0, item);
        }
        if (this.isSuicideAttack) {
            caster.doDie(caster);
        }
    }
    
    public boolean hasAnyEffectType(final EffectType... effectTypes) {
        if (Objects.isNull(effectTypes)) {
            return false;
        }
        if (this.effectsMask == -1L) {
            synchronized (this) {
                if (this.effectsMask == -1L) {
                    this.effectsMask = this.effects.values().stream().flatMap((Function<? super List<AbstractEffect>, ? extends Stream<?>>)Collection::stream).mapToLong(e -> e.getEffectType().mask()).reduce(0L, (a, b) -> a | b);
                }
            }
        }
        for (final EffectType type : effectTypes) {
            if ((this.effectsMask & type.mask()) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAnyEffectType(final EffectScope effectScope, final EffectType... effectTypes) {
        return !this.hasEffects(effectScope) && !Objects.isNull(effectTypes) && this.effects.get(effectScope).stream().anyMatch(e -> Util.contains((Object[])effectTypes, (Object)e.getEffectType()));
    }
    
    public void addEffect(final EffectScope effectScope, final AbstractEffect effect) {
        this.effects.computeIfAbsent(effectScope, k -> new ArrayList()).add(effect);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    void setAttributeType(final AttributeType type) {
        this.attributeType = type;
    }
    
    public AttributeType getAttributeType() {
        return this.attributeType;
    }
    
    void setAttributeValue(final int value) {
        this.attributeValue = value;
    }
    
    public int getAttributeValue() {
        return this.attributeValue;
    }
    
    public boolean allowOnTransform() {
        return this.isPassive();
    }
    
    void setAbnormalInstant(final boolean instant) {
        this.isAbnormalInstant = instant;
    }
    
    public boolean isAbnormalInstant() {
        return this.isAbnormalInstant;
    }
    
    void setAbnormalType(final AbnormalType type) {
        this.abnormalType = type;
    }
    
    public AbnormalType getAbnormalType() {
        return this.abnormalType;
    }
    
    void setAbnormalSubordination(final AbnormalType subordination) {
        this.subordinationAbnormalType = subordination;
    }
    
    public AbnormalType getSubordinationAbnormalType() {
        return this.subordinationAbnormalType;
    }
    
    void setAbnormalLevel(final int level) {
        this.abnormalLvl = level;
    }
    
    public int getAbnormalLvl() {
        return this.abnormalLvl;
    }
    
    void setAbnormalTime(final int time) {
        if (!this.useCustomTime) {
            this.abnormalTime = time;
        }
    }
    
    public int getAbnormalTime() {
        return this.abnormalTime;
    }
    
    void setAbnormalVisualEffect(final EnumSet<AbnormalVisualEffect> visual) {
        visual.remove(AbnormalVisualEffect.NONE);
        this.abnormalVisualEffect = visual;
    }
    
    public EnumSet<AbnormalVisualEffect> getAbnormalVisualEffect() {
        return this.abnormalVisualEffect;
    }
    
    public boolean hasAbnormalVisualEffect() {
        return Util.falseIfNullOrElse((Object)this.abnormalVisualEffect, (Predicate)Predicate.not(AbstractCollection::isEmpty));
    }
    
    public int getMagicLevel() {
        return this.magicLevel;
    }
    
    void setMagicLevel(final int level) {
        this.magicLevel = level;
    }
    
    public int getActivateRate() {
        return this.activateRate;
    }
    
    void setActivateRate(final int rate) {
        this.activateRate = rate;
    }
    
    public int getMinChance() {
        return this.minChance;
    }
    
    public int getMaxChance() {
        return this.maxChance;
    }
    
    void setNextAction(final NextActionType action) {
        this.nextAction = action;
    }
    
    public NextActionType getNextAction() {
        return this.nextAction;
    }
    
    void setCastRange(final int range) {
        this.castRange = range;
    }
    
    public int getCastRange() {
        return this.castRange;
    }
    
    void setEffectRange(final int effectRange) {
        this.effectRange = effectRange;
    }
    
    public int getEffectRange() {
        return this.effectRange;
    }
    
    void setHpConsume(final int consume) {
        this.hpConsume = consume;
    }
    
    public int getHpConsume() {
        return this.hpConsume;
    }
    
    public boolean isDebuff() {
        return this.debuff;
    }
    
    public int getDisplayId() {
        return this.displayId;
    }
    
    void setDisplayId(final int id) {
        this.displayId = id;
    }
    
    public int getDisplayLevel() {
        return this.level;
    }
    
    public BasicProperty getBasicProperty() {
        return this.basicProperty;
    }
    
    public int getItemConsumeCount() {
        return this.itemConsumeCount;
    }
    
    void setItemConsumeCount(final int count) {
        this.itemConsumeCount = count;
    }
    
    public int getItemConsumeId() {
        return this.itemConsumeId;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    void setLevel(final int level) {
        this.level = level;
    }
    
    public int getSubLevel() {
        return 0;
    }
    
    public SkillType getSkillType() {
        return this.type;
    }
    
    public boolean isPhysical() {
        return this.type == SkillType.PHYSIC;
    }
    
    public boolean isMagic() {
        return this.type == SkillType.MAGIC;
    }
    
    public boolean isStatic() {
        return this.type == SkillType.STATIC;
    }
    
    public boolean isDance() {
        return this.type == SkillType.DANCE;
    }
    
    public boolean isStaticReuse() {
        return this.staticReuse;
    }
    
    void setStaticReuse(final boolean staticReuse) {
        this.staticReuse = staticReuse;
    }
    
    public int getMpConsume() {
        return this.manaConsume;
    }
    
    public int getMpInitialConsume() {
        return this.manaInitialConsume;
    }
    
    public int getMpPerChanneling() {
        return this.mpPerChanneling;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getReuseDelay() {
        return this.reuseDelay;
    }
    
    public int getReuseDelayGroup() {
        return this.reuseDelayGroup;
    }
    
    public long getReuseHashCode() {
        return this.reuseHashCode;
    }
    
    public int getHitTime() {
        return this.hitTime;
    }
    
    void setHitTime(final int time) {
        this.hitTime = time;
    }
    
    public double getHitCancelTime() {
        return this.hitCancelTime;
    }
    
    void setHitCancelTime(final double time) {
        this.hitCancelTime = time;
    }
    
    public int getCoolTime() {
        return this.coolTime;
    }
    
    void setCoolTime(final int time) {
        this.coolTime = time;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
    }
    
    void setTargetType(final TargetType type) {
        this.targetType = type;
    }
    
    void setAffectScope(final AffectScope scope) {
        this.affectScope = scope;
    }
    
    public AffectObject getAffectObject() {
        return this.affectObject;
    }
    
    void setAffectObject(final AffectObject object) {
        this.affectObject = object;
    }
    
    public int getAffectRange() {
        return this.affectRange;
    }
    
    void setAffectRange(final int range) {
        this.affectRange = range;
    }
    
    public int getAffectLimit() {
        return (this.affectMin > 0 || this.affectRandom > 0) ? (this.affectMin + Rnd.get(this.affectRandom)) : 0;
    }
    
    public boolean isActive() {
        return this.operateType.isActive();
    }
    
    public boolean isPassive() {
        return this.operateType.isPassive();
    }
    
    public boolean isToggle() {
        return this.operateType.isToggle();
    }
    
    public boolean isAura() {
        return this.operateType.isAura();
    }
    
    public boolean isHidingMessages() {
        return this.operateType.isHidingMessages();
    }
    
    public boolean isNotBroadcastable() {
        return this.operateType.isNotBroadcastable();
    }
    
    public boolean isContinuous() {
        return this.operateType.isContinuous() || this.isSelfContinuous();
    }
    
    public boolean isFlyType() {
        return this.operateType.isFlyType();
    }
    
    public boolean isSelfContinuous() {
        return this.operateType.isSelfContinuous();
    }
    
    public boolean isChanneling() {
        return this.operateType.isChanneling();
    }
    
    public boolean isTriggeredSkill() {
        return this.isTriggeredSkill;
    }
    
    public SkillOperateType getOperateType() {
        return this.operateType;
    }
    
    public boolean isTransformation() {
        return this.abnormalType == AbnormalType.TRANSFORM;
    }
    
    public int getEffectPoint() {
        return this.effectPoint;
    }
    
    void setEffectPoint(final int effectPoints) {
        this.effectPoint = effectPoints;
    }
    
    public boolean useSoulShot() {
        return this.hasAnyEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK);
    }
    
    public boolean useSpiritShot() {
        return this.type == SkillType.MAGIC;
    }
    
    public boolean isHeroSkill() {
        return SkillTreesData.getInstance().isHeroSkill(this.id, this.level);
    }
    
    public boolean isGMSkill() {
        return SkillTreesData.getInstance().isGMSkill(this.id, this.level);
    }
    
    public boolean is7Signs() {
        return this.id > 4360 && this.id < 4367;
    }
    
    public boolean isHealingPotionSkill() {
        return this.abnormalType == AbnormalType.HP_RECOVER;
    }
    
    public int getMaxSoulConsumeCount() {
        return this.soulMaxConsume;
    }
    
    public int getChargeConsumeCount() {
        return this.chargeConsume;
    }
    
    public boolean isStayAfterDeath() {
        return this.stayAfterDeath || this.irreplacableBuff;
    }
    
    void setStayAfterDeath(final boolean stayAfterDeath) {
        this.stayAfterDeath = stayAfterDeath;
    }
    
    public boolean isBad() {
        return this.effectPoint < 0;
    }
    
    public List<AbstractEffect> getEffects(final EffectScope effectScope) {
        return this.effects.get(effectScope);
    }
    
    public boolean hasEffects(final EffectScope effectScope) {
        return Util.falseIfNullOrElse((Object)this.effects.get(effectScope), (Predicate)Predicate.not(List::isEmpty));
    }
    
    public void addCondition(final SkillConditionScope skillConditionScope, final SkillCondition skillCondition) {
        this.conditions.computeIfAbsent(skillConditionScope, k -> new ArrayList()).add(skillCondition);
    }
    
    public boolean canBeDispelled() {
        return this.canBeDispelled;
    }
    
    public boolean canBeStolen() {
        return !this.isPassive() && !this.isToggle() && !this.debuff && !this.irreplacableBuff && !this.isHeroSkill() && !this.isGMSkill() && (!this.isStatic() || this.getId() == CommonSkill.CARAVANS_SECRET_MEDICINE.getId()) && this.canBeDispelled;
    }
    
    public boolean isClanSkill() {
        return SkillTreesData.getInstance().isClanSkill(this.id, this.level);
    }
    
    public boolean isExcludedFromCheck() {
        return this.excludedFromCheck;
    }
    
    public boolean isWithoutAction() {
        return this.withoutAction;
    }
    
    void setWithoutAction(final boolean withoutAction) {
        this.withoutAction = withoutAction;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public long getChannelingTickInterval() {
        return this.channelingTickInterval;
    }
    
    public long getChannelingTickInitialDelay() {
        return this.channelingStart;
    }
    
    public boolean canCastWhileDisabled() {
        return this.canCastWhileDisabled;
    }
    
    public boolean isSharedWithSummon() {
        return this.isSharedWithSummon;
    }
    
    public boolean isDeleteAbnormalOnLeave() {
        return this.deleteAbnormalOnLeave;
    }
    
    public boolean isIrreplacableBuff() {
        return this.irreplacableBuff;
    }
    
    public boolean isBlockActionUseSkill() {
        return this.blockActionUseSkill;
    }
    
    public Set<AbnormalType> getAbnormalResists() {
        return this.abnormalResists;
    }
    
    public double getMagicCriticalRate() {
        return this.magicCriticalRate;
    }
    
    void setMagicCriticalRate(final double rate) {
        this.magicCriticalRate = rate;
    }
    
    public SkillBuffType getBuffType() {
        return this.buffType;
    }
    
    public boolean isEnchantable() {
        return false;
    }
    
    void setTrait(final TraitType trait) {
        this.traitType = trait;
    }
    
    public TraitType getTrait() {
        return this.traitType;
    }
    
    void setProperty(final BasicProperty property) {
        this.basicProperty = property;
    }
    
    void setLevelBonusRate(final int rate) {
        this.levelBonusRate = rate;
    }
    
    public int getLevelBonusRate() {
        return this.levelBonusRate;
    }
    
    void setRemoveOnAction(final boolean removeOnAction) {
        this.removedOnAnyActionExceptMove = removeOnAction;
    }
    
    public boolean isRemovedOnAnyActionExceptMove() {
        return this.removedOnAnyActionExceptMove;
    }
    
    void setRemoveOnDamage(final boolean removeOnDamage) {
        this.removedOnDamage = removeOnDamage;
    }
    
    public boolean isRemovedOnDamage() {
        return this.removedOnDamage;
    }
    
    void setBlockedOnOlympiad(final boolean blockedOnOlympiad) {
        this.blockedInOlympiad = blockedOnOlympiad;
    }
    
    public boolean isBlockedInOlympiad() {
        return this.blockedInOlympiad;
    }
    
    void setSuicide(final boolean suicide) {
        this.isSuicideAttack = suicide;
    }
    
    public boolean isSuicideAttack() {
        return this.isSuicideAttack;
    }
    
    void setTriggered(final boolean triggered) {
        this.isTriggeredSkill = triggered;
    }
    
    void setDispellable(final boolean dispellable) {
        this.canBeDispelled = dispellable;
    }
    
    void setCheck(final boolean check) {
        this.excludedFromCheck = !check;
    }
    
    void setCanCastDisabled(final boolean castDisabled) {
        this.canCastWhileDisabled = castDisabled;
    }
    
    void setSummonShared(final boolean summonShared) {
        this.isSharedWithSummon = summonShared;
    }
    
    void setRemoveAbnormalOnLeave(final boolean remove) {
        this.deleteAbnormalOnLeave = remove;
    }
    
    void setIrreplacable(final boolean irreplacable) {
        this.irreplacableBuff = irreplacable;
    }
    
    void setBlockActionSkill(final boolean block) {
        this.blockActionUseSkill = block;
    }
    
    void setSkillAutoUseType(final SkillAutoUseType skillAutoUseType) {
        this.skillAutoUseType = skillAutoUseType;
    }
    
    void setSoulConsume(final int souls) {
        this.soulMaxConsume = souls;
    }
    
    void setChargeConsume(final int charges) {
        this.chargeConsume = charges;
    }
    
    void setAffectMin(final int affectMin) {
        this.affectMin = affectMin;
    }
    
    void setAffectRandom(final int affectRandom) {
        this.affectRandom = affectRandom;
    }
    
    void setResistAbnormals(final Set<AbnormalType> abnormals) {
        this.abnormalResists = abnormals;
    }
    
    void setChannelingSkill(final int skill) {
        this.channelingSkillId = skill;
    }
    
    public int getChannelingSkillId() {
        return this.channelingSkillId;
    }
    
    void setChannelingMpConsume(final int mpConsume) {
        this.mpPerChanneling = mpConsume;
    }
    
    void setChannelingInitialDelay(final long delay) {
        this.channelingStart = delay;
    }
    
    void setChannelingInterval(final long interval) {
        this.channelingTickInterval = interval;
    }
    
    void setReuse(final int reuse) {
        if (!this.useCustomDelay) {
            this.reuseDelay = reuse;
        }
    }
    
    void setManaInitConsume(final int consume) {
        this.manaInitialConsume = consume;
    }
    
    void setManaConsume(final int consume) {
        this.manaConsume = consume;
    }
    
    void setItemConsume(final int item) {
        this.itemConsumeId = item;
    }
    
    void setFanStartAngle(final int angle) {
        this.fanStartAngle = angle;
    }
    
    public int getFanStartAngle() {
        return this.fanStartAngle;
    }
    
    void setFanRadius(final int radius) {
        this.fanRadius = radius;
    }
    
    public int getFanRadius() {
        return this.fanRadius;
    }
    
    void setFanAngle(final int angle) {
        this.fanAngle = angle;
    }
    
    public int getFanAngle() {
        return this.fanAngle;
    }
    
    void setAbnormalChance(final int chance) {
        this.activateRate = chance;
    }
    
    Skill clone(final boolean keepEffects, final boolean keepConditions) throws CloneNotSupportedException {
        final Skill clone = this.clone();
        if (!keepEffects) {
            clone.effects = new EnumMap<EffectScope, List<AbstractEffect>>(EffectScope.class);
        }
        if (!keepConditions) {
            clone.conditions = new EnumMap<SkillConditionScope, List<SkillCondition>>(SkillConditionScope.class);
        }
        return clone;
    }
    
    @Override
    protected Skill clone() throws CloneNotSupportedException {
        return (Skill)super.clone();
    }
    
    @Override
    public String toString() {
        return String.format("Skill %s (%d, %d)", this.name, this.id, this.level);
    }
    
    public boolean isAutoUse() {
        return Util.falseIfNullOrElse((Object)this.skillAutoUseType, t -> t != SkillAutoUseType.NONE);
    }
    
    public boolean isAutoTransformation() {
        return this.skillAutoUseType == SkillAutoUseType.TRANSFORM;
    }
    
    public boolean isAutoBuff() {
        return this.skillAutoUseType == SkillAutoUseType.BUFF;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Skill.class);
    }
}
