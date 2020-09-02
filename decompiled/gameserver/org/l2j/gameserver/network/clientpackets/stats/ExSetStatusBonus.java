// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.stats;

import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExSetStatusBonus extends ClientPacket
{
    private short str;
    private short dex;
    private short con;
    private short intt;
    private short wit;
    private short men;
    
    @Override
    protected void readImpl() throws Exception {
        final short unk = this.readShort();
        final short unk2 = this.readShort();
        this.str = this.readShort();
        this.dex = this.readShort();
        this.con = this.readShort();
        this.intt = this.readShort();
        this.wit = this.readShort();
        this.men = this.readShort();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final PlayerStatsData statsData = player.getStatsData();
        if (statsData.update(this.str, this.dex, this.con, this.intt, this.wit, this.men)) {
            ((GameClient)this.client).sendPacket(new UserInfo(player, new UserInfoType[] { UserInfoType.STATS, UserInfoType.STATS_POINTS, UserInfoType.BASE_STATS }));
            player.getStats().recalculateStats(true);
        }
    }
}
