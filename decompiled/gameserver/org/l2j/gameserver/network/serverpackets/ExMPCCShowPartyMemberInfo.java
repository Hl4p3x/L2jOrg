// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Party;

public class ExMPCCShowPartyMemberInfo extends ServerPacket
{
    private final Party _party;
    
    public ExMPCCShowPartyMemberInfo(final Party party) {
        this._party = party;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MPCC_SHOW_PARTY_MEMBERS_INFO);
        this.writeInt(this._party.getMemberCount());
        for (final Player pc : this._party.getMembers()) {
            this.writeString((CharSequence)pc.getName());
            this.writeInt(pc.getObjectId());
            this.writeInt(pc.getClassId().getId());
        }
    }
}
