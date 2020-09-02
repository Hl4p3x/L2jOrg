// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.VehicleInfo;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.VehicleDeparture;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.VehicleStarted;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.model.actor.instance.Boat;

public class BoatAI extends VehicleAI
{
    public BoatAI(final Boat boat) {
        super(boat);
    }
    
    @Override
    protected void moveTo(final int x, final int y, final int z) {
        if (!this.actor.isMovementDisabled()) {
            if (!this._clientMoving) {
                this.actor.broadcastPacket(new VehicleStarted(this.getActor(), 1));
            }
            this._clientMoving = true;
            this.actor.moveToLocation(x, y, z, 0);
            this.actor.broadcastPacket(new VehicleDeparture(this.getActor()));
        }
    }
    
    @Override
    public void clientStopMoving(final Location loc) {
        if (this.actor.isMoving()) {
            this.actor.stopMove(loc);
        }
        if (this._clientMoving || loc != null) {
            this._clientMoving = false;
            this.actor.broadcastPacket(new VehicleStarted(this.getActor(), 0));
            this.actor.broadcastPacket(new VehicleInfo(this.getActor()));
        }
    }
    
    @Override
    public void describeStateToPlayer(final Player player) {
        if (this._clientMoving) {
            player.sendPacket(new VehicleDeparture(this.getActor()));
        }
    }
    
    @Override
    public Boat getActor() {
        return (Boat)this.actor;
    }
}
