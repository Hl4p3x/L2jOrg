// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExMercenarySiegeHUDInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_HUD_INFO);
        this.writeInt(3);
        this.writeInt(0);
        this.writeLong(System.currentTimeMillis());
    }
}
