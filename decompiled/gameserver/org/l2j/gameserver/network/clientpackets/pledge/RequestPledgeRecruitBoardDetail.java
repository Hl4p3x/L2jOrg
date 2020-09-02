// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeRecruitBoardDetail;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeRecruitBoardDetail extends ClientPacket
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
        final PledgeRecruitData pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(this._clanId);
        if (pledgeRecruitInfo == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExPledgeRecruitBoardDetail(pledgeRecruitInfo));
    }
}
