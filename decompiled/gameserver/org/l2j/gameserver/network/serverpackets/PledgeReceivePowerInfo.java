// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ClanMember;

public class PledgeReceivePowerInfo extends ServerPacket
{
    private final ClanMember _member;
    
    public PledgeReceivePowerInfo(final ClanMember member) {
        this._member = member;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VIEW_PLEDGE_POWER);
        this.writeInt(this._member.getPowerGrade());
        this.writeString((CharSequence)this._member.getName());
        this.writeInt(this._member.getClan().getRankPrivs(this._member.getPowerGrade()).getBitmask());
    }
}
