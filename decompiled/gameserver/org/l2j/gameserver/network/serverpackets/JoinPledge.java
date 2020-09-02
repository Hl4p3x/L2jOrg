// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class JoinPledge extends ServerPacket
{
    private final int _pledgeId;
    
    public JoinPledge(final int pledgeId) {
        this._pledgeId = pledgeId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.JOIN_PLEDGE);
        this.writeInt(this._pledgeId);
    }
}
