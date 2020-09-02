// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.PledgeWaitingData;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeDraftListSearch extends ServerPacket
{
    final List<PledgeWaitingData> _pledgeRecruitList;
    
    public ExPledgeDraftListSearch(final List<PledgeWaitingData> pledgeRecruitList) {
        this._pledgeRecruitList = pledgeRecruitList;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_DRAFT_LIST_SEARCH);
        this.writeInt(this._pledgeRecruitList.size());
        for (final PledgeWaitingData prl : this._pledgeRecruitList) {
            this.writeInt(prl.getPlayerId());
            this.writeString((CharSequence)prl.getPlayerName());
            this.writeInt(prl.getKarma());
            this.writeInt(prl.getPlayerClassId());
            this.writeInt(prl.getPlayerLvl());
        }
    }
}
