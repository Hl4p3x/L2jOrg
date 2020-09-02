// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeWaitingUser extends ServerPacket
{
    private final PledgeApplicantData _pledgeRecruitInfo;
    
    public ExPledgeWaitingUser(final PledgeApplicantData pledgeRecruitInfo) {
        this._pledgeRecruitInfo = pledgeRecruitInfo;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_WAITING_USER);
        this.writeInt(this._pledgeRecruitInfo.getPlayerId());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getMessage());
    }
}
