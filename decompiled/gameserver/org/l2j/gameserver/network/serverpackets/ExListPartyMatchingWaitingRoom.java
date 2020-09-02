// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.primitive.maps.IntLongMap;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import io.github.joealisson.primitive.pair.IntLong;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import java.util.LinkedList;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public class ExListPartyMatchingWaitingRoom extends ServerPacket
{
    private static final int NUM_PER_PAGE = 64;
    private final int _size;
    private final List<Player> _players;
    
    public ExListPartyMatchingWaitingRoom(final Player player, final int page, final int minLevel, final int maxLevel, final List<ClassId> classIds, final String query) {
        this._players = new LinkedList<Player>();
        final List<Player> players = MatchingRoomManager.getInstance().getPlayerInWaitingList(minLevel, maxLevel, classIds, query);
        this._size = players.size();
        final int startIndex = (page - 1) * 64;
        int chunkSize = this._size - startIndex;
        if (chunkSize > 64) {
            chunkSize = 64;
        }
        for (int i = startIndex; i < startIndex + chunkSize; ++i) {
            this._players.add(players.get(i));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_LIST_PARTY_MATCHING_WAITING_ROOM);
        this.writeInt(this._size);
        this.writeInt(this._players.size());
        for (final Player player : this._players) {
            this.writeString((CharSequence)player.getName());
            this.writeInt(player.getClassId().getId());
            this.writeInt(player.getLevel());
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            this.writeInt((instance != null && instance.getTemplateId() >= 0) ? instance.getTemplateId() : -1);
            final IntLongMap _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player);
            this.writeInt(_instanceTimes.size());
            for (final IntLong entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                this.writeInt(entry.getKey());
                this.writeInt((int)instanceTime);
            }
        }
    }
}
