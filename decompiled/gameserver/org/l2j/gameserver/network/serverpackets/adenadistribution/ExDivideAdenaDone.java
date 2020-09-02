// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.adenadistribution;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExDivideAdenaDone extends ServerPacket
{
    private final boolean _isPartyLeader;
    private final boolean _isCCLeader;
    private final long _adenaCount;
    private final long _distributedAdenaCount;
    private final int _memberCount;
    private final String _distributorName;
    
    public ExDivideAdenaDone(final boolean isPartyLeader, final boolean isCCLeader, final long adenaCount, final long distributedAdenaCount, final int memberCount, final String distributorName) {
        this._isPartyLeader = isPartyLeader;
        this._isCCLeader = isCCLeader;
        this._adenaCount = adenaCount;
        this._distributedAdenaCount = distributedAdenaCount;
        this._memberCount = memberCount;
        this._distributorName = distributorName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DIVIDE_ADENA_DONE);
        this.writeByte((byte)(byte)(this._isPartyLeader ? 1 : 0));
        this.writeByte((byte)(byte)(this._isCCLeader ? 1 : 0));
        this.writeInt(this._memberCount);
        this.writeLong(this._distributedAdenaCount);
        this.writeLong(this._adenaCount);
        this.writeString((CharSequence)this._distributorName);
    }
}
