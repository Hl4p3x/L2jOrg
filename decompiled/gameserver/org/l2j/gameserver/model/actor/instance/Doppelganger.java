// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.ai.CtrlIntention;
import java.util.Iterator;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.DoppelgangerAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.slf4j.Logger;
import org.l2j.gameserver.model.actor.Npc;

public class Doppelganger extends Npc
{
    protected static final Logger log;
    private boolean _copySummonerEffects;
    
    public Doppelganger(final NpcTemplate template, final Player owner) {
        super(template);
        this._copySummonerEffects = true;
        this.setSummoner(owner);
        this.setCloneObjId(owner.getObjectId());
        this.setClanId(owner.getClanId());
        this.setInstance(owner.getInstanceWorld());
        this.setXYZInvisible(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());
        ((DoppelgangerAI)this.getAI()).setStartFollowController(true);
        this.followSummoner(true);
    }
    
    @Override
    protected CreatureAI initAI() {
        return new DoppelgangerAI(this);
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        if (this._copySummonerEffects && this.getSummoner() != null) {
            for (final BuffInfo summonerInfo : this.getSummoner().getEffectList().getEffects()) {
                if (summonerInfo.getAbnormalTime() > 0) {
                    final BuffInfo info = new BuffInfo(this.getSummoner(), this, summonerInfo.getSkill(), false, null, null);
                    info.setAbnormalTime(summonerInfo.getAbnormalTime());
                    this.getEffectList().add(info);
                }
            }
        }
    }
    
    public void followSummoner(final boolean followSummoner) {
        if (followSummoner) {
            if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE || this.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                this.setRunning();
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this.getSummoner());
            }
        }
        else if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
    }
    
    public void setCopySummonerEffects(final boolean copySummonerEffects) {
        this._copySummonerEffects = copySummonerEffects;
    }
    
    @Override
    public final byte getPvpFlag() {
        return (byte)((this.getSummoner() != null) ? this.getSummoner().getPvpFlag() : 0);
    }
    
    @Override
    public final Team getTeam() {
        return (this.getSummoner() != null) ? this.getSummoner().getTeam() : Team.NONE;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return (this.getSummoner() != null) ? this.getSummoner().isAutoAttackable(attacker) : super.isAutoAttackable(attacker);
    }
    
    @Override
    public void doAttack(final double damage, final Creature target, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect) {
        super.doAttack(damage, target, skill, isDOT, directlyToHp, critical, reflect);
        this.sendDamageMessage(target, skill, (int)damage, 0.0, critical, false);
    }
    
    @Override
    public void sendDamageMessage(final Creature target, final Skill skill, final int damage, final double elementalDamage, final boolean crit, final boolean miss) {
        if (miss || !GameUtils.isPlayer(this.getSummoner())) {
            return;
        }
        if (target.getObjectId() != this.getSummoner().getObjectId()) {
            if (this.getActingPlayer().isInOlympiadMode() && GameUtils.isPlayer(target) && ((Player)target).isInOlympiadMode() && ((Player)target).getOlympiadGameId() == this.getActingPlayer().getOlympiadGameId()) {
                OlympiadGameManager.getInstance().notifyCompetitorDamage(this.getSummoner().getActingPlayer(), damage);
            }
            SystemMessage sm;
            if ((target.isHpBlocked() && !GameUtils.isNpc(target)) || (GameUtils.isPlayer(target) && target.isAffected(EffectFlag.DUELIST_FURY) && !this.getActingPlayer().isAffected(EffectFlag.FACEOFF))) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
                sm.addNpcName(this);
                sm.addString(target.getName());
                sm.addInt(damage);
                sm.addPopup(target.getObjectId(), this.getObjectId(), damage * -1);
            }
            this.sendPacket(sm);
        }
    }
    
    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final Skill skill, final DamageInfo.DamageType damageType) {
        super.reduceCurrentHp(damage, attacker, skill, damageType);
        if (GameUtils.isPlayer(this.getSummoner()) && attacker != null && !this.isDead() && !this.isHpBlocked()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
            sm.addNpcName(this);
            sm.addString(attacker.getName());
            sm.addInt((int)damage);
            sm.addPopup(this.getObjectId(), attacker.getObjectId(), (int)(-damage));
            this.sendPacket(sm);
        }
    }
    
    @Override
    public Player getActingPlayer() {
        return (this.getSummoner() != null) ? this.getSummoner().getActingPlayer() : super.getActingPlayer();
    }
    
    @Override
    public void onTeleported() {
        this.deleteMe();
    }
    
    @Override
    public void sendPacket(final ServerPacket... packets) {
        if (this.getSummoner() != null) {
            this.getSummoner().sendPacket(packets);
        }
    }
    
    @Override
    public void sendPacket(final SystemMessageId id) {
        if (this.getSummoner() != null) {
            this.getSummoner().sendPacket(id);
        }
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILorg/l2j/gameserver/model/actor/Creature;)Ljava/lang/String;, super.toString(), this.getId(), this.getSummoner());
    }
    
    static {
        log = LoggerFactory.getLogger(Doppelganger.class.getName());
    }
}
