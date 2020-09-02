// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class RadarControl extends ServerPacket
{
    private final int _showRadar;
    private final int _type;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public RadarControl(final int showRadar, final int type, final int x, final int y, final int z) {
        this._showRadar = showRadar;
        this._type = type;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RADAR_CONTROL);
        this.writeInt(this._showRadar);
        this.writeInt(this._type);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
