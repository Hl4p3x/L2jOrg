// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.l2store;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestBRProductInfo extends ClientPacket
{
    private int _brId;
    
    public void readImpl() {
        this._brId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player != null) {
            L2Store.getInstance().showProductInfo(player, this._brId);
        }
    }
}
