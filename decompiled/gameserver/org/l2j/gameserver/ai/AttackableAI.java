// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.slf4j.LoggerFactory;
import java.lang.ref.WeakReference;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.function.Function;
import org.l2j.gameserver.model.AggroInfo;
import org.l2j.gameserver.model.effects.EffectType;
import java.util.List;
import java.util.Set;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableFactionCall;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.FriendlyNpc;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableHate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import java.util.Iterator;
import java.util.Objects;
import org.l2j.gameserver.enums.AISkillScope;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.taskmanager.AttackableThinkTaskManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Guard;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Attackable;
import org.slf4j.Logger;

public class AttackableAI extends CreatureAI
{
    private static final Logger LOGGER;
    private static final int RANDOM_WALK_RATE = 1;
    private static final int MAX_ATTACK_TIMEOUT = 1200;
    int lastBuffTick;
    private int _attackTimeout;
    private int _globalAggro;
    private boolean _thinking;
    private int chaostime;
    
    public AttackableAI(final Attackable attackable) {
        super(attackable);
        this.chaostime = 0;
        this._attackTimeout = Integer.MAX_VALUE;
        this._globalAggro = -10;
    }
    
    private boolean isAggressiveTowards(final Creature target) {
        if (target == null || this.getActiveChar() == null) {
            return false;
        }
        if (target.isInvul()) {
            return false;
        }
        if (GameUtils.isDoor(target)) {
            return false;
        }
        final Attackable me = this.getActiveChar();
        if (target.isAlikeDead()) {
            return false;
        }
        if (GameUtils.isPlayable(target) && !me.isRaid() && !me.canSeeThroughSilentMove() && ((Playable)target).isSilentMovingAffected()) {
            return false;
        }
        final Player player = target.getActingPlayer();
        if (player != null) {
            if (!player.getAccessLevel().canTakeAggro()) {
                return false;
            }
            if (player.isRecentFakeDeath()) {
                return false;
            }
            if (me instanceof Guard) {
                final Creature creature;
                final Attackable attackable;
                World.getInstance().forEachVisibleObjectInRange(me, Guard.class, 500, guard -> {
                    if (guard.isAttackingNow() && guard.getTarget() == creature) {
                        attackable.getAI().startFollow(creature);
                        attackable.addDamageHate(creature, 0, 10);
                    }
                    return;
                });
                if (player.getReputation() < 0) {
                    return true;
                }
            }
        }
        else if (GameUtils.isMonster(me)) {
            if (!Config.ALT_MOB_AGRO_IN_PEACEZONE && target.isInsideZone(ZoneType.PEACE)) {
                return false;
            }
            if (!me.isAggressive()) {
                return false;
            }
        }
        return (!me.isChampion() || !Config.CHAMPION_PASSIVE) && target.isAutoAttackable(me) && GeoEngine.getInstance().canSeeTarget(me, target);
    }
    
    public void startAITask() {
        AttackableThinkTaskManager.getInstance().add(this.getActiveChar());
    }
    
    @Override
    public void stopAITask() {
        AttackableThinkTaskManager.getInstance().remove(this.getActiveChar());
        super.stopAITask();
    }
    
    @Override
    synchronized void changeIntention(CtrlIntention intention, final Object... args) {
        if (intention == CtrlIntention.AI_INTENTION_IDLE || intention == CtrlIntention.AI_INTENTION_ACTIVE) {
            final Attackable npc = this.getActiveChar();
            if (!npc.isAlikeDead()) {
                if (World.getInstance().hasVisiblePlayer(npc)) {
                    intention = CtrlIntention.AI_INTENTION_ACTIVE;
                }
                else if (npc.getSpawn() != null) {
                    final Location loc = npc.getSpawn();
                    final int range = Config.MAX_DRIFT_RANGE;
                    if (!MathUtil.isInsideRadius3D(npc, loc, range + range)) {
                        intention = CtrlIntention.AI_INTENTION_ACTIVE;
                    }
                }
            }
            if (intention == CtrlIntention.AI_INTENTION_IDLE) {
                super.changeIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[0]);
                this.stopAITask();
                this.actor.detachAI();
                return;
            }
        }
        super.changeIntention(intention, args);
        this.startAITask();
    }
    
    @Override
    protected void changeIntentionToCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        this.setTarget(target);
        super.changeIntentionToCast(skill, target, item, forceUse, dontMove);
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
        this._attackTimeout = 1200 + WorldTimeController.getInstance().getGameTicks();
        if (this.lastBuffTick + 30 < WorldTimeController.getInstance().getGameTicks()) {
            for (final Skill buff : this.getActiveChar().getTemplate().getAISkills(AISkillScope.BUFF)) {
                final Creature buffTarget = this.skillTargetReconsider(buff, true);
                if (Objects.nonNull(buffTarget)) {
                    this.setTarget(buffTarget);
                    this.actor.doCast(buff);
                    this.setTarget(target);
                    break;
                }
            }
            this.lastBuffTick = WorldTimeController.getInstance().getGameTicks();
        }
        super.onIntentionAttack(target);
    }
    
    protected void thinkCast() {
        final WorldObject target = this._skill.getTarget(this.actor, this.getTarget(), this._forceUse, this._dontMove, false);
        if (this.checkTargetLost(target)) {
            return;
        }
        if (this.maybeMoveToPawn(target, this.actor.getMagicalAttackRange(this._skill))) {
            return;
        }
        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        this.actor.doCast(this._skill, this._item, this._forceUse, this._dontMove);
    }
    
    protected void thinkActive() {
        final Attackable npc = this.getActiveChar();
        WorldObject target = this.getTarget();
        if (this._globalAggro != 0) {
            if (this._globalAggro < 0) {
                ++this._globalAggro;
            }
            else {
                --this._globalAggro;
            }
        }
        if (this._globalAggro >= 0) {
            if (npc.isAggressive() || npc instanceof Guard) {
                final int range = (npc instanceof Guard) ? 500 : npc.getAggroRange();
                TerminateReturn term;
                final Attackable reference;
                int hating;
                int hating2;
                World.getInstance().forEachVisibleObjectInRange(npc, Creature.class, range, t -> {
                    if (this.isAggressiveTowards(t)) {
                        if (GameUtils.isPlayable(t)) {
                            term = EventDispatcher.getInstance().notifyEvent(new OnAttackableHate(this.getActiveChar(), t.getActingPlayer(), GameUtils.isSummon(t)), this.getActiveChar(), TerminateReturn.class);
                            if (term == null || !term.terminate()) {
                                hating = reference.getHating(t);
                                if (hating == 0) {
                                    reference.addDamageHate(t, 0, 1);
                                }
                                if (reference instanceof Guard) {
                                    World.getInstance().forEachVisibleObjectInRange(reference, Guard.class, 500, guard -> guard.addDamageHate(t, 0, 10));
                                }
                            }
                        }
                        else if (t instanceof FriendlyNpc) {
                            hating2 = reference.getHating(t);
                            if (hating2 == 0) {
                                reference.addDamageHate(t, 0, t.getHateBaseAmount());
                            }
                        }
                    }
                    return;
                });
            }
            Creature hated;
            if (npc.isConfused() && GameUtils.isCreature(target)) {
                hated = (Creature)target;
            }
            else {
                hated = npc.getMostHated();
            }
            if (hated != null && !npc.isCoreAIDisabled()) {
                final int aggro = npc.getHating(hated);
                if (aggro + this._globalAggro > 0) {
                    if (!npc.isRunning()) {
                        npc.setRunning();
                    }
                    this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated);
                }
                return;
            }
        }
        if (npc.getCurrentHp() == npc.getMaxHp() && npc.getCurrentMp() == npc.getMaxMp() && !npc.getAttackByList().isEmpty() && Rnd.get(500) == 0) {
            npc.clearAggroList();
            npc.getAttackByList().clear();
        }
        if (!npc.canReturnToSpawnPoint()) {
            return;
        }
        if ((npc instanceof Guard || npc instanceof Defender) && !GameUtils.isWalker(npc) && !npc.isRandomWalkingEnabled()) {
            npc.returnHome();
        }
        final Creature leader = npc.getLeader();
        if (leader != null && !leader.isAlikeDead()) {
            final int minRadius = 30;
            int offset;
            if (npc.isRaidMinion()) {
                offset = 500;
            }
            else {
                offset = 200;
            }
            if (leader.isRunning()) {
                npc.setRunning();
            }
            else {
                npc.setWalking();
            }
            if (!MathUtil.isInsideRadius3D(npc, leader, offset)) {
                int x1 = Rnd.get(60, offset * 2);
                int y1 = Rnd.get(x1, offset * 2);
                y1 = (int)Math.sqrt(y1 * y1 - x1 * x1);
                if (x1 > offset + 30) {
                    x1 = leader.getX() + x1 - offset;
                }
                else {
                    x1 = leader.getX() - x1 + 30;
                }
                if (y1 > offset + 30) {
                    y1 = leader.getY() + y1 - offset;
                }
                else {
                    y1 = leader.getY() - y1 + 30;
                }
                this.moveTo(x1, y1, leader.getZ());
                return;
            }
            if (Rnd.chance(1)) {
                for (final Skill sk : npc.getTemplate().getAISkills(AISkillScope.BUFF)) {
                    target = this.skillTargetReconsider(sk, true);
                    if (target != null) {
                        this.setTarget(target);
                        npc.doCast(sk);
                    }
                }
            }
        }
        else if (npc.getSpawn() != null && Rnd.chance(1) && npc.isRandomWalkingEnabled()) {
            int x2 = 0;
            int y2 = 0;
            int z1 = 0;
            final int range2 = Config.MAX_DRIFT_RANGE;
            for (final Skill sk2 : npc.getTemplate().getAISkills(AISkillScope.BUFF)) {
                target = this.skillTargetReconsider(sk2, true);
                if (target != null) {
                    this.setTarget(target);
                    npc.doCast(sk2);
                    return;
                }
            }
            x2 = npc.getSpawn().getX();
            y2 = npc.getSpawn().getY();
            z1 = npc.getSpawn().getZ();
            if (!MathUtil.isInsideRadius2D(npc, x2, y2, range2)) {
                npc.setisReturningToSpawnPoint(true);
            }
            else {
                final int deltaX = Rnd.get(range2 * 2);
                int deltaY = Rnd.get(deltaX, range2 * 2);
                deltaY = (int)Math.sqrt(deltaY * deltaY - deltaX * deltaX);
                x2 = deltaX + x2 - range2;
                y2 = deltaY + y2 - range2;
                z1 = npc.getZ();
            }
            final Location moveLoc = this.actor.isFlying() ? new Location(x2, y2, z1) : GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), x2, y2, z1, npc.getInstanceWorld());
            this.moveTo(moveLoc.getX(), moveLoc.getY(), moveLoc.getZ());
        }
    }
    
    protected void thinkAttack() {
        final Attackable npc = this.getActiveChar();
        if (npc == null || npc.isCastingNow()) {
            return;
        }
        if (Config.AGGRO_DISTANCE_CHECK_ENABLED && GameUtils.isMonster(npc) && !GameUtils.isWalker(npc)) {
            final Spawn spawn = npc.getSpawn();
            if (spawn != null && MathUtil.calculateDistance3D(npc, spawn.getLocation()) > Config.AGGRO_DISTANCE_CHECK_RANGE && (Config.AGGRO_DISTANCE_CHECK_RAIDS || !npc.isRaid()) && (Config.AGGRO_DISTANCE_CHECK_INSTANCES || !npc.isInInstance())) {
                if (Config.AGGRO_DISTANCE_CHECK_RESTORE_LIFE) {
                    npc.setCurrentHp(npc.getMaxHp());
                    npc.setCurrentMp(npc.getMaxMp());
                }
                npc.abortAttack();
                npc.clearAggroList();
                npc.getAttackByList().clear();
                if (npc.hasAI()) {
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, spawn.getLocation());
                }
                else {
                    npc.teleToLocation(spawn.getLocation(), true);
                }
                if (((Monster)this.actor).hasMinions()) {
                    for (final Monster minion : ((Monster)this.actor).getMinionList().getSpawnedMinions()) {
                        if (Config.AGGRO_DISTANCE_CHECK_RESTORE_LIFE) {
                            minion.setCurrentHp(minion.getMaxHp());
                            minion.setCurrentMp(minion.getMaxMp());
                        }
                        minion.abortAttack();
                        minion.clearAggroList();
                        minion.getAttackByList().clear();
                        if (minion.hasAI()) {
                            minion.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, spawn.getLocation());
                        }
                        else {
                            minion.teleToLocation(spawn.getLocation(), true);
                        }
                    }
                }
                return;
            }
        }
        Creature target = npc.getMostHated();
        if (this.getTarget() != target) {
            this.setTarget(target);
        }
        if (target == null || target.isAlikeDead()) {
            npc.stopHating(target);
            return;
        }
        if (this._attackTimeout < WorldTimeController.getInstance().getGameTicks()) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            npc.setWalking();
            if (GameUtils.isMonster(npc) && npc.getSpawn() != null && !npc.isInInstance() && (npc.isInCombat() || !World.getInstance().hasVisiblePlayer(npc))) {
                npc.teleToLocation(npc.getSpawn(), false);
            }
            return;
        }
        final int collision = npc.getTemplate().getCollisionRadius();
        final Set<Integer> clans = this.getActiveChar().getTemplate().getClans();
        if (clans != null && !clans.isEmpty()) {
            final int factionRange = npc.getTemplate().getClanHelpRange() + collision;
            try {
                final Creature finalTarget = target;
                final Creature target2;
                final Attackable attackable;
                World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, factionRange, called -> {
                    if (!this.getActiveChar().getTemplate().isClan(called.getTemplate().getClans())) {
                        return;
                    }
                    else {
                        if (called.hasAI() && Math.abs(target2.getZ() - called.getZ()) < 600 && attackable.getAttackByList().stream().anyMatch(o -> o.get() == target2) && (called.getAI().intention == CtrlIntention.AI_INTENTION_IDLE || called.getAI().intention == CtrlIntention.AI_INTENTION_ACTIVE)) {
                            if (GameUtils.isPlayable(target2)) {
                                called.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target2, 1);
                                EventDispatcher.getInstance().notifyEventAsync(new OnAttackableFactionCall(called, this.getActiveChar(), target2.getActingPlayer(), GameUtils.isSummon(target2)), called);
                            }
                            else if (GameUtils.isAttackable(called) && called.getAI().intention != CtrlIntention.AI_INTENTION_ATTACK) {
                                called.addDamageHate(target2, 0, attackable.getHating(target2));
                                called.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target2);
                            }
                        }
                        return;
                    }
                });
            }
            catch (NullPointerException e) {
                AttackableAI.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
            }
        }
        if (npc.isCoreAIDisabled()) {
            return;
        }
        final int combinedCollision = collision + target.getTemplate().getCollisionRadius();
        final List<Skill> aiSuicideSkills = npc.getTemplate().getAISkills(AISkillScope.SUICIDE);
        if (!aiSuicideSkills.isEmpty() && (int)(npc.getCurrentHp() / npc.getMaxHp() * 100.0) < 30 && npc.hasSkillChance()) {
            final Skill skill = aiSuicideSkills.get(Rnd.get(aiSuicideSkills.size()));
            if (SkillCaster.checkUseConditions(npc, skill) && this.checkSkillTarget(skill, target)) {
                npc.doCast(skill);
                AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;)Ljava/lang/String;, this, skill));
                return;
            }
        }
        if (!npc.isMovementDisabled() && Rnd.get(100) <= 3) {
            final Creature actualTarget = target;
            final Object obj;
            final ILocational object;
            final int radius;
            World.getInstance().forAnyVisibleObject(npc, Attackable.class, nearby -> this.moveIfNeed(npc, actualTarget, collision, combinedCollision), nearby -> !nearby.equals(obj) && MathUtil.isInsideRadius2D(object, nearby, radius));
        }
        if (!npc.isMovementDisabled() && npc.getTemplate().getDodge() > 0 && Rnd.get(100) <= npc.getTemplate().getDodge()) {
            final double distance2 = MathUtil.calculateDistanceSq2D(npc, target);
            if (Math.sqrt(distance2) <= 60 + combinedCollision) {
                int posX = npc.getX();
                int posY = npc.getY();
                final int posZ = npc.getZ() + 30;
                if (target.getX() < posX) {
                    posX += 300;
                }
                else {
                    posX -= 300;
                }
                if (target.getY() < posY) {
                    posY += 300;
                }
                else {
                    posY -= 300;
                }
                if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ, npc.getInstanceWorld())) {
                    this.setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(posX, posY, posZ, 0));
                }
                return;
            }
        }
        if (npc.isRaid() || npc.isRaidMinion()) {
            ++this.chaostime;
            boolean changeTarget = false;
            if (npc instanceof RaidBoss && this.chaostime > Config.RAID_CHAOS_TIME) {
                final double multiplier = ((Monster)npc).hasMinions() ? 200.0 : 100.0;
                changeTarget = (Rnd.get(100) <= 100.0 - npc.getCurrentHp() * multiplier / npc.getMaxHp());
            }
            else if (npc instanceof GrandBoss && this.chaostime > Config.GRAND_CHAOS_TIME) {
                final double chaosRate = 100.0 - npc.getCurrentHp() * 300.0 / npc.getMaxHp();
                changeTarget = ((chaosRate <= 10.0 && Rnd.get(100) <= 10) || (chaosRate > 10.0 && Rnd.get(100) <= chaosRate));
            }
            else if (this.chaostime > Config.MINION_CHAOS_TIME) {
                changeTarget = (Rnd.get(100) <= 100.0 - npc.getCurrentHp() * 200.0 / npc.getMaxHp());
            }
            if (changeTarget) {
                target = this.targetReconsider(true);
                if (target != null) {
                    this.setTarget(target);
                    this.chaostime = 0;
                    return;
                }
            }
        }
        if (target == null) {
            target = this.targetReconsider(false);
            if (target == null) {
                return;
            }
            this.setTarget(target);
        }
        if (npc.hasSkillChance()) {
            if (!npc.getTemplate().getAISkills(AISkillScope.HEAL).isEmpty()) {
                final Skill healSkill = npc.getTemplate().getAISkills(AISkillScope.HEAL).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.HEAL).size()));
                if (SkillCaster.checkUseConditions(npc, healSkill)) {
                    final Creature healTarget = this.skillTargetReconsider(healSkill, false);
                    if (healTarget != null) {
                        final double healChance = (100 - healTarget.getCurrentHpPercent()) * 1.5;
                        if (Rnd.get(100) < healChance && this.checkSkillTarget(healSkill, healTarget)) {
                            this.setTarget(healTarget);
                            npc.doCast(healSkill);
                            AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, healSkill, this.getTarget()));
                            return;
                        }
                    }
                }
            }
            if (!npc.getTemplate().getAISkills(AISkillScope.BUFF).isEmpty()) {
                final Skill buffSkill = npc.getTemplate().getAISkills(AISkillScope.BUFF).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.BUFF).size()));
                if (SkillCaster.checkUseConditions(npc, buffSkill)) {
                    final Creature buffTarget = this.skillTargetReconsider(buffSkill, true);
                    if (this.checkSkillTarget(buffSkill, buffTarget)) {
                        this.setTarget(buffTarget);
                        npc.doCast(buffSkill);
                        AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, buffSkill, this.getTarget()));
                        return;
                    }
                }
            }
            if (target.isMoving() && !npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).isEmpty()) {
                final Skill immobolizeSkill = npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).size()));
                if (SkillCaster.checkUseConditions(npc, immobolizeSkill) && this.checkSkillTarget(immobolizeSkill, target)) {
                    npc.doCast(immobolizeSkill);
                    AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, immobolizeSkill, this.getTarget()));
                    return;
                }
            }
            if (target.isCastingNow() && !npc.getTemplate().getAISkills(AISkillScope.COT).isEmpty()) {
                final Skill muteSkill = npc.getTemplate().getAISkills(AISkillScope.COT).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.COT).size()));
                if (SkillCaster.checkUseConditions(npc, muteSkill) && this.checkSkillTarget(muteSkill, target)) {
                    npc.doCast(muteSkill);
                    AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, muteSkill, this.getTarget()));
                    return;
                }
            }
            if (!npc.getShortRangeSkills().isEmpty()) {
                final Skill shortRangeSkill = npc.getShortRangeSkills().get(Rnd.get(npc.getShortRangeSkills().size()));
                if (SkillCaster.checkUseConditions(npc, shortRangeSkill) && this.checkSkillTarget(shortRangeSkill, target)) {
                    npc.doCast(shortRangeSkill);
                    AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, shortRangeSkill, this.getTarget()));
                    return;
                }
            }
            if (!npc.getLongRangeSkills().isEmpty()) {
                final Skill longRangeSkill = npc.getLongRangeSkills().get(Rnd.get(npc.getLongRangeSkills().size()));
                if (SkillCaster.checkUseConditions(npc, longRangeSkill) && this.checkSkillTarget(longRangeSkill, target)) {
                    npc.doCast(longRangeSkill);
                    AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, longRangeSkill, this.getTarget()));
                    return;
                }
            }
            if (!npc.getTemplate().getAISkills(AISkillScope.GENERAL).isEmpty()) {
                final Skill generalSkill = npc.getTemplate().getAISkills(AISkillScope.GENERAL).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.GENERAL).size()));
                if (SkillCaster.checkUseConditions(npc, generalSkill) && this.checkSkillTarget(generalSkill, target)) {
                    npc.doCast(generalSkill);
                    AttackableAI.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/ai/AttackableAI;Lorg/l2j/gameserver/engine/skill/api/Skill;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, generalSkill, this.getTarget()));
                    return;
                }
            }
        }
        final int range = npc.getPhysicalAttackRange() + combinedCollision;
        if (!MathUtil.isInsideRadius2D(npc, target, range)) {
            if (this.checkTarget(target)) {
                this.moveToPawn(target, range);
                return;
            }
            target = this.targetReconsider(false);
            if (target == null) {
                return;
            }
            this.setTarget(target);
        }
        this.actor.doAutoAttack(target);
    }
    
    private void moveIfNeed(final Attackable npc, final Creature target, final int collision, final int combinedCollision) {
        int newX = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newX += target.getX();
        }
        else {
            newX = target.getX() - newX;
        }
        int newY = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newY += target.getY();
        }
        else {
            newY = target.getY() - newY;
        }
        if (!MathUtil.isInsideRadius2D(npc, newX, newY, collision)) {
            final int newZ = npc.getZ() + 30;
            this.moveTo(GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ, npc.getInstanceWorld()));
        }
    }
    
    private boolean checkSkillTarget(final Skill skill, final WorldObject target) {
        if (target == null) {
            return false;
        }
        if (skill.getTarget(this.getActiveChar(), target, false, this.getActiveChar().isMovementDisabled(), false) == null) {
            return false;
        }
        if (!GameUtils.checkIfInRange(skill.getCastRange(), this.getActiveChar(), target, true)) {
            return false;
        }
        if (GameUtils.isCreature(target)) {
            if (skill.isContinuous()) {
                if (((Creature)target).getEffectList().hasAbnormalType(skill.getAbnormalType(), i -> i.getSkill().getAbnormalLvl() >= skill.getAbnormalLvl())) {
                    return false;
                }
                if ((!skill.isDebuff() || !skill.isBad()) && target.isAutoAttackable(this.getActiveChar())) {
                    return false;
                }
            }
            if (skill.hasAnyEffectType(EffectType.DISPEL, EffectType.DISPEL_BY_SLOT)) {
                if (skill.isBad()) {
                    if (((Creature)target).getEffectList().getBuffCount() == 0) {
                        return false;
                    }
                }
                else if (((Creature)target).getEffectList().getDebuffCount() == 0) {
                    return false;
                }
            }
            return ((Creature)target).getCurrentHp() != ((Creature)target).getMaxHp() || !skill.hasAnyEffectType(EffectType.HEAL);
        }
        return true;
    }
    
    private boolean checkTarget(final WorldObject target) {
        if (target == null) {
            return false;
        }
        final Attackable npc = this.getActiveChar();
        if (!GameUtils.isCreature(target)) {
            return true;
        }
        if (((Creature)target).isDead()) {
            return false;
        }
        if (npc.isMovementDisabled()) {
            if (!MathUtil.isInsideRadius2D(npc, target, npc.getPhysicalAttackRange() + npc.getTemplate().getCollisionRadius() + ((Creature)target).getTemplate().getCollisionRadius())) {
                return false;
            }
            if (!GeoEngine.getInstance().canSeeTarget(npc, target)) {
                return false;
            }
        }
        return target.isAutoAttackable(npc);
    }
    
    private Creature skillTargetReconsider(final Skill skill, final boolean insideCastRange) {
        final Attackable npc = this.getActiveChar();
        if (!SkillCaster.checkUseConditions(npc, skill)) {
            return null;
        }
        final boolean isBad = skill.isContinuous() ? skill.isDebuff() : skill.isBad();
        final int range = insideCastRange ? (skill.getCastRange() + this.getActiveChar().getTemplate().getCollisionRadius()) : 2000;
        if (isBad) {
            final Stream<Creature> filter = npc.getAggroList().values().stream().map((Function<? super AggroInfo, ? extends Creature>)AggroInfo::getAttacker).filter(c -> this.checkSkillTarget(skill, c));
            final Attackable obj = npc;
            Objects.requireNonNull(obj);
            return filter.max(Comparator.comparingInt((ToIntFunction<? super Object>)obj::getHating)).orElse(null);
        }
        if (skill.hasAnyEffectType(EffectType.HEAL)) {
            return World.getInstance().findFirstVisibleObject(npc, Creature.class, range, true, c -> this.checkSkillTarget(skill, c), Comparator.comparingInt(Creature::getCurrentHpPercent));
        }
        return World.getInstance().findAnyVisibleObject(npc, Creature.class, range, true, c -> this.checkSkillTarget(skill, c));
    }
    
    private Creature targetReconsider(final boolean randomTarget) {
        final Attackable npc = this.getActiveChar();
        if (randomTarget) {
            Stream<Creature> stream = npc.getAggroList().values().stream().map((Function<? super AggroInfo, ? extends Creature>)AggroInfo::getAttacker).filter(this::checkTarget);
            if (npc.isAggressive()) {
                stream = Stream.concat((Stream<? extends Creature>)stream, World.getInstance().getVisibleObjectsInRange(npc, Creature.class, npc.getAggroRange(), this::checkTarget).stream());
            }
            return stream.findAny().orElse(null);
        }
        return npc.getAggroList().values().stream().filter(a -> this.checkTarget(a.getAttacker())).min(Comparator.comparingInt(AggroInfo::getHate)).map((Function<? super AggroInfo, ? extends Creature>)AggroInfo::getAttacker).orElse(npc.isAggressive() ? World.getInstance().findAnyVisibleObject(npc, Creature.class, npc.getAggroRange(), false, this::checkTarget) : null);
    }
    
    @Override
    public void onEvtThink() {
        if (this._thinking || this.getActiveChar().isAllSkillsDisabled()) {
            return;
        }
        this._thinking = true;
        try {
            switch (this.getIntention()) {
                case AI_INTENTION_ACTIVE: {
                    this.thinkActive();
                    break;
                }
                case AI_INTENTION_ATTACK: {
                    this.thinkAttack();
                    break;
                }
                case AI_INTENTION_CAST: {
                    this.thinkCast();
                    break;
                }
            }
        }
        catch (Exception ex) {}
        finally {
            this._thinking = false;
        }
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
        final Attackable me = this.getActiveChar();
        final WorldObject target = this.getTarget();
        this._attackTimeout = 1200 + WorldTimeController.getInstance().getGameTicks();
        if (this._globalAggro < 0) {
            this._globalAggro = 0;
        }
        me.addDamageHate(attacker, 0, 1);
        if (!me.isRunning()) {
            me.setRunning();
        }
        if (!this.getActiveChar().isCoreAIDisabled()) {
            if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
                this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
            }
            else if (me.getMostHated() != target) {
                this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
            }
        }
        if (GameUtils.isMonster(me)) {
            Monster master = (Monster)me;
            if (master.hasMinions()) {
                master.getMinionList().onAssist(me, attacker);
            }
            master = master.getLeader();
            if (master != null && master.hasMinions()) {
                master.getMinionList().onAssist(me, attacker);
            }
        }
        super.onEvtAttacked(attacker);
    }
    
    @Override
    protected void onEvtAggression(final Creature target, final int aggro) {
        final Attackable me = this.getActiveChar();
        if (me.isDead()) {
            return;
        }
        if (target != null) {
            me.addDamageHate(target, 0, aggro);
            if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
                if (!me.isRunning()) {
                    me.setRunning();
                }
                this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            }
            if (GameUtils.isMonster(me)) {
                Monster master = (Monster)me;
                if (master.hasMinions()) {
                    master.getMinionList().onAssist(me, target);
                }
                master = master.getLeader();
                if (master != null && master.hasMinions()) {
                    master.getMinionList().onAssist(me, target);
                }
            }
        }
    }
    
    @Override
    protected void onIntentionActive() {
        this._attackTimeout = Integer.MAX_VALUE;
        super.onIntentionActive();
    }
    
    public void setGlobalAggro(final int value) {
        this._globalAggro = value;
    }
    
    @Override
    public WorldObject getTarget() {
        return this.actor.getTarget();
    }
    
    @Override
    public void setTarget(final WorldObject target) {
        this.actor.setTarget(target);
    }
    
    public Attackable getActiveChar() {
        return (Attackable)this.actor;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AttackableAI.class);
    }
}
