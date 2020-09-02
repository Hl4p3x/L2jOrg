// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class CSShowComBoard extends ServerPacket
{
    private final byte[] _html;
    
    public CSShowComBoard(final byte[] html) {
        this._html = html;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_BOARD);
        this.writeByte((byte)1);
        this.writeBytes(this._html);
    }
}
