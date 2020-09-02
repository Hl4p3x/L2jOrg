// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExChangeNpcState extends ServerPacket
{
    private final int _objId;
    private final int _state;
    
    public ExChangeNpcState(final int objId, final int state) {
        this._objId = objId;
        this._state = state;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_NPC_STATE);
        this.writeInt(this._objId);
        this.writeInt(this._state);
    }
}
