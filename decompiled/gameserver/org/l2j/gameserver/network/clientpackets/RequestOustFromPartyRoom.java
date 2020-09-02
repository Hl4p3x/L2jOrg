// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestOustFromPartyRoom extends ClientPacket
{
    private int _charObjId;
    
    public void readImpl() {
        this._charObjId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player member = World.getInstance().findPlayer(this._charObjId);
        if (member == null) {
            return;
        }
        final MatchingRoom room = player.getMatchingRoom();
        if (room == null || room.getRoomType() != MatchingRoomType.PARTY || room.getLeader() != player || player == member) {
            return;
        }
        final Party playerParty = player.getParty();
        final Party memberParty = member.getParty();
        if (playerParty != null && memberParty != null && playerParty.getLeaderObjectId() == memberParty.getLeaderObjectId()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE);
        }
        else {
            room.deleteMember(member, true);
        }
    }
}
