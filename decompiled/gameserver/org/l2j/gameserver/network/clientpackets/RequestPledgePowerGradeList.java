// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgePowerGradeList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgePowerGradeList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Clan clan = player.getClan();
        if (clan != null) {
            final Clan.RankPrivs[] privs = clan.getAllRankPrivs();
            player.sendPacket(new PledgePowerGradeList(privs));
        }
    }
}
