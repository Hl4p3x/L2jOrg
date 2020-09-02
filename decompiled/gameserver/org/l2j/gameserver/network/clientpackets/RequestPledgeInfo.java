// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PledgeInfo;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeInfo extends ClientPacket
{
    private int _clanId;
    
    public void readImpl() {
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan clan = ClanTable.getInstance().getClan(this._clanId);
        if (clan == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new PledgeInfo(clan));
    }
}
