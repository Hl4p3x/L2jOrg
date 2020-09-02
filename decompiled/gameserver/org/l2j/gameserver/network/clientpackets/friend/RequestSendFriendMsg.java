// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.FriendSay;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestSendFriendMsg extends ClientPacket
{
    private static Logger LOGGER_CHAT;
    private String _message;
    private String _reciever;
    
    public void readImpl() {
        this._message = this.readString();
        this._reciever = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._message == null || this._message.isEmpty() || this._message.length() > 300) {
            return;
        }
        final Player targetPlayer = World.getInstance().findPlayer(this._reciever);
        if (targetPlayer == null || !targetPlayer.getFriendList().contains(player.getObjectId())) {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }
        if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).logChat()) {
            RequestSendFriendMsg.LOGGER_CHAT.info("PRIV_MSG [{} to {}] {}", new Object[] { player, targetPlayer, this._message });
        }
        targetPlayer.sendPacket(new FriendSay(player.getName(), this._reciever, this._message));
    }
    
    static {
        RequestSendFriendMsg.LOGGER_CHAT = LoggerFactory.getLogger("chat");
    }
}
