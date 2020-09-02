// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class AutoAttackStop extends ServerPacket
{
    private final int _targetObjId;
    
    public AutoAttackStop(final int targetObjId) {
        this._targetObjId = targetObjId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.COMBAT_MODE_FINISH);
        this.writeInt(this._targetObjId);
    }
}
