// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPurchaseLimitShopItemList;
import org.l2j.gameserver.network.GameClient;

public class RequestOpenWndWithoutNPC extends ClientPacket
{
    private int dialogId;
    
    @Override
    protected void readImpl() throws Exception {
        this.dialogId = this.readByte();
    }
    
    @Override
    protected void runImpl() throws Exception {
        if (this.dialogId == 4) {
            ((GameClient)this.getClient()).sendPacket(new ExPurchaseLimitShopItemList());
        }
    }
}
