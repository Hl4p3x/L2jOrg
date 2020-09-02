// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerAI extends PlayableAI
{
    private boolean _thinking;
    private IntentionCommand _nextIntention;
    
    public PlayerAI(final Player player) {
        super(player);
        this._nextIntention = null;
    }
    
    private void saveNextIntention(final CtrlIntention intention, final Object arg0, final Object arg1) {
        this._nextIntention = new IntentionCommand(intention, arg0, arg1);
    }
    
    @Override
    public IntentionCommand getNextIntention() {
        return this._nextIntention;
    }
    
    protected synchronized void changeIntention(final CtrlIntention intention, final Object... args) {
        final Object localArg0 = (args.length > 0) ? args[0] : null;
        final Object localArg2 = (args.length > 1) ? args[1] : null;
        final Object globalArg0 = (this._intentionArgs != null && this._intentionArgs.length > 0) ? this._intentionArgs[0] : null;
        final Object globalArg2 = (this._intentionArgs != null && this._intentionArgs.length > 1) ? this._intentionArgs[1] : null;
        if (intention != CtrlIntention.AI_INTENTION_CAST || ((Skill)args[0]).isBad()) {
            this._nextIntention = null;
            super.changeIntention(intention, args);
            return;
        }
        if (intention == this.intention && globalArg0 == localArg0 && globalArg2 == localArg2) {
            super.changeIntention(intention, args);
            return;
        }
        this.saveNextIntention(this.intention, globalArg0, globalArg2);
        super.changeIntention(intention, args);
    }
    
    @Override
    protected void onEvtReadyToAct() {
        if (this._nextIntention != null) {
            this.setIntention(this._nextIntention._crtlIntention, this._nextIntention._arg0, this._nextIntention._arg1);
            this._nextIntention = null;
        }
        super.onEvtReadyToAct();
    }
    
    @Override
    protected void onEvtCancel() {
        this._nextIntention = null;
        super.onEvtCancel();
    }
    
    @Override
    protected void onEvtFinishCasting() {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_CAST) {
            final IntentionCommand nextIntention = this._nextIntention;
            if (nextIntention != null) {
                if (nextIntention._crtlIntention != CtrlIntention.AI_INTENTION_CAST) {
                    this.setIntention(nextIntention._crtlIntention, nextIntention._arg0, nextIntention._arg1);
                }
                else {
                    this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
            }
            else {
                this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }
        }
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
        super.onEvtAttacked(attacker);
        if (this.actor.getActingPlayer().hasServitors()) {
            this.actor.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI)summon.getAI()).isDefending()).forEach(summon -> ((SummonAI)summon.getAI()).defendAttack(attacker));
        }
    }
    
    @Override
    protected void onEvtEvaded(final Creature attacker) {
        super.onEvtEvaded(attacker);
        if (this.actor.getActingPlayer().hasServitors()) {
            this.actor.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI)summon.getAI()).isDefending()).forEach(summon -> ((SummonAI)summon.getAI()).defendAttack(attacker));
        }
    }
    
    @Override
    protected void onIntentionRest() {
        if (this.getIntention() != CtrlIntention.AI_INTENTION_REST) {
            this.changeIntention(CtrlIntention.AI_INTENTION_REST, new Object[0]);
            this.setTarget(null);
            this.clientStopMoving(null);
        }
    }
    
    @Override
    protected void onIntentionActive() {
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    @Override
    protected void onIntentionMoveTo(final ILocational loc) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow() || this.actor.isAttackingNow()) {
            this.clientActionFailed();
            this.saveNextIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc, null);
            return;
        }
        this.changeIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
        this.clientStopAutoAttack();
        this.actor.abortAttack();
        this.moveTo(loc.getX(), loc.getY(), loc.getZ());
    }
    
    @Override
    protected void clientNotifyDead() {
        this._clientMovingToPawnOffset = 0;
        this._clientMoving = false;
        super.clientNotifyDead();
    }
    
    private void thinkAttack() {
        final WorldObject target = this.getTarget();
        if (!GameUtils.isCreature(target)) {
            return;
        }
        if (this.checkTargetLostOrDead((Creature)target)) {
            this.setTarget(null);
            return;
        }
        if (this.maybeMoveToPawn(target, this.actor.getPhysicalAttackRange())) {
            return;
        }
        this.clientStopMoving(null);
        this.actor.doAutoAttack((Creature)target);
    }
    
    private void thinkCast() {
        final WorldObject target = this._skill.getTarget(this.actor, this._forceUse, this._dontMove, false);
        if (this._skill.getTargetType() == TargetType.GROUND && GameUtils.isPlayer(this.actor)) {
            if (this.maybeMoveToPosition(((Player)this.actor).getCurrentSkillWorldPosition(), this.actor.getMagicalAttackRange(this._skill))) {
                return;
            }
        }
        else {
            if (this.checkTargetLost(target)) {
                if (this._skill.isBad() && target != null) {
                    this.setTarget(null);
                }
                return;
            }
            if (target != null && this.maybeMoveToPawn(target, this.actor.getMagicalAttackRange(this._skill))) {
                return;
            }
        }
        this.actor.doCast(this._skill, this._item, this._forceUse, this._dontMove);
    }
    
    private void thinkPickUp() {
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            return;
        }
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
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            return;
        }
        final WorldObject target = this.getTarget();
        if (this.checkTargetLost(target)) {
            return;
        }
        if (this.maybeMoveToPawn(target, 36)) {
            return;
        }
        if (!(target instanceof StaticWorldObject)) {
            this.getActor().doInteract((Creature)target);
        }
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    @Override
    public void onEvtThink() {
        if (this._thinking && this.getIntention() != CtrlIntention.AI_INTENTION_CAST) {
            return;
        }
        this._thinking = true;
        try {
            if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                this.thinkAttack();
            }
            else if (this.getIntention() == CtrlIntention.AI_INTENTION_CAST) {
                this.thinkCast();
            }
            else if (this.getIntention() == CtrlIntention.AI_INTENTION_PICK_UP) {
                this.thinkPickUp();
            }
            else if (this.getIntention() == CtrlIntention.AI_INTENTION_INTERACT) {
                this.thinkInteract();
            }
        }
        finally {
            this._thinking = false;
        }
    }
    
    @Override
    protected void onEvtForgetObject(final WorldObject object) {
        super.onEvtForgetObject(object);
        this.actor.sendPacket(new DeleteObject(object));
    }
    
    @Override
    public Player getActor() {
        return (Player)super.getActor();
    }
}
