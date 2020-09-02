// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.GameClient;

public final class CannotMoveAnymoreInVehicle extends ClientPacket
{
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _boatId;
    
    public void readImpl() {
        this._boatId = this.readInt();
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
        this._heading = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.isInBoat() && player.getBoat().getObjectId() == this._boatId) {
            player.setInVehiclePosition(new Location(this._x, this._y, this._z));
            player.setHeading(this._heading);
            final StopMoveInVehicle msg = new StopMoveInVehicle(player, this._boatId);
            player.broadcastPacket(msg);
        }
    }
}
