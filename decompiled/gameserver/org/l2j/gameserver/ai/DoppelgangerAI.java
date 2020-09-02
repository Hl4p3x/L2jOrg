// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.MoveToLocation;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Doppelganger;
import org.l2j.gameserver.model.actor.Creature;

public class DoppelgangerAI extends CreatureAI
{
    private volatile boolean _thinking;
    private volatile boolean _startFollow;
    private Creature _lastAttack;
    
    public DoppelgangerAI(final Doppelganger clone) {
        super(clone);
        this._lastAttack = null;
    }
    
    @Override
    protected void onIntentionIdle() {
        this.stopFollow();
        this._startFollow = false;
        this.onIntentionActive();
    }
    
    @Override
    protected void onIntentionActive() {
        if (this._startFollow) {
            this.setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this.getActor().getSummoner());
        }
        else {
            super.onIntentionActive();
        }
    }
    
    private void thinkAttack() {
        final WorldObject target = this.getTarget();
        final Creature attackTarget = GameUtils.isCreature(target) ? ((Creature)target) : null;
        if (this.checkTargetLostOrDead(attackTarget)) {
            this.setTarget(null);
            return;
        }
        if (this.maybeMoveToPawn(target, this.actor.getPhysicalAttackRange())) {
            return;
        }
        this.clientStopMoving(null);
        this.actor.doAutoAttack(attackTarget);
    }
    
    private void thinkCast() {
        if (this.actor.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }
        final WorldObject target = this._skill.getTarget(this.actor, this._forceUse, this._dontMove, false);
        if (this.checkTargetLost(target)) {
            this.setTarget(null);
            return;
        }
        final boolean val = this._startFollow;
        if (this.maybeMoveToPawn(target, this.actor.getMagicalAttackRange(this._skill))) {
            return;
        }
        this.getActor().followSummoner(false);
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this._startFollow = val;
        this.actor.doCast(this._skill, this._item, this._forceUse, this._dontMove);
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
            this.getActor().followSummoner(this._startFollow);
        }
        else {
            this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, this._lastAttack);
            this._lastAttack = null;
        }
    }
    
    public void notifyFollowStatusChange() {
        this._startFollow = !this._startFollow;
        switch (this.getIntention()) {
            case AI_INTENTION_ACTIVE:
            case AI_INTENTION_FOLLOW:
            case AI_INTENTION_IDLE:
            case AI_INTENTION_MOVE_TO:
            case AI_INTENTION_PICK_UP: {
                this.getActor().followSummoner(this._startFollow);
                break;
            }
        }
    }
    
    public void setStartFollowController(final boolean val) {
        this._startFollow = val;
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            this._lastAttack = (GameUtils.isCreature(this.getTarget()) ? ((Creature)this.getTarget()) : null);
        }
        else {
            this._lastAttack = null;
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
    
    @Override
    public void moveToPawn(final WorldObject pawn, int offset) {
        if (!this.actor.isMovementDisabled() && this.actor.getMoveSpeed() > 0.0) {
            if (offset < 10) {
                offset = 10;
            }
            boolean sendPacket = true;
            if (this._clientMoving && this.getTarget() == pawn) {
                if (this._clientMovingToPawnOffset == offset) {
                    if (WorldTimeController.getInstance().getGameTicks() < this._moveToPawnTimeout) {
                        return;
                    }
                    sendPacket = false;
                }
                else if (this.actor.isOnGeodataPath() && WorldTimeController.getInstance().getGameTicks() < this._moveToPawnTimeout + 10) {
                    return;
                }
            }
            this._clientMoving = true;
            this._clientMovingToPawnOffset = offset;
            this.setTarget(pawn);
            this._moveToPawnTimeout = WorldTimeController.getInstance().getGameTicks();
            this._moveToPawnTimeout += 10;
            if (pawn == null) {
                return;
            }
            final Location loc = new Location(pawn.getX() + Rnd.get(-offset, offset), pawn.getY() + Rnd.get(-offset, offset), pawn.getZ());
            this.actor.moveToLocation(loc.getX(), loc.getY(), loc.getZ(), 0);
            if (!this.actor.isMoving()) {
                this.clientActionFailed();
                return;
            }
            if (sendPacket) {
                this.actor.broadcastPacket(new MoveToLocation(this.actor));
            }
        }
        else {
            this.clientActionFailed();
        }
    }
    
    @Override
    public Doppelganger getActor() {
        return (Doppelganger)super.getActor();
    }
}
