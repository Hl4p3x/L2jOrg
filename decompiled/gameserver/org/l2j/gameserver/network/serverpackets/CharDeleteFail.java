// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.CharacterDeleteFailType;

public class CharDeleteFail extends ServerPacket
{
    private final int _error;
    
    public CharDeleteFail(final CharacterDeleteFailType type) {
        this._error = type.ordinal();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_DELETE_FAIL);
        this.writeInt(this._error);
    }
}
