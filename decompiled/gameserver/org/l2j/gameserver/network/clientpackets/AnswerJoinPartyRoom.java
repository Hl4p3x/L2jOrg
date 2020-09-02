// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class AnswerJoinPartyRoom extends ClientPacket
{
    private boolean _answer;
    
    public void readImpl() {
        this._answer = (this.readInt() == 1);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player partner = player.getActiveRequester();
        if (partner == null) {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            player.setActiveRequester(null);
            return;
        }
        if (this._answer && !partner.isRequestExpired()) {
            final MatchingRoom room = partner.getMatchingRoom();
            if (room == null) {
                return;
            }
            room.addMember(player);
        }
        else {
            partner.sendPacket(SystemMessageId.THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION);
        }
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}
