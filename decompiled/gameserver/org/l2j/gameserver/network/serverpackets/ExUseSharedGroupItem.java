// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExUseSharedGroupItem extends ServerPacket
{
    private final int _itemId;
    private final int _grpId;
    private final int _remainingTime;
    private final int _totalTime;
    
    public ExUseSharedGroupItem(final int itemId, final int grpId, final long remainingTime, final int totalTime) {
        this._itemId = itemId;
        this._grpId = grpId;
        this._remainingTime = (int)(remainingTime / 1000L);
        this._totalTime = totalTime / 1000;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USE_SHARED_GROUP_ITEM);
        this.writeInt(this._itemId);
        this.writeInt(this._grpId);
        this.writeInt(this._remainingTime);
        this.writeInt(this._totalTime);
    }
}
