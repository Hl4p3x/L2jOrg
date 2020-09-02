// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.matching;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.network.serverpackets.ExClosePartyRoom;
import java.util.stream.Stream;
import org.l2j.gameserver.network.serverpackets.PartyRoomInfo;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ExPartyRoomMember;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ListPartyWaiting;
import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PartyMatchingRoom extends MatchingRoom
{
    public PartyMatchingRoom(final String title, final int loot, final int minlvl, final int maxlvl, final int maxmem, final Player leader) {
        super(title, loot, minlvl, maxlvl, maxmem, leader);
    }
    
    @Override
    protected void onRoomCreation(final Player player) {
        player.broadcastUserInfo(UserInfoType.CLAN);
        player.sendPacket(new ListPartyWaiting(PartyMatchingRoomLevelType.ALL, -1, 1, player.getLevel()));
        player.sendPacket(SystemMessageId.YOU_HAVE_CREATED_A_PARTY_ROOM);
    }
    
    @Override
    protected void notifyInvalidCondition(final Player player) {
        player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM);
    }
    
    @Override
    protected void notifyNewMember(final Player player) {
        this.getMembers().stream().filter(p -> p != player).forEach(p -> p.sendPacket(new ExPartyRoomMember(p, this)));
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_ENTERED_THE_PARTY_ROOM);
        sm.addPcName(player);
        final Stream<Object> filter = this.getMembers().stream().filter(p -> p != player);
        final SystemMessage obj = sm;
        Objects.requireNonNull(obj);
        filter.forEach((Consumer<? super Object>)obj::sendTo);
        player.sendPacket(new PartyRoomInfo(this));
        player.sendPacket(new ExPartyRoomMember(player, this));
    }
    
    @Override
    protected void notifyRemovedMember(final Player player, final boolean kicked, final boolean leaderChanged) {
        final SystemMessage sm = SystemMessage.getSystemMessage(kicked ? SystemMessageId.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMessageId.C1_HAS_LEFT_THE_PARTY_ROOM);
        sm.addPcName(player);
        final ServerPacket serverPacket;
        this.getMembers().forEach(p -> {
            p.sendPacket(new PartyRoomInfo(this));
            p.sendPacket(new ExPartyRoomMember(player, this));
            p.sendPacket(serverPacket);
            p.sendPacket(SystemMessageId.THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED);
            return;
        });
        final SystemMessage sm2 = SystemMessage.getSystemMessage(kicked ? SystemMessageId.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMessageId.YOU_HAVE_EXITED_THE_PARTY_ROOM);
        player.sendPacket(sm2);
        player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
    }
    
    @Override
    public void disbandRoom() {
        this.getMembers().forEach(p -> {
            p.sendPacket(SystemMessageId.THE_PARTY_ROOM_HAS_BEEN_DISBANDED);
            p.sendPacket(ExClosePartyRoom.STATIC_PACKET);
            p.setMatchingRoom(null);
            p.broadcastUserInfo(UserInfoType.CLAN);
            MatchingRoomManager.getInstance().addToWaitingList(p);
            return;
        });
        this.getMembers().clear();
        MatchingRoomManager.getInstance().removeMatchingRoom(this);
    }
    
    @Override
    public MatchingRoomType getRoomType() {
        return MatchingRoomType.PARTY;
    }
    
    @Override
    public MatchingMemberType getMemberType(final Player player) {
        if (this.isLeader(player)) {
            return MatchingMemberType.PARTY_LEADER;
        }
        final Party leaderParty = this.getLeader().getParty();
        final Party playerParty = player.getParty();
        if (leaderParty != null && playerParty != null && playerParty == leaderParty) {
            return MatchingMemberType.PARTY_MEMBER;
        }
        return MatchingMemberType.WAITING_PLAYER;
    }
}
