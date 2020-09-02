// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.GetOffVehicle;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.GameClient;

public final class RequestGetOffVehicle extends ClientPacket
{
    private int _boatId;
    private int _x;
    private int _y;
    private int _z;
    
    public void readImpl() {
        this._boatId = this.readInt();
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!activeChar.isInBoat() || activeChar.getBoat().getObjectId() != this._boatId || activeChar.getBoat().isMoving() || !MathUtil.isInsideRadius3D(activeChar, this._x, this._y, this._z, 1000)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        activeChar.broadcastPacket(new StopMoveInVehicle(activeChar, this._boatId));
        activeChar.setVehicle(null);
        activeChar.setInVehiclePosition(null);
        ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
        activeChar.broadcastPacket(new GetOffVehicle(activeChar.getObjectId(), this._boatId, this._x, this._y, this._z));
        activeChar.setXYZ(this._x, this._y, this._z);
        activeChar.setInsideZone(ZoneType.PEACE, false);
        activeChar.revalidateZone(true);
    }
}
