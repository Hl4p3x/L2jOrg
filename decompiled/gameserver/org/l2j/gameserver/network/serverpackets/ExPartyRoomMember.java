// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.primitive.maps.IntLongMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import io.github.joealisson.primitive.pair.IntLong;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;

public class ExPartyRoomMember extends ServerPacket
{
    private final PartyMatchingRoom _room;
    private final MatchingMemberType _type;
    
    public ExPartyRoomMember(final Player player, final PartyMatchingRoom room) {
        this._room = room;
        this._type = room.getMemberType(player);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PARTY_ROOM_MEMBER);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._room.getMembersCount());
        for (final Player member : this._room.getMembers()) {
            this.writeInt(member.getObjectId());
            this.writeString((CharSequence)member.getName());
            this.writeInt(member.getActiveClass());
            this.writeInt(member.getLevel());
            this.writeInt(MapRegionManager.getInstance().getBBs(member.getLocation()));
            this.writeInt(this._room.getMemberType(member).ordinal());
            final IntLongMap _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(member);
            this.writeInt(_instanceTimes.size());
            for (final IntLong entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                this.writeInt(entry.getKey());
                this.writeInt((int)instanceTime);
            }
        }
    }
}
