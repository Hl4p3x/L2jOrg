// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class RequestEnchant extends ServerPacket
{
    private final int _result;
    
    public RequestEnchant(final int result) {
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PRIVATE_STORE_WHOLE_MSG);
        this.writeInt(this._result);
    }
}
