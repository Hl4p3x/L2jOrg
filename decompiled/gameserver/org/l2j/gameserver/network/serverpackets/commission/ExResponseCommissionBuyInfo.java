// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public class ExResponseCommissionBuyInfo extends AbstractItemPacket
{
    public static final ExResponseCommissionBuyInfo FAILED;
    private final CommissionItem _commissionItem;
    
    public ExResponseCommissionBuyInfo(final CommissionItem commissionItem) {
        this._commissionItem = commissionItem;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_BUY_INFO);
        this.writeInt((int)((this._commissionItem != null) ? 1 : 0));
        if (this._commissionItem != null) {
            this.writeLong(this._commissionItem.getPricePerUnit());
            this.writeLong(this._commissionItem.getCommissionId());
            this.writeInt(0);
            this.writeItem(this._commissionItem.getItemInfo());
        }
    }
    
    static {
        FAILED = new ExResponseCommissionBuyInfo(null);
    }
}
