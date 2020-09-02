// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.adenadistribution;

import java.util.List;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaStart;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestDivideAdenaStart extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Party party = player.getParty();
        if (party == null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
            return;
        }
        final CommandChannel commandChannel = party.getCommandChannel();
        if (commandChannel != null && !commandChannel.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER);
            return;
        }
        if (!party.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
            return;
        }
        final List<Player> targets = (commandChannel != null) ? commandChannel.getMembers() : party.getMembers();
        if (player.getAdena() < targets.size()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }
        if (targets.stream().anyMatch(t -> t.hasRequest(AdenaDistributionRequest.class, (Class<? extends AbstractRequest>[])new Class[0]))) {
            return;
        }
        final int adenaObjectId = player.getInventory().getAdenaInstance().getObjectId();
        final Player distributor;
        final List<Player> players;
        final int adenaObjectId2;
        targets.forEach(t -> {
            t.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_STARTED);
            t.addRequest(new AdenaDistributionRequest(t, distributor, players, adenaObjectId2, distributor.getAdena()));
            return;
        });
        player.sendPacket(ExDivideAdenaStart.STATIC_PACKET);
    }
}
