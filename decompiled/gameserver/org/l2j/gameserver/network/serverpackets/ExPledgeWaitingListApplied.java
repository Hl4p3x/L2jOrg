// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;

public class ExPledgeWaitingListApplied extends ServerPacket
{
    private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
    private final PledgeRecruitInfo _pledgeRecruitInfo;
    
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
