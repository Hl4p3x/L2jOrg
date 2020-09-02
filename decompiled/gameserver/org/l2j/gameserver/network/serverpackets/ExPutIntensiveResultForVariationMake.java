// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPutIntensiveResultForVariationMake extends ServerPacket
{
    private final int _refinerItemObjId;
    private final int _lifestoneItemId;
    private final int _gemstoneItemId;
    private final long _gemstoneCount;
    private final int _unk2;
    
    public ExPutIntensiveResultForVariationMake(final int refinerItemObjId, final int lifeStoneId, final int gemstoneItemId, final long gemstoneCount) {
        this._refinerItemObjId = refinerItemObjId;
        this._lifestoneItemId = lifeStoneId;
        this._gemstoneItemId = gemstoneItemId;
        this._gemstoneCount = gemstoneCount;
        this._unk2 = 1;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PUT_INTENSIVE_RESULT_FOR_VARIATION_MAKE);
        this.writeInt(this._refinerItemObjId);
        this.writeInt(this._lifestoneItemId);
        this.writeInt(this._gemstoneItemId);
        this.writeLong(this._gemstoneCount);
        this.writeInt(this._unk2);
    }
}
