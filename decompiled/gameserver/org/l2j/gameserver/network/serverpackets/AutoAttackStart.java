// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class AutoAttackStart extends ServerPacket
{
    private final int _targetObjId;
    
    public AutoAttackStart(final int targetId) {
        this._targetObjId = targetId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.COMBAT_MODE_START);
        this.writeInt(this._targetObjId);
    }
}
