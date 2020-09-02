// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;

public final class Ride extends ServerPacket
{
    private final int _objectId;
    private final int _mounted;
    private final int _rideType;
    private final int _rideNpcId;
    private final Location _loc;
    
    public Ride(final Player player) {
        this._objectId = player.getObjectId();
        this._mounted = (player.isMounted() ? 1 : 0);
        this._rideType = player.getMountType().ordinal();
        this._rideNpcId = player.getMountNpcId() + 1000000;
        this._loc = player.getLocation();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RIDE);
        this.writeInt(this._objectId);
        this.writeInt(this._mounted);
        this.writeInt(this._rideType);
        this.writeInt(this._rideNpcId);
        this.writeInt(this._loc.getX());
        this.writeInt(this._loc.getY());
        this.writeInt(this._loc.getZ());
    }
}
