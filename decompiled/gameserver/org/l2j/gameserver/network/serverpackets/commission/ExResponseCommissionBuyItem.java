// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExResponseCommissionBuyItem extends ServerPacket
{
    public static final ExResponseCommissionBuyItem FAILED;
    private final CommissionItem _commissionItem;
    
    public ExResponseCommissionBuyItem(final CommissionItem commissionItem) {
        this._commissionItem = commissionItem;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_BUY_ITEM);
        this.writeInt((int)((this._commissionItem != null) ? 1 : 0));
        if (this._commissionItem != null) {
            final ItemInfo itemInfo = this._commissionItem.getItemInfo();
            this.writeInt(itemInfo.getEnchantLevel());
            this.writeInt(itemInfo.getId());
            this.writeLong(itemInfo.getCount());
        }
    }
    
    static {
        FAILED = new ExResponseCommissionBuyItem(null);
    }
}
