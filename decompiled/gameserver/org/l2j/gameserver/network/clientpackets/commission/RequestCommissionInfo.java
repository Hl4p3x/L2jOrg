// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCommissionInfo extends ClientPacket
{
    private int _itemObjectId;
    
    public void readImpl() {
        this._itemObjectId = this.readInt();
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
        final Item itemInstance = player.getInventory().getItemByObjectId(this._itemObjectId);
        if (itemInstance != null) {
            ((GameClient)this.client).sendPacket(player.getLastCommissionInfos().getOrDefault(itemInstance.getId(), ExResponseCommissionInfo.EMPTY));
        }
        else {
            ((GameClient)this.client).sendPacket(ExResponseCommissionInfo.EMPTY);
        }
    }
}
