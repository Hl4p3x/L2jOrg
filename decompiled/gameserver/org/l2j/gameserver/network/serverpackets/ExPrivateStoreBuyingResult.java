// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPrivateStoreBuyingResult extends ServerPacket
{
    private final int _objectId;
    private final long _count;
    private final String _seller;
    
    public ExPrivateStoreBuyingResult(final int objectId, final long count, final String seller) {
        this._objectId = objectId;
        this._count = count;
        this._seller = seller;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PRIVATE_STORE_BUYING_RESULT);
        this.writeInt(this._objectId);
        this.writeLong(this._count);
        this.writeString((CharSequence)this._seller);
    }
}
