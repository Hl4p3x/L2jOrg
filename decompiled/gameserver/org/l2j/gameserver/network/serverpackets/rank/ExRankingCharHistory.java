// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.rank;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.data.database.data.RankHistoryData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRankingCharHistory extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        final List<RankHistoryData> history = RankEngine.getInstance().getPlayerHistory(client.getPlayer());
        this.writeId(ServerExPacketId.EX_RANKING_CHAR_HISTORY);
        this.writeInt(history.size());
        for (final RankHistoryData data : history) {
            this.writeInt(data.getDate());
            this.writeInt(data.getRank());
            this.writeLong(data.getExp());
        }
    }
}
