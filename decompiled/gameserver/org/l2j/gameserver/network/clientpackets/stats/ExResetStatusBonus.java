// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.stats;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExResetStatusBonus extends ClientPacket
{
    @Override
    protected void readImpl() throws Exception {
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.reduceAdena("Reset Stats", 2900000L, player, true)) {
            player.getStatsData().reset();
            ((GameClient)this.client).sendPacket(new UserInfo(player, new UserInfoType[] { UserInfoType.STATS, UserInfoType.STATS_POINTS, UserInfoType.BASE_STATS }));
            player.getStats().recalculateStats(true);
        }
    }
}
