// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExLightingCandleEvent extends ServerPacket
{
    public static final ExLightingCandleEvent ENABLED;
    public static final ExLightingCandleEvent DISABLED;
    private short enabled;
    
    private ExLightingCandleEvent(final short enabled) {
        this.enabled = enabled;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_LIGHTING_CANDLE);
        this.writeShort(this.enabled);
    }
    
    static {
        ENABLED = new ExLightingCandleEvent((short)1);
        DISABLED = new ExLightingCandleEvent((short)1);
    }
}
