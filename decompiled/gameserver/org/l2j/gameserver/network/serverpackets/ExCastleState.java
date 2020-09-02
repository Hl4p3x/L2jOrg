// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.enums.CastleSide;

public class ExCastleState extends ServerPacket
{
    private final int _castleId;
    private final CastleSide _castleSide;
    
    public ExCastleState(final Castle castle) {
        this._castleId = castle.getId();
        this._castleSide = castle.getSide();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CASTLE_STATE);
        this.writeInt(this._castleId);
        this.writeInt(this._castleSide.ordinal());
    }
}
