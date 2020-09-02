// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeRecruitBoardSearch extends ServerPacket
{
    static final int CLAN_PER_PAGE = 12;
    final List<PledgeRecruitData> _clanList;
    private final int _currentPage;
    private final int _totalNumberOfPage;
    private final int _clanOnCurrentPage;
    private final int _startIndex;
    private final int _endIndex;
    
    public ExPledgeRecruitBoardSearch(final List<PledgeRecruitData> clanList, final int currentPage) {
        this._clanList = clanList;
        this._currentPage = currentPage;
        this._totalNumberOfPage = (int)Math.ceil(this._clanList.size() / 12.0);
        this._startIndex = (this._currentPage - 1) * 12;
        this._endIndex = ((this._startIndex + 12 > this._clanList.size()) ? this._clanList.size() : (this._startIndex + 12));
        this._clanOnCurrentPage = this._endIndex - this._startIndex;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_BOARD_SEARCH);
        this.writeInt(this._currentPage);
        this.writeInt(this._totalNumberOfPage);
        this.writeInt(this._clanOnCurrentPage);
        for (int i = this._startIndex; i < this._endIndex; ++i) {
            this.writeInt(this._clanList.get(i).getClanId());
            this.writeInt(this._clanList.get(i).getClan().getAllyId());
        }
        for (int i = this._startIndex; i < this._endIndex; ++i) {
            final Clan clan = this._clanList.get(i).getClan();
            this.writeInt(clan.getCrestId());
            this.writeInt(clan.getAllyCrestId());
            this.writeString((CharSequence)clan.getName());
            this.writeString((CharSequence)clan.getLeaderName());
            this.writeInt(clan.getLevel());
            this.writeInt(clan.getMembersCount());
            this.writeInt(this._clanList.get(i).getKarma());
            this.writeString((CharSequence)this._clanList.get(i).getInformation());
            this.writeInt(this._clanList.get(i).getApplicationType());
            this.writeInt(this._clanList.get(i).getRecruitType());
        }
    }
}
