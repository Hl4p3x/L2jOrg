// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.model.holders.SkillUseHolder;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.network.serverpackets.MagicSkillCanceld;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.network.serverpackets.MoveToPawn;
import org.l2j.gameserver.network.serverpackets.ExRotation;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.Weapon;
import java.util.Iterator;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.Collection;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import java.lang.ref.WeakReference;
import org.slf4j.Logger;

public class SkillCaster implements Runnable
{
    private static final Logger LOGGER;
    private final WeakReference<Creature> _caster;
    private final WeakReference<WorldObject> _target;
    private final Skill _skill;
    private final Item _item;
    private final SkillCastingType _castingType;
    private int _hitTime;
    private int _cancelTime;
    private int _coolTime;
    private Collection<WorldObject> _targets;
    private ScheduledFuture<?> _task;
    private int _phase;
    
    private SkillCaster(final Creature caster, final WorldObject target, final Skill skill, final Item item, final SkillCastingType castingType, final boolean ctrlPressed, final boolean shiftPressed) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(skill);
        Objects.requireNonNull(castingType);
        this._caster = new WeakReference<Creature>(caster);
        this._target = new WeakReference<WorldObject>(target);
        this._skill = skill;
        this._item = item;
        this._castingType = castingType;
        this.calcSkillTiming(caster, skill);
    }
    
    public static SkillCaster castSkill(final Creature caster, final WorldObject target, final Skill skill, final Item item, final SkillCastingType castingType, final boolean ctrlPressed, final boolean shiftPressed) {
        return castSkill(caster, target, skill, item, castingType, ctrlPressed, shiftPressed, -1);
    }
    
    public static SkillCaster castSkill(final Creature caster, WorldObject target, final Skill skill, final Item item, final SkillCastingType castingType, final boolean ctrlPressed, final boolean shiftPressed, final int castTime) {
        if (caster == null || skill == null || castingType == null) {
            return null;
        }
        if (!checkUseConditions(caster, skill, castingType)) {
            return null;
        }
        target = skill.getTarget(caster, target, ctrlPressed, shiftPressed, false);
        if (target == null) {
            return null;
        }
        if (GameUtils.isPlayer(caster) && GameUtils.isMonster(target) && skill.getEffectPoint() > 0 && !ctrlPressed) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return null;
        }
        if (skill.getCastRange() > 0 && !GameUtils.checkIfInRange(skill.getCastRange(), caster, target, false)) {
            return null;
        }
        final SkillCaster skillCaster = new SkillCaster(caster, target, skill, item, castingType, ctrlPressed, shiftPressed);
        skillCaster.run();
        return skillCaster;
    }
    
    public static void callSkill(final Creature caster, final WorldObject target, final Collection<WorldObject> targets, final Skill skill, final Item item) {
        try {
            if (caster.isAttackingDisabled() && skill.isBad()) {
                return;
            }
            if (skill.isToggle() && caster.isAffectedBySkill(skill.getId())) {
                return;
            }
            for (final WorldObject obj : targets) {
                if (!GameUtils.isCreature(obj)) {
                    continue;
                }
                final Creature creature = (Creature)obj;
                if (!Config.RAID_DISABLE_CURSE && creature.isRaid() && creature.giveRaidCurse() && caster.getLevel() >= creature.getLevel() + 9 && (skill.isBad() || (creature.getTarget() == caster && ((Attackable)creature).getAggroList().containsKey(caster)))) {
                    final CommonSkill curse = skill.isBad() ? CommonSkill.RAID_CURSE2 : CommonSkill.RAID_CURSE;
                    final Skill curseSkill = curse.getSkill();
                    if (curseSkill != null) {
                        curseSkill.applyEffects(creature, caster);
                    }
                }
                if (skill.isStatic()) {
                    continue;
                }
                final Weapon activeWeapon = caster.getActiveWeaponItem();
                if (activeWeapon != null && !creature.isDead()) {
                    activeWeapon.applyConditionalSkills(caster, creature, skill, ItemSkillType.ON_MAGIC_SKILL);
                }
                if (!caster.hasTriggerSkills()) {
                    continue;
                }
                for (final OptionsSkillHolder holder : caster.getTriggerSkills().values()) {
                    if (((skill.isMagic() && holder.getSkillType() == OptionsSkillType.MAGIC) || (skill.isPhysical() && holder.getSkillType() == OptionsSkillType.ATTACK)) && Rnd.get(100) < holder.getChance()) {
                        triggerCast(caster, creature, holder.getSkill(), null, false);
                    }
                }
            }
            skill.activateSkill(caster, item, (WorldObject[])targets.toArray(new WorldObject[0]));
            final Player player = caster.getActingPlayer();
            if (player != null) {
                for (final WorldObject obj2 : targets) {
                    if (!GameUtils.isCreature(obj2)) {
                        continue;
                    }
                    if (skill.isBad()) {
                        if (GameUtils.isPlayable(obj2)) {
                            player.updatePvPStatus((Creature)obj2);
                            if (GameUtils.isSummon(obj2)) {
                                ((Summon)obj2).updateAndBroadcastStatus(1);
                            }
                        }
                        else if (GameUtils.isAttackable(obj2)) {
                            ((Attackable)obj2).addDamageHate(caster, 0, -skill.getEffectPoint());
                            ((Creature)obj2).addAttackerToAttackByList(caster);
                        }
                        if (!((Creature)obj2).hasAI() || skill.hasAnyEffectType(EffectType.HATE)) {
                            continue;
                        }
                        ((Creature)obj2).getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
                    }
                    else {
                        if (obj2 == player || ((skill.getEffectPoint() <= 0 || !GameUtils.isMonster(obj2)) && (!GameUtils.isPlayable(obj2) || (obj2.getActingPlayer().getPvpFlag() <= 0 && ((Creature)obj2).getReputation() >= 0)))) {
                            continue;
                        }
                        player.updatePvPStatus();
                    }
                }
                final Player caster2;
                Attackable attackable;
                WorldObject npcTarget;
                final Iterator<WorldObject> iterator4;
                WorldObject skillTarget;
                Creature originalCaster;
                World.getInstance().forEachVisibleObjectInRange(player, Npc.class, 1000, npcMob -> {
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcSkillSee(npcMob, caster2, skill, GameUtils.isSummon(caster), (WorldObject[])targets.toArray(WorldObject[]::new)), npcMob);
                    if (GameUtils.isAttackable(npcMob)) {
                        attackable = npcMob;
                        if (skill.getEffectPoint() > 0 && attackable.hasAI() && attackable.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                            npcTarget = attackable.getTarget();
                            targets.iterator();
                            while (iterator4.hasNext()) {
                                skillTarget = iterator4.next();
                                if (npcTarget == skillTarget || npcMob == skillTarget) {
                                    originalCaster = (GameUtils.isSummon(caster) ? caster : caster2);
                                    attackable.addDamageHate(originalCaster, 0, skill.getEffectPoint() * 150 / (attackable.getLevel() + 7));
                                }
                            }
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            SkillCaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/Creature;)Ljava/lang/String;, caster), (Throwable)e);
        }
    }
    
    public static void triggerCast(final Creature activeChar, final Creature target, final Skill skill) {
        triggerCast(activeChar, target, skill, null, true);
    }
    
    public static void triggerCast(final Creature activeChar, WorldObject target, final Skill skill, final Item item, final boolean ignoreTargetType) {
        try {
            if (activeChar == null || skill == null) {
                return;
            }
            if (skill.checkCondition(activeChar, target)) {
                if (activeChar.isSkillDisabled(skill)) {
                    return;
                }
                if (skill.getReuseDelay() > 0) {
                    activeChar.disableSkill(skill, skill.getReuseDelay());
                }
                if (!ignoreTargetType) {
                    final WorldObject objTarget = skill.getTarget(activeChar, false, false, false);
                    if (GameUtils.isCreature(objTarget)) {
                        target = objTarget;
                    }
                }
                final WorldObject[] targets = skill.getTargetsAffected(activeChar, target).toArray(new WorldObject[0]);
                if (!skill.isNotBroadcastable()) {
                    activeChar.broadcastPacket(new MagicSkillUse(activeChar, target, skill.getDisplayId(), skill.getLevel(), 0, 0));
                }
                skill.activateSkill(activeChar, item, targets);
            }
        }
        catch (Exception e) {
            SkillCaster.LOGGER.warn("Failed simultaneous cast: ", (Throwable)e);
        }
    }
    
    public static boolean checkUseConditions(final Creature caster, final Skill skill) {
        return checkUseConditions(caster, skill, SkillCastingType.NORMAL);
    }
    
    public static boolean checkUseConditions(final Creature caster, final Skill skill, final SkillCastingType castingType) {
        if (caster == null) {
            return false;
        }
        if (skill == null || caster.isSkillDisabled(skill) || (skill.isFlyType() && caster.isMovementDisabled())) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillUse(caster, skill, skill.isWithoutAction()), caster, TerminateReturn.class);
        if (term != null && term.terminate()) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (castingType != null && caster.isCastingNow(castingType)) {
            caster.sendPacket(ActionFailed.get(castingType));
            return false;
        }
        if (caster.getCurrentMp() < caster.getStats().getMpConsume(skill) + caster.getStats().getMpInitialConsume(skill)) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (caster.getCurrentHp() <= skill.getHpConsume()) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (!skill.isStatic()) {
            if (skill.isMagic()) {
                if (caster.isMuted()) {
                    caster.sendPacket(ActionFailed.STATIC_PACKET);
                    return false;
                }
            }
            else if (caster.isPhysicalMuted()) {
                caster.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        }
        final Weapon weapon = caster.getActiveWeaponItem();
        if (weapon != null && weapon.useWeaponSkillsOnly() && !caster.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            final List<ItemSkillHolder> weaponSkills = weapon.getSkills(ItemSkillType.NORMAL);
            if (weaponSkills != null && !weaponSkills.stream().anyMatch(sh -> sh.getSkillId() == skill.getId())) {
                caster.sendPacket(SystemMessageId.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPON_S_SKILL);
                return false;
            }
        }
        if (skill.getItemConsumeId() > 0 && skill.getItemConsumeCount() > 0 && caster.getInventory() != null) {
            final Item requiredItem = caster.getInventory().getItemByItemId(skill.getItemConsumeId());
            if (requiredItem == null || requiredItem.getCount() < skill.getItemConsumeCount()) {
                if (skill.hasAnyEffectType(EffectType.SUMMON)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.SUMMONING_A_SERVITOR_COSTS_S2_S1);
                    sm.addItemName(skill.getItemConsumeId());
                    sm.addInt(skill.getItemConsumeCount());
                    caster.sendPacket(sm);
                }
                else {
                    caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL));
                }
                return false;
            }
        }
        if (GameUtils.isPlayer(caster)) {
            final Player player = caster.getActingPlayer();
            if (player.inObserverMode()) {
                return false;
            }
            if (player.isInOlympiadMode() && skill.isBlockedInOlympiad()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_SKILL_IN_A_OLYMPIAD_MATCH);
                return false;
            }
            if (caster.hasSkillReuse(skill.getReuseHashCode())) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE);
                sm.addSkillName(skill);
                caster.sendPacket(sm);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        final boolean instantCast = this._castingType == SkillCastingType.SIMULTANEOUS || this._skill.isAbnormalInstant() || this._skill.isWithoutAction() || this._skill.isToggle();
        if (instantCast) {
            triggerCast(this._caster.get(), this._target.get(), this._skill, this._item, false);
            return;
        }
        long nextTaskDelay = 0L;
        boolean hasNextPhase = false;
        switch (this._phase++) {
            case 0: {
                hasNextPhase = this.startCasting();
                nextTaskDelay = this._hitTime;
                break;
            }
            case 1: {
                hasNextPhase = this.launchSkill();
                nextTaskDelay = this._cancelTime;
                break;
            }
            case 2: {
                hasNextPhase = this.finishSkill();
                nextTaskDelay = this._coolTime;
                break;
            }
        }
        if (hasNextPhase) {
            this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)this, nextTaskDelay);
        }
        else {
            this.stopCasting(false);
        }
    }
    
    public boolean startCasting() {
        final Creature caster = this._caster.get();
        final WorldObject target = this._target.get();
        if (caster == null || target == null) {
            return false;
        }
        this._coolTime = Formulas.calcAtkSpd(caster, this._skill, this._skill.getCoolTime());
        final int displayedCastTime = this._hitTime + this._cancelTime;
        final boolean instantCast = this._castingType == SkillCastingType.SIMULTANEOUS || this._skill.isAbnormalInstant() || this._skill.isWithoutAction();
        if (!instantCast) {
            caster.addSkillCaster(this._castingType, this);
        }
        int reuseDelay = caster.getStats().getReuseTime(this._skill);
        if (reuseDelay > 10) {
            if (!this._skill.isStatic() && this._skill.getOperateType() == SkillOperateType.A1 && Formulas.calcSkillMastery(caster, this._skill)) {
                reuseDelay = 100;
                caster.sendPacket(SystemMessageId.A_SKILL_IS_READY_TO_BE_USED_AGAIN);
            }
            if (reuseDelay > 30000) {
                caster.addTimeStamp(this._skill, reuseDelay);
            }
            else {
                caster.disableSkill(this._skill, reuseDelay);
            }
        }
        if (!instantCast) {
            caster.getAI().clientStopMoving(null);
        }
        if (target != caster) {
            caster.setHeading(MathUtil.calculateHeadingFrom(caster, target));
            caster.broadcastPacket(new ExRotation(caster.getObjectId(), caster.getHeading()));
            if (GameUtils.isPlayer(caster) && !caster.isCastingNow() && GameUtils.isCreature(target)) {
                caster.sendPacket(new MoveToPawn(caster, target, (int)MathUtil.calculateDistance2D(caster, target)));
                caster.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
        if (!this._skill.isWithoutAction()) {
            caster.stopEffectsOnAction();
        }
        final int initmpcons = caster.getStats().getMpInitialConsume(this._skill);
        if (initmpcons > 0) {
            if (initmpcons > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }
            caster.getStatus().reduceMp(initmpcons);
            final StatusUpdate su = new StatusUpdate(caster);
            su.addUpdate(StatusUpdateType.CUR_MP, (int)caster.getCurrentMp());
            caster.sendPacket(su);
        }
        final int actionId = GameUtils.isSummon(caster) ? ActionManager.getInstance().getSkillActionId(this._skill.getId()) : -1;
        if (!this._skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillUse(caster, target, this._skill.getDisplayId(), this._skill.getDisplayLevel(), displayedCastTime, reuseDelay, this._skill.getReuseDelayGroup(), actionId, this._castingType));
        }
        if (GameUtils.isPlayer(caster) && !instantCast) {
            caster.sendPacket((this._skill.getId() != 2046) ? SystemMessage.getSystemMessage(SystemMessageId.YOU_USE_S1).addSkillName(this._skill) : SystemMessage.getSystemMessage(SystemMessageId.SUMMONING_YOUR_PET));
        }
        if (this._skill.getItemConsumeId() > 0 && this._skill.getItemConsumeCount() > 0 && caster.getInventory() != null) {
            final Item requiredItem = caster.getInventory().getItemByItemId(this._skill.getItemConsumeId());
            if (this._skill.isBad() || requiredItem.getAction() == ActionType.NONE) {
                caster.destroyItem(this._skill.toString(), requiredItem.getObjectId(), this._skill.getItemConsumeCount(), caster, false);
            }
        }
        if (GameUtils.isCreature(target)) {
            this._skill.applyEffectScope(EffectScope.START, new BuffInfo(caster, (Creature)target, this._skill, false, this._item, null), true, false);
        }
        if (this._skill.isChanneling()) {
            caster.getSkillChannelizer().startChanneling(this._skill);
        }
        return true;
    }
    
    public boolean launchSkill() {
        final Creature caster = this._caster.get();
        final WorldObject target = this._target.get();
        if (caster == null || target == null) {
            return false;
        }
        if (this._skill.getEffectRange() > 0 && !GameUtils.checkIfInRange(this._skill.getEffectRange(), caster, target, true)) {
            if (GameUtils.isPlayer(caster)) {
                caster.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return false;
        }
        this._targets = this._skill.getTargetsAffected(caster, target);
        if (this._skill.isFlyType()) {
            this.handleSkillFly(caster, target);
        }
        if (!this._skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillLaunched(caster, this._skill.getDisplayId(), this._skill.getDisplayLevel(), this._castingType, this._targets));
        }
        return true;
    }
    
    public boolean finishSkill() {
        final Creature caster = this._caster.get();
        final WorldObject target = this._target.get();
        if (caster == null || target == null) {
            return false;
        }
        final StatusUpdate su = new StatusUpdate(caster);
        final double mpConsume = (this._skill.getMpConsume() > 0) ? caster.getStats().getMpConsume(this._skill) : 0.0;
        if (mpConsume > 0.0) {
            if (mpConsume > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }
            caster.getStatus().reduceMp(mpConsume);
            su.addUpdate(StatusUpdateType.CUR_MP, (int)caster.getCurrentMp());
        }
        final double consumeHp = this._skill.getHpConsume();
        if (consumeHp > 0.0) {
            if (consumeHp >= caster.getCurrentHp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
                return false;
            }
            caster.getStatus().reduceHp(consumeHp, caster, true);
            su.addUpdate(StatusUpdateType.CUR_HP, (int)caster.getCurrentHp());
        }
        if (su.hasUpdates()) {
            caster.sendPacket(su);
        }
        if (GameUtils.isPlayer(caster)) {
            if (this._skill.getMaxSoulConsumeCount() > 0 && !caster.getActingPlayer().decreaseSouls(this._skill.getMaxSoulConsumeCount(), this._skill)) {
                return false;
            }
            if (this._skill.getChargeConsumeCount() > 0 && !caster.getActingPlayer().decreaseCharges(this._skill.getChargeConsumeCount())) {
                return false;
            }
        }
        if (this._item != null && this._item.getAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS && this._skill.getItemConsumeId() > 0 && this._skill.getItemConsumeCount() > 0 && !caster.destroyItem(this._skill.toString(), this._item.getObjectId(), this._skill.getItemConsumeCount(), target, true)) {
            return false;
        }
        EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillFinishCast(caster, target, this._skill, this._skill.isWithoutAction()), caster);
        callSkill(caster, target, this._targets, this._skill, this._item);
        if (!this._skill.isWithoutAction() && this._skill.isBad() && this._skill.getTargetType() != TargetType.DOOR_TREASURE) {
            caster.getAI().clientStartAutoAttack();
        }
        caster.notifyQuestEventSkillFinished(this._skill, target);
        if (this._skill.useSoulShot()) {
            caster.consumeAndRechargeShots(ShotType.SOULSHOTS, this._targets.size());
        }
        if (this._skill.useSpiritShot()) {
            caster.consumeAndRechargeShots(ShotType.SPIRITSHOTS, this._targets.size());
        }
        return true;
    }
    
    public void stopCasting(final boolean aborted) {
        if (this._task != null) {
            this._task.cancel(false);
            this._task = null;
        }
        final Creature caster = this._caster.get();
        final WorldObject target = this._target.get();
        if (caster == null) {
            return;
        }
        caster.removeSkillCaster(this._castingType);
        if (caster.isChanneling()) {
            caster.getSkillChannelizer().stopChanneling();
        }
        if (aborted) {
            caster.broadcastPacket(new MagicSkillCanceld(caster.getObjectId()));
            caster.sendPacket(ActionFailed.get(this._castingType));
        }
        if (GameUtils.isPlayer(caster)) {
            final Player currPlayer = caster.getActingPlayer();
            final SkillUseHolder queuedSkill = currPlayer.getQueuedSkill();
            if (queuedSkill != null) {
                final Player player;
                final SkillUseHolder skillUseHolder;
                ThreadPool.execute(() -> {
                    player.setQueuedSkill(null, null, false, false);
                    player.useMagic(skillUseHolder.getSkill(), skillUseHolder.getItem(), skillUseHolder.isCtrlPressed(), skillUseHolder.isShiftPressed());
                });
                return;
            }
        }
        if (this._skill.getNextAction() != NextActionType.NONE && caster.getAI().getNextIntention() == null) {
            if (this._skill.getNextAction() == NextActionType.ATTACK && target != null && target != caster && target.isAutoAttackable(caster)) {
                caster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            }
            else if (this._skill.getNextAction() == NextActionType.CAST && target != null && target != caster && target.isAutoAttackable(caster)) {
                caster.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, this._skill, target, this._item, false, false);
            }
            else {
                caster.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING);
            }
        }
        else {
            caster.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING);
        }
    }
    
    private void calcSkillTiming(final Creature creature, final Skill skill) {
        final double timeFactor = Formulas.calcSkillTimeFactor(creature, skill);
        final double cancelTime = Formulas.calcSkillCancelTime(creature, skill);
        if (skill.getOperateType().isChanneling()) {
            this._hitTime = (int)Math.max(skill.getHitTime() - cancelTime, 0.0);
            this._cancelTime = 2866;
        }
        else {
            this._hitTime = (int)Math.max(skill.getHitTime() / timeFactor - cancelTime, 0.0);
            this._cancelTime = (int)cancelTime;
        }
        this._coolTime = (int)(skill.getCoolTime() / timeFactor);
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public Creature getCaster() {
        return this._caster.get();
    }
    
    public WorldObject getTarget() {
        return this._target.get();
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public boolean canAbortCast() {
        return this.getCaster().getTarget() == null;
    }
    
    public SkillCastingType getCastingType() {
        return this._castingType;
    }
    
    public boolean isNormalFirstType() {
        return this._castingType == SkillCastingType.NORMAL;
    }
    
    public boolean isNormalSecondType() {
        return this._castingType == SkillCastingType.NORMAL_SECOND;
    }
    
    public boolean isAnyNormalType() {
        return this._castingType == SkillCastingType.NORMAL || this._castingType == SkillCastingType.NORMAL_SECOND;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;Lorg/l2j/gameserver/engine/skill/api/Skill;Ljava/lang/Object;Lorg/l2j/gameserver/model/skills/SkillCastingType;)Ljava/lang/String;, super.toString(), this._caster.get(), this._skill, this._target.get(), this._castingType);
    }
    
    private void handleSkillFly(final Creature creature, final WorldObject target) {
        int x = 0;
        int y = 0;
        int z = 0;
        FlyToLocation.FlyType flyType = FlyToLocation.FlyType.CHARGE;
        switch (this._skill.getOperateType()) {
            case DA4:
            case DA5: {
                final double course = (this._skill.getOperateType() == SkillOperateType.DA4) ? Math.toRadians(270.0) : Math.toRadians(90.0);
                final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(target.getHeading()));
                double nRadius = creature.getCollisionRadius();
                if (GameUtils.isCreature(target)) {
                    nRadius += ((Creature)target).getCollisionRadius();
                }
                x = target.getX() + (int)(Math.cos(3.141592653589793 + radian + course) * nRadius);
                y = target.getY() + (int)(Math.sin(3.141592653589793 + radian + course) * nRadius);
                z = target.getZ();
                break;
            }
            case DA3: {
                flyType = FlyToLocation.FlyType.WARP_BACK;
                final double radian2 = Math.toRadians(MathUtil.convertHeadingToDegree(creature.getHeading()));
                x = creature.getX() + (int)(Math.cos(3.141592653589793 + radian2) * this._skill.getCastRange());
                y = creature.getY() + (int)(Math.sin(3.141592653589793 + radian2) * this._skill.getCastRange());
                z = creature.getZ();
                break;
            }
            case DA2:
            case DA1: {
                if (creature == target) {
                    final double course = Math.toRadians(180.0);
                    final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(creature.getHeading()));
                    x = creature.getX() + (int)(Math.cos(3.141592653589793 + radian + course) * this._skill.getCastRange());
                    y = creature.getY() + (int)(Math.sin(3.141592653589793 + radian + course) * this._skill.getCastRange());
                    z = creature.getZ();
                    break;
                }
                final Creature c;
                final double radius = creature.getCollisionRadius() + ((target instanceof Creature && (c = (Creature)target) == target) ? c.getCollisionRadius() : 0.0);
                final double angle = MathUtil.calculateAngleFrom(creature, target);
                x = (int)(target.getX() + radius * Math.cos(angle));
                y = (int)(target.getY() + radius * Math.sin(angle));
                z = target.getZ();
                break;
            }
        }
        final Location destination = creature.isFlying() ? new Location(x, y, z) : GeoEngine.getInstance().canMoveToTargetLoc(creature.getX(), creature.getY(), creature.getZ(), x, y, z, creature.getInstanceWorld());
        creature.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        creature.broadcastPacket(new FlyToLocation(creature, destination, flyType, 0, 0, 333));
        creature.setXYZ(destination);
        creature.revalidateZone(true);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SkillCaster.class);
    }
}
