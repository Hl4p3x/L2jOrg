// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Set;

public class ExMPCCPartymasterList extends ServerPacket
{
    private final Set<String> _leadersName;
    
    public ExMPCCPartymasterList(final Set<String> leadersName) {
        this._leadersName = leadersName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MPCC_PARTYMASTER_LIST);
        this.writeInt(this._leadersName.size());
        this._leadersName.forEach(x$0 -> this.writeString(x$0));
    }
}
