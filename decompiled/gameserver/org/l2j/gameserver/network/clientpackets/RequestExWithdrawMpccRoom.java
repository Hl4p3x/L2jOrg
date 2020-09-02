// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.network.GameClient;

public class RequestExWithdrawMpccRoom extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final MatchingRoom room = activeChar.getMatchingRoom();
        if (room != null && room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL) {
            room.deleteMember(activeChar, false);
        }
    }
}
