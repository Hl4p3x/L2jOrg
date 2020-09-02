// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNevitAdventTimeChange extends ServerPacket
{
    private final boolean _paused;
    private final int _time;
    
    public ExNevitAdventTimeChange(final int time) {
        this._time = ((time > 240000) ? 240000 : time);
        this._paused = (this._time < 1);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_CRYSTALITEM_INFO);
        this.writeByte((byte)(byte)(this._paused ? 0 : 1));
        this.writeInt(this._time);
    }
}
