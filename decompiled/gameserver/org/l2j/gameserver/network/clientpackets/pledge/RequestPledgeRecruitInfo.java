// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeRecruitInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeRecruitInfo extends ClientPacket
{
    private int _clanId;
    
    public void readImpl() {
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Clan clan = ClanTable.getInstance().getClan(this._clanId);
        if (clan == null) {
            return;
        }
        player.sendPacket(new ExPledgeRecruitInfo(this._clanId));
    }
}
