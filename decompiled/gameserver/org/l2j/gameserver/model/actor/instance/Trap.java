// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.actor.tasks.npc.trap.TrapTriggerTask;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.tasks.npc.trap.TrapTask;
import org.l2j.gameserver.enums.InstanceType;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.List;
import org.l2j.gameserver.model.actor.Npc;

public final class Trap extends Npc
{
    private static final int TICK = 1000;
    private final int _lifeTime;
    private final List<Integer> _playersWhoDetectedMe;
    private final SkillHolder _skill;
    private boolean _hasLifeTime;
    private boolean _isInArena;
    private boolean _isTriggered;
    private Player _owner;
    private int _remainingTime;
    private ScheduledFuture<?> _trapTask;
    
    public Trap(final NpcTemplate template, final int instanceId, final int lifeTime) {
        super(template);
        this._playersWhoDetectedMe = new ArrayList<Integer>();
        this._isInArena = false;
        this._trapTask = null;
        this.setInstanceType(InstanceType.L2TrapInstance);
        this.setInstanceById(instanceId);
        this.setName(template.getName());
        this.setIsInvul(false);
        this._owner = null;
        this._isTriggered = false;
        this._skill = this.getParameters().getObject("trap_skill", SkillHolder.class);
        this._hasLifeTime = (lifeTime >= 0);
        this._lifeTime = ((lifeTime != 0) ? lifeTime : 30000);
        this._remainingTime = this._lifeTime;
        if (this._skill != null) {
            this._trapTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new TrapTask(this), 1000L, 1000L);
        }
    }
    
    public Trap(final NpcTemplate template, final Player owner, final int lifeTime) {
        this(template, owner.getInstanceId(), lifeTime);
        this._owner = owner;
    }
    
    @Override
    public void broadcastPacket(final ServerPacket mov) {
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (this._isTriggered || this.canBeSeen(player)) {
                player.sendPacket(mov);
            }
        });
    }
    
    @Override
    public void broadcastPacket(final ServerPacket mov, final int radiusInKnownlist) {
        World.getInstance().forEachVisibleObjectInRange(this, Player.class, radiusInKnownlist, player -> {
            if (this._isTriggered || this.canBeSeen(player)) {
                player.sendPacket(mov);
            }
        });
    }
    
    public boolean canBeSeen(final Creature cha) {
        if (cha != null && this._playersWhoDetectedMe.contains(cha.getObjectId())) {
            return true;
        }
        if (this._owner == null || cha == null) {
            return false;
        }
        if (cha == this._owner) {
            return true;
        }
        if (GameUtils.isPlayer(cha)) {
            if (((Player)cha).inObserverMode()) {
                return false;
            }
            if (this._owner.isInOlympiadMode() && ((Player)cha).isInOlympiadMode() && ((Player)cha).getOlympiadSide() != this._owner.getOlympiadSide()) {
                return false;
            }
        }
        return this._isInArena || (this._owner.isInParty() && cha.isInParty() && this._owner.getParty().getLeaderObjectId() == cha.getParty().getLeaderObjectId());
    }
    
    @Override
    public boolean deleteMe() {
        this._owner = null;
        return super.deleteMe();
    }
    
    @Override
    public Player getActingPlayer() {
        return this._owner;
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }
    
    @Override
    public int getReputation() {
        return (this._owner != null) ? this._owner.getReputation() : 0;
    }
    
    public Player getOwner() {
        return this._owner;
    }
    
    @Override
    public byte getPvpFlag() {
        return (byte)((this._owner != null) ? this._owner.getPvpFlag() : 0);
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }
    
    public Skill getSkill() {
        return this._skill.getSkill();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return !this.canBeSeen(attacker);
    }
    
    public boolean isTriggered() {
        return this._isTriggered;
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this._isInArena = (this.isInsideZone(ZoneType.PVP) && !this.isInsideZone(ZoneType.SIEGE));
        this._playersWhoDetectedMe.clear();
    }
    
    @Override
    public void doAttack(final double damage, final Creature target, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect) {
        super.doAttack(damage, target, skill, isDOT, directlyToHp, critical, reflect);
        this.sendDamageMessage(target, skill, (int)damage, 0.0, critical, false);
    }
    
    @Override
    public void sendDamageMessage(final Creature target, final Skill skill, final int damage, final double elementalDamage, final boolean crit, final boolean miss) {
        if (miss || this._owner == null) {
            return;
        }
        if (this._owner.isInOlympiadMode() && GameUtils.isPlayer(target) && ((Player)target).isInOlympiadMode() && ((Player)target).getOlympiadGameId() == this._owner.getOlympiadGameId()) {
            OlympiadGameManager.getInstance().notifyCompetitorDamage(this.getOwner(), damage);
        }
        if (target.isHpBlocked() && !GameUtils.isNpc(target)) {
            this._owner.sendPacket(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
        }
        else {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
            sm.addString(this.getName());
            sm.addString(target.getName());
            sm.addInt(damage);
            sm.addPopup(target.getObjectId(), this.getObjectId(), damage * -1);
            this._owner.sendPacket(sm);
        }
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        if (this._isTriggered || this.canBeSeen(activeChar)) {
            activeChar.sendPacket(new NpcInfo(this));
        }
    }
    
    public void setDetected(final Creature detector) {
        if (this._isInArena) {
            if (GameUtils.isPlayable(detector)) {
                this.sendInfo(detector.getActingPlayer());
            }
            return;
        }
        if (this._owner != null && this._owner.getPvpFlag() == 0 && this._owner.getReputation() >= 0) {
            return;
        }
        this._playersWhoDetectedMe.add(detector.getObjectId());
        EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(this, detector, TrapAction.TRAP_DETECTED), this);
        if (GameUtils.isPlayable(detector)) {
            this.sendInfo(detector.getActingPlayer());
        }
    }
    
    public void stopDecay() {
        DecayTaskManager.getInstance().cancel(this);
    }
    
    public void triggerTrap(final Creature target) {
        if (this._trapTask != null) {
            this._trapTask.cancel(true);
            this._trapTask = null;
        }
        this._isTriggered = true;
        this.broadcastPacket(new NpcInfo(this));
        this.setTarget(target);
        EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(this, target, TrapAction.TRAP_TRIGGERED), this);
        ThreadPool.schedule((Runnable)new TrapTriggerTask(this), 500L);
    }
    
    public void unSummon() {
        if (this._trapTask != null) {
            this._trapTask.cancel(true);
            this._trapTask = null;
        }
        this._owner = null;
        if (this.isSpawned() && !this.isDead()) {
            ZoneManager.getInstance().getRegion(this).removeFromZones(this);
            this.deleteMe();
        }
    }
    
    public boolean hasLifeTime() {
        return this._hasLifeTime;
    }
    
    public void setHasLifeTime(final boolean val) {
        this._hasLifeTime = val;
    }
    
    public int getRemainingTime() {
        return this._remainingTime;
    }
    
    public void setRemainingTime(final int time) {
        this._remainingTime = time;
    }
    
    public int getLifeTime() {
        return this._lifeTime;
    }
}
