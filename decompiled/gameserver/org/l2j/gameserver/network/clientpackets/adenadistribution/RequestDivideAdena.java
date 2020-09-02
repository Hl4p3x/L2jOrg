// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaDone;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestDivideAdena extends ClientPacket
{
    private int _adenaObjId;
    private long _adenaCount;
    
    public void readImpl() {
        this._adenaObjId = this.readInt();
        this._adenaCount = this.readLong();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final AdenaDistributionRequest request = player.getRequest(AdenaDistributionRequest.class);
        if (request == null) {
            return;
        }
        if (request.getDistributor() != player) {
            this.cancelDistribution(request);
            return;
        }
        if (request.getAdenaObjectId() != this._adenaObjId) {
            this.cancelDistribution(request);
            return;
        }
        final Party party = player.getParty();
        if (party == null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
            this.cancelDistribution(request);
            return;
        }
        final CommandChannel commandChannel = party.getCommandChannel();
        if (commandChannel != null && !commandChannel.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER);
            this.cancelDistribution(request);
            return;
        }
        if (!party.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
            this.cancelDistribution(request);
            return;
        }
        final List<Player> targets = (commandChannel != null) ? commandChannel.getMembers() : party.getMembers();
        if (player.getAdena() < targets.size()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            this.cancelDistribution(request);
            return;
        }
        if (player.getAdena() < request.getAdenaCount()) {
            player.sendPacket(SystemMessageId.THE_ADENA_IN_POSSESSION_HAS_BEEN_DECREASED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            this.cancelDistribution(request);
            return;
        }
        if (targets.size() < request.getPlayers().size()) {
            player.sendPacket(SystemMessageId.THE_DISTRIBUTION_PARTICIPANTS_HAVE_CHANGED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            this.cancelDistribution(request);
            return;
        }
        if (player.getAdena() < this._adenaCount) {
            player.sendPacket(SystemMessageId.DISTRIBUTION_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA_FOR_DISTRIBUTION);
            this.cancelDistribution(request);
            return;
        }
        final long memberAdenaGet = (long)Math.floor(this._adenaCount / (float)targets.size());
        if (player.reduceAdena("Adena Distribution", memberAdenaGet * targets.size(), player, false)) {
            for (final Player target : targets) {
                if (target == null) {
                    continue;
                }
                target.addAdena("Adena Distribution", memberAdenaGet, player, false);
                target.sendPacket(new ExDivideAdenaDone(party.isLeader(target), commandChannel != null && commandChannel.isLeader(target), this._adenaCount, memberAdenaGet, targets.size(), player.getName()));
                target.removeRequest(AdenaDistributionRequest.class);
            }
        }
        else {
            this.cancelDistribution(request);
        }
    }
    
    private void cancelDistribution(final AdenaDistributionRequest request) {
        request.getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
            p.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
            p.removeRequest(AdenaDistributionRequest.class);
        });
    }
}
