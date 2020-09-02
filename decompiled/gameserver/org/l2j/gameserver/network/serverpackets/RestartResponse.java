// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class RestartResponse extends ServerPacket
{
    public static final RestartResponse TRUE;
    public static final RestartResponse FALSE;
    private final boolean _result;
    
    private RestartResponse(final boolean result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RESTART_RESPONSE);
        this.writeInt((int)(this._result ? 1 : 0));
    }
    
    static {
        TRUE = new RestartResponse(true);
        FALSE = new RestartResponse(false);
    }
}
