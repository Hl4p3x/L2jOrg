// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRedSky extends ServerPacket
{
    private final int _duration;
    
    public ExRedSky(final int duration) {
        this._duration = duration;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REDSKY);
        this.writeInt(this._duration);
    }
}
