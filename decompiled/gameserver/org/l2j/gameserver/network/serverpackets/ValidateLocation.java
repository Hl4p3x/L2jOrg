// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.Location;

public class ValidateLocation extends ServerPacket
{
    private final int _charObjId;
    private final Location _loc;
    
    public ValidateLocation(final WorldObject obj) {
        this._charObjId = obj.getObjectId();
        this._loc = obj.getLocation();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VALIDATE_LOCATION);
        this.writeInt(this._charObjId);
        this.writeInt(this._loc.getX());
        this.writeInt(this._loc.getY());
        this.writeInt(this._loc.getZ());
        this.writeInt(this._loc.getHeading());
        this.writeByte((byte)(-1));
    }
}
