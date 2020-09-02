// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExManageMpccRoomMember extends ServerPacket
{
    private final Player _activeChar;
    private final MatchingMemberType _memberType;
    private final ExManagePartyRoomMemberType _type;
    
    public ExManageMpccRoomMember(final Player player, final CommandChannelMatchingRoom room, final ExManagePartyRoomMemberType mode) {
        this._activeChar = player;
        this._memberType = room.getMemberType(player);
        this._type = mode;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MANAGE_PARTY_ROOM_MEMBER);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._activeChar.getObjectId());
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeInt(this._activeChar.getLevel());
        this.writeInt(MapRegionManager.getInstance().getBBs(this._activeChar.getLocation()));
        this.writeInt(this._memberType.ordinal());
    }
}
