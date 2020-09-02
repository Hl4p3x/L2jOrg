// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public class ManagePledgePower extends ServerPacket
{
    private final int _action;
    private final Clan _clan;
    private final int _rank;
    
    public ManagePledgePower(final Clan clan, final int action, final int rank) {
        this._clan = clan;
        this._action = action;
        this._rank = rank;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MANAGE_PLEDGE_POWER);
        this.writeInt(this._rank);
        this.writeInt(this._action);
        this.writeInt(this._clan.getRankPrivs(this._rank).getBitmask());
    }
}
