// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Attackable;

public class FriendlyNpcAI extends AttackableAI
{
    public FriendlyNpcAI(final Attackable attackable) {
        super(attackable);
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
    }
    
    @Override
    protected void onEvtAggression(final Creature target, final int aggro) {
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
        if (target == null) {
            this.clientActionFailed();
            return;
        }
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow() || this.actor.isControlBlocked()) {
            this.clientActionFailed();
            return;
        }
        this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
        this.setTarget(target);
        this.stopFollow();
        this.notifyEvent(CtrlEvent.EVT_THINK, null);
    }
    
    @Override
    protected void thinkAttack() {
        final Attackable npc = this.getActiveChar();
        if (npc.isCastingNow() || npc.isCoreAIDisabled()) {
            return;
        }
        final WorldObject target = this.getTarget();
        final Creature originalAttackTarget = GameUtils.isCreature(target) ? ((Creature)target) : null;
        if (originalAttackTarget == null || originalAttackTarget.isAlikeDead()) {
            if (originalAttackTarget != null) {
                npc.stopHating(originalAttackTarget);
            }
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            npc.setWalking();
            return;
        }
        final int collision = npc.getTemplate().getCollisionRadius();
        this.setTarget(originalAttackTarget);
        final int combinedCollision = collision + originalAttackTarget.getTemplate().getCollisionRadius();
        if (!npc.isMovementDisabled() && Rnd.get(100) <= 3) {
            final Object obj;
            final ILocational object;
            final int radius;
            World.getInstance().forAnyVisibleObject(npc, Attackable.class, nearby -> this.moteToTargetIfNeed(npc, originalAttackTarget, collision, combinedCollision), nearby -> !nearby.equals(obj) && MathUtil.isInsideRadius2D(object, nearby, radius));
        }
        if (!npc.isMovementDisabled() && npc.getTemplate().getDodge() > 0 && Rnd.get(100) <= npc.getTemplate().getDodge() && MathUtil.isInsideRadius2D(npc, originalAttackTarget, 60 + combinedCollision)) {
            int posX = npc.getX();
            int posY = npc.getY();
            final int posZ = npc.getZ() + 30;
            if (originalAttackTarget.getX() < posX) {
                posX += 300;
            }
            else {
                posX -= 300;
            }
            if (originalAttackTarget.getY() < posY) {
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
        final double dist = MathUtil.calculateDistance2D(npc, originalAttackTarget);
        final int dist2 = (int)dist - collision;
        int range = npc.getPhysicalAttackRange() + combinedCollision;
        if (originalAttackTarget.isMoving()) {
            range += 50;
            if (npc.isMoving()) {
                range += 50;
            }
        }
        if (dist2 > range || !GeoEngine.getInstance().canSeeTarget(npc, originalAttackTarget)) {
            if (originalAttackTarget.isMoving()) {
                range -= 100;
            }
            if (range < 5) {
                range = 5;
            }
            this.moveToPawn(originalAttackTarget, range);
            return;
        }
        this.actor.doAutoAttack(originalAttackTarget);
    }
    
    private void moteToTargetIfNeed(final Attackable npc, final Creature originalAttackTarget, final int collision, final int combinedCollision) {
        int newX = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newX += originalAttackTarget.getX();
        }
        else {
            newX = originalAttackTarget.getX() - newX;
        }
        int newY = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newY += originalAttackTarget.getY();
        }
        else {
            newY = originalAttackTarget.getY() - newY;
        }
        if (!MathUtil.isInsideRadius2D(npc, newX, newY, collision)) {
            final int newZ = npc.getZ() + 30;
            if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ, npc.getInstanceWorld())) {
                this.moveTo(newX, newY, newZ);
            }
        }
    }
    
    @Override
    protected void thinkCast() {
        final WorldObject target = this._skill.getTarget(this.actor, this._forceUse, this._dontMove, false);
        if (this.checkTargetLost(target)) {
            this.setTarget(null);
            return;
        }
        if (this.maybeMoveToPawn(target, this.actor.getMagicalAttackRange(this._skill))) {
            return;
        }
        this.actor.doCast(this._skill, this._item, this._forceUse, this._dontMove);
    }
}
