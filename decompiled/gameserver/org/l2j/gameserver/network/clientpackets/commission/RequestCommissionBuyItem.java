// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCommissionBuyItem extends ClientPacket
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
        CommissionManager.getInstance().buyItem(player, this._commissionId);
    }
}
