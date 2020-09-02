// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public abstract class PledgeAbstractPacket extends ServerPacket
{
    protected final Clan clan;
    
    public PledgeAbstractPacket(final Clan clan) {
        this.clan = clan;
    }
    
    protected void writeClanInfo(final int pledgeId) {
        this.writeInt(this.clan.getCrestId());
        this.writeInt(this.clan.getLevel());
        this.writeInt(this.clan.getCastleId());
        this.writeInt(0);
        this.writeInt(this.clan.getHideoutId());
        this.writeInt(0);
        this.writeInt(this.clan.getRank());
        this.writeInt(this.clan.getReputationScore());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(this.clan.getAllyId());
        this.writeString((CharSequence)this.clan.getAllyName());
        this.writeInt(this.clan.getAllyCrestId());
        this.writeInt(this.clan.isAtWar());
        this.writeInt(0);
        this.writeInt(this.clan.getSubPledgeMembersCount(pledgeId));
    }
}
