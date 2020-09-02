// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ClanMember;

public class PledgeReceiveMemberInfo extends ServerPacket
{
    private final ClanMember _member;
    
    public PledgeReceiveMemberInfo(final ClanMember member) {
        this._member = member;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VIEW_PLEDGE_MEMBER_INFO);
        this.writeInt(this._member.getPledgeType());
        this.writeString((CharSequence)this._member.getName());
        this.writeString((CharSequence)this._member.getTitle());
        this.writeInt(this._member.getPowerGrade());
        if (this._member.getPledgeType() != 0) {
            this.writeString((CharSequence)this._member.getClan().getSubPledge(this._member.getPledgeType()).getName());
        }
        else {
            this.writeString((CharSequence)this._member.getClan().getName());
        }
        this.writeString((CharSequence)this._member.getApprenticeOrSponsorName());
    }
}
