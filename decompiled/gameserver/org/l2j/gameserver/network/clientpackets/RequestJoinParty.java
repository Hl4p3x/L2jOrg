// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.network.serverpackets.AskJoinParty;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.PartyRequest;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestJoinParty extends ClientPacket
{
    private String name;
    private int partyDistributionTypeId;
    
    public void readImpl() {
        this.name = this.readString();
        this.partyDistributionTypeId = this.readInt();
    }
    
    public void runImpl() {
        final Player requestor = ((GameClient)this.client).getPlayer();
        final Player target = World.getInstance().findPlayer(this.name);
        if (Objects.isNull(target)) {
            requestor.sendPacket(SystemMessageId.YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_PARTY);
            return;
        }
        if (Objects.isNull(target.getClient()) || target.getClient().isDetached()) {
            requestor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE)).addString(this.name));
            return;
        }
        if (requestor.isPartyBanned()) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED);
            return;
        }
        if (target.isPartyBanned()) {
            requestor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY)).addString(this.name));
            return;
        }
        if (requestor.isOnEvent()) {
            requestor.sendMessage("You cannot invite to a party while participating in an event.");
            return;
        }
        if (target.isInParty()) {
            requestor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED)).addString(this.name));
            return;
        }
        if (BlockList.isBlocked(target, requestor)) {
            requestor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST)).addString(this.name));
            return;
        }
        if (target == requestor) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }
        if (target.isJailed() || requestor.isJailed()) {
            requestor.sendMessage("You cannot invite a player while is in Jail.");
            return;
        }
        if ((target.isInOlympiadMode() || requestor.isInOlympiadMode()) && (target.isInOlympiadMode() != requestor.isInOlympiadMode() || target.getOlympiadGameId() != requestor.getOlympiadGameId() || target.getOlympiadSide() != requestor.getOlympiadSide())) {
            requestor.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }
        if (target.hasRequest(PartyRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        requestor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_INVITED_TO_THE_PARTY)).addString(target.getName()));
        if (!requestor.isInParty()) {
            this.createNewParty(target, requestor);
        }
        else {
            this.addTargetToParty(target, requestor);
        }
    }
    
    private void addTargetToParty(final Player target, final Player requestor) {
        final Party party = requestor.getParty();
        if (!party.isLeader(requestor)) {
            requestor.sendPacket(SystemMessageId.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
        }
        else if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS) {
            requestor.sendPacket(SystemMessageId.THE_PARTY_IS_FULL);
        }
        else if (party.getPendingInvitation() && !party.isInvitationRequestExpired()) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
        }
        else {
            this.sendPartyRequest(target, requestor, party.getDistributionType(), party);
        }
    }
    
    private void createNewParty(final Player target, final Player requestor) {
        final PartyDistributionType partyDistributionType = PartyDistributionType.findById(this.partyDistributionTypeId);
        if (partyDistributionType == null) {
            return;
        }
        final Party party = new Party(requestor, partyDistributionType);
        this.sendPartyRequest(target, requestor, partyDistributionType, party);
    }
    
    private void sendPartyRequest(final Player target, final Player requestor, final PartyDistributionType partyDistributionType, final Party party) {
        final PartyRequest request = new PartyRequest(requestor, target, party);
        request.scheduleTimeout(30000L);
        requestor.addRequest(request);
        target.addRequest(request);
        target.sendPacket(new AskJoinParty(requestor.getName(), partyDistributionType));
        party.setPendingInvitation(true);
    }
}
