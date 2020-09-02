// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class TradeDone extends ServerPacket
{
    public static final TradeDone CANCELLED;
    public static final TradeDone COMPLETED;
    private final boolean completed;
    
    private TradeDone(final boolean completed) {
        this.completed = completed;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_DONE);
        this.writeInt(this.completed);
    }
    
    static {
        CANCELLED = new TradeDone(false);
        COMPLETED = new TradeDone(true);
    }
}
