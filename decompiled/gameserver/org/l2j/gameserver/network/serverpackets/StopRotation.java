// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class StopRotation extends ServerPacket
{
    private final int _charObjId;
    private final int _degree;
    private final int _speed;
    
    public StopRotation(final int objectId, final int degree, final int speed) {
        this._charObjId = objectId;
        this._degree = degree;
        this._speed = speed;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FINISH_ROTATING);
        this.writeInt(this._charObjId);
        this.writeInt(this._degree);
        this.writeInt(this._speed);
        this.writeInt(0);
    }
}
