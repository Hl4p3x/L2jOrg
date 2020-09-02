// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCostumeShortCutList extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_COSTUME_SHORTCUT_LIST);
        this.writeByte(1);
        this.writeInt(0);
    }
}
