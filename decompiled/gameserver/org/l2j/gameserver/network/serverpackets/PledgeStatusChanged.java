// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public final class PledgeStatusChanged extends ServerPacket
{
    private final Clan _clan;
    
    public PledgeStatusChanged(final Clan clan) {
        this._clan = clan;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_STATUS_CHANGED);
        this.writeInt(0);
        this.writeInt(this._clan.getLeaderId());
        this.writeInt(this._clan.getId());
        this.writeInt(this._clan.getCrestId());
        this.writeInt(this._clan.getAllyId());
        this.writeInt(this._clan.getAllyCrestId());
        this.writeInt(this._clan.getCrestLargeId());
        this.writeInt(0);
    }
}
