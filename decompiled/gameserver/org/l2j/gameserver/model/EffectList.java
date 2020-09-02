// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.HashSet;
import java.util.Arrays;
import org.l2j.gameserver.model.olympiad.OlympiadStadium;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Function;
import org.l2j.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadSpelledInfo;
import org.l2j.gameserver.network.serverpackets.PartySpelled;
import java.util.Optional;
import org.l2j.gameserver.network.serverpackets.AbnormalStatusUpdate;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.skills.SkillBuffType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.effects.EffectFlag;
import java.util.function.Consumer;
import org.l2j.gameserver.network.serverpackets.ShortBuffStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Set;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.Queue;
import org.l2j.gameserver.model.actor.Creature;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public final class EffectList
{
    private static final Logger LOGGER;
    private final AtomicInteger buffCount;
    private final AtomicInteger triggerBuffCount;
    private final AtomicInteger danceCount;
    private final AtomicInteger toggleCount;
    private final AtomicInteger debuffCount;
    private final AtomicInteger hasBuffsRemovedOnAnyAction;
    private final AtomicInteger hasBuffsRemovedOnDamage;
    private final Creature owner;
    private final AtomicInteger hiddenBuffs;
    private final Queue<BuffInfo> actives;
    private final Set<BuffInfo> passives;
    private final Set<BuffInfo> options;
    private volatile Set<AbnormalType> stackedEffects;
    private final Set<AbnormalType> blockedAbnormalTypes;
    private volatile Set<AbnormalVisualEffect> abnormalVisualEffects;
    private BuffInfo shortBuff;
    private long effectFlags;
    
    public EffectList(final Creature owner) {
        this.buffCount = new AtomicInteger();
        this.triggerBuffCount = new AtomicInteger();
        this.danceCount = new AtomicInteger();
        this.toggleCount = new AtomicInteger();
        this.debuffCount = new AtomicInteger();
        this.hasBuffsRemovedOnAnyAction = new AtomicInteger();
        this.hasBuffsRemovedOnDamage = new AtomicInteger();
        this.hiddenBuffs = new AtomicInteger();
        this.actives = new ConcurrentLinkedQueue<BuffInfo>();
        this.passives = (Set<BuffInfo>)ConcurrentHashMap.newKeySet();
        this.options = (Set<BuffInfo>)ConcurrentHashMap.newKeySet();
        this.stackedEffects = EnumSet.noneOf(AbnormalType.class);
        this.blockedAbnormalTypes = EnumSet.noneOf(AbnormalType.class);
        this.abnormalVisualEffects = EnumSet.noneOf(AbnormalVisualEffect.class);
        this.shortBuff = null;
        this.owner = owner;
    }
    
    public Set<BuffInfo> getPassives() {
        return Collections.unmodifiableSet((Set<? extends BuffInfo>)this.passives);
    }
    
    public Set<BuffInfo> getOptions() {
        return Collections.unmodifiableSet((Set<? extends BuffInfo>)this.options);
    }
    
    public Collection<BuffInfo> getEffects() {
        return Collections.unmodifiableCollection((Collection<? extends BuffInfo>)this.actives);
    }
    
    public List<BuffInfo> getBuffs() {
        return this.actives.stream().filter(b -> b.getSkill().getBuffType().isBuff()).collect((Collector<? super Object, ?, List<BuffInfo>>)Collectors.toList());
    }
    
    public List<BuffInfo> getDances() {
        return this.actives.stream().filter(b -> b.getSkill().getBuffType().isDance()).collect((Collector<? super Object, ?, List<BuffInfo>>)Collectors.toList());
    }
    
    public List<BuffInfo> getDebuffs() {
        return this.actives.stream().filter(b -> b.getSkill().isDebuff()).collect((Collector<? super Object, ?, List<BuffInfo>>)Collectors.toList());
    }
    
    public boolean isAffectedBySkill(final int skillId) {
        return this.actives.stream().anyMatch(i -> i.getSkill().getId() == skillId) || this.passives.stream().anyMatch(i -> i.getSkill().getId() == skillId);
    }
    
    public BuffInfo getBuffInfoBySkillId(final int skillId) {
        return Stream.concat(this.actives.stream(), this.passives.stream()).filter(b -> b.getSkill().getId() == skillId).findFirst().orElse(null);
    }
    
    public int remainTimeBySkillIdOrAbnormalType(final int skillId, final AbnormalType type) {
        return this.actives.stream().filter(b -> this.isSkillOrHasType(skillId, type, b)).mapToInt(BuffInfo::getTime).findFirst().orElse(0);
    }
    
    private boolean isSkillOrHasType(final int skillId, final AbnormalType type, final BuffInfo buff) {
        return buff.getSkill().getId() == skillId || (type != AbnormalType.NONE && buff.getSkill().getAbnormalType() == type);
    }
    
    public final boolean hasAbnormalType(final AbnormalType type) {
        return this.stackedEffects.contains(type);
    }
    
    public boolean hasAbnormalType(final Collection<AbnormalType> types) {
        final Stream<Object> stream = this.stackedEffects.stream();
        Objects.requireNonNull(types);
        return stream.anyMatch(types::contains);
    }
    
    public boolean hasAbnormalType(final AbnormalType type, final Predicate<BuffInfo> filter) {
        return this.hasAbnormalType(type) && this.actives.stream().filter(i -> i.isAbnormalType(type)).anyMatch((Predicate<? super Object>)filter);
    }
    
    public BuffInfo getFirstBuffInfoByAbnormalType(final AbnormalType type) {
        return this.hasAbnormalType(type) ? this.actives.stream().filter(i -> i.isAbnormalType(type)).findFirst().orElse(null) : null;
    }
    
    public void addBlockedAbnormalTypes(final Set<AbnormalType> blockedAbnormalTypes) {
        this.blockedAbnormalTypes.addAll(blockedAbnormalTypes);
    }
    
    public boolean removeBlockedAbnormalTypes(final Set<AbnormalType> blockedBuffSlots) {
        return this.blockedAbnormalTypes.removeAll(blockedBuffSlots);
    }
    
    public Set<AbnormalType> getBlockedAbnormalTypes() {
        return Collections.unmodifiableSet((Set<? extends AbnormalType>)this.blockedAbnormalTypes);
    }
    
    public void shortBuffStatusUpdate(final BuffInfo info) {
        if (GameUtils.isPlayer(this.owner)) {
            if ((this.shortBuff = info) == null) {
                this.owner.sendPacket(ShortBuffStatusUpdate.RESET_SHORT_BUFF);
            }
            else {
                this.owner.sendPacket(new ShortBuffStatusUpdate(info.getSkill().getId(), info.getSkill().getLevel(), info.getSkill().getSubLevel(), info.getTime()));
            }
        }
    }
    
    public int getBuffCount() {
        return this.actives.isEmpty() ? 0 : (this.buffCount.get() - this.hiddenBuffs.get());
    }
    
    public int getDanceCount() {
        return this.danceCount.get();
    }
    
    public int getTriggeredBuffCount() {
        return this.triggerBuffCount.get();
    }
    
    public int getToggleCount() {
        return this.toggleCount.get();
    }
    
    public int getDebuffCount() {
        return this.debuffCount.get();
    }
    
    public int getHiddenBuffsCount() {
        return this.hiddenBuffs.get();
    }
    
    public void stopAllEffects(final boolean broadcast) {
        this.stopEffects(b -> !b.getSkill().isIrreplacableBuff(), true, broadcast);
    }
    
    public void stopAllEffectsExceptThoseThatLastThroughDeath() {
        this.stopEffects(info -> !info.getSkill().isStayAfterDeath(), true, true);
    }
    
    public void stopAllEffectsWithoutExclusions(final boolean update, final boolean broadcast) {
        this.actives.forEach(this::remove);
        this.passives.forEach(this::remove);
        this.options.forEach(this::remove);
        if (update) {
            this.updateEffectList(broadcast);
        }
    }
    
    public void stopAllToggles() {
        if (this.toggleCount.get() > 0) {
            this.stopEffects(b -> b.getSkill().isToggle() && !b.getSkill().isIrreplacableBuff(), true, true);
        }
    }
    
    public void stopAllTogglesOfGroup(final int toggleGroup) {
        if (this.toggleCount.get() > 0) {
            this.stopEffects(b -> b.getSkill().isToggle(), true, true);
        }
    }
    
    public void stopAllPassives(final boolean update, final boolean broadcast) {
        if (!this.passives.isEmpty()) {
            this.passives.forEach(this::remove);
            if (update) {
                this.updateEffectList(broadcast);
            }
        }
    }
    
    public void stopAllOptions(final boolean update, final boolean broadcast) {
        if (!this.options.isEmpty()) {
            this.options.forEach(this::remove);
            if (update) {
                this.updateEffectList(broadcast);
            }
        }
    }
    
    public void stopEffects(final EffectFlag effectFlag) {
        if (this.isAffected(effectFlag)) {
            this.stopEffects(info -> info.getEffects().stream().anyMatch(effect -> effect != null && (effect.getEffectFlags() & effectFlag.getMask()) != 0x0L), true, true);
        }
    }
    
    public void stopSkillEffects(final boolean removed, final int skillId) {
        final BuffInfo info = this.getBuffInfoBySkillId(skillId);
        if (info != null) {
            this.remove(info, removed, true, true);
        }
    }
    
    public void stopSkillEffects(final boolean removed, final Skill skill) {
        this.stopSkillEffects(removed, skill.getId());
    }
    
    public boolean stopEffects(final AbnormalType type) {
        if (this.hasAbnormalType(type)) {
            this.stopEffects(i -> i.isAbnormalType(type), true, true);
            return true;
        }
        return false;
    }
    
    public boolean stopEffects(final Collection<AbnormalType> types) {
        if (this.hasAbnormalType(types)) {
            this.stopEffects(i -> types.contains(i.getSkill().getAbnormalType()), true, true);
            return true;
        }
        return false;
    }
    
    public void stopEffects(final Predicate<BuffInfo> filter, final boolean update, final boolean broadcast) {
        if (!this.actives.isEmpty()) {
            this.actives.stream().filter((Predicate<? super Object>)filter).forEach((Consumer<? super Object>)this::remove);
            if (update) {
                this.updateEffectList(broadcast);
            }
        }
    }
    
    public void stopEffectsOnAction() {
        if (this.hasBuffsRemovedOnAnyAction.get() > 0) {
            this.stopEffects(info -> info.getSkill().isRemovedOnAnyActionExceptMove(), true, true);
        }
    }
    
    public void stopEffectsOnDamage() {
        if (this.hasBuffsRemovedOnDamage.get() > 0) {
            this.stopEffects(info -> info.getSkill().isRemovedOnDamage(), true, true);
        }
    }
    
    private boolean isLimitExceeded(final SkillBuffType... buffTypes) {
        for (final SkillBuffType buffType : buffTypes) {
            switch (buffType) {
                case TRIGGER: {
                    if (this.triggerBuffCount.get() > Config.TRIGGERED_BUFFS_MAX_AMOUNT) {
                        return true;
                    }
                }
                case DANCE: {
                    if (this.danceCount.get() > Config.DANCES_MAX_AMOUNT) {
                        return true;
                    }
                }
                case DEBUFF: {
                    if (this.debuffCount.get() > 24) {
                        return true;
                    }
                }
                case BUFF: {
                    if (this.getBuffCount() > this.owner.getStats().getMaxBuffCount()) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    private int increaseDecreaseCount(final BuffInfo info, final boolean increase) {
        if (!info.isInUse()) {
            if (increase) {
                this.hiddenBuffs.incrementAndGet();
            }
            else {
                this.hiddenBuffs.decrementAndGet();
            }
        }
        if (info.getSkill().isRemovedOnAnyActionExceptMove()) {
            if (increase) {
                this.hasBuffsRemovedOnAnyAction.incrementAndGet();
            }
            else {
                this.hasBuffsRemovedOnAnyAction.decrementAndGet();
            }
        }
        if (info.getSkill().isRemovedOnDamage()) {
            if (increase) {
                this.hasBuffsRemovedOnDamage.incrementAndGet();
            }
            else {
                this.hasBuffsRemovedOnDamage.decrementAndGet();
            }
        }
        switch (info.getSkill().getBuffType()) {
            case TRIGGER: {
                return increase ? this.triggerBuffCount.incrementAndGet() : this.triggerBuffCount.decrementAndGet();
            }
            case DANCE: {
                return increase ? this.danceCount.incrementAndGet() : this.danceCount.decrementAndGet();
            }
            case TOGGLE: {
                return increase ? this.toggleCount.incrementAndGet() : this.toggleCount.decrementAndGet();
            }
            case DEBUFF: {
                return increase ? this.debuffCount.incrementAndGet() : this.debuffCount.decrementAndGet();
            }
            case BUFF: {
                return increase ? this.buffCount.incrementAndGet() : this.buffCount.decrementAndGet();
            }
            default: {
                return 0;
            }
        }
    }
    
    private void remove(final BuffInfo info) {
        this.remove(info, true, false, false);
    }
    
    public void remove(final BuffInfo info, final boolean removed, final boolean update, final boolean broadcast) {
        if (info == null) {
            return;
        }
        if (info.getOption() != null) {
            this.removeOption(info, removed);
        }
        else if (info.getSkill().isPassive()) {
            this.removePassive(info, removed);
        }
        else {
            this.removeActive(info, removed);
            if (GameUtils.isNpc(this.owner)) {
                this.updateEffectList(broadcast);
            }
        }
        if (update) {
            this.updateEffectList(broadcast);
        }
    }
    
    private synchronized void removeActive(final BuffInfo info, final boolean removed) {
        if (!this.actives.isEmpty()) {
            this.actives.remove(info);
            if (info == this.shortBuff) {
                this.shortBuffStatusUpdate(null);
            }
            info.stopAllEffects(removed);
            this.increaseDecreaseCount(info, false);
            info.getSkill().applyEffectScope(EffectScope.END, info, true, false);
        }
    }
    
    private void removePassive(final BuffInfo info, final boolean removed) {
        if (!this.passives.isEmpty()) {
            this.passives.remove(info);
            info.stopAllEffects(removed);
        }
    }
    
    private void removeOption(final BuffInfo info, final boolean removed) {
        if (!this.options.isEmpty()) {
            this.options.remove(info);
            info.stopAllEffects(removed);
        }
    }
    
    public void add(final BuffInfo info) {
        if (info == null) {
            return;
        }
        if (info.getEffected().isDead()) {
            return;
        }
        if (info.getSkill() == null) {
            this.addOption(info);
        }
        else if (info.getSkill().isPassive()) {
            this.addPassive(info);
        }
        else {
            this.addActive(info);
        }
        this.updateEffectList(true);
    }
    
    private synchronized void addActive(final BuffInfo info) {
        final Skill skill = info.getSkill();
        if (info.getEffected().isDead()) {
            return;
        }
        if (this.blockedAbnormalTypes.contains(skill.getAbnormalType())) {
            return;
        }
        if (skill.isTriggeredSkill()) {
            final BuffInfo triggerInfo = info.getEffected().getEffectList().getBuffInfoBySkillId(skill.getId());
            if (triggerInfo != null && triggerInfo.getSkill().getLevel() >= skill.getLevel()) {
                return;
            }
        }
        if (info.getEffector() != null) {
            if (info.getEffector() != info.getEffected() && skill.isBad()) {
                if (info.getEffected().isDebuffBlocked() || (info.getEffector().isGM() && !info.getEffector().getAccessLevel().canGiveDamage())) {
                    return;
                }
                if (GameUtils.isPlayer(info.getEffector()) && GameUtils.isPlayer(info.getEffected()) && info.getEffected().isAffected(EffectFlag.DUELIST_FURY) && !info.getEffector().isAffected(EffectFlag.DUELIST_FURY)) {
                    return;
                }
            }
            if (info.getEffected().isBuffBlocked() && !skill.isBad()) {
                return;
            }
        }
        if (this.hasAbnormalType(skill.getAbnormalType())) {
            for (final BuffInfo existingInfo : this.actives) {
                final Skill existingSkill = existingInfo.getSkill();
                if ((skill.getAbnormalType().isNone() && existingSkill.getId() == skill.getId()) || (!skill.getAbnormalType().isNone() && existingSkill.getAbnormalType() == skill.getAbnormalType())) {
                    if (!skill.getSubordinationAbnormalType().isNone() && skill.getSubordinationAbnormalType() == existingSkill.getSubordinationAbnormalType()) {
                        if (info.getEffectorObjectId() == 0 || existingInfo.getEffectorObjectId() == 0) {
                            continue;
                        }
                        if (info.getEffectorObjectId() != existingInfo.getEffectorObjectId()) {
                            continue;
                        }
                    }
                    if (skill.getAbnormalLvl() >= existingSkill.getAbnormalLvl()) {
                        if ((skill.isAbnormalInstant() || existingSkill.isIrreplacableBuff()) && skill.getId() != existingSkill.getId()) {
                            existingInfo.setInUse(false);
                            this.hiddenBuffs.incrementAndGet();
                        }
                        else {
                            this.remove(existingInfo);
                        }
                    }
                    else {
                        if (!skill.isIrreplacableBuff()) {
                            return;
                        }
                        info.setInUse(false);
                    }
                }
            }
        }
        this.increaseDecreaseCount(info, true);
        if (this.isLimitExceeded(SkillBuffType.values())) {
            for (final BuffInfo existingInfo : this.actives) {
                if (existingInfo.isInUse() && !skill.is7Signs() && this.isLimitExceeded(existingInfo.getSkill().getBuffType())) {
                    this.remove(existingInfo);
                }
                if (!this.isLimitExceeded(SkillBuffType.values())) {
                    break;
                }
            }
        }
        this.actives.add(info);
        info.initializeEffects();
    }
    
    private void addPassive(final BuffInfo info) {
        final Skill skill = info.getSkill();
        if (!skill.getAbnormalType().isNone()) {
            EffectList.LOGGER.warn("Passive {} with abnormal type: {}!", (Object)skill, (Object)skill.getAbnormalType());
        }
        if (!skill.checkCondition(info.getEffector(), info.getEffected())) {
            return;
        }
        this.passives.stream().filter(Objects::nonNull).filter(b -> b.getSkill().getId() == skill.getId()).forEach(b -> {
            b.setInUse(false);
            this.passives.remove(b);
            return;
        });
        this.passives.add(info);
        info.initializeEffects();
    }
    
    private void addOption(final BuffInfo info) {
        if (info.getOption() != null) {
            this.options.stream().filter(Objects::nonNull).filter(b -> b.getOption().getId() == info.getOption().getId()).forEach(b -> {
                b.setInUse(false);
                this.options.remove(b);
                return;
            });
            this.options.add(info);
            info.initializeEffects();
        }
    }
    
    public void updateEffectIcons(final boolean partyOnly) {
        final Player player = this.owner.getActingPlayer();
        if (player != null) {
            final Party party = player.getParty();
            final Optional<AbnormalStatusUpdate> asu = (GameUtils.isPlayer(this.owner) && !partyOnly) ? Optional.of(new AbnormalStatusUpdate()) : Optional.empty();
            final Optional<PartySpelled> ps = (party != null || GameUtils.isSummon(this.owner)) ? Optional.of(new PartySpelled(this.owner)) : Optional.empty();
            final Optional<ExOlympiadSpelledInfo> os = (player.isInOlympiadMode() && player.isOlympiadStart()) ? Optional.of(new ExOlympiadSpelledInfo(player)) : Optional.empty();
            if (!this.actives.isEmpty()) {
                final Optional optional;
                final Optional optional2;
                final Optional optional3;
                this.actives.stream().filter(Objects::nonNull).filter(BuffInfo::isInUse).forEach(info -> {
                    if (info.getSkill().isHealingPotionSkill()) {
                        this.shortBuffStatusUpdate(info);
                    }
                    else {
                        optional.ifPresent(a -> a.addSkill(info));
                        optional2.filter(p -> !info.getSkill().isToggle()).ifPresent(p -> p.addSkill(info));
                        optional3.ifPresent(o -> o.addSkill(info));
                    }
                    return;
                });
            }
            final Optional<AbnormalStatusUpdate> optional4 = asu;
            Objects.requireNonNull(this.owner);
            final WorldObject worldObject;
            optional4.ifPresent(xva$0 -> worldObject.sendPacket(xva$0));
            if (party != null) {
                final Optional<PartySpelled> optional5 = ps;
                final Party obj = party;
                Objects.requireNonNull(obj);
                optional5.ifPresent(obj::broadcastPacket);
            }
            else {
                final Optional<PartySpelled> optional6 = ps;
                Objects.requireNonNull(player);
                final Player player2;
                optional6.ifPresent(xva$0 -> player2.sendPacket(xva$0));
            }
            if (os.isPresent()) {
                final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
                if (game != null && game.isBattleStarted()) {
                    final Optional<ExOlympiadSpelledInfo> optional7 = os;
                    final OlympiadStadium stadium = game.getStadium();
                    Objects.requireNonNull(stadium);
                    optional7.ifPresent(stadium::broadcastPacketToObservers);
                }
            }
        }
        final ExAbnormalStatusUpdateFromTarget upd = new ExAbnormalStatusUpdateFromTarget(this.owner);
        final Stream<Object> map = this.owner.getStatus().getStatusListener().stream().filter((Predicate<? super Object>)GameUtils::isPlayer).map((Function<? super Object, ?>)WorldObject::getActingPlayer);
        final ExAbnormalStatusUpdateFromTarget obj2 = upd;
        Objects.requireNonNull(obj2);
        map.forEach((Consumer<? super Object>)obj2::sendTo);
        if (GameUtils.isPlayer(this.owner) && this.owner.getTarget() == this.owner) {
            this.owner.sendPacket(upd);
        }
    }
    
    public Set<AbnormalVisualEffect> getCurrentAbnormalVisualEffects() {
        return this.abnormalVisualEffects;
    }
    
    public boolean hasAbnormalVisualEffect(final AbnormalVisualEffect ave) {
        return this.abnormalVisualEffects.contains(ave);
    }
    
    public final void startAbnormalVisualEffect(final AbnormalVisualEffect... aves) {
        this.abnormalVisualEffects.addAll(Arrays.asList(aves));
        this.owner.updateAbnormalVisualEffects();
    }
    
    public final void stopAbnormalVisualEffect(final AbnormalVisualEffect... aves) {
        for (final AbnormalVisualEffect ave : aves) {
            this.abnormalVisualEffects.remove(ave);
        }
        this.owner.updateAbnormalVisualEffects();
    }
    
    private void updateEffectList(final boolean broadcast) {
        long flags = 0L;
        final Set<AbnormalType> abnormalTypeFlags = EnumSet.noneOf(AbnormalType.class);
        final Set<AbnormalVisualEffect> abnormalVisualEffectFlags = EnumSet.noneOf(AbnormalVisualEffect.class);
        final Set<BuffInfo> unhideBuffs = new HashSet<BuffInfo>();
        for (final BuffInfo info : this.actives) {
            if (info != null) {
                final Skill skill = info.getSkill();
                if (this.hiddenBuffs.get() > 0 && this.stackedEffects.contains(skill.getAbnormalType())) {
                    if (info.isInUse()) {
                        unhideBuffs.removeIf(b -> b.isAbnormalType(skill.getAbnormalType()));
                    }
                    else {
                        final Skill skill2;
                        if (!abnormalTypeFlags.contains(skill.getAbnormalType()) || unhideBuffs.removeIf(b -> b.isAbnormalType(skill2.getAbnormalType()) && b.getSkill().getAbnormalLvl() <= skill2.getAbnormalLvl())) {
                            unhideBuffs.add(info);
                        }
                    }
                }
                for (final AbstractEffect e : info.getEffects()) {
                    flags |= e.getEffectFlags();
                }
                abnormalTypeFlags.add(skill.getAbnormalType());
                if (!skill.hasAbnormalVisualEffect()) {
                    continue;
                }
                final EnumSet<AbnormalVisualEffect> visual = skill.getAbnormalVisualEffect();
                abnormalVisualEffectFlags.addAll(visual);
                this.abnormalVisualEffects.addAll(visual);
                if (!broadcast) {
                    continue;
                }
                this.owner.updateAbnormalVisualEffects();
            }
        }
        for (final BuffInfo info : this.passives) {
            if (info != null) {
                for (final AbstractEffect e2 : info.getEffects()) {
                    flags |= e2.getEffectFlags();
                }
            }
        }
        this.effectFlags = flags;
        this.stackedEffects = abnormalTypeFlags;
        unhideBuffs.forEach(b -> {
            b.setInUse(true);
            this.hiddenBuffs.decrementAndGet();
            return;
        });
        this.owner.getStats().recalculateStats(broadcast);
        if (broadcast) {
            if (!abnormalVisualEffectFlags.containsAll(this.abnormalVisualEffects)) {
                this.abnormalVisualEffects = abnormalVisualEffectFlags;
                this.owner.updateAbnormalVisualEffects();
            }
            this.updateEffectIcons(false);
        }
    }
    
    public boolean isAffected(final EffectFlag flag) {
        return (this.effectFlags & flag.getMask()) != 0x0L;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EffectList.class);
    }
}
