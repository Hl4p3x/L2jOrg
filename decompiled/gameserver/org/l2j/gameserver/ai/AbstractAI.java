// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.taskmanager.CreatureFollowTaskManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.Die;
import org.l2j.gameserver.network.serverpackets.AutoAttackStop;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.network.serverpackets.AutoAttackStart;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.network.serverpackets.MoveToPawn;
import org.l2j.gameserver.network.serverpackets.MoveToLocation;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.concurrent.Future;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.slf4j.Logger;

public abstract class AbstractAI implements Ctrl
{
    private static final Logger LOGGER;
    protected final Creature actor;
    protected CtrlIntention intention;
    protected Object[] _intentionArgs;
    protected volatile boolean _clientMoving;
    protected int _clientMovingToPawnOffset;
    protected int _moveToPawnTimeout;
    Skill _skill;
    Item _item;
    boolean _forceUse;
    boolean _dontMove;
    private volatile boolean _clientAutoAttacking;
    private WorldObject _target;
    private Future<?> _followTask;
    private NextAction _nextAction;
    
    protected AbstractAI(final Creature creature) {
        this.intention = CtrlIntention.AI_INTENTION_IDLE;
        this._intentionArgs = null;
        this._followTask = null;
        this.actor = creature;
    }
    
    public NextAction getNextAction() {
        return this._nextAction;
    }
    
    public void setNextAction(final NextAction nextAction) {
        this._nextAction = nextAction;
    }
    
    @Override
    public Creature getActor() {
        return this.actor;
    }
    
    @Override
    public CtrlIntention getIntention() {
        return this.intention;
    }
    
    @Override
    public final void setIntention(final CtrlIntention intention) {
        this.setIntention(intention, null, null);
    }
    
    synchronized void changeIntention(final CtrlIntention intention, final Object... args) {
        this.intention = intention;
        this._intentionArgs = args;
    }
    
    @SafeVarargs
    @Override
    public final void setIntention(final CtrlIntention intention, final Object... args) {
        if (intention != CtrlIntention.AI_INTENTION_FOLLOW && intention != CtrlIntention.AI_INTENTION_ATTACK) {
            this.stopFollow();
        }
        switch (intention) {
            case AI_INTENTION_IDLE: {
                this.onIntentionIdle();
                break;
            }
            case AI_INTENTION_ACTIVE: {
                this.onIntentionActive();
                break;
            }
            case AI_INTENTION_REST: {
                this.onIntentionRest();
                break;
            }
            case AI_INTENTION_ATTACK: {
                this.onIntentionAttack((Creature)args[0]);
                break;
            }
            case AI_INTENTION_CAST: {
                this.onIntentionCast((Skill)args[0], (WorldObject)args[1], (args.length > 2) ? ((Item)args[2]) : null, args.length > 3 && (boolean)args[3], args.length > 4 && (boolean)args[4]);
                break;
            }
            case AI_INTENTION_MOVE_TO: {
                this.onIntentionMoveTo((ILocational)args[0]);
                break;
            }
            case AI_INTENTION_FOLLOW: {
                this.onIntentionFollow((Creature)args[0]);
                break;
            }
            case AI_INTENTION_PICK_UP: {
                this.onIntentionPickUp((WorldObject)args[0]);
                break;
            }
            case AI_INTENTION_INTERACT: {
                this.onIntentionInteract((WorldObject)args[0]);
                break;
            }
        }
        if (this._nextAction != null && this._nextAction.getIntentions().contains(intention)) {
            this._nextAction = null;
        }
    }
    
    @Override
    public final void notifyEvent(final CtrlEvent evt) {
        this.notifyEvent(evt, null, null);
    }
    
    @Override
    public final void notifyEvent(final CtrlEvent evt, final Object arg0) {
        this.notifyEvent(evt, arg0, null);
    }
    
    @Override
    public final void notifyEvent(final CtrlEvent evt, final Object arg0, final Object arg1) {
        if ((!this.actor.isSpawned() && !this.actor.isTeleporting()) || !this.actor.hasAI()) {
            return;
        }
        switch (evt) {
            case EVT_THINK: {
                this.onEvtThink();
                break;
            }
            case EVT_ATTACKED: {
                this.onEvtAttacked((Creature)arg0);
                break;
            }
            case EVT_AGGRESSION: {
                this.onEvtAggression((Creature)arg0, ((Number)arg1).intValue());
                break;
            }
            case EVT_ACTION_BLOCKED: {
                this.onEvtActionBlocked((Creature)arg0);
                break;
            }
            case EVT_ROOTED: {
                this.onEvtRooted((Creature)arg0);
                break;
            }
            case EVT_CONFUSED: {
                this.onEvtConfused((Creature)arg0);
                break;
            }
            case EVT_MUTED: {
                this.onEvtMuted((Creature)arg0);
                break;
            }
            case EVT_EVADED: {
                this.onEvtEvaded((Creature)arg0);
                break;
            }
            case EVT_READY_TO_ACT: {
                if (!this.actor.isCastingNow()) {
                    this.onEvtReadyToAct();
                    break;
                }
                break;
            }
            case EVT_ARRIVED: {
                if (!this.actor.isCastingNow()) {
                    this.onEvtArrived();
                    break;
                }
                break;
            }
            case EVT_ARRIVED_REVALIDATE: {
                if (this.actor.isMoving()) {
                    this.onEvtArrivedRevalidate();
                    break;
                }
                break;
            }
            case EVT_ARRIVED_BLOCKED: {
                this.onEvtArrivedBlocked((Location)arg0);
                break;
            }
            case EVT_FORGET_OBJECT: {
                this.onEvtForgetObject((WorldObject)arg0);
                break;
            }
            case EVT_CANCEL: {
                this.onEvtCancel();
                break;
            }
            case EVT_DEAD: {
                this.onEvtDead();
                break;
            }
            case EVT_FAKE_DEATH: {
                this.onEvtFakeDeath();
                break;
            }
            case EVT_FINISH_CASTING: {
                this.onEvtFinishCasting();
                break;
            }
        }
        if (this._nextAction != null && this._nextAction.getEvents().contains(evt)) {
            this._nextAction.doAction();
        }
    }
    
    protected abstract void onIntentionIdle();
    
    protected abstract void onIntentionActive();
    
    protected abstract void onIntentionRest();
    
    protected abstract void onIntentionAttack(final Creature target);
    
    protected abstract void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove);
    
    protected abstract void onIntentionMoveTo(final ILocational destination);
    
    protected abstract void onIntentionFollow(final Creature target);
    
    protected abstract void onIntentionPickUp(final WorldObject item);
    
    protected abstract void onIntentionInteract(final WorldObject object);
    
    protected abstract void onEvtThink();
    
    protected abstract void onEvtAttacked(final Creature attacker);
    
    protected abstract void onEvtAggression(final Creature target, final int aggro);
    
    protected abstract void onEvtActionBlocked(final Creature attacker);
    
    protected abstract void onEvtRooted(final Creature attacker);
    
    protected abstract void onEvtConfused(final Creature attacker);
    
    protected abstract void onEvtMuted(final Creature attacker);
    
    protected abstract void onEvtEvaded(final Creature attacker);
    
    protected abstract void onEvtReadyToAct();
    
    protected abstract void onEvtArrived();
    
    protected abstract void onEvtArrivedRevalidate();
    
    protected abstract void onEvtArrivedBlocked(final Location blocked_at_pos);
    
    protected abstract void onEvtForgetObject(final WorldObject object);
    
    protected abstract void onEvtCancel();
    
    protected abstract void onEvtDead();
    
    protected abstract void onEvtFakeDeath();
    
    protected abstract void onEvtFinishCasting();
    
    protected void clientActionFailed() {
        if (GameUtils.isPlayer(this.actor)) {
            this.actor.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    public void moveToPawn(final WorldObject pawn, int offset) {
        if (!this.actor.isMovementDisabled() && !this.actor.isAttackingNow() && !this.actor.isCastingNow()) {
            if (offset < 10) {
                offset = 10;
            }
            if (this._clientMoving && this._target == pawn) {
                if (this._clientMovingToPawnOffset == offset) {
                    if (WorldTimeController.getInstance().getGameTicks() < this._moveToPawnTimeout) {
                        return;
                    }
                }
                else if (this.actor.isOnGeodataPath() && WorldTimeController.getInstance().getGameTicks() < this._moveToPawnTimeout + 10) {
                    return;
                }
            }
            this._clientMoving = true;
            this._clientMovingToPawnOffset = offset;
            this._target = pawn;
            this._moveToPawnTimeout = WorldTimeController.getInstance().getGameTicks();
            this._moveToPawnTimeout += 10;
            if (pawn == null) {
                return;
            }
            this.actor.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);
            if (!this.actor.isMoving()) {
                this.clientActionFailed();
                return;
            }
            if (GameUtils.isCreature(pawn)) {
                if (this.actor.isOnGeodataPath()) {
                    this.actor.broadcastPacket(new MoveToLocation(this.actor));
                    this._clientMovingToPawnOffset = 0;
                }
                else {
                    this.actor.broadcastPacket(new MoveToPawn(this.actor, pawn, offset));
                }
            }
            else {
                this.actor.broadcastPacket(new MoveToLocation(this.actor));
            }
        }
        else {
            this.clientActionFailed();
        }
    }
    
    public void moveTo(final ILocational loc) {
        this.moveTo(loc.getX(), loc.getY(), loc.getZ());
    }
    
    protected void moveTo(final int x, final int y, final int z) {
        if (!this.actor.isMovementDisabled()) {
            this._clientMoving = true;
            this._clientMovingToPawnOffset = 0;
            this.actor.moveToLocation(x, y, z, 0);
            this.actor.broadcastPacket(new MoveToLocation(this.actor));
        }
        else {
            this.clientActionFailed();
        }
    }
    
    public void clientStopMoving(final Location loc) {
        if (this.actor.isMoving()) {
            this.actor.stopMove(loc);
        }
        this._clientMovingToPawnOffset = 0;
        this._clientMoving = false;
    }
    
    protected void clientStoppedMoving() {
        if (this._clientMovingToPawnOffset > 0) {
            this._clientMovingToPawnOffset = 0;
            this.actor.broadcastPacket(new StopMove(this.actor));
        }
        this._clientMoving = false;
    }
    
    public boolean isAutoAttacking() {
        return this._clientAutoAttacking;
    }
    
    public void setAutoAttacking(final boolean isAutoAttacking) {
        if (GameUtils.isSummon(this.actor)) {
            final Summon summon = (Summon)this.actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().setAutoAttacking(isAutoAttacking);
            }
            return;
        }
        this._clientAutoAttacking = isAutoAttacking;
    }
    
    public void clientStartAutoAttack() {
        if (GameUtils.isSummon(this.actor)) {
            final Summon summon = (Summon)this.actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().clientStartAutoAttack();
            }
            return;
        }
        if (!this._clientAutoAttacking) {
            if (GameUtils.isPlayer(this.actor) && this.actor.hasSummon()) {
                final Summon pet = this.actor.getPet();
                if (pet != null) {
                    pet.broadcastPacket(new AutoAttackStart(pet.getObjectId()));
                }
                this.actor.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStart(s.getObjectId())));
            }
            this.actor.broadcastPacket(new AutoAttackStart(this.actor.getObjectId()));
            this.setAutoAttacking(true);
        }
        AttackStanceTaskManager.getInstance().addAttackStanceTask(this.actor);
    }
    
    void clientStopAutoAttack() {
        if (GameUtils.isSummon(this.actor)) {
            final Summon summon = (Summon)this.actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().clientStopAutoAttack();
            }
            return;
        }
        if (GameUtils.isPlayer(this.actor)) {
            if (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(this.actor) && this.isAutoAttacking()) {
                AttackStanceTaskManager.getInstance().addAttackStanceTask(this.actor);
            }
        }
        else if (this._clientAutoAttacking) {
            this.actor.broadcastPacket(new AutoAttackStop(this.actor.getObjectId()));
            this.setAutoAttacking(false);
        }
    }
    
    protected void clientNotifyDead() {
        final Die msg = new Die(this.actor);
        this.actor.broadcastPacket(msg);
        this.intention = CtrlIntention.AI_INTENTION_IDLE;
        this._target = null;
        this.stopFollow();
    }
    
    public void describeStateToPlayer(final Player player) {
        if (this.actor.isVisibleFor(player) && this._clientMoving) {
            if (this._clientMovingToPawnOffset != 0 && this.isFollowing()) {
                player.sendPacket(new MoveToPawn(this.actor, this._target, this._clientMovingToPawnOffset));
            }
            else {
                player.sendPacket(new MoveToLocation(this.actor));
            }
        }
    }
    
    public boolean isFollowing() {
        return GameUtils.isCreature(this._target) && this.intention == CtrlIntention.AI_INTENTION_FOLLOW;
    }
    
    public synchronized void startFollow(final Creature target) {
        this.startFollow(target, -1);
    }
    
    public synchronized void startFollow(final Creature target, final int range) {
        this.stopFollow();
        this.setTarget(target);
        if (range == -1) {
            CreatureFollowTaskManager.getInstance().addNormalFollow(this.actor, range);
        }
        else {
            CreatureFollowTaskManager.getInstance().addAttackFollow(this.actor, range);
        }
    }
    
    public synchronized void stopFollow() {
        CreatureFollowTaskManager.getInstance().remove(this.actor);
    }
    
    public WorldObject getTarget() {
        return this._target;
    }
    
    public void setTarget(final WorldObject target) {
        this._target = target;
    }
    
    public void stopAITask() {
        this.stopFollow();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/Creature;)Ljava/lang/String;, this.actor);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractAI.class);
    }
}
