// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.actor.status.NpcStatus;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.SiegeFlagStatus;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Siegable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Npc;

public class SiegeFlag extends Npc
{
    private final Clan _clan;
    private final boolean _isAdvanced;
    private Siegable _siege;
    private boolean _canTalk;
    
    public SiegeFlag(final Player player, final NpcTemplate template, final boolean advanced) {
        super(template);
        this.setInstanceType(InstanceType.L2SiegeFlagInstance);
        this._clan = player.getClan();
        this._canTalk = true;
        this._siege = SiegeManager.getInstance().getSiege(player);
        if (this._clan == null || this._siege == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        }
        final SiegeClanData sc = this._siege.getAttackerClan(this._clan);
        if (sc == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        }
        sc.addFlag(this);
        this._isAdvanced = advanced;
        this.getStatus();
        this.setIsInvul(false);
    }
    
    @Override
    public boolean canBeAttacked() {
        return !this.isInvul();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return !this.isInvul();
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (this._siege != null && this._clan != null) {
            final SiegeClanData sc = this._siege.getAttackerClan(this._clan);
            if (sc != null) {
                sc.removeFlag(this);
            }
        }
        return true;
    }
    
    @Override
    public void onForcedAttack(final Player player) {
        this.onAction(player);
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (player == null || !this.canTarget(player)) {
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
        }
        else if (interact) {
            if (this.isAutoAttackable(player) && Math.abs(player.getZ() - this.getZ()) < 100) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            }
            else {
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
    }
    
    public boolean isAdvancedHeadquarter() {
        return this._isAdvanced;
    }
    
    @Override
    public SiegeFlagStatus getStatus() {
        return (SiegeFlagStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new SiegeFlagStatus(this));
    }
    
    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final Skill skill, final DamageInfo.DamageType damageType) {
        super.reduceCurrentHp(damage, attacker, skill, damageType);
        if (this.canTalk() && Objects.nonNull(this.getCastle()) && this.getCastle().getSiege().isInProgress() && this._clan != null) {
            this._clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_CAMP_IS_UNDER_ATTACK));
            this.setCanTalk(false);
            ThreadPool.schedule((Runnable)new ScheduleTalkTask(), 20000L);
        }
    }
    
    void setCanTalk(final boolean val) {
        this._canTalk = val;
    }
    
    private boolean canTalk() {
        return this._canTalk;
    }
    
    private class ScheduleTalkTask implements Runnable
    {
        public ScheduleTalkTask() {
        }
        
        @Override
        public void run() {
            SiegeFlag.this.setCanTalk(true);
        }
    }
}
