// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShowTownMap extends ServerPacket
{
    private final String _texture;
    private final int _x;
    private final int _y;
    
    public ShowTownMap(final String texture, final int x, final int y) {
        this._texture = texture;
        this._x = x;
        this._y = y;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_TOWNMAP);
        this.writeString((CharSequence)this._texture);
        this.writeInt(this._x);
        this.writeInt(this._y);
    }
}
