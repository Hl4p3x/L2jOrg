// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.friend.FriendAddRequestResult;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestAnswerFriendInvite extends ClientPacket
{
    private static final Logger LOGGER;
    private int _response;
    
    public void readImpl() {
        this.readByte();
        this._response = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player requestor = player.getActiveRequester();
        if (requestor == null) {
            return;
        }
        if (player == requestor) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }
        if (player.getFriendList().contains(requestor.getObjectId()) || requestor.getFriendList().contains(player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST);
            sm.addString(player.getName());
            requestor.sendPacket(sm);
            return;
        }
        if (this._response == 1) {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).saveFriendship(requestor.getObjectId(), player.getObjectId());
            SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
            requestor.sendPacket(msg);
            msg = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST);
            msg.addString(player.getName());
            requestor.sendPacket(msg);
            requestor.getFriendList().add(player.getObjectId());
            msg = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST);
            msg.addString(requestor.getName());
            player.sendPacket(msg);
            player.getFriendList().add(requestor.getObjectId());
            player.sendPacket(new FriendAddRequestResult(requestor, 1));
            requestor.sendPacket(new FriendAddRequestResult(player, 1));
        }
        else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
            requestor.sendPacket(msg);
        }
        player.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestAnswerFriendInvite.class);
    }
}
