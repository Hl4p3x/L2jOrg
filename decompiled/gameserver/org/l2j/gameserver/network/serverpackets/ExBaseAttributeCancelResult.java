// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBaseAttributeCancelResult extends ServerPacket
{
    private final int _objId;
    private final byte _attribute;
    
    public ExBaseAttributeCancelResult(final int objId, final byte attribute) {
        this._objId = objId;
        this._attribute = attribute;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BASE_ATTRIBUTE_CANCEL_RESULT);
        this.writeInt(1);
        this.writeInt(this._objId);
        this.writeInt((int)this._attribute);
    }
}
