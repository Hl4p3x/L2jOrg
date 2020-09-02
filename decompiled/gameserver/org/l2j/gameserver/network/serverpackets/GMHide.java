// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

@Deprecated
public class GMHide extends ServerPacket
{
    private final int _mode;
    
    public GMHide(final int mode) {
        this._mode = mode;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_HIDE);
        this.writeInt(this._mode);
    }
}
