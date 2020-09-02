// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
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
            PrimeShopData.getInstance().showProductInfo(player, this._brId);
        }
    }
}
