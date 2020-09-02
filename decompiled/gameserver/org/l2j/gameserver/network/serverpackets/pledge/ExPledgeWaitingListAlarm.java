// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeWaitingListAlarm extends ServerPacket
{
    public static final ExPledgeWaitingListAlarm STATIC_PACKET;
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_WAITING_LIST_ALARM);
    }
    
    static {
        STATIC_PACKET = new ExPledgeWaitingListAlarm();
    }
}
