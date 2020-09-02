// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class JoinParty extends ServerPacket
{
    private final int _response;
    
    public JoinParty(final int response) {
        this._response = response;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.JOIN_PARTY);
        this.writeInt(this._response);
        this.writeInt(0);
    }
}
