// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRequestChangeNicknameColor extends ServerPacket
{
    private final int _itemObjectId;
    
    public ExRequestChangeNicknameColor(final int itemObjectId) {
        this._itemObjectId = itemObjectId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_NICKNAME_COLOR);
        this.writeInt(this._itemObjectId);
    }
}
