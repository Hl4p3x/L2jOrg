// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.primitive.IntSet;

public class BlockListPacket extends ServerPacket
{
    private final IntSet blockedIds;
    
    public BlockListPacket(final IntSet playersId) {
        this.blockedIds = playersId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.BLOCK_PACKET_LIST);
        this.writeInt(this.blockedIds.size());
        final PlayerNameTable playerNameTable = PlayerNameTable.getInstance();
        this.blockedIds.forEach(id -> {
            this.writeString((CharSequence)playerNameTable.getNameById(id));
            this.writeString((CharSequence)"");
        });
    }
}
