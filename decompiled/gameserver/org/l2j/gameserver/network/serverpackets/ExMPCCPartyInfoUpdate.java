// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Party;

public class ExMPCCPartyInfoUpdate extends ServerPacket
{
    private final int _mode;
    private final int _LeaderOID;
    private final int _memberCount;
    private final String _name;
    
    public ExMPCCPartyInfoUpdate(final Party party, final int mode) {
        this._name = party.getLeader().getName();
        this._LeaderOID = party.getLeaderObjectId();
        this._memberCount = party.getMemberCount();
        this._mode = mode;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MPCC_PARTY_INFO_UPDATE);
        this.writeString((CharSequence)this._name);
        this.writeInt(this._LeaderOID);
        this.writeInt(this._memberCount);
        this.writeInt(this._mode);
    }
}
