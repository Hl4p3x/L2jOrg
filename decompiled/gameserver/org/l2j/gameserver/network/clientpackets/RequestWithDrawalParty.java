// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.GameClient;

public final class RequestWithDrawalParty extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Party party = player.getParty();
        if (party != null) {
            party.removePartyMember(player, Party.MessageType.LEFT);
            final MatchingRoom room = player.getMatchingRoom();
            if (room != null) {
                room.deleteMember(player, false);
            }
        }
    }
}
