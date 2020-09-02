// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.luckygame;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExStartLuckyGame extends ServerPacket
{
    private final LuckyGameType _type;
    private final int _ticketCount;
    
    public ExStartLuckyGame(final LuckyGameType type, final long ticketCount) {
        this._type = type;
        this._ticketCount = (int)ticketCount;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_START_LUCKY_GAME);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._ticketCount);
    }
}
