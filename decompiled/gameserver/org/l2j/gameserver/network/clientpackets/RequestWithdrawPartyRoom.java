// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.network.GameClient;

public final class RequestWithdrawPartyRoom extends ClientPacket
{
    private int _roomId;
    
    public void readImpl() {
        this._roomId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final MatchingRoom room = activeChar.getMatchingRoom();
        if (room == null) {
            return;
        }
        if (room.getId() != this._roomId || room.getRoomType() != MatchingRoomType.PARTY) {
            return;
        }
        room.deleteMember(activeChar, false);
    }
}
