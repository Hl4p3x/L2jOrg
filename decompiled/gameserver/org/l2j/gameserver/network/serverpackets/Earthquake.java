// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.interfaces.ILocational;

public class Earthquake extends ServerPacket
{
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _intensity;
    private final int _duration;
    
    public Earthquake(final ILocational location, final int intensity, final int duration) {
        this._x = location.getX();
        this._y = location.getY();
        this._z = location.getZ();
        this._intensity = intensity;
        this._duration = duration;
    }
    
    public Earthquake(final int x, final int y, final int z, final int intensity, final int duration) {
        this._x = x;
        this._y = y;
        this._z = z;
        this._intensity = intensity;
        this._duration = duration;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.EARTHQUAKE);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._intensity);
        this.writeInt(this._duration);
        this.writeInt(0);
    }
}
