// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExAutoFishAvailable extends ServerPacket
{
    public static ExAutoFishAvailable YES;
    public static ExAutoFishAvailable NO;
    private final boolean _available;
    
    private ExAutoFishAvailable(final boolean available) {
        this._available = available;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_AUTOFISH_AVAILABLE);
        this.writeByte((byte)(byte)(this._available ? 1 : 0));
    }
    
    static {
        ExAutoFishAvailable.YES = new ExAutoFishAvailable(true);
        ExAutoFishAvailable.NO = new ExAutoFishAvailable(false);
    }
}
