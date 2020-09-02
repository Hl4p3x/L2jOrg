// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShowPCCafeCouponShowUI extends ServerPacket
{
    public static final ShowPCCafeCouponShowUI STATIC_PACKET;
    
    private ShowPCCafeCouponShowUI() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PCCAFE_COUPON_SHOW_UI);
    }
    
    static {
        STATIC_PACKET = new ShowPCCafeCouponShowUI();
    }
}
