// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public class PledgePowerGradeList extends ServerPacket
{
    private final Clan.RankPrivs[] _privs;
    
    public PledgePowerGradeList(final Clan.RankPrivs[] privs) {
        this._privs = privs;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_POWER_GRADE_LIST);
        this.writeInt(this._privs.length);
        for (final Clan.RankPrivs temp : this._privs) {
            this.writeInt(temp.getRank());
            this.writeInt(temp.getParty());
        }
    }
}
