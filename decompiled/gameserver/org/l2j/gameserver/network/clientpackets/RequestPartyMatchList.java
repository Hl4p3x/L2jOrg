// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.PartyRoomInfo;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.network.GameClient;

public class RequestPartyMatchList extends ClientPacket
{
    private int _roomId;
    private int _maxMembers;
    private int _minLevel;
    private int _maxLevel;
    private int _lootType;
    private String _roomTitle;
    
    public void readImpl() {
        this._roomId = this.readInt();
        this._maxMembers = this.readInt();
        this._minLevel = this.readInt();
        this._maxLevel = this.readInt();
        this._lootType = this.readInt();
        this._roomTitle = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._roomId <= 0 && activeChar.getMatchingRoom() == null) {
            final PartyMatchingRoom room = new PartyMatchingRoom(this._roomTitle, this._lootType, this._minLevel, this._maxLevel, this._maxMembers, activeChar);
            activeChar.setMatchingRoom(room);
        }
        else {
            final MatchingRoom room2 = activeChar.getMatchingRoom();
            if (room2.getId() == this._roomId && room2.getRoomType() == MatchingRoomType.PARTY && room2.isLeader(activeChar)) {
                room2.setLootType(this._lootType);
                room2.setMinLvl(this._minLevel);
                room2.setMaxLvl(this._maxLevel);
                room2.setMaxMembers(this._maxMembers);
                room2.setTitle(this._roomTitle);
                final PartyRoomInfo packet = new PartyRoomInfo((PartyMatchingRoom)room2);
                final Set<Player> members = room2.getMembers();
                final PartyRoomInfo obj = packet;
                Objects.requireNonNull(obj);
                members.forEach(obj::sendTo);
            }
        }
    }
}
