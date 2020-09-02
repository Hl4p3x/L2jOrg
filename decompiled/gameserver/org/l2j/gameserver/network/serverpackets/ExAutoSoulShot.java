// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExAutoSoulShot extends ServerPacket
{
    private final int _itemId;
    private final boolean _enable;
    private final int _type;
    
    public ExAutoSoulShot(final int itemId, final boolean enable, final int type) {
        this._itemId = itemId;
        this._enable = enable;
        this._type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_AUTO_SOULSHOT);
        this.writeInt(this._itemId);
        this.writeInt(this._enable);
        this.writeInt(this._type);
    }
}
