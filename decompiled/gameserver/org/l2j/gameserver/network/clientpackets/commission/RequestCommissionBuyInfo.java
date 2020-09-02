// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionBuyInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCommissionBuyInfo extends ClientPacket
{
    private long _commissionId;
    
    public void readImpl() {
        this._commissionId = this.readLong();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            ((GameClient)this.client).sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }
        if (!player.isInventoryUnder80(false) || player.getWeightPenalty() >= 3) {
            ((GameClient)this.client).sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            ((GameClient)this.client).sendPacket(ExResponseCommissionBuyInfo.FAILED);
            return;
        }
        final CommissionItem commissionItem = CommissionManager.getInstance().getCommissionItem(this._commissionId);
        if (commissionItem != null) {
            ((GameClient)this.client).sendPacket(new ExResponseCommissionBuyInfo(commissionItem));
        }
        else {
            ((GameClient)this.client).sendPacket(SystemMessageId.ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST);
            ((GameClient)this.client).sendPacket(ExResponseCommissionBuyInfo.FAILED);
        }
    }
}
