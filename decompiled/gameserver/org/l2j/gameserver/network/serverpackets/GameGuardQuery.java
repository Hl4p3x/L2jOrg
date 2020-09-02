// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class GameGuardQuery extends ServerPacket
{
    public static final GameGuardQuery STATIC_PACKET;
    
    private GameGuardQuery() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GAMEGUARD_QUERY);
        this.writeInt(659766745);
        this.writeInt(779265309);
        this.writeInt(538379147);
        this.writeInt(-1017438557);
    }
    
    static {
        STATIC_PACKET = new GameGuardQuery();
    }
}
