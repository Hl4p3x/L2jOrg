// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeRecruitBoardDetail extends ServerPacket
{
    final PledgeRecruitData _pledgeRecruitInfo;
    
    public ExPledgeRecruitBoardDetail(final PledgeRecruitData pledgeRecruitInfo) {
        this._pledgeRecruitInfo = pledgeRecruitInfo;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_BOARD_DETAIL);
        this.writeInt(this._pledgeRecruitInfo.getClanId());
        this.writeInt(this._pledgeRecruitInfo.getKarma());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getInformation());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getDetailedInformation());
        this.writeInt(this._pledgeRecruitInfo.getApplicationType());
        this.writeInt(this._pledgeRecruitInfo.getRecruitType());
    }
}
