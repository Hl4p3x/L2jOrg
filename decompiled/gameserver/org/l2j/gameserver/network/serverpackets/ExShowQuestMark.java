// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowQuestMark extends ServerPacket
{
    private final int _questId;
    private final int _questState;
    
    public ExShowQuestMark(final int questId, final int questState) {
        this._questId = questId;
        this._questState = questState;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_QUEST_MARK);
        this.writeInt(this._questId);
        this.writeInt(this._questState);
    }
}
