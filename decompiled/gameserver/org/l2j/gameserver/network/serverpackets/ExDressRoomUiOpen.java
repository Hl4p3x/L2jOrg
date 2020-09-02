// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDressRoomUiOpen extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_COMPLETED_DAILY_QUEST_LIST);
        this.writeInt(0);
    }
}
