// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutCommissionResultForVariationMake extends ServerPacket
{
    private final int _gemstoneObjId;
    private final int _itemId;
    private final long _gemstoneCount;
    private final int _unk1;
    private final int _unk2;
    
    public ExPutCommissionResultForVariationMake(final int gemstoneObjId, final long count, final int itemId) {
        this._gemstoneObjId = gemstoneObjId;
        this._itemId = itemId;
        this._gemstoneCount = count;
        this._unk1 = 0;
        this._unk2 = 1;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_COMMISSION_RESULT_FOR_VARIATION_MAKE);
        this.writeInt(this._gemstoneObjId);
        this.writeInt(this._itemId);
        this.writeLong(this._gemstoneCount);
        this.writeLong((long)this._unk1);
        this.writeInt(this._unk2);
    }
}
