// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeRecruitInfo extends ServerPacket
{
    private final Clan _clan;
    
    public ExPledgeRecruitInfo(final int clanId) {
        this._clan = ClanTable.getInstance().getClan(clanId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_INFO);
        final SubPledgeData[] subPledges = this._clan.getAllSubPledges();
        this.writeString((CharSequence)this._clan.getName());
        this.writeString((CharSequence)this._clan.getLeaderName());
        this.writeInt(this._clan.getLevel());
        this.writeInt(this._clan.getMembersCount());
        this.writeInt(subPledges.length);
        for (final SubPledgeData subPledge : subPledges) {
            this.writeInt(subPledge.getId());
            this.writeString((CharSequence)subPledge.getName());
        }
    }
}
