// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitBoardDetail;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;

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
        final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(this._clanId);
        if (pledgeRecruitInfo == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExPledgeRecruitBoardDetail(pledgeRecruitInfo));
    }
}
