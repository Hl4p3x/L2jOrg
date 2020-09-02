// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNotifyPremiumItem extends ServerPacket
{
    public static final ExNotifyPremiumItem STATIC_PACKET;
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_NOTIFY_PREMIUM_ITEM);
    }
    
    static {
        STATIC_PACKET = new ExNotifyPremiumItem();
    }
}
