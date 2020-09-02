// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.payback;

import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPaybackList extends ServerPacket
{
    private final byte eventType;
    
    public ExPaybackList(final byte eventType) {
        this.eventType = eventType;
    }
    
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_PAYBACK_LIST);
        this.writeInt(2);
        for (int i = 0; i < 2; ++i) {
            this.writeInt(3);
            for (int j = 0; j < 3; ++j) {
                this.writeInt(57 + j);
                this.writeInt(1);
            }
            this.writeByte(i);
            this.writeInt(6);
            this.writeByte(5);
        }
        this.writeByte(this.eventType);
        this.writeInt((int)Instant.now().plus(6L, (TemporalUnit)ChronoUnit.HOURS).getEpochSecond());
        this.writeInt(57);
        this.writeInt(2);
    }
}
