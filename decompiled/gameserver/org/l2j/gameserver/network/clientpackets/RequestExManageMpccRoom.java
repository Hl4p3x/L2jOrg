// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExMPCCRoomInfo;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.network.GameClient;

public class RequestExManageMpccRoom extends ClientPacket
{
    private int _roomId;
    private int _maxMembers;
    private int _minLevel;
    private int _maxLevel;
    private String _title;
    
    public void readImpl() {
        this._roomId = this.readInt();
        this._maxMembers = this.readInt();
        this._minLevel = this.readInt();
        this._maxLevel = this.readInt();
        this.readInt();
        this._title = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final MatchingRoom room = activeChar.getMatchingRoom();
        if (room == null || room.getId() != this._roomId || room.getRoomType() != MatchingRoomType.COMMAND_CHANNEL || room.getLeader() != activeChar) {
            return;
        }
        room.setTitle(this._title);
        room.setMaxMembers(this._maxMembers);
        room.setMinLvl(this._minLevel);
        room.setMaxLvl(this._maxLevel);
        room.getMembers().forEach(p -> p.sendPacket(new ExMPCCRoomInfo((CommandChannelMatchingRoom)room)));
        activeChar.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
    }
}
