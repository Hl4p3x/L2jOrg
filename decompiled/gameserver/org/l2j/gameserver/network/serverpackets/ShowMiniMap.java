// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShowMiniMap extends ServerPacket
{
    private final int _mapId;
    
    public ShowMiniMap(final int mapId) {
        this._mapId = mapId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_MINIMAP);
        this.writeInt(this._mapId);
        this.writeByte(0);
    }
}
