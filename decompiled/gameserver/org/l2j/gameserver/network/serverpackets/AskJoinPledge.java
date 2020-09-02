// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public final class AskJoinPledge extends ServerPacket
{
    private final Player _requestor;
    private final int _pledgeType;
    private final String _pledgeName;
    
    public AskJoinPledge(final Player requestor, final int pledgeType, final String pledgeName) {
        this._requestor = requestor;
        this._pledgeType = pledgeType;
        this._pledgeName = pledgeName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ASK_JOIN_PLEDGE);
        this.writeInt(this._requestor.getObjectId());
        this.writeString((CharSequence)this._requestor.getName());
        this.writeString((CharSequence)this._pledgeName);
        if (this._pledgeType != 0) {
            this.writeInt(this._pledgeType);
        }
    }
}
