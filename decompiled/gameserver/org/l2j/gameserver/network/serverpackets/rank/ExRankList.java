// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.rank;

import java.util.Objects;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.data.database.data.RankData;
import java.util.Collections;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRankList extends ServerPacket
{
    private final int race;
    private final byte group;
    private final byte scope;
    
    public ExRankList(final byte group, final byte scope, final int race) {
        this.group = group;
        this.scope = scope;
        this.race = race;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RANKING_CHAR_RANKERS);
        this.writeByte(this.group);
        this.writeByte(this.scope);
        this.writeInt(this.race);
        List<RankData> list = null;
        switch (this.group) {
            case 0: {
                list = this.listServerRankers(client.getPlayer(), this.scope);
                break;
            }
            case 1: {
                list = this.listRaceRankers(client.getPlayer(), this.scope, this.race);
                break;
            }
            case 2: {
                list = this.listClanRankers(client.getPlayer());
                break;
            }
            case 3: {
                list = this.listFriendsRankers(client.getPlayer());
                break;
            }
            default: {
                list = Collections.emptyList();
                break;
            }
        }
        final List<RankData> rankers = list;
        this.writeInt(rankers.size());
        for (final RankData ranker : rankers) {
            this.writeSizedString((CharSequence)ranker.getPlayerName());
            this.writeSizedString((CharSequence)ranker.getClanName());
            this.writeInt((int)ranker.getLevel());
            this.writeInt((int)ranker.getClassId());
            this.writeInt((int)ranker.getRace());
            this.writeInt(ranker.getRank());
            this.writeInt(ranker.getRankSnapshot());
            this.writeInt(ranker.getRankRaceSnapshot());
        }
    }
    
    private List<RankData> listRaceRankers(final Player player, final byte scope, final int race) {
        if (scope == 0) {
            return RankEngine.getInstance().getRaceRankers(race);
        }
        return RankEngine.getInstance().getRaceRankersByPlayer(player);
    }
    
    private List<RankData> listServerRankers(final Player player, final byte scope) {
        if (scope == 0) {
            return RankEngine.getInstance().getRankers();
        }
        return RankEngine.getInstance().getRankersByPlayer(player);
    }
    
    private List<RankData> listFriendsRankers(final Player player) {
        return player.getFriendList().isEmpty() ? Collections.emptyList() : RankEngine.getInstance().getFriendRankers(player);
    }
    
    private List<RankData> listClanRankers(final Player player) {
        return Objects.nonNull(player.getClan()) ? RankEngine.getInstance().getClanRankers(player.getClanId()) : Collections.emptyList();
    }
}
