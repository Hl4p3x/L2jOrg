// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;

public class ExPledgeWaitingUser extends ServerPacket
{
    private final PledgeApplicantInfo _pledgeRecruitInfo;
    
    public ExPledgeWaitingUser(final PledgeApplicantInfo pledgeRecruitInfo) {
        this._pledgeRecruitInfo = pledgeRecruitInfo;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_WAITING_USER);
        this.writeInt(this._pledgeRecruitInfo.getPlayerId());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getMessage());
    }
}
