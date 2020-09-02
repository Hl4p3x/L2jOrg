// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.clan.entry.PledgeWaitingInfo;
import java.util.List;

public class ExPledgeDraftListSearch extends ServerPacket
{
    final List<PledgeWaitingInfo> _pledgeRecruitList;
    
    public ExPledgeDraftListSearch(final List<PledgeWaitingInfo> pledgeRecruitList) {
        this._pledgeRecruitList = pledgeRecruitList;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_DRAFT_LIST_SEARCH);
        this.writeInt(this._pledgeRecruitList.size());
        for (final PledgeWaitingInfo prl : this._pledgeRecruitList) {
            this.writeInt(prl.getPlayerId());
            this.writeString((CharSequence)prl.getPlayerName());
            this.writeInt(prl.getKarma());
            this.writeInt(prl.getPlayerClassId());
            this.writeInt(prl.getPlayerLvl());
        }
    }
}
