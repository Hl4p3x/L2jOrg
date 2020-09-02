// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowFortressSiegeInfo extends ServerPacket
{
    private final int _csize = 0;
    private final int _csize2 = 0;
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_FORTRESS_SIEGE_INFO);
        this.writeInt(0);
        this.writeInt(0);
        for (int i = 0; i < 0; ++i) {
            this.writeInt(0);
        }
    }
}
