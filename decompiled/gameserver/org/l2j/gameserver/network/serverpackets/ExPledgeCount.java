// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public class ExPledgeCount extends ServerPacket
{
    private final int _count;
    
    public ExPledgeCount(final Clan clan) {
        this._count = clan.getOnlineMembersCount();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_COUNT);
        this.writeInt(this._count);
    }
}
