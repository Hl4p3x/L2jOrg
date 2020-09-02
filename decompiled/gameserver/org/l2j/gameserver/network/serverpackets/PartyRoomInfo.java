// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;

public class PartyRoomInfo extends ServerPacket
{
    private final PartyMatchingRoom _room;
    
    public PartyRoomInfo(final PartyMatchingRoom room) {
        this._room = room;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_ROOM_INFO);
        this.writeInt(this._room.getId());
        this.writeInt(this._room.getMaxMembers());
        this.writeInt(this._room.getMinLvl());
        this.writeInt(this._room.getMaxLvl());
        this.writeInt(this._room.getLootType());
        this.writeInt(this._room.getLocation());
        this.writeString((CharSequence)this._room.getTitle());
    }
}
