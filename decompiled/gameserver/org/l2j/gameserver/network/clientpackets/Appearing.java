// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.enums.UserInfoType;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class Appearing extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (player.isTeleporting()) {
            player.onTeleported();
        }
        ((GameClient)this.client).sendPacket(new UserInfo(player, new UserInfoType[] { UserInfoType.POSITION }));
    }
}
