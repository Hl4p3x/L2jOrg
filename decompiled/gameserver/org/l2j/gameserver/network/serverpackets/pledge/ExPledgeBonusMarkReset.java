// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeBonusMarkReset extends ServerPacket
{
    public static ExPledgeBonusMarkReset STATIC_PACKET;
    
    private ExPledgeBonusMarkReset() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_ACTIVITY_MARK_RESET);
    }
    
    static {
        ExPledgeBonusMarkReset.STATIC_PACKET = new ExPledgeBonusMarkReset();
    }
}
