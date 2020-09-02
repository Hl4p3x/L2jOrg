// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.GetOnVehicle;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Location;

public final class RequestGetOnVehicle extends ClientPacket
{
    private int _boatId;
    private Location _pos;
    
    public void readImpl() {
        this._boatId = this.readInt();
        this._pos = new Location(this.readInt(), this.readInt(), this.readInt());
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        Boat boat;
        if (activeChar.isInBoat()) {
            boat = activeChar.getBoat();
            if (boat.getObjectId() != this._boatId) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        else {
            boat = BoatManager.getInstance().getBoat(this._boatId);
            if (boat == null || boat.isMoving() || !MathUtil.isInsideRadius3D(activeChar, boat, 1000)) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        activeChar.setInVehiclePosition(this._pos);
        activeChar.setVehicle(boat);
        activeChar.broadcastPacket(new GetOnVehicle(activeChar.getObjectId(), boat.getObjectId(), this._pos));
        activeChar.setXYZ(boat.getX(), boat.getY(), boat.getZ());
        activeChar.setInsideZone(ZoneType.PEACE, true);
        activeChar.revalidateZone(true);
    }
}
