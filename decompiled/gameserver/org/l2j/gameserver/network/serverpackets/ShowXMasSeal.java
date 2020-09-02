// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShowXMasSeal extends ServerPacket
{
    private final int _item;
    
    public ShowXMasSeal(final int item) {
        this._item = item;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_XMASSEAL);
        this.writeInt(this._item);
    }
}
