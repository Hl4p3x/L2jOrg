// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.shuttle.ExShuttleMove;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.model.actor.instance.Shuttle;

public class ShuttleAI extends VehicleAI
{
    public ShuttleAI(final Shuttle shuttle) {
        super(shuttle);
    }
    
    public void moveTo(final int x, final int y, final int z) {
        if (!this.actor.isMovementDisabled()) {
            this._clientMoving = true;
            this.actor.moveToLocation(x, y, z, 0);
            this.actor.broadcastPacket(new ExShuttleMove(this.getActor(), x, y, z));
        }
    }
    
    @Override
    public Shuttle getActor() {
        return (Shuttle)this.actor;
    }
}
