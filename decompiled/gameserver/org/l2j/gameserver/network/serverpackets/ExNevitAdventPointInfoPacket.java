// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNevitAdventPointInfoPacket extends ServerPacket
{
    private final int _points;
    
    public ExNevitAdventPointInfoPacket(final int points) {
        this._points = points;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_AGATHION_ENERGY_INFO);
        this.writeInt(this._points);
    }
}
