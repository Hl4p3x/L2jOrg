// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipLuckyGameInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_LUCKY_GAME_INFO);
        this.writeByte((byte)1);
        this.writeInt((int)client.getPlayer().getAdena());
        this.writeInt(client.getCoin());
    }
}
