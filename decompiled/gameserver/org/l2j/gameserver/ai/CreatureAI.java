// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import java.util.Iterator;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveFinished;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AutoAttackStop;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.slf4j.Logger;

public class CreatureAI extends AbstractAI
{
    private static final Logger LOGGER;
    
    public CreatureAI(final Creature creature) {
        super(creature);
    }
    
    public IntentionCommand getNextIntention() {
        return null;
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
        this.clientStartAutoAttack();
    }
    
    @Override
    protected void onIntentionIdle() {
        this.changeIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[0]);
        this.clientStopMoving(null);
        this.clientStopAutoAttack();
    }
    
    @Override
    protected void onIntentionActive() {
        if (this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
            this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, new Object[0]);
            this.clientStopMoving(null);
            this.clientStopAutoAttack();
            this.onEvtThink();
        }
    }
    
    @Override
    protected void onIntentionRest() {
        this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
        if (target == null || !target.isTargetable()) {
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
        if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            if (this.getTarget() != target) {
                this.setTarget(target);
                this.notifyEvent(CtrlEvent.EVT_THINK, null);
            }
            else {
                this.clientActionFailed();
            }
        }
        else {
            this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            this.setTarget(target);
            this.notifyEvent(CtrlEvent.EVT_THINK, null);
        }
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST && skill.isMagic()) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAttackingNow()) {
            ThreadPool.schedule((Runnable)new CastTask(this.actor, skill, target, item, forceUse, dontMove), this.actor.getAttackEndTime() - TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()));
        }
        else {
            this.changeIntentionToCast(skill, target, item, forceUse, dontMove);
        }
    }
    
    protected void changeIntentionToCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
        this._skill = skill;
        this._item = item;
        this._forceUse = forceUse;
        this._dontMove = dontMove;
        this.changeIntention(CtrlIntention.AI_INTENTION_CAST, skill);
        this.notifyEvent(CtrlEvent.EVT_THINK, null);
    }
    
    @Override
    protected void onIntentionMoveTo(final ILocational loc) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            this.clientActionFailed();
            return;
        }
        this.changeIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
        this.clientStopAutoAttack();
        this.actor.abortAttack();
        this.moveTo(loc.getX(), loc.getY(), loc.getZ());
    }
    
    @Override
    protected void onIntentionFollow(final Creature target) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isMovementDisabled() || this.actor.getMoveSpeed() <= 0.0) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isDead()) {
            this.clientActionFailed();
            return;
        }
        if (this.actor == target) {
            this.clientActionFailed();
            return;
        }
        this.clientStopAutoAttack();
        this.changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
        this.startFollow(target);
    }
    
    @Override
    protected void onIntentionPickUp(final WorldObject object) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            this.clientActionFailed();
            return;
        }
        this.clientStopAutoAttack();
        if (GameUtils.isItem(object) && ((Item)object).getItemLocation() != ItemLocation.VOID) {
            return;
        }
        this.changeIntention(CtrlIntention.AI_INTENTION_PICK_UP, object);
        this.setTarget(object);
        if (object.getX() == 0 && object.getY() == 0) {
            CreatureAI.LOGGER.warn("Object in coords 0,0 - using a temporary fix");
            object.setXYZ(this.getActor().getX(), this.getActor().getY(), this.getActor().getZ() + 5);
        }
        this.moveToPawn(object, 20);
    }
    
    @Override
    protected void onIntentionInteract(final WorldObject object) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_REST) {
            this.clientActionFailed();
            return;
        }
        if (this.actor.isAllSkillsDisabled() || this.actor.isCastingNow()) {
            this.clientActionFailed();
            return;
        }
        this.clientStopAutoAttack();
        if (this.getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
            this.changeIntention(CtrlIntention.AI_INTENTION_INTERACT, object);
            this.setTarget(object);
            this.moveToPawn(object, 60);
        }
    }
    
    public void onEvtThink() {
    }
    
    @Override
    protected void onEvtAggression(final Creature target, final int aggro) {
    }
    
    @Override
    protected void onEvtActionBlocked(final Creature attacker) {
        this.actor.broadcastPacket(new AutoAttackStop(this.actor.getObjectId()));
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this.actor)) {
            AttackStanceTaskManager.getInstance().removeAttackStanceTask(this.actor);
        }
        this.setAutoAttacking(false);
        this.clientStopMoving(null);
    }
    
    @Override
    protected void onEvtRooted(final Creature attacker) {
        this.clientStopMoving(null);
        this.onEvtAttacked(attacker);
    }
    
    @Override
    protected void onEvtConfused(final Creature attacker) {
        this.clientStopMoving(null);
        this.onEvtAttacked(attacker);
    }
    
    @Override
    protected void onEvtMuted(final Creature attacker) {
        this.onEvtAttacked(attacker);
    }
    
    @Override
    protected void onEvtEvaded(final Creature attacker) {
    }
    
    @Override
    protected void onEvtReadyToAct() {
        this.onEvtThink();
    }
    
    @Override
    protected void onEvtArrived() {
        this.getActor().revalidateZone(true);
        if (this.getActor().moveToNextRoutePoint()) {
            return;
        }
        if (GameUtils.isAttackable(this.getActor())) {
            ((Attackable)this.getActor()).setisReturningToSpawnPoint(false);
        }
        this.clientStoppedMoving();
        if (this.getIntention() == CtrlIntention.AI_INTENTION_MOVE_TO) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        if (GameUtils.isNpc(this.actor)) {
            final Npc npc = (Npc)this.actor;
            WalkingManager.getInstance().onArrived(npc);
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcMoveFinished(npc), npc);
        }
        this.onEvtThink();
    }
    
    @Override
    protected void onEvtArrivedRevalidate() {
        this.onEvtThink();
    }
    
    @Override
    protected void onEvtArrivedBlocked(final Location blocked_at_loc) {
        if (this.getIntention() == CtrlIntention.AI_INTENTION_MOVE_TO || this.getIntention() == CtrlIntention.AI_INTENTION_CAST) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        this.clientStopMoving(blocked_at_loc);
        this.onEvtThink();
    }
    
    @Override
    protected void onEvtForgetObject(final WorldObject object) {
        final WorldObject target = this.getTarget();
        this.getActor().abortCast(sc -> sc.getTarget().equals(object));
        if (target == object) {
            this.setTarget(null);
            if (this.isFollowing()) {
                this.clientStopMoving(null);
                this.stopFollow();
            }
            if (this.getIntention() != CtrlIntention.AI_INTENTION_MOVE_TO) {
                this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
        }
        if (Objects.equals(this.getActor().getTarget(), object)) {
            this.getActor().setTarget(null);
        }
        if (this.actor == object) {
            this.setTarget(null);
            this.stopFollow();
            this.clientStopMoving(null);
            this.changeIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[0]);
        }
    }
    
    @Override
    protected void onEvtCancel() {
        this.actor.abortCast();
        this.stopFollow();
        if (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(this.actor)) {
            this.actor.broadcastPacket(new AutoAttackStop(this.actor.getObjectId()));
        }
        this.onEvtThink();
    }
    
    @Override
    protected void onEvtDead() {
        this.stopAITask();
        this.clientNotifyDead();
        if (!GameUtils.isPlayable(this.actor)) {
            this.actor.setWalking();
        }
    }
    
    @Override
    protected void onEvtFakeDeath() {
        this.stopFollow();
        this.clientStopMoving(null);
        this.intention = CtrlIntention.AI_INTENTION_IDLE;
        this.setTarget(null);
    }
    
    @Override
    protected void onEvtFinishCasting() {
    }
    
    protected boolean maybeMoveToPosition(final ILocational worldPosition, final int offset) {
        if (worldPosition == null) {
            CreatureAI.LOGGER.warn("maybeMoveToPosition: worldPosition == NULL!");
            return false;
        }
        if (offset < 0) {
            return false;
        }
        if (MathUtil.isInsideRadius2D(this.actor, worldPosition, offset + this.actor.getTemplate().getCollisionRadius())) {
            if (this.isFollowing()) {
                this.stopFollow();
            }
            return false;
        }
        if (this.actor.isMovementDisabled() || this.actor.getMoveSpeed() <= 0.0) {
            return true;
        }
        if (!this.actor.isRunning() && !(this instanceof PlayerAI) && !(this instanceof SummonAI)) {
            this.actor.setRunning();
        }
        this.stopFollow();
        int x = this.actor.getX();
        int y = this.actor.getY();
        final double dx = worldPosition.getX() - x;
        final double dy = worldPosition.getY() - y;
        double dist = Math.hypot(dx, dy);
        final double sin = dy / dist;
        final double cos = dx / dist;
        dist -= offset - 5;
        x += (int)(dist * cos);
        y += (int)(dist * sin);
        this.moveTo(x, y, worldPosition.getZ());
        return true;
    }
    
    protected boolean maybeMoveToPawn(final WorldObject target, int offset) {
        if (target == null) {
            CreatureAI.LOGGER.warn("maybeMoveToPawn: target == NULL!");
            return false;
        }
        if (offset < 0) {
            return false;
        }
        int offsetWithCollision = offset + this.actor.getTemplate().getCollisionRadius();
        if (GameUtils.isCreature(target)) {
            offsetWithCollision += ((Creature)target).getTemplate().getCollisionRadius();
        }
        if (MathUtil.isInsideRadius2D(this.actor, target, offsetWithCollision)) {
            if (this.isFollowing()) {
                this.stopFollow();
            }
            return false;
        }
        if (this.isFollowing()) {
            if (!MathUtil.isInsideRadius2D(this.actor, target, offsetWithCollision + 100)) {
                return true;
            }
            this.stopFollow();
            return false;
        }
        else {
            if (this.actor.isMovementDisabled() || this.actor.getMoveSpeed() <= 0.0) {
                if (this.actor.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                    this.actor.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
                return true;
            }
            if (this.actor.getAI().getIntention() == CtrlIntention.AI_INTENTION_CAST && GameUtils.isPlayer(this.actor) && this.actor.checkTransformed(transform -> !transform.isCombat())) {
                this.actor.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
                this.actor.sendPacket(ActionFailed.STATIC_PACKET);
                return true;
            }
            if (!this.actor.isRunning() && !(this instanceof PlayerAI) && !(this instanceof SummonAI)) {
                this.actor.setRunning();
            }
            this.stopFollow();
            if (GameUtils.isCreature(target) && !GameUtils.isDoor(target)) {
                if (((Creature)target).isMoving()) {
                    offset -= 100;
                }
                if (offset < 5) {
                    offset = 5;
                }
                this.startFollow((Creature)target, offset);
            }
            else {
                this.moveToPawn(target, offset);
            }
            return true;
        }
    }
    
    protected boolean checkTargetLostOrDead(final Creature target) {
        if (target != null && !target.isAlikeDead()) {
            return false;
        }
        if (GameUtils.isPlayer(target) && ((Player)target).isFakeDeath()) {
            target.stopFakeDeath(true);
            return false;
        }
        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        return true;
    }
    
    protected boolean checkTargetLost(final WorldObject target) {
        if (GameUtils.isPlayer(target)) {
            final Player target2 = (Player)target;
            if (target2.isFakeDeath()) {
                target2.stopFakeDeath(true);
                return false;
            }
        }
        if (target == null) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return true;
        }
        if (this.actor != null && this._skill != null && this._skill.isBad() && this._skill.getAffectRange() > 0 && !GeoEngine.getInstance().canSeeTarget(this.actor, target)) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return true;
        }
        return false;
    }
    
    public void setActiveIfIdle() {
        if (this.intention == CtrlIntention.AI_INTENTION_IDLE) {
            this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CreatureAI.class);
    }
    
    public static class IntentionCommand
    {
        protected final CtrlIntention _crtlIntention;
        protected final Object _arg0;
        protected final Object _arg1;
        
        protected IntentionCommand(final CtrlIntention pIntention, final Object pArg0, final Object pArg1) {
            this._crtlIntention = pIntention;
            this._arg0 = pArg0;
            this._arg1 = pArg1;
        }
        
        public CtrlIntention getCtrlIntention() {
            return this._crtlIntention;
        }
    }
    
    public static class CastTask implements Runnable
    {
        private final Creature _activeChar;
        private final WorldObject _target;
        private final Skill _skill;
        private final Item _item;
        private final boolean _forceUse;
        private final boolean _dontMove;
        
        public CastTask(final Creature actor, final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
            this._activeChar = actor;
            this._target = target;
            this._skill = skill;
            this._item = item;
            this._forceUse = forceUse;
            this._dontMove = dontMove;
        }
        
        @Override
        public void run() {
            if (this._activeChar.isAttackingNow()) {
                this._activeChar.abortAttack();
            }
            this._activeChar.getAI().changeIntentionToCast(this._skill, this._target, this._item, this._forceUse, this._dontMove);
        }
    }
    
    protected class SelfAnalysis
    {
        public boolean isMage;
        public boolean isBalanced;
        public boolean isArcher;
        public boolean isHealer;
        public boolean isFighter;
        public boolean cannotMoveOnLand;
        public Set<Skill> generalSkills;
        public Set<Skill> buffSkills;
        public int lastBuffTick;
        public Set<Skill> debuffSkills;
        public int lastDebuffTick;
        public Set<Skill> cancelSkills;
        public Set<Skill> healSkills;
        public Set<Skill> generalDisablers;
        public Set<Skill> sleepSkills;
        public Set<Skill> rootSkills;
        public Set<Skill> muteSkills;
        public Set<Skill> resurrectSkills;
        public boolean hasHealOrResurrect;
        public boolean hasLongRangeSkills;
        public boolean hasLongRangeDamageSkills;
        public int maxCastRange;
        
        public SelfAnalysis() {
            this.isMage = false;
            this.isArcher = false;
            this.isHealer = false;
            this.isFighter = false;
            this.cannotMoveOnLand = false;
            this.generalSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.buffSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.lastBuffTick = 0;
            this.debuffSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.lastDebuffTick = 0;
            this.cancelSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.healSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.generalDisablers = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.sleepSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.rootSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.muteSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.resurrectSkills = (Set<Skill>)ConcurrentHashMap.newKeySet();
            this.hasHealOrResurrect = false;
            this.hasLongRangeSkills = false;
            this.hasLongRangeDamageSkills = false;
            this.maxCastRange = 0;
        }
        
        public void init() {
            switch (((NpcTemplate)CreatureAI.this.actor.getTemplate()).getAIType()) {
                case FIGHTER: {
                    this.isFighter = true;
                    break;
                }
                case MAGE: {
                    this.isMage = true;
                    break;
                }
                case CORPSE:
                case BALANCED: {
                    this.isBalanced = true;
                    break;
                }
                case ARCHER: {
                    this.isArcher = true;
                    break;
                }
                case HEALER: {
                    this.isHealer = true;
                    break;
                }
                default: {
                    this.isFighter = true;
                    break;
                }
            }
            if (GameUtils.isNpc(CreatureAI.this.actor)) {
                switch (CreatureAI.this.actor.getId()) {
                    case 20314:
                    case 20849: {
                        this.cannotMoveOnLand = true;
                        break;
                    }
                    default: {
                        this.cannotMoveOnLand = false;
                        break;
                    }
                }
            }
            for (final Skill sk : CreatureAI.this.actor.getAllSkills()) {
                if (sk.isPassive()) {
                    continue;
                }
                final int castRange = sk.getCastRange();
                boolean hasLongRangeDamageSkill = false;
                if (sk.isContinuous()) {
                    if (!sk.isDebuff()) {
                        this.buffSkills.add(sk);
                    }
                    else {
                        this.debuffSkills.add(sk);
                    }
                }
                else {
                    if (sk.hasAnyEffectType(EffectType.DISPEL, EffectType.DISPEL_BY_SLOT)) {
                        this.cancelSkills.add(sk);
                    }
                    else if (sk.hasAnyEffectType(EffectType.HEAL)) {
                        this.healSkills.add(sk);
                        this.hasHealOrResurrect = true;
                    }
                    else if (sk.hasAnyEffectType(EffectType.SLEEP)) {
                        this.sleepSkills.add(sk);
                    }
                    else if (sk.hasAnyEffectType(EffectType.BLOCK_ACTIONS)) {
                        switch (sk.getId()) {
                            case 367:
                            case 4111:
                            case 4383:
                            case 4578:
                            case 4616: {
                                this.sleepSkills.add(sk);
                                break;
                            }
                            default: {
                                this.generalDisablers.add(sk);
                                break;
                            }
                        }
                    }
                    else if (sk.hasAnyEffectType(EffectType.ROOT)) {
                        this.rootSkills.add(sk);
                    }
                    else if (sk.hasAnyEffectType(EffectType.BLOCK_CONTROL)) {
                        this.debuffSkills.add(sk);
                    }
                    else if (sk.hasAnyEffectType(EffectType.MUTE)) {
                        this.muteSkills.add(sk);
                    }
                    else if (sk.hasAnyEffectType(EffectType.RESURRECTION)) {
                        this.resurrectSkills.add(sk);
                        this.hasHealOrResurrect = true;
                    }
                    else {
                        this.generalSkills.add(sk);
                        hasLongRangeDamageSkill = true;
                    }
                    if (castRange > 70) {
                        this.hasLongRangeSkills = true;
                        if (hasLongRangeDamageSkill) {
                            this.hasLongRangeDamageSkills = true;
                        }
                    }
                    if (castRange <= this.maxCastRange) {
                        continue;
                    }
                    this.maxCastRange = castRange;
                }
            }
            if (!this.hasLongRangeDamageSkills && this.isMage) {
                this.isBalanced = true;
                this.isMage = false;
                this.isFighter = false;
            }
            if (!this.hasLongRangeSkills && (this.isMage || this.isBalanced)) {
                this.isBalanced = false;
                this.isMage = false;
                this.isFighter = true;
            }
            if (this.generalSkills.isEmpty() && this.isMage) {
                this.isBalanced = true;
                this.isMage = false;
            }
        }
    }
}
