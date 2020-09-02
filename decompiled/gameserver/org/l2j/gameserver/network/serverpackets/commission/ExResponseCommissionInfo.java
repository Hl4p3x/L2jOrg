// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExResponseCommissionInfo extends ServerPacket
{
    public static final ExResponseCommissionInfo EMPTY;
    private final int _result;
    private final int _itemId;
    private final long _presetPricePerUnit;
    private final long _presetAmount;
    private final int _presetDurationType;
    
    private ExResponseCommissionInfo() {
        this._result = 0;
        this._itemId = 0;
        this._presetPricePerUnit = 0L;
        this._presetAmount = 0L;
        this._presetDurationType = -1;
    }
    
    public ExResponseCommissionInfo(final int itemId, final long presetPricePerUnit, final long presetAmount, final int presetDurationType) {
        this._result = 1;
        this._itemId = itemId;
        this._presetPricePerUnit = presetPricePerUnit;
        this._presetAmount = presetAmount;
        this._presetDurationType = presetDurationType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_INFO);
        this.writeInt(this._result);
        this.writeInt(this._itemId);
        this.writeLong(this._presetPricePerUnit);
        this.writeLong(this._presetAmount);
        this.writeInt(this._presetDurationType);
    }
    
    static {
        EMPTY = new ExResponseCommissionInfo();
    }
}
