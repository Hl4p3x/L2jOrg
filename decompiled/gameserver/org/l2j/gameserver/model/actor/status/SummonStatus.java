// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import java.util.Iterator;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;

public class SummonStatus extends PlayableStatus
{
    public SummonStatus(final Summon activeChar) {
        super(activeChar);
    }
    
    @Override
    public void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public void reduceHp(double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHPConsumption) {
        if (attacker == null || this.getOwner().isDead()) {
            return;
        }
        final Player attackerPlayer = attacker.getActingPlayer();
        if (attackerPlayer != null && (this.getOwner().getOwner() == null || this.getOwner().getOwner().getDuelId() != attackerPlayer.getDuelId())) {
            attackerPlayer.setDuelState(4);
        }
        final Player caster = this.getOwner().getTransferingDamageTo();
        if (this.getOwner().getOwner().getParty() != null) {
            if (caster != null && GameUtils.checkIfInRange(1000, this.getOwner(), caster, true) && !caster.isDead() && this.getOwner().getParty().getMembers().contains(caster)) {
                double transferDmg = value * this.getOwner().getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0.0) / 100.0;
                transferDmg = Math.min((int)caster.getCurrentHp() - 1, transferDmg);
                if (transferDmg > 0.0) {
                    int membersInRange = 0;
                    for (final Player member : caster.getParty().getMembers()) {
                        if (GameUtils.checkIfInRange(1000, member, caster, false) && member != caster) {
                            ++membersInRange;
                        }
                    }
                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null, false, false, false, false, DamageInfo.DamageType.TRANSFERED_DAMAGE);
                        value -= transferDmg;
                    }
                }
            }
        }
        else if (caster != null && caster == this.getOwner().getOwner() && GameUtils.checkIfInRange(1000, this.getOwner(), caster, true) && !caster.isDead()) {
            int transferDmg2 = (int)value * (int)this.getOwner().getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0.0) / 100;
            transferDmg2 = Math.min((int)caster.getCurrentHp() - 1, transferDmg2);
            if (transferDmg2 > 0) {
                if (GameUtils.isPlayable(attacker) && caster.getCurrentCp() > 0.0) {
                    if (caster.getCurrentCp() > transferDmg2) {
                        caster.getStatus().reduceCp(transferDmg2);
                    }
                    else {
                        transferDmg2 -= (int)caster.getCurrentCp();
                        caster.getStatus().reduceCp((int)caster.getCurrentCp());
                    }
                }
                caster.reduceCurrentHp(transferDmg2, attacker, null, DamageInfo.DamageType.TRANSFERED_DAMAGE);
                value -= transferDmg2;
            }
        }
        super.reduceHp(value, attacker, awake, isDOT, isHPConsumption);
    }
    
    @Override
    public Summon getOwner() {
        return (Summon)super.getOwner();
    }
}
