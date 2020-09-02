// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadMode extends ServerPacket
{
    private final int _mode;
    
    public ExOlympiadMode(final int mode) {
        this._mode = mode;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_MODE);
        this.writeByte((byte)this._mode);
    }
}
