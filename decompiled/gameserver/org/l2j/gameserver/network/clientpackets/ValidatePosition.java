// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.GetOnVehicle;
import org.l2j.gameserver.engine.geo.SyncMode;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.network.GameClient;

public class ValidatePosition extends ClientPacket
{
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _data;
    
    public void readImpl() {
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
        this._heading = this.readInt();
        this._data = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.isTeleporting() || player.inObserverMode()) {
            return;
        }
        final int realX = player.getX();
        final int realY = player.getY();
        int realZ = player.getZ();
        if (realX == this._x && realY == this._y && realZ == this._z) {
            return;
        }
        if (this._x == 0 && this._y == 0 && realX != 0) {
            return;
        }
        final GeoEngineSettings geoSettings = (GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class);
        if (player.isInBoat()) {
            if (geoSettings.isSyncMode(SyncMode.SERVER)) {
                final int dx = this._x - player.getInVehiclePosition().getX();
                final int dy = this._y - player.getInVehiclePosition().getY();
                final double diffSq = dx * dx + dy * dy;
                if (diffSq > 250000.0) {
                    ((GameClient)this.client).sendPacket(new GetOnVehicle(player.getObjectId(), this._data, player.getInVehiclePosition()));
                }
            }
            return;
        }
        if (player.isFalling(this._z)) {
            return;
        }
        final int dx = this._x - realX;
        final int dy = this._y - realY;
        final int dz = this._z - realZ;
        final double diffSq = dx * dx + dy * dy;
        if (diffSq < 10.0) {
            return;
        }
        if (player.isFlyingMounted() && this._x > -166168) {
            player.untransform();
        }
        if (player.isFlying() || player.isInsideZone(ZoneType.WATER)) {
            player.setXYZ(realX, realY, this._z);
            if (diffSq > 90000.0) {
                player.sendPacket(new ValidateLocation(player));
            }
        }
        else if (diffSq < 360000.0) {
            if (geoSettings.isSyncMode(SyncMode.Z_ONLY)) {
                player.setXYZ(realX, realY, this._z);
                return;
            }
            if (geoSettings.isSyncMode(SyncMode.CLIENT)) {
                if (!player.isMoving() || !player.validateMovementHeading(this._heading)) {
                    if (diffSq < 2500.0) {
                        player.setXYZ(realX, realY, this._z);
                    }
                    else {
                        player.setXYZ(this._x, this._y, this._z);
                    }
                }
                else {
                    player.setXYZ(realX, realY, this._z);
                }
                player.setHeading(this._heading);
                return;
            }
            if (diffSq > 250000.0 || Math.abs(dz) > 200) {
                if (Math.abs(dz) > 200 && Math.abs(dz) < 1500 && Math.abs(this._z - player.getClientZ()) < 800) {
                    player.setXYZ(realX, realY, this._z);
                    realZ = this._z;
                }
                else {
                    if (player.isFalling(this._z)) {
                        player.setXYZ(realX, realY, this._z);
                    }
                    player.sendPacket(new ValidateLocation(player));
                }
            }
        }
        player.setClientX(this._x);
        player.setClientY(this._y);
        player.setClientZ(this._z);
        player.setClientHeading(this._heading);
        if (!DoorDataManager.getInstance().checkIfDoorsBetween(realX, realY, realZ, this._x, this._y, this._z, player.getInstanceWorld(), false)) {
            player.setLastServerPosition(realX, realY, realZ);
        }
    }
}
