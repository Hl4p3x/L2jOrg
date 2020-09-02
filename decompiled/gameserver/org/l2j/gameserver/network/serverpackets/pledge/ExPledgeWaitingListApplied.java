// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeWaitingListApplied extends ServerPacket
{
    private final PledgeApplicantData _pledgePlayerRecruitInfo;
    private final PledgeRecruitData _pledgeRecruitInfo;
    
    public ExPledgeWaitingListApplied(final int clanId, final int playerId) {
        this._pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
        this._pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_WAITING_LIST_APPLIED);
        this.writeInt(this._pledgeRecruitInfo.getClan().getId());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getClan().getName());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getClan().getLeaderName());
        this.writeInt(this._pledgeRecruitInfo.getClan().getLevel());
        this.writeInt(this._pledgeRecruitInfo.getClan().getMembersCount());
        this.writeInt(this._pledgeRecruitInfo.getKarma());
        this.writeString((CharSequence)this._pledgeRecruitInfo.getInformation());
        this.writeString((CharSequence)this._pledgePlayerRecruitInfo.getMessage());
    }
}
