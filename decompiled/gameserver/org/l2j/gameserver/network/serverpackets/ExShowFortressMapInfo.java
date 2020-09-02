// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowFortressMapInfo extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_FORTRESS_MAP_INFO);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
