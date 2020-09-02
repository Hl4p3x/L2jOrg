// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PrivateStoreType;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class RequestPrivateStoreQuitSell extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        player.setPrivateStoreType(PrivateStoreType.NONE);
        player.standUp();
        player.broadcastUserInfo();
    }
}
