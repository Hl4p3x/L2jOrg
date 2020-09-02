// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PlayerSelectionInfo;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.RestartResponse;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestRestart extends ClientPacket
{
    protected static final Logger LOGGER_ACCOUNTING;
    
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!player.canLogout()) {
            ((GameClient)this.client).sendPacket(RestartResponse.FALSE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        RequestRestart.LOGGER_ACCOUNTING.info("{} Logged out", (Object)this.client);
        Disconnection.of((GameClient)this.client, player).storeMe().deleteMe();
        ((GameClient)this.client).setConnectionState(ConnectionState.AUTHENTICATED);
        ((GameClient)this.client).sendPacket(RestartResponse.TRUE);
        ((GameClient)this.client).sendPacket(new PlayerSelectionInfo((GameClient)this.client));
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
