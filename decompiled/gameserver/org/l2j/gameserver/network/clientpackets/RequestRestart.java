// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.util.OfflineTradeUtil;
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
        RequestRestart.LOGGER_ACCOUNTING.info(invokedynamic(makeConcatWithConstants:(Lio/github/joealisson/mmocore/Client;)Ljava/lang/String;, this.client));
        if (!OfflineTradeUtil.enteredOfflineMode(player)) {
            Disconnection.of((GameClient)this.client, player).storeMe().deleteMe();
        }
        ((GameClient)this.client).setConnectionState(ConnectionState.AUTHENTICATED);
        ((GameClient)this.client).sendPacket(RestartResponse.TRUE);
        final CharSelectionInfo cl = new CharSelectionInfo(((GameClient)this.client).getAccountName(), ((GameClient)this.client).getSessionId().getGameServerSessionId());
        ((GameClient)this.client).sendPacket(cl);
        ((GameClient)this.client).setCharSelection(cl.getCharInfo());
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
