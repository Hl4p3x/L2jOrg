// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.commission;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCommissionRegister extends ClientPacket
{
    private static final Logger LOGGER;
    private int _itemObjectId;
    private long _pricePerUnit;
    private long _itemCount;
    private int _durationType;
    
    public void readImpl() {
        this._itemObjectId = this.readInt();
        this.readString();
        this._pricePerUnit = this.readLong();
        this._itemCount = this.readLong();
        this._durationType = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._durationType < 0 || this._durationType > 3) {
            RequestCommissionRegister.LOGGER.warn("Player {} sent incorrect commission duration type: {}.", (Object)player, (Object)this._durationType);
            return;
        }
        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            ((GameClient)this.client).sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }
        CommissionManager.getInstance().registerItem(player, this._itemObjectId, this._itemCount, this._pricePerUnit, (byte)(this._durationType * 2 + 1));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestCommissionRegister.class);
    }
}
