// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class StartRotation extends ServerPacket
{
    private final int _charObjId;
    private final int _degree;
    private final int _side;
    private final int _speed;
    
    public StartRotation(final int objectId, final int degree, final int side, final int speed) {
        this._charObjId = objectId;
        this._degree = degree;
        this._side = side;
        this._speed = speed;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.START_ROTATING);
        this.writeInt(this._charObjId);
        this.writeInt(this._degree);
        this.writeInt(this._side);
        this.writeInt(this._speed);
    }
}
