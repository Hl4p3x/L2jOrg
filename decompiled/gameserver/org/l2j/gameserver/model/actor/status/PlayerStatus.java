// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCpChange;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerStatus extends PlayableStatus
{
    private double currentCp;
    
    public PlayerStatus(final Player player) {
        super(player);
        this.currentCp = 0.0;
    }
    
    @Override
    public final void reduceCp(final int value) {
        this.setCurrentCp(Math.max(0.0, this.currentCp - value));
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false, false);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHPConsumption) {
        this.reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
    }
    
    public final void reduceHp(double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHPConsumption, final boolean ignoreCP) {
        final Player me = this.getOwner();
        if (me.isDead()) {
            return;
        }
        if (me.isHpBlocked() && !isDOT && !isHPConsumption) {
            return;
        }
        if (me.isAffected(EffectFlag.DUELIST_FURY) && !attacker.isAffected(EffectFlag.FACEOFF)) {
            return;
        }
        if (!isHPConsumption) {
            if (awake) {
                me.stopEffectsOnDamage();
            }
            if (me.isCrafting() || me.isInStoreMode()) {
                me.setPrivateStoreType(PrivateStoreType.NONE);
                me.standUp();
                me.broadcastUserInfo();
            }
            else if (me.isSitting()) {
                me.standUp();
            }
            if (!isDOT) {
                if (Formulas.calcStunBreak(me)) {
                    me.stopStunning(true);
                }
                if (Formulas.calcRealTargetBreak()) {
                    me.getEffectList().stopEffects(AbnormalType.REAL_TARGET);
                }
            }
        }
        double fullValue = value;
        double tDmg = 0.0;
        if (Objects.nonNull(attacker) && attacker != me) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if (attackerPlayer != null) {
                if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                    return;
                }
                if (me.isInDuel()) {
                    if (me.getDuelState() == 2 || me.getDuelState() == 3) {
                        return;
                    }
                    if (attackerPlayer.getDuelId() != me.getDuelId()) {
                        me.setDuelState(4);
                    }
                }
            }
            final Summon summon = me.getFirstServitor();
            if (Objects.nonNull(summon) && MathUtil.isInsideRadius3D(me, summon, 1000)) {
                tDmg = value * me.getStats().getValue(Stat.TRANSFER_DAMAGE_SUMMON_PERCENT, 0.0) / 100.0;
                tDmg = Math.min(summon.getCurrentHp() - 1.0, tDmg);
                if (tDmg > 0.0) {
                    summon.reduceCurrentHp(tDmg, attacker, null, DamageInfo.DamageType.OTHER);
                    value = (fullValue = value - tDmg);
                }
            }
            double mpDam = value * me.getStats().getValue(Stat.MANA_SHIELD_PERCENT, 0.0) / 100.0;
            if (mpDam > 0.0) {
                mpDam = value - mpDam;
                if (mpDam <= me.getCurrentMp()) {
                    me.reduceCurrentMp(mpDam);
                    me.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP)).addInt((int)mpDam));
                    return;
                }
                me.sendPacket(SystemMessageId.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                me.stopSkillEffects(true, 1556);
                value = mpDam - me.getCurrentMp();
                me.setCurrentMp(0.0);
            }
            final Player caster = me.getTransferingDamageTo();
            if (Objects.nonNull(caster) && !caster.isDead() && me != caster && Objects.nonNull(me.getParty()) && me.getParty().contains(caster) && MathUtil.isInsideRadius3D(me, caster, 1000)) {
                double transferDmg = value * me.getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0.0) / 100.0;
                transferDmg = Math.min(caster.getCurrentHp() - 1.0, transferDmg);
                if (transferDmg > 0.0) {
                    int membersInRange = 0;
                    for (final Player member : caster.getParty().getMembers()) {
                        if (member != caster && MathUtil.isInsideRadius3D(member, caster, 1000)) {
                            ++membersInRange;
                        }
                    }
                    if (GameUtils.isPlayable(attacker) && caster.getCurrentCp() > 0.0) {
                        if (caster.getCurrentCp() > transferDmg) {
                            caster.getStatus().reduceCp((int)transferDmg);
                        }
                        else {
                            transferDmg -= caster.getCurrentCp();
                            caster.getStatus().reduceCp((int)caster.getCurrentCp());
                        }
                    }
                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null, DamageInfo.DamageType.TRANSFERED_DAMAGE);
                        value = (fullValue = value - transferDmg);
                    }
                }
            }
            if (!ignoreCP && GameUtils.isPlayable(attacker)) {
                if (this.currentCp >= value) {
                    this.setCurrentCp(this.currentCp - value);
                    value = 0.0;
                }
                else {
                    value -= this.currentCp;
                    this.setCurrentCp(0.0, false);
                }
            }
            if (fullValue > 0.0 && !isDOT) {
                SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addString(me.getName()).addString(attacker.getName()).addInt((int)fullValue).addPopup(me.getObjectId(), attacker.getObjectId(), (int)(-fullValue));
                me.sendPacket(smsg);
                if (tDmg > 0.0 && attackerPlayer != null) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR);
                    smsg.addInt((int)fullValue);
                    smsg.addInt((int)tDmg);
                    attackerPlayer.sendPacket(smsg);
                }
            }
        }
        if (value > 0.0) {
            double newHp = Math.max(this.getCurrentHp() - value, me.isUndying() ? 1.0 : 0.0);
            if (newHp <= 0.0) {
                if (me.isInDuel()) {
                    me.disableAllSkills();
                    this.stopHpMpRegeneration();
                    if (Objects.nonNull(attacker)) {
                        attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        attacker.sendPacket(ActionFailed.STATIC_PACKET);
                    }
                    DuelManager.getInstance().onPlayerDefeat(me);
                    newHp = 1.0;
                }
                else {
                    newHp = 0.0;
                }
            }
            this.setCurrentHp(newHp);
        }
        if (me.getCurrentHp() < 0.5 && !isHPConsumption && !me.isUndying()) {
            me.abortAttack();
            me.abortCast();
            if (me.isInOlympiadMode()) {
                this.stopHpMpRegeneration();
                me.setIsDead(true);
                me.setIsPendingRevive(true);
                Util.doIfNonNull((Object)me.getPet(), pet -> pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
                me.getServitors().values().forEach(s -> s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
                return;
            }
            me.doDie(attacker);
        }
    }
    
    @Override
    public final double getCurrentCp() {
        return this.currentCp;
    }
    
    @Override
    public final void setCurrentCp(final double newCp) {
        this.setCurrentCp(newCp, true);
    }
    
    @Override
    public final void setCurrentCp(double newCp, final boolean broadcastPacket) {
        final int currentCp = (int)this.currentCp;
        final int maxCp = this.getOwner().getStats().getMaxCp();
        synchronized (this) {
            if (this.getOwner().isDead()) {
                return;
            }
            if (newCp < 0.0) {
                newCp = 0.0;
            }
            if (newCp >= maxCp) {
                this.currentCp = maxCp;
                this._flagsRegenActive &= 0xFFFFFFFB;
                if (this._flagsRegenActive == 0) {
                    this.stopHpMpRegeneration();
                }
            }
            else {
                this.currentCp = newCp;
                this._flagsRegenActive |= 0x4;
                this.startHpMpRegeneration();
            }
        }
        if (currentCp != this.currentCp && broadcastPacket) {
            this.getOwner().broadcastStatusUpdate();
        }
        if (currentCp != this.currentCp) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerCpChange(this.getOwner()), this.getOwner());
        }
    }
    
    @Override
    protected void doRegeneration() {
        final PlayerStats charstat = this.getOwner().getStats();
        if (this.currentCp < charstat.getMaxRecoverableCp()) {
            this.setCurrentCp(this.currentCp + this.getOwner().getStats().getValue(Stat.REGENERATE_CP_RATE), false);
        }
        if (this.getCurrentHp() < charstat.getMaxRecoverableHp()) {
            this.setCurrentHp(this.getCurrentHp() + this.getOwner().getStats().getValue(Stat.REGENERATE_HP_RATE), false);
        }
        if (this.getCurrentMp() < charstat.getMaxRecoverableMp()) {
            this.setCurrentMp(this.getCurrentMp() + this.getOwner().getStats().getValue(Stat.REGENERATE_MP_RATE), false);
        }
        this.getOwner().broadcastStatusUpdate();
    }
    
    @Override
    public Player getOwner() {
        return (Player)super.getOwner();
    }
}
