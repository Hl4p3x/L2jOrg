// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.data.database.data.RankData;
import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRankingCharInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        final RankData rank = RankEngine.getInstance().getRank(client.getPlayer());
        this.writeId(ServerExPacketId.EX_RANKING_CHAR_INFO);
        if (Objects.isNull(rank)) {
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
        }
        else {
            this.writeInt(rank.getRank());
            this.writeInt(rank.getRankRace());
            this.writeInt(rank.getRankSnapshot());
            this.writeInt(rank.getRankRaceSnapshot());
        }
    }
}
