// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.network.GameClient;

public class RequestDismissPartyRoom extends ClientPacket
{
    private int _roomid;
    
    public void readImpl() {
        this._roomid = this.readInt();
        this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final MatchingRoom room = player.getMatchingRoom();
        if (room == null || room.getId() != this._roomid || room.getRoomType() != MatchingRoomType.PARTY || room.getLeader() != player) {
            return;
        }
        room.disbandRoom();
    }
}
