// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.slf4j.Logger;

public class PledgeReceiveSubPledgeCreated extends ServerPacket
{
    private static final Logger LOGGER;
    private final SubPledgeData _subPledge;
    private final Clan _clan;
    
    public PledgeReceiveSubPledgeCreated(final SubPledgeData subPledge, final Clan clan) {
        this._subPledge = subPledge;
        this._clan = clan;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SUBPLEDGE_UPDATED);
        this.writeInt(1);
        this.writeInt(this._subPledge.getId());
        this.writeString((CharSequence)this._subPledge.getName());
        this.writeString((CharSequence)this.getLeaderName());
    }
    
    private String getLeaderName() {
        final int LeaderId = this._subPledge.getLeaderId();
        if (this._subPledge.getId() == -1 || LeaderId == 0) {
            return "";
        }
        if (this._clan.getClanMember(LeaderId) == null) {
            PledgeReceiveSubPledgeCreated.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;I)Ljava/lang/String;, LeaderId, this._clan.getName(), this._clan.getId()));
            return "";
        }
        return this._clan.getClanMember(LeaderId).getName();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PledgeReceiveSubPledgeCreated.class);
    }
}
