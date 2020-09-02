// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeWaitingList extends ServerPacket
{
    private final IntMap<PledgeApplicantData> pledgePlayerRecruitInfos;
    
    public ExPledgeWaitingList(final int clanId) {
        this.pledgePlayerRecruitInfos = ClanEntryManager.getInstance().getApplicantListForClan(clanId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_WAITING_LIST);
        this.writeInt(this.pledgePlayerRecruitInfos.size());
        for (final PledgeApplicantData recruitInfo : this.pledgePlayerRecruitInfos.values()) {
            this.writeInt(recruitInfo.getPlayerId());
            this.writeString((CharSequence)recruitInfo.getPlayerName());
            this.writeInt(recruitInfo.getClassId());
            this.writeInt(recruitInfo.getPlayerLvl());
        }
    }
}
