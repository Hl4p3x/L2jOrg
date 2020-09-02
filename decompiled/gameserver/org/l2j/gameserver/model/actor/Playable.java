// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.enums.ClanWarState;
import java.util.Iterator;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.PlayableStats;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.instance.Player;

public abstract class Playable extends Creature
{
    private Creature _lockedTarget;
    private Player transferDmgTo;
    
    public Playable(final int objectId, final CreatureTemplate template) {
        super(objectId, template);
        this._lockedTarget = null;
        this.transferDmgTo = null;
        this.setInstanceType(InstanceType.Playable);
        this.setIsInvul(false);
    }
    
    public Playable(final CreatureTemplate template) {
        super(template);
        this._lockedTarget = null;
        this.transferDmgTo = null;
        this.setInstanceType(InstanceType.Playable);
        this.setIsInvul(false);
    }
    
    @Override
    public PlayableStats getStats() {
        return (PlayableStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new PlayableStats(this));
    }
    
    @Override
    public PlayableStatus getStatus() {
        return (PlayableStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new PlayableStatus(this));
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        boolean deleteBuffs = true;
        if (this.isNoblesseBlessedAffected()) {
            this.stopEffects(EffectFlag.NOBLESS_BLESSING);
            deleteBuffs = false;
        }
        if (this.isResurrectSpecialAffected()) {
            this.stopEffects(EffectFlag.RESURRECTION_SPECIAL);
            deleteBuffs = false;
        }
        if (deleteBuffs) {
            this.stopAllEffectsExceptThoseThatLastThroughDeath();
        }
        final Player actingPlayer = this.getActingPlayer();
        if (!actingPlayer.isNotifyQuestOfDeathEmpty()) {
            for (final QuestState qs : actingPlayer.getNotifyQuestOfDeath()) {
                qs.getQuest().notifyDeath((killer == null) ? this : killer, this, qs);
            }
        }
        if (killer != null) {
            final Player killerPlayer = killer.getActingPlayer();
            if (killerPlayer != null) {
                killerPlayer.onPlayeableKill(this);
            }
        }
        return true;
    }
    
    public boolean checkIfPvP(final Player target) {
        final Player player = this.getActingPlayer();
        if (player == null || target == null || player == target || target.getReputation() < 0 || target.getPvpFlag() > 0 || target.isOnDarkSide()) {
            return true;
        }
        if (player.isInParty() && player.getParty().containsPlayer(target)) {
            return false;
        }
        final Clan playerClan = player.getClan();
        if (playerClan != null && !player.isAcademyMember() && !target.isAcademyMember()) {
            final ClanWar war = playerClan.getWarWith(target.getClanId());
            return war != null && war.getState() == ClanWarState.MUTUAL;
        }
        return false;
    }
    
    @Override
    public boolean canBeAttacked() {
        return true;
    }
    
    public final boolean isNoblesseBlessedAffected() {
        return this.isAffected(EffectFlag.NOBLESS_BLESSING);
    }
    
    public final boolean isResurrectSpecialAffected() {
        return this.isAffected(EffectFlag.RESURRECTION_SPECIAL);
    }
    
    public boolean isSilentMovingAffected() {
        return this.isAffected(EffectFlag.SILENT_MOVE);
    }
    
    public final boolean isProtectionBlessingAffected() {
        return this.isAffected(EffectFlag.PROTECTION_BLESSING);
    }
    
    @Override
    public void updateEffectIcons(final boolean partyOnly) {
        this.getEffectList().updateEffectIcons(partyOnly);
    }
    
    public boolean isLockedTarget() {
        return this._lockedTarget != null;
    }
    
    public Creature getLockedTarget() {
        return this._lockedTarget;
    }
    
    public void setLockedTarget(final Creature cha) {
        this._lockedTarget = cha;
    }
    
    public void setTransferDamageTo(final Player val) {
        this.transferDmgTo = val;
    }
    
    public Player getTransferingDamageTo() {
        return this.transferDmgTo;
    }
    
    public abstract void doPickupItem(final WorldObject object);
    
    public abstract boolean useMagic(final Skill skill, final Item item, final boolean forceUse, final boolean dontMove);
    
    public abstract void storeMe();
    
    public abstract void storeEffect(final boolean storeEffects);
    
    public abstract void restoreEffects();
}
