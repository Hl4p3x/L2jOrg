// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;
import java.util.concurrent.Future;
import org.l2j.gameserver.model.actor.Creature;

public class SummonAI extends PlayableAI implements Runnable
{
    private static final int AVOID_RADIUS = 70;
    private volatile boolean _thinking;
    private volatile boolean _startFollow;
    private Creature _lastAttack;
    private volatile boolean _startAvoid;
    private volatile boolean _isDefending;
    private Future<?> _avoidTask;
    
    public SummonAI(final Summon summon) {
        super(summon);
        this._startFollow = ((Summon)this.actor).getFollowStatus();
        this._lastAttack = null;
        this._avoidTask = null;
    }
    
    @Override
    protected void onIntentionIdle() {
        this.stopFollow();
        this._startFollow = false;
        this.onIntentionActive();
    }
    
    @Override
    protected void onIntentionActive() {
        final Summon summon = (Summon)this.actor;
        if (this._startFollow) {
            this.setIntention(CtrlIntention.AI_INTENTION_FOLLOW, summon.getOwner());
        }
        else {
            super.onIntentionActive();
        }
    }
    
    @Override
    synchronized void changeIntention(final CtrlIntention intention, final Object... args) {
        switch (intention) {
            case AI_INTENTION_ACTIVE:
            case AI_INTENTION_FOLLOW: {
                this.startAvoidTask();
                break;
            }
            default: {
                this.stopAvoidTask();
                break;
            }
        }
        super.changeIntention(intention, args);
    }
    
    private void thinkAttack() {
        final WorldObject target = this.getTarget();
        final Creature attackTarget = GameUtils.isCreature(target) ? ((Creature)target) : null;
        if (this.checkTargetLostOrDead(attackTarget)) {
            this.setTarget(null);
            ((Summon)this.actor).setFollowStatus(true);
            return;
        }
        if (this.maybeMoveToPawn(attackTarget, this.actor.getPhysicalAttackRange())) {
            return;
        }
        this.clientStopMoving(null);
        this.actor.doAutoAttack(attackTarget);
    }
    
    private void thinkCast() {
        final Summon summon = (Summon)this.actor;
        if (summon.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }
        final WorldObject target = this._skill.getTarget(this.actor, this._forceUse, this._dontMove, false);
        if (this.checkTargetLost(target)) {
            this.setTarget(null);
            summon.setFollowStatus(true);
            return;
        }
        final boolean val = this._startFollow;
        if (this.maybeMoveToPawn(target, this.actor.getMagicalAttackRange(this._skill))) {
            return;
        }
        summon.setFollowStatus(false);
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this._startFollow = val;
        this.actor.doCast(this._skill, this._item, this._forceUse, this._dontMove);
    }
    
    private void thinkPickUp() {
        final WorldObject target = this.getTarget();
        if (this.checkTargetLost(target)) {
            return;
        }
        if (this.maybeMoveToPawn(target, 36)) {
            return;
        }
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this.getActor().doPickupItem(target);
    }
    
    private void thinkInteract() {
        final WorldObject target = this.getTarget();
        if (this.checkTargetLost(target)) {
            return;
        }
        if (this.maybeMoveToPawn(target, 36)) {
            return;
        }
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    @Override
    public void onEvtThink() {
        if (this._thinking || this.actor.isCastingNow() || this.actor.isAllSkillsDisabled()) {
            return;
        }
        this._thinking = true;
        try {
            switch (this.getIntention()) {
                case AI_INTENTION_ATTACK: {
                    this.thinkAttack();
                    break;
                }
                case AI_INTENTION_CAST: {
                    this.thinkCast();
                    break;
                }
                case AI_INTENTION_PICK_UP: {
                    this.thinkPickUp();
                    break;
                }
                case AI_INTENTION_INTERACT: {
                    this.thinkInteract();
                    break;
                }
            }
        }
        finally {
            this._thinking = false;
        }
    }
    
    @Override
    protected void onEvtFinishCasting() {
        if (this._lastAttack == null) {
            ((Summon)this.actor).setFollowStatus(this._startFollow);
        }
        else {
            this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, this._lastAttack);
            this._lastAttack = null;
        }
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
        super.onEvtAttacked(attacker);
        if (this._isDefending) {
            this.allServitorsDefend(attacker);
        }
        else {
            this.avoidAttack(attacker);
        }
    }
    
    @Override
    protected void onEvtEvaded(final Creature attacker) {
        super.onEvtEvaded(attacker);
        if (this._isDefending) {
            this.allServitorsDefend(attacker);
        }
        else {
            this.avoidAttack(attacker);
        }
    }
    
    private void avoidAttack(final Creature attacker) {
        if (this.actor.isCastingNow()) {
            return;
        }
        final Creature owner = this.getActor().getOwner();
        if (owner != null && owner != attacker && MathUtil.isInsideRadius3D(owner, this.actor, 140)) {
            this._startAvoid = true;
        }
    }
    
    public void defendAttack(final Creature attacker) {
        if (this.actor.isAttackingNow() || this.actor.isCastingNow()) {
            return;
        }
        final Summon summon = this.getActor();
        if (summon.getOwner() != null && summon.getOwner() != attacker && !summon.isMoving() && summon.canAttack(attacker, false)) {
            summon.doAttack(attacker);
        }
    }
    
    @Override
    public void run() {
        if (this._startAvoid) {
            this._startAvoid = false;
            if (!this._clientMoving && !this.actor.isDead() && !this.actor.isMovementDisabled() && this.actor.getMoveSpeed() > 0.0) {
                final int ownerX = ((Summon)this.actor).getOwner().getX();
                final int ownerY = ((Summon)this.actor).getOwner().getY();
                final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - this.actor.getY(), ownerX - this.actor.getX());
                final int targetX = ownerX + (int)(70.0 * Math.cos(angle));
                final int targetY = ownerY + (int)(70.0 * Math.sin(angle));
                if (GeoEngine.getInstance().canMoveToTarget(this.actor.getX(), this.actor.getY(), this.actor.getZ(), targetX, targetY, this.actor.getZ(), this.actor.getInstanceWorld())) {
                    this.moveTo(targetX, targetY, this.actor.getZ());
                }
            }
        }
    }
    
    public void notifyFollowStatusChange() {
        this._startFollow = !this._startFollow;
        switch (this.getIntention()) {
            case AI_INTENTION_ACTIVE:
            case AI_INTENTION_FOLLOW:
            case AI_INTENTION_PICK_UP:
            case AI_INTENTION_IDLE:
            case AI_INTENTION_MOVE_TO: {
                ((Summon)this.actor).setFollowStatus(this._startFollow);
                break;
            }
        }
    }
    
    public void setStartFollowController(final boolean val) {
        this._startFollow = val;
    }
    
    private void allServitorsDefend(final Creature attacker) {
        final Creature Owner = this.getActor().getOwner();
        if (Owner != null && Owner.getActingPlayer().hasServitors()) {
            Owner.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI)summon.getAI()).isDefending()).forEach(summon -> ((SummonAI)summon.getAI()).defendAttack(attacker));
        }
        else {
            this.defendAttack(attacker);
        }
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            this._lastAttack = ((this.getTarget() != null && GameUtils.isCreature(this.getTarget())) ? ((Creature)this.getTarget()) : null);
        }
        else {
            this._lastAttack = null;
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
    
    private void startAvoidTask() {
        if (this._avoidTask == null) {
            this._avoidTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)this, 100L, 100L);
        }
    }
    
    private void stopAvoidTask() {
        if (this._avoidTask != null) {
            this._avoidTask.cancel(false);
            this._avoidTask = null;
        }
    }
    
    @Override
    public void stopAITask() {
        this.stopAvoidTask();
        super.stopAITask();
    }
    
    @Override
    public Summon getActor() {
        return (Summon)super.getActor();
    }
    
    public boolean isDefending() {
        return this._isDefending;
    }
    
    public void setDefending(final boolean isDefending) {
        this._isDefending = isDefending;
    }
}
