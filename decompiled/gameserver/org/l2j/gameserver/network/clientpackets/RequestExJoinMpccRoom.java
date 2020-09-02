// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.network.GameClient;

public class RequestExJoinMpccRoom extends ClientPacket
{
    private int _roomId;
    
    public void readImpl() {
        this._roomId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getMatchingRoom() != null) {
            return;
        }
        final MatchingRoom room = MatchingRoomManager.getInstance().getCCMatchingRoom(this._roomId);
        if (room != null) {
            room.addMember(activeChar);
        }
    }
}
