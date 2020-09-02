// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCostumeUseItem extends ServerPacket
{
    private final int costumeId;
    private final boolean success;
    
    public ExCostumeUseItem(final int costumeId, final boolean success) {
        this.costumeId = costumeId;
        this.success = success;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COSTUME_USE_ITEM);
        this.writeByte(this.success);
        this.writeInt(this.costumeId);
    }
}
