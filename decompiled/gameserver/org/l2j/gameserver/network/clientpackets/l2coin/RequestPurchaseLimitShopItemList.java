// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.l2coin;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.l2coin.ExPurchaseLimitShopItemListNew;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPurchaseLimitShopItemList extends ClientPacket
{
    private byte index;
    
    @Override
    protected void readImpl() throws Exception {
        this.index = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExPurchaseLimitShopItemListNew(this.index));
    }
}
