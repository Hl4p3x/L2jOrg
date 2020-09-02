// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExChangeToAwakenedClass extends ServerPacket
{
    private final int _classId;
    
    public ExChangeToAwakenedClass(final int classId) {
        this._classId = classId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_TO_AWAKENED_CLASS);
        this.writeInt(this._classId);
    }
}
