// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.util.OfflineTradeUtil;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class Logout extends ClientPacket
{
    protected static final Logger LOGGER_ACCOUNTING;
    
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            ((GameClient)this.client).closeNow();
            return;
        }
        if (!player.canLogout()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        Logout.LOGGER_ACCOUNTING.info(invokedynamic(makeConcatWithConstants:(Lio/github/joealisson/mmocore/Client;)Ljava/lang/String;, this.client));
        if (!OfflineTradeUtil.enteredOfflineMode(player)) {
            Disconnection.of((GameClient)this.client, player).defaultSequence(false);
        }
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
