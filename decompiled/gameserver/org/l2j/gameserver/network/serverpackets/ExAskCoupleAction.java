// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExAskCoupleAction extends ServerPacket
{
    private final int _charObjId;
    private final int _actionId;
    
    public ExAskCoupleAction(final int charObjId, final int social) {
        this._charObjId = charObjId;
        this._actionId = social;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ASK_COUPLE_ACTION);
        this.writeInt(this._actionId);
        this.writeInt(this._charObjId);
    }
}
