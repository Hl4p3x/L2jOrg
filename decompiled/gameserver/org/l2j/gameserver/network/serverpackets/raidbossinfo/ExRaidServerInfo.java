// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRaidServerInfo extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RAID_SERVER_INFO);
    }
}
