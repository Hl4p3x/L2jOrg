// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class WarehouseDone extends ServerPacket
{
    private final boolean success;
    
    public WarehouseDone(final boolean success) {
        this.success = success;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.WAREHOUSE_DONE);
        this.writeInt(this.success);
    }
}
