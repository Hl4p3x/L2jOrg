// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.model.actor.Summon;
import java.util.concurrent.ScheduledFuture;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.effects.EffectTickTask;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.stats.Formulas;
import java.util.ArrayList;
import org.l2j.gameserver.model.effects.EffectTaskInfo;
import java.util.Map;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.List;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public final class BuffInfo
{
    private final int _effectorObjectId;
    private final Creature _effector;
    private final Creature _effected;
    private final Skill _skill;
    private final List<AbstractEffect> _effects;
    private final boolean _hideStartMessage;
    private final Item _item;
    private final Options _option;
    private volatile Map<AbstractEffect, EffectTaskInfo> _tasks;
    private int _abnormalTime;
    private int _periodStartTicks;
    private volatile boolean _isRemoved;
    private volatile boolean _isInUse;
    
    public BuffInfo(final Creature effector, final Creature effected, final Skill skill, final boolean hideStartMessage, final Item item, final Options option) {
        this._effects = new ArrayList<AbstractEffect>(1);
        this._isRemoved = false;
        this._isInUse = true;
        this._effectorObjectId = ((effector != null) ? effector.getObjectId() : 0);
        this._effector = effector;
        this._effected = effected;
        this._skill = skill;
        this._abnormalTime = Formulas.calcEffectAbnormalTime(effector, effected, skill);
        this._periodStartTicks = WorldTimeController.getInstance().getGameTicks();
        this._hideStartMessage = hideStartMessage;
        this._item = item;
        this._option = option;
    }
    
    public List<AbstractEffect> getEffects() {
        return this._effects;
    }
    
    public void addEffect(final AbstractEffect effect) {
        this._effects.add(effect);
    }
    
    private void addTask(final AbstractEffect effect, final EffectTaskInfo effectTaskInfo) {
        if (this._tasks == null) {
            synchronized (this) {
                if (this._tasks == null) {
                    this._tasks = new ConcurrentHashMap<AbstractEffect, EffectTaskInfo>();
                }
            }
        }
        this._tasks.put(effect, effectTaskInfo);
    }
    
    private EffectTaskInfo getEffectTask(final AbstractEffect effect) {
        return (this._tasks == null) ? null : this._tasks.get(effect);
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public int getAbnormalTime() {
        return this._abnormalTime;
    }
    
    public void setAbnormalTime(final int abnormalTime) {
        this._abnormalTime = abnormalTime;
    }
    
    public int getPeriodStartTicks() {
        return this._periodStartTicks;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public Options getOption() {
        return this._option;
    }
    
    public int getTime() {
        return this._abnormalTime - (WorldTimeController.getInstance().getGameTicks() - this._periodStartTicks) / 10;
    }
    
    public boolean isRemoved() {
        return this._isRemoved;
    }
    
    public void setRemoved(final boolean val) {
        this._isRemoved = val;
    }
    
    public boolean isInUse() {
        return this._isInUse;
    }
    
    public void setInUse(final boolean val) {
        this._isInUse = val;
        if (this._skill != null && !this._skill.isHidingMessages() && GameUtils.isPlayer(this._effected)) {
            if (val) {
                if (!this._hideStartMessage && !this._skill.isAura()) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_EFFECT_CAN_BE_FELT);
                    sm.addSkillName(this._skill);
                    this._effected.sendPacket(sm);
                }
            }
            else {
                final SystemMessage sm = SystemMessage.getSystemMessage(this._skill.isToggle() ? SystemMessageId.S1_HAS_BEEN_ABORTED : SystemMessageId.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED);
                sm.addSkillName(this._skill);
                this._effected.sendPacket(sm);
            }
        }
    }
    
    public int getEffectorObjectId() {
        return this._effectorObjectId;
    }
    
    public Creature getEffector() {
        return this._effector;
    }
    
    public Creature getEffected() {
        return this._effected;
    }
    
    public void stopAllEffects(final boolean removed) {
        this.setRemoved(removed);
        this._effected.removeBuffInfoTime(this);
        this.finishEffects();
    }
    
    public void initializeEffects() {
        if (this._effected == null || this._skill == null) {
            return;
        }
        if (!this._hideStartMessage && GameUtils.isPlayer(this._effected) && !this._skill.isHidingMessages() && !this._skill.isAura()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_EFFECT_CAN_BE_FELT);
            sm.addSkillName(this._skill);
            this._effected.sendPacket(sm);
        }
        if (this._abnormalTime > 0) {
            this._effected.addBuffInfoTime(this);
        }
        for (final AbstractEffect effect : this._effects) {
            if (!effect.isInstant()) {
                if (this._effected.isDead() && !this._skill.isPassive()) {
                    continue;
                }
                effect.onStart(this._effector, this._effected, this._skill, this._item);
                if (effect.getTicks() <= 0) {
                    continue;
                }
                final EffectTickTask effectTask = new EffectTickTask(this, effect);
                final ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)effectTask, effect.getTicks() * Config.EFFECT_TICK_RATIO, effect.getTicks() * Config.EFFECT_TICK_RATIO);
                this.addTask(effect, new EffectTaskInfo(effectTask, scheduledFuture));
            }
        }
    }
    
    public void onTick(final AbstractEffect effect) {
        boolean continueForever = false;
        if (this._isInUse) {
            continueForever = effect.onActionTime(this._effector, this._effected, this._skill, this._item);
        }
        if (!continueForever && this._skill.isToggle()) {
            final EffectTaskInfo task = this.getEffectTask(effect);
            if (task != null) {
                task.getScheduledFuture().cancel(true);
                this._effected.getEffectList().stopSkillEffects(true, this._skill);
            }
        }
    }
    
    public void finishEffects() {
        if (this._tasks != null) {
            for (final EffectTaskInfo effectTask : this._tasks.values()) {
                effectTask.getScheduledFuture().cancel(true);
            }
        }
        for (final AbstractEffect effect : this._effects) {
            effect.onExit(this._effector, this._effected, this._skill);
        }
        if (this._skill != null && (!GameUtils.isSummon(this._effected) || ((Summon)this._effected).getOwner().hasSummon()) && !this._skill.isHidingMessages()) {
            SystemMessageId smId = null;
            if (this._skill.isToggle()) {
                smId = SystemMessageId.S1_HAS_BEEN_ABORTED;
            }
            else if (this._isRemoved) {
                smId = SystemMessageId.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED;
            }
            else if (!this._skill.isPassive()) {
                smId = SystemMessageId.S1_HAS_WORN_OFF;
            }
            if (smId != null && this._effected.getActingPlayer() != null && this._effected.getActingPlayer().isOnline()) {
                final SystemMessage sm = SystemMessage.getSystemMessage(smId);
                sm.addSkillName(this._skill);
                this._effected.sendPacket(sm);
            }
        }
    }
    
    public void resetAbnormalTime(final int abnormalTime) {
        if (this._abnormalTime > 0) {
            this._periodStartTicks = WorldTimeController.getInstance().getGameTicks();
            this._abnormalTime = abnormalTime;
            this._effected.removeBuffInfoTime(this);
            this._effected.addBuffInfoTime(this);
        }
    }
    
    public boolean isAbnormalType(final AbnormalType type) {
        return this._skill.getAbnormalType() == type;
    }
}
