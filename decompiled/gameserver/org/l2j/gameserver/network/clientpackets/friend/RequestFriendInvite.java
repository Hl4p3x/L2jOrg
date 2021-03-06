// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.network.serverpackets.friend.FriendAddRequest;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestFriendInvite extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    private void scheduleDeny(final Player player) {
        if (player != null) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST));
            player.onTransactionResponse();
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player friend = World.getInstance().findPlayer(this._name);
        if (friend == null || !friend.isOnline() || friend.isInvisible()) {
            player.sendPacket(SystemMessageId.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }
        if (friend == player) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }
        if (player.isInOlympiadMode() || friend.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }
        if (BlockList.isBlocked(friend, player)) {
            player.sendMessage("You are in target's block list.");
            return;
        }
        if (BlockList.isBlocked(player, friend)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BLOCKED_C1);
            sm.addString(friend.getName());
            player.sendPacket(sm);
            return;
        }
        if (player.getFriendList().contains(friend.getObjectId())) {
            player.sendPacket(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
            return;
        }
        if (friend.isProcessingRequest()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(this._name);
            player.sendPacket(sm);
            return;
        }
        player.onTransactionRequest(friend);
        friend.sendPacket(new FriendAddRequest(player.getName()));
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST);
        sm.addString(this._name);
        player.sendPacket(sm);
    }
}
