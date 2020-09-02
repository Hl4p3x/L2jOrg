// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;

public class PetStatus extends SummonStatus
{
    private int _currentFed;
    
    public PetStatus(final Pet activeChar) {
        super(activeChar);
        this._currentFed = 0;
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHpConsumption) {
        if (this.getOwner().isDead()) {
            return;
        }
        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
        if (attacker != null) {
            if (!isDOT && this.getOwner().getOwner() != null) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_RECEIVED_S2_DAMAGE_BY_C1);
                sm.addString(attacker.getName());
                sm.addInt((int)value);
                this.getOwner().sendPacket(sm);
            }
            this.getOwner().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
        }
    }
    
    public int getCurrentFed() {
        return this._currentFed;
    }
    
    public void setCurrentFed(final int value) {
        this._currentFed = value;
    }
    
    @Override
    public Pet getOwner() {
        return (Pet)super.getOwner();
    }
}
