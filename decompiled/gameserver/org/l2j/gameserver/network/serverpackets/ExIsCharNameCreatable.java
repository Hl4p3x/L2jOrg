// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExIsCharNameCreatable extends ServerPacket
{
    private final int _allowed;
    
    public ExIsCharNameCreatable(final int allowed) {
        this._allowed = allowed;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHECK_CHAR_NAME);
        this.writeInt(this._allowed);
    }
}
