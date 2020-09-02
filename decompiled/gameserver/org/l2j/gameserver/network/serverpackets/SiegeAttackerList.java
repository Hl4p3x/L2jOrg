// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import java.util.Iterator;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.entity.Castle;

public final class SiegeAttackerList extends ServerPacket
{
    private final Castle _castle;
    
    public SiegeAttackerList(final Castle castle) {
        this._castle = castle;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CASTLE_SIEGE_ATTACKER_LIST);
        this.writeInt(this._castle.getId());
        this.writeInt(0);
        this.writeInt(1);
        this.writeInt(0);
        final int size = this._castle.getSiege().getAttackerClans().size();
        if (size > 0) {
            this.writeInt(size);
            this.writeInt(size);
            for (final SiegeClanData siegeclan : this._castle.getSiege().getAttackerClans().values()) {
                final Clan clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
                if (clan == null) {
                    continue;
                }
                this.writeInt(clan.getId());
                this.writeString((CharSequence)clan.getName());
                this.writeString((CharSequence)clan.getLeaderName());
                this.writeInt(clan.getCrestId());
                this.writeInt(0);
                this.writeInt(clan.getAllyId());
                this.writeString((CharSequence)clan.getAllyName());
                this.writeString((CharSequence)"");
                this.writeInt(clan.getAllyCrestId());
            }
        }
        else {
            this.writeInt(0);
            this.writeInt(0);
        }
    }
}
