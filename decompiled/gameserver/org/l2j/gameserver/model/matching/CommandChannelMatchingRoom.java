// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.matching;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.ExDissmissMPCCRoom;
import java.util.stream.Stream;
import org.l2j.gameserver.network.serverpackets.ExMPCCRoomMember;
import org.l2j.gameserver.network.serverpackets.ExMPCCRoomInfo;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ExManageMpccRoomMember;
import org.l2j.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;

public class CommandChannelMatchingRoom extends MatchingRoom
{
    public CommandChannelMatchingRoom(final String title, final int loot, final int minlvl, final int maxlvl, final int maxmem, final Player leader) {
        super(title, loot, minlvl, maxlvl, maxmem, leader);
    }
    
    @Override
    protected void onRoomCreation(final Player player) {
        player.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED);
    }
    
    @Override
    protected void notifyInvalidCondition(final Player player) {
        player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
    }
    
    @Override
    protected void notifyNewMember(final Player player) {
        this.getMembers().stream().filter(p -> p != player).forEach(p -> p.sendPacket(new ExManageMpccRoomMember(p, this, ExManagePartyRoomMemberType.ADD_MEMBER)));
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM);
        sm.addPcName(player);
        final Stream<Object> filter = this.getMembers().stream().filter(p -> p != player);
        final SystemMessage obj = sm;
        Objects.requireNonNull(obj);
        filter.forEach((Consumer<? super Object>)obj::sendTo);
        player.sendPacket(new ExMPCCRoomInfo(this));
        player.sendPacket(new ExMPCCRoomMember(player, this));
    }
    
    @Override
    protected void notifyRemovedMember(final Player player, final boolean kicked, final boolean leaderChanged) {
        this.getMembers().forEach(p -> {
            p.sendPacket(new ExMPCCRoomInfo(this));
            p.sendPacket(new ExMPCCRoomMember(player, this));
            return;
        });
        final SystemMessage sm = SystemMessage.getSystemMessage(kicked ? SystemMessageId.YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM : SystemMessageId.YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM);
        player.sendPacket(sm);
    }
    
    @Override
    public void disbandRoom() {
        this.getMembers().forEach(p -> {
            p.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED);
            p.sendPacket(ExDissmissMPCCRoom.STATIC_PACKET);
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
        return MatchingRoomType.COMMAND_CHANNEL;
    }
    
    @Override
    public MatchingMemberType getMemberType(final Player player) {
        if (this.isLeader(player)) {
            return MatchingMemberType.COMMAND_CHANNEL_LEADER;
        }
        final Party playerParty = player.getParty();
        if (playerParty == null) {
            return MatchingMemberType.WAITING_PLAYER_NO_PARTY;
        }
        final Party leaderParty = this.getLeader().getParty();
        if (leaderParty != null) {
            final CommandChannel cc = leaderParty.getCommandChannel();
            if (leaderParty == playerParty || (cc != null && cc.getPartys().contains(playerParty))) {
                return MatchingMemberType.COMMAND_CHANNEL_PARTY_MEMBER;
            }
        }
        return MatchingMemberType.WAITING_PARTY;
    }
}
