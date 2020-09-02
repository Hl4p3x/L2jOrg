// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMpChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHpChange;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.Set;
import org.l2j.gameserver.model.actor.Creature;
import org.slf4j.Logger;

public class CreatureStatus
{
    protected static final Logger LOGGER;
    protected static final byte REGEN_FLAG_CP = 4;
    private static final byte REGEN_FLAG_HP = 1;
    private static final byte REGEN_FLAG_MP = 2;
    private final Creature owner;
    protected byte _flagsRegenActive;
    private double _currentHp;
    private double _currentMp;
    private Set<Creature> _StatusListener;
    private Future<?> _regTask;
    
    public CreatureStatus(final Creature owner) {
        this._flagsRegenActive = 0;
        this._currentHp = 0.0;
        this._currentMp = 0.0;
        this.owner = owner;
    }
    
    public final void addStatusListener(final Creature object) {
        if (object == this.owner) {
            return;
        }
        this.getStatusListener().add(object);
    }
    
    public final void removeStatusListener(final Creature object) {
        this.getStatusListener().remove(object);
    }
    
    public final Set<Creature> getStatusListener() {
        if (this._StatusListener == null) {
            this._StatusListener = (Set<Creature>)ConcurrentHashMap.newKeySet();
        }
        return this._StatusListener;
    }
    
    public void reduceCp(final int value) {
    }
    
    public void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    public void reduceHp(final double value, final Creature attacker, final boolean isHpConsumption) {
        this.reduceHp(value, attacker, true, false, isHpConsumption);
    }
    
    public void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHPConsumption) {
        final Creature activeChar = this.owner;
        if (activeChar.isDead()) {
            return;
        }
        if (activeChar.isHpBlocked() && !isDOT && !isHPConsumption) {
            return;
        }
        if (attacker != null) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if (attackerPlayer != null && attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                return;
            }
        }
        if (!isDOT && !isHPConsumption) {
            if (awake) {
                activeChar.stopEffectsOnDamage();
            }
            if (Formulas.calcStunBreak(activeChar)) {
                activeChar.stopStunning(true);
            }
            if (Formulas.calcRealTargetBreak()) {
                this.owner.getEffectList().stopEffects(AbnormalType.REAL_TARGET);
            }
        }
        if (value > 0.0) {
            this.setCurrentHp(Math.max(this._currentHp - value, activeChar.isUndying() ? 1.0 : 0.0));
        }
        if (activeChar.getCurrentHp() < 0.5) {
            activeChar.doDie(attacker);
        }
    }
    
    public void reduceMp(final double value) {
        this.setCurrentMp(Math.max(this._currentMp - value, 0.0));
    }
    
    public final synchronized void startHpMpRegeneration() {
        if (this._regTask == null && !this.owner.isDead()) {
            final int period = Formulas.getRegeneratePeriod(this.owner);
            this._regTask = (Future<?>)ThreadPool.scheduleAtFixedRate(this::doRegeneration, (long)period, (long)period);
        }
    }
    
    public final synchronized void stopHpMpRegeneration() {
        if (this._regTask != null) {
            this._regTask.cancel(false);
            this._regTask = null;
            this._flagsRegenActive = 0;
        }
    }
    
    public double getCurrentCp() {
        return 0.0;
    }
    
    public void setCurrentCp(final double newCp) {
    }
    
    public void setCurrentCp(final double newCp, final boolean broadcastPacket) {
    }
    
    public final double getCurrentHp() {
        return this._currentHp;
    }
    
    public final void setCurrentHp(final double newHp) {
        this.setCurrentHp(newHp, true);
    }
    
    public boolean setCurrentHp(final double newHp, final boolean broadcastPacket) {
        final int oldHp = (int)this._currentHp;
        final double maxHp = this.owner.getStats().getMaxHp();
        synchronized (this) {
            if (this.owner.isDead()) {
                return false;
            }
            if (newHp >= maxHp) {
                this._currentHp = maxHp;
                this._flagsRegenActive &= 0xFFFFFFFE;
                if (this._flagsRegenActive == 0) {
                    this.stopHpMpRegeneration();
                }
            }
            else {
                this._currentHp = newHp;
                this._flagsRegenActive |= 0x1;
                this.startHpMpRegeneration();
            }
        }
        final boolean hpWasChanged = oldHp != this._currentHp;
        if (hpWasChanged) {
            if (broadcastPacket) {
                this.owner.broadcastStatusUpdate();
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureHpChange(this.getOwner(), oldHp, this._currentHp), this.getOwner());
            if (this.getOwner() instanceof Player) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHpChange((Player)this.getOwner()), this.getOwner());
            }
        }
        return hpWasChanged;
    }
    
    public final void setCurrentHpMp(final double newHp, final double newMp) {
        boolean hpOrMpWasChanged = this.setCurrentHp(newHp, false);
        hpOrMpWasChanged |= this.setCurrentMp(newMp, false);
        if (hpOrMpWasChanged) {
            this.owner.broadcastStatusUpdate();
        }
    }
    
    public final double getCurrentMp() {
        return this._currentMp;
    }
    
    public final void setCurrentMp(final double newMp) {
        this.setCurrentMp(newMp, true);
    }
    
    public final boolean setCurrentMp(final double newMp, final boolean broadcastPacket) {
        final int currentMp = (int)this._currentMp;
        final int maxMp = this.owner.getStats().getMaxMp();
        synchronized (this) {
            if (this.owner.isDead()) {
                return false;
            }
            if (newMp >= maxMp) {
                this._currentMp = maxMp;
                this._flagsRegenActive &= 0xFFFFFFFD;
                if (this._flagsRegenActive == 0) {
                    this.stopHpMpRegeneration();
                }
            }
            else {
                this._currentMp = newMp;
                this._flagsRegenActive |= 0x2;
                this.startHpMpRegeneration();
            }
        }
        final boolean mpWasChanged = currentMp != this._currentMp;
        if (mpWasChanged && broadcastPacket) {
            this.owner.broadcastStatusUpdate();
        }
        if (mpWasChanged && this.getOwner() instanceof Player) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMpChange((Player)this.getOwner()), this.getOwner());
        }
        return mpWasChanged;
    }
    
    protected void doRegeneration() {
        if (!this.owner.isDead() && (this._currentHp < this.owner.getMaxRecoverableHp() || this._currentMp < this.owner.getMaxRecoverableMp())) {
            final double newHp = this._currentHp + this.owner.getStats().getValue(Stat.REGENERATE_HP_RATE);
            final double newMp = this._currentMp + this.owner.getStats().getValue(Stat.REGENERATE_MP_RATE);
            this.setCurrentHpMp(newHp, newMp);
        }
        else {
            this.stopHpMpRegeneration();
        }
    }
    
    public Creature getOwner() {
        return this.owner;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CreatureStatus.class);
    }
}
