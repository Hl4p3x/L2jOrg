// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRotation extends ServerPacket
{
    private final int _charId;
    private final int _heading;
    
    public ExRotation(final int charId, final int heading) {
        this._charId = charId;
        this._heading = heading;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ROTATION);
        this.writeInt(this._charId);
        this.writeInt(this._heading);
    }
}
