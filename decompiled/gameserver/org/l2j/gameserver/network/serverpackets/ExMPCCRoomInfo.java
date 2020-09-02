// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;

public class ExMPCCRoomInfo extends ServerPacket
{
    private final CommandChannelMatchingRoom _room;
    
    public ExMPCCRoomInfo(final CommandChannelMatchingRoom room) {
        this._room = room;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MPCC_ROOM_INFO);
        this.writeInt(this._room.getId());
        this.writeInt(this._room.getMaxMembers());
        this.writeInt(this._room.getMinLvl());
        this.writeInt(this._room.getMaxLvl());
        this.writeInt(this._room.getLootType());
        this.writeInt(this._room.getLocation());
        this.writeString((CharSequence)this._room.getTitle());
    }
}
