// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import java.util.LinkedList;
import org.l2j.gameserver.model.matching.MatchingRoom;
import java.util.List;

public class ExListMpccWaiting extends ServerPacket
{
    private static final int NUM_PER_PAGE = 64;
    private final int _size;
    private final List<MatchingRoom> _rooms;
    
    public ExListMpccWaiting(final int page, final int location, final int level) {
        this._rooms = new LinkedList<MatchingRoom>();
        final List<MatchingRoom> rooms = MatchingRoomManager.getInstance().getCCMathchingRooms(location, level);
        this._size = rooms.size();
        final int startIndex = (page - 1) * 64;
        int chunkSize = this._size - startIndex;
        if (chunkSize > 64) {
            chunkSize = 64;
        }
        for (int i = startIndex; i < startIndex + chunkSize; ++i) {
            this._rooms.add(rooms.get(i));
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_LIST_MPCC_WAITING);
        this.writeInt(this._size);
        this.writeInt(this._rooms.size());
        for (final MatchingRoom room : this._rooms) {
            this.writeInt(room.getId());
            this.writeString((CharSequence)room.getTitle());
            this.writeInt(room.getMembersCount());
            this.writeInt(room.getMinLvl());
            this.writeInt(room.getMaxLvl());
            this.writeInt(room.getLocation());
            this.writeInt(room.getMaxMembers());
            this.writeString((CharSequence)room.getLeader().getName());
        }
    }
}
