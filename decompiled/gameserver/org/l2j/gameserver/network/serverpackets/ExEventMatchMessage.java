// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExEventMatchMessage extends ServerPacket
{
    private final int _type;
    private final String _message;
    
    public ExEventMatchMessage(final int type, final String message) {
        this._type = type;
        this._message = message;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_EVENT_MATCH_MESSAGE);
        this.writeByte((byte)this._type);
        this.writeString((CharSequence)this._message);
    }
}
