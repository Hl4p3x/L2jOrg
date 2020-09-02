// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCostumeLock extends ServerPacket
{
    private final int costumeId;
    private final boolean lock;
    private final boolean result;
    
    public ExCostumeLock(final int costumeId, final boolean lock, final boolean result) {
        this.costumeId = costumeId;
        this.lock = lock;
        this.result = result;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COSTUME_LOCK);
        this.writeByte(this.result);
        this.writeInt(this.costumeId);
        this.writeByte(this.lock);
    }
}
