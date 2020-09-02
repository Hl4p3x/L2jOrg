// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPrivateStoreSellingResult extends ServerPacket
{
    private final int _objectId;
    private final long _count;
    private final String _buyer;
    
    public ExPrivateStoreSellingResult(final int objectId, final long count, final String buyer) {
        this._objectId = objectId;
        this._count = count;
        this._buyer = buyer;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PRIVATE_STORE_SELLING_RESULT);
        this.writeInt(this._objectId);
        this.writeLong(this._count);
        this.writeString((CharSequence)this._buyer);
    }
}
