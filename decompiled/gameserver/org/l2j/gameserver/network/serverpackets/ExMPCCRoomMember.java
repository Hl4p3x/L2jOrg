// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;

public class ExMPCCRoomMember extends ServerPacket
{
    private final CommandChannelMatchingRoom _room;
    private final MatchingMemberType _type;
    
    public ExMPCCRoomMember(final Player player, final CommandChannelMatchingRoom room) {
        this._room = room;
        this._type = room.getMemberType(player);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MPCC_ROOM_MEMBER);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._room.getMembersCount());
        for (final Player member : this._room.getMembers()) {
            this.writeInt(member.getObjectId());
            this.writeString((CharSequence)member.getName());
            this.writeInt(member.getLevel());
            this.writeInt(member.getClassId().getId());
            this.writeInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            this.writeInt(this._room.getMemberType(member).ordinal());
        }
    }
}
