// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class MagicSkillCanceld extends ServerPacket
{
    private final int _objectId;
    
    public MagicSkillCanceld(final int objectId) {
        this._objectId = objectId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MAGIC_SKILL_CANCELED);
        this.writeInt(this._objectId);
    }
}
