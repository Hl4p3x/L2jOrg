// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import java.util.LinkedList;
import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.model.matching.MatchingRoom;
import java.util.List;

public class ListPartyWaiting extends ServerPacket
{
    private static final int NUM_PER_PAGE = 64;
    private final List<MatchingRoom> _rooms;
    private final int _size;
    
    public ListPartyWaiting(final PartyMatchingRoomLevelType type, final int location, final int page, final int requestorLevel) {
        this._rooms = new LinkedList<MatchingRoom>();
        final List<MatchingRoom> rooms = MatchingRoomManager.getInstance().getPartyMathchingRooms(location, type, requestorLevel);
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
        this.writeId(ServerPacketId.LIST_PARTY_WAITING);
        this.writeInt(this._size);
        this.writeInt(this._rooms.size());
        for (final MatchingRoom room : this._rooms) {
            this.writeInt(room.getId());
            this.writeString((CharSequence)room.getTitle());
            this.writeInt(room.getLocation());
            this.writeInt(room.getMinLvl());
            this.writeInt(room.getMaxLvl());
            this.writeInt(room.getMaxMembers());
            this.writeString((CharSequence)room.getLeader().getName());
            this.writeInt(room.getMembersCount());
            for (final Player member : room.getMembers()) {
                this.writeInt(member.getClassId().getId());
                this.writeString((CharSequence)member.getName());
            }
        }
        this.writeInt(World.getInstance().getPartyCount());
        this.writeInt(World.getInstance().getPartyMemberCount());
    }
}
