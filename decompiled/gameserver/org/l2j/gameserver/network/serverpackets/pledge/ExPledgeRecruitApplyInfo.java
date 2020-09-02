// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeRecruitApplyInfo extends ServerPacket
{
    private final ClanEntryStatus _status;
    
    public ExPledgeRecruitApplyInfo(final ClanEntryStatus status) {
        this._status = status;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_APPLY_INFO);
        this.writeInt(this._status.ordinal());
    }
}
