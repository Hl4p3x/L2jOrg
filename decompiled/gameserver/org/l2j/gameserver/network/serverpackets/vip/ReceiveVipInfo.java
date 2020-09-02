// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import java.time.temporal.Temporal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipInfo extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        final Player player = client.getPlayer();
        final VipEngine vipData = VipEngine.getInstance();
        final byte vipTier = player.getVipTier();
        final int vipDuration = (int)ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(player.getVipTierExpiration()));
        this.writeId(ServerExPacketId.EX_VIP_INFO);
        this.writeByte(vipTier);
        this.writeLong(player.getVipPoints());
        this.writeInt(vipDuration);
        this.writeLong(vipData.getPointsToLevel(vipTier + 1));
        this.writeLong(vipData.getPointsDepreciatedOnLevel(vipTier));
        this.writeByte(vipTier);
        this.writeLong(vipData.getPointsToLevel(vipTier));
    }
}
