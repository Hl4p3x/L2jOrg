// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestPartyMatchDetail extends ClientPacket
{
    private int _roomId;
    private int _location;
    private int _level;
    
    public void readImpl() {
        this._roomId = this.readInt();
        this._location = this.readInt();
        this._level = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isInMatchingRoom()) {
            return;
        }
        final MatchingRoom room = (this._roomId > 0) ? MatchingRoomManager.getInstance().getPartyMathchingRoom(this._roomId) : MatchingRoomManager.getInstance().getPartyMathchingRoom(this._location, this._level);
        if (room != null) {
            room.addMember(activeChar);
        }
    }
}
