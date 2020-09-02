// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;

public class NpcStatus extends CreatureStatus
{
    public NpcStatus(final Npc activeChar) {
        super(activeChar);
    }
    
    @Override
    public void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHpConsumption) {
        if (this.getOwner().isDead()) {
            return;
        }
        if (attacker != null) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if (attackerPlayer != null && attackerPlayer.isInDuel()) {
                attackerPlayer.setDuelState(4);
            }
            this.getOwner().addAttackerToAttackByList(attacker);
        }
        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
    }
    
    @Override
    public Npc getOwner() {
        return (Npc)super.getOwner();
    }
}
