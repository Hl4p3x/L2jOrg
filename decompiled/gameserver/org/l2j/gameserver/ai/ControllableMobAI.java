// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import java.util.List;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.Location;
import java.util.Iterator;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.MobGroup;
import org.l2j.gameserver.model.actor.Creature;

public final class ControllableMobAI extends AttackableAI
{
    public static final int AI_IDLE = 1;
    public static final int AI_NORMAL = 2;
    public static final int AI_FORCEATTACK = 3;
    public static final int AI_FOLLOW = 4;
    public static final int AI_CAST = 5;
    public static final int AI_ATTACK_GROUP = 6;
    private int _alternateAI;
    private boolean _isThinking;
    private boolean _isNotMoving;
    private Creature _forcedTarget;
    private MobGroup _targetGroup;
    
    public ControllableMobAI(final ControllableMob controllableMob) {
        super(controllableMob);
        this.setAlternateAI(1);
    }
    
    protected void thinkFollow() {
        final Attackable me = (Attackable)this.actor;
        if (!GameUtils.checkIfInRange(300, me, this.getForcedTarget(), true)) {
            final int signX = Rnd.nextBoolean() ? -1 : 1;
            final int signY = Rnd.nextBoolean() ? -1 : 1;
            final int randX = Rnd.get(300);
            final int randY = Rnd.get(300);
            this.moveTo(this.getForcedTarget().getX() + signX * randX, this.getForcedTarget().getY() + signY * randY, this.getForcedTarget().getZ());
        }
    }
    
    @Override
    public void onEvtThink() {
        if (this._isThinking) {
            return;
        }
        this.setThinking(true);
        try {
            switch (this._alternateAI) {
                case 1: {
                    if (this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
                        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        break;
                    }
                    break;
                }
                case 4: {
                    this.thinkFollow();
                    break;
                }
                case 5: {
                    this.thinkCast();
                    break;
                }
                case 3: {
                    this.thinkForceAttack();
                    break;
                }
                case 6: {
                    this.thinkAttackGroup();
                    break;
                }
                default: {
                    if (this.getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                        this.thinkActive();
                        break;
                    }
                    if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                        this.thinkAttack();
                        break;
                    }
                    break;
                }
            }
        }
        finally {
            this.setThinking(false);
        }
    }
    
    @Override
    protected void thinkCast() {
        WorldObject target = this._skill.getTarget(this.actor, this._forceUse, this._dontMove, false);
        if (!GameUtils.isCreature(target) || ((Creature)target).isAlikeDead()) {
            target = this._skill.getTarget(this.actor, this.findNextRndTarget(), this._forceUse, this._dontMove, false);
        }
        if (target == null) {
            return;
        }
        this.setTarget(target);
        if (!this.actor.isMuted()) {
            int max_range = 0;
            for (final Skill sk : this.actor.getAllSkills()) {
                if (GameUtils.checkIfInRange(sk.getCastRange(), this.actor, target, true) && !this.actor.isSkillDisabled(sk) && this.actor.getCurrentMp() > this.actor.getStats().getMpConsume(sk)) {
                    this.actor.doCast(sk);
                    return;
                }
                max_range = Math.max(max_range, sk.getCastRange());
            }
            if (!this._isNotMoving) {
                this.moveToPawn(target, max_range);
            }
        }
    }
    
    protected void thinkAttackGroup() {
        final Creature target = this.getForcedTarget();
        if (target == null || target.isAlikeDead()) {
            this.setForcedTarget(this.findNextGroupTarget());
            this.clientStopMoving(null);
        }
        if (target == null) {
            return;
        }
        this.setTarget(target);
        final ControllableMob theTarget = (ControllableMob)target;
        final ControllableMobAI ctrlAi = (ControllableMobAI)theTarget.getAI();
        ctrlAi.forceAttack(this.actor);
        final double dist2 = MathUtil.calculateDistanceSq2D(this.actor, target);
        int max_range;
        final int range = max_range = this.actor.getPhysicalAttackRange() + this.actor.getTemplate().getCollisionRadius() + target.getTemplate().getCollisionRadius();
        if (!this.actor.isMuted() && dist2 > (range + 20) * (range + 20)) {
            for (final Skill sk : this.actor.getAllSkills()) {
                final int castRange = sk.getCastRange();
                if (castRange * castRange >= dist2 && !this.actor.isSkillDisabled(sk) && this.actor.getCurrentMp() > this.actor.getStats().getMpConsume(sk)) {
                    this.actor.doCast(sk);
                    return;
                }
                max_range = Math.max(max_range, castRange);
            }
            if (!this._isNotMoving) {
                this.moveToPawn(target, range);
            }
            return;
        }
        this.actor.doAutoAttack(target);
    }
    
    protected void thinkForceAttack() {
        if (this.getForcedTarget() == null || this.getForcedTarget().isAlikeDead()) {
            this.clientStopMoving(null);
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            this.setAlternateAI(1);
        }
        this.setTarget(this.getForcedTarget());
        final double dist2 = MathUtil.calculateDistanceSq2D(this.actor, this.getForcedTarget());
        int max_range;
        final int range = max_range = this.actor.getPhysicalAttackRange() + this.actor.getTemplate().getCollisionRadius() + this.getForcedTarget().getTemplate().getCollisionRadius();
        if (!this.actor.isMuted() && dist2 > (range + 20) * (range + 20)) {
            for (final Skill sk : this.actor.getAllSkills()) {
                final int castRange = sk.getCastRange();
                if (castRange * castRange >= dist2 && !this.actor.isSkillDisabled(sk) && this.actor.getCurrentMp() > this.actor.getStats().getMpConsume(sk)) {
                    this.actor.doCast(sk);
                    return;
                }
                max_range = Math.max(max_range, castRange);
            }
            if (!this._isNotMoving) {
                this.moveToPawn(this.getForcedTarget(), this.actor.getPhysicalAttackRange());
            }
            return;
        }
        this.actor.doAutoAttack(this.getForcedTarget());
    }
    
    @Override
    protected void thinkAttack() {
        Creature target = this.getForcedTarget();
        if (target == null || target.isAlikeDead()) {
            if (target != null) {
                final Attackable npc2 = (Attackable)this.actor;
                npc2.stopHating(target);
            }
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        else {
            final Creature finalTarget = target;
            if (((Npc)this.actor).getTemplate().getClans() != null) {
                final Object arg0;
                World.getInstance().forEachVisibleObject(this.actor, Npc.class, npc -> {
                    if (!npc.isInMyClan((Npc)this.actor)) {
                        return;
                    }
                    else {
                        if (MathUtil.isInsideRadius3D(this.actor, npc, npc.getTemplate().getClanHelpRange())) {
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, arg0, 1);
                        }
                        return;
                    }
                });
            }
            this.setTarget(target);
            final double dist2 = MathUtil.calculateDistanceSq2D(this.actor, target);
            int max_range;
            final int range = max_range = this.actor.getPhysicalAttackRange() + this.actor.getTemplate().getCollisionRadius() + target.getTemplate().getCollisionRadius();
            if (!this.actor.isMuted() && dist2 > (range + 20) * (range + 20)) {
                for (final Skill sk : this.actor.getAllSkills()) {
                    final int castRange = sk.getCastRange();
                    if (castRange * castRange >= dist2 && !this.actor.isSkillDisabled(sk) && this.actor.getCurrentMp() > this.actor.getStats().getMpConsume(sk)) {
                        this.actor.doCast(sk);
                        return;
                    }
                    max_range = Math.max(max_range, castRange);
                }
                this.moveToPawn(target, range);
                return;
            }
            Creature hated;
            if (this.actor.isConfused()) {
                hated = this.findNextRndTarget();
            }
            else {
                hated = target;
            }
            if (hated == null) {
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            }
            if (hated != target) {
                target = hated;
            }
            if (!this.actor.isMuted() && Rnd.get(5) == 3) {
                for (final Skill sk2 : this.actor.getAllSkills()) {
                    final int castRange2 = sk2.getCastRange();
                    if (castRange2 * castRange2 >= dist2 && !this.actor.isSkillDisabled(sk2) && this.actor.getCurrentMp() < this.actor.getStats().getMpConsume(sk2)) {
                        this.actor.doCast(sk2);
                        return;
                    }
                }
            }
            this.actor.doAutoAttack(target);
        }
    }
    
    @Override
    protected void thinkActive() {
        Creature hated;
        if (this.actor.isConfused()) {
            hated = this.findNextRndTarget();
        }
        else {
            final WorldObject target = this.actor.getTarget();
            hated = (GameUtils.isCreature(target) ? ((Creature)target) : null);
        }
        if (hated != null) {
            this.actor.setRunning();
            this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated);
        }
    }
    
    private boolean checkAutoAttackCondition(final Creature target) {
        if (!GameUtils.isAttackable(this.actor)) {
            return false;
        }
        final Attackable me = (Attackable)this.actor;
        return !GameUtils.isNpc(target) && !GameUtils.isDoor(target) && !target.isAlikeDead() && MathUtil.isInsideRadius2D(me, target, me.getAggroRange()) && Math.abs(this.actor.getZ() - target.getZ()) <= 100 && !target.isInvul() && (!GameUtils.isPlayer(target) || !((Player)target).isSpawnProtected()) && (!GameUtils.isPlayable(target) || !((Playable)target).isSilentMovingAffected()) && !GameUtils.isNpc(target) && me.isAggressive();
    }
    
    private Creature findNextRndTarget() {
        final List<Creature> potentialTarget = new ArrayList<Creature>();
        final List<Creature> list;
        World.getInstance().forEachVisibleObject(this.actor, Creature.class, target -> {
            if (GameUtils.checkIfInShortRange(((Attackable)this.actor).getAggroRange(), this.actor, target, true) && this.checkAutoAttackCondition(target)) {
                list.add(target);
            }
            return;
        });
        return potentialTarget.isEmpty() ? null : potentialTarget.get(Rnd.get(potentialTarget.size()));
    }
    
    private ControllableMob findNextGroupTarget() {
        return this.getGroupTarget().getRandomMob();
    }
    
    public int getAlternateAI() {
        return this._alternateAI;
    }
    
    public void setAlternateAI(final int _alternateai) {
        this._alternateAI = _alternateai;
    }
    
    public void forceAttack(final Creature target) {
        this.setAlternateAI(3);
        this.setForcedTarget(target);
    }
    
    public void forceAttackGroup(final MobGroup group) {
        this.setForcedTarget(null);
        this.setGroupTarget(group);
        this.setAlternateAI(6);
    }
    
    public void stop() {
        this.setAlternateAI(1);
        this.clientStopMoving(null);
    }
    
    public void move(final int x, final int y, final int z) {
        this.moveTo(x, y, z);
    }
    
    public void follow(final Creature target) {
        this.setAlternateAI(4);
        this.setForcedTarget(target);
    }
    
    public boolean isThinking() {
        return this._isThinking;
    }
    
    public void setThinking(final boolean isThinking) {
        this._isThinking = isThinking;
    }
    
    public boolean isNotMoving() {
        return this._isNotMoving;
    }
    
    public void setNotMoving(final boolean isNotMoving) {
        this._isNotMoving = isNotMoving;
    }
    
    private Creature getForcedTarget() {
        return this._forcedTarget;
    }
    
    private void setForcedTarget(final Creature forcedTarget) {
        this._forcedTarget = forcedTarget;
    }
    
    private MobGroup getGroupTarget() {
        return this._targetGroup;
    }
    
    private void setGroupTarget(final MobGroup targetGroup) {
        this._targetGroup = targetGroup;
    }
}
