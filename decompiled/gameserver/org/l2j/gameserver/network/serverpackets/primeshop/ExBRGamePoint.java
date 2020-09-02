// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBRGamePoint extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_NOTIFY_GAME_POINT);
        final Player player = client.getPlayer();
        this.writeInt(player.getObjectId());
        this.writeLong((long)client.getCoin());
        this.writeInt(0);
    }
}
