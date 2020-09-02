// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.util.MinionList;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.Attackable;

public class Monster extends Attackable
{
    private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
    protected boolean _enableMinions;
    protected ScheduledFuture<?> _maintenanceTask;
    private Monster _master;
    private volatile MinionList _minionList;
    
    public Monster(final NpcTemplate template) {
        super(template);
        this._enableMinions = true;
        this._maintenanceTask = null;
        this._master = null;
        this._minionList = null;
        this.setInstanceType(InstanceType.L2MonsterInstance);
        this.setAutoAttackable(true);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return (Config.GUARD_ATTACK_AGGRO_MOB && this.getTemplate().isAggressive() && attacker instanceof Guard) || (!GameUtils.isMonster(attacker) && (GameUtils.isPlayable(attacker) || GameUtils.isAttackable(attacker) || attacker instanceof Trap || attacker instanceof EffectPoint) && super.isAutoAttackable(attacker));
    }
    
    @Override
    public boolean isAggressive() {
        return this.getTemplate().isAggressive() && !this.isAffected(EffectFlag.PASSIVE);
    }
    
    @Override
    public void onSpawn() {
        if (!this.isTeleporting()) {
            if (this._master != null) {
                this.setRandomWalking(false);
                this.setIsRaidMinion(this._master.isRaid());
                this._master.getMinionList().onMinionSpawn(this);
            }
            this.startMaintenanceTask();
        }
        super.onSpawn();
    }
    
    @Override
    public void onTeleported() {
        super.onTeleported();
        if (this.hasMinions()) {
            this.getMinionList().onMasterTeleported();
        }
    }
    
    protected int getMaintenanceInterval() {
        return 1000;
    }
    
    protected void startMaintenanceTask() {
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (this._maintenanceTask != null) {
            this._maintenanceTask.cancel(false);
            this._maintenanceTask = null;
        }
        return true;
    }
    
    @Override
    public boolean deleteMe() {
        if (this._maintenanceTask != null) {
            this._maintenanceTask.cancel(false);
            this._maintenanceTask = null;
        }
        if (this.hasMinions()) {
            this.getMinionList().onMasterDie(true);
        }
        if (this._master != null) {
            this._master.getMinionList().onMinionDie(this, 0);
        }
        return super.deleteMe();
    }
    
    @Override
    public Monster getLeader() {
        return this._master;
    }
    
    public void setLeader(final Monster leader) {
        this._master = leader;
    }
    
    public void enableMinions(final boolean b) {
        this._enableMinions = b;
    }
    
    public boolean hasMinions() {
        return this._minionList != null;
    }
    
    public void setMinionList(final MinionList minionList) {
        this._minionList = minionList;
    }
    
    public MinionList getMinionList() {
        if (this._minionList == null) {
            synchronized (this) {
                if (this._minionList == null) {
                    this._minionList = new MinionList(this);
                }
            }
        }
        return this._minionList;
    }
    
    @Override
    public boolean giveRaidCurse() {
        return (this.isRaidMinion() && this._master != null) ? this._master.giveRaidCurse() : super.giveRaidCurse();
    }
    
    @Override
    public synchronized void doCast(final Skill skill, final Item item, final boolean ctrlPressed, final boolean shiftPressed) {
        if (!skill.isBad() && GameUtils.isPlayer(this.getTarget())) {
            this.abortAllSkillCasters();
            return;
        }
        super.doCast(skill, item, ctrlPressed, shiftPressed);
    }
}
