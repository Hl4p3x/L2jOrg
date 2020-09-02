// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Location;

public class ObservationReturn extends ServerPacket
{
    private final Location _loc;
    
    public ObservationReturn(final Location loc) {
        this._loc = loc;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.OBSERVER_END);
        this.writeInt(this._loc.getX());
        this.writeInt(this._loc.getY());
        this.writeInt(this._loc.getZ());
    }
}
