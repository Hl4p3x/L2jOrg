// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.entity.Castle;

public final class SiegeDefenderList extends ServerPacket
{
    private final Castle _castle;
    
    public SiegeDefenderList(final Castle castle) {
        this._castle = castle;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CASTLE_SIEGE_DEFENDER_LIST);
        this.writeInt(this._castle.getId());
        this.writeInt(0);
        this.writeInt(1);
        this.writeInt(0);
        final int size = this._castle.getSiege().getDefendersWaiting().size() + this._castle.getSiege().getDefenderClans().size() + ((this._castle.getOwner() != null) ? 1 : 0);
        this.writeInt(size);
        this.writeInt(size);
        final Clan ownerClan = this._castle.getOwner();
        if (ownerClan != null) {
            this.writeInt(ownerClan.getId());
            this.writeString((CharSequence)ownerClan.getName());
            this.writeString((CharSequence)ownerClan.getLeaderName());
            this.writeInt(ownerClan.getCrestId());
            this.writeInt(0);
            this.writeInt(SiegeClanType.OWNER.ordinal());
            this.writeInt(ownerClan.getAllyId());
            this.writeString((CharSequence)ownerClan.getAllyName());
            this.writeString((CharSequence)"");
            this.writeInt(ownerClan.getAllyCrestId());
        }
        for (final SiegeClanData siegeClan : this._castle.getSiege().getDefenderClans().values()) {
            final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan != null) {
                if (defendingClan == this._castle.getOwner()) {
                    continue;
                }
                this.writeInt(defendingClan.getId());
                this.writeString((CharSequence)defendingClan.getName());
                this.writeString((CharSequence)defendingClan.getLeaderName());
                this.writeInt(defendingClan.getCrestId());
                this.writeInt(0);
                this.writeInt(SiegeClanType.DEFENDER.ordinal());
                this.writeInt(defendingClan.getAllyId());
                this.writeString((CharSequence)defendingClan.getAllyName());
                this.writeString((CharSequence)"");
                this.writeInt(defendingClan.getAllyCrestId());
            }
        }
        for (final SiegeClanData siegeClan : this._castle.getSiege().getDefendersWaiting().values()) {
            final Clan defendingClan = ClanTable.getInstance().getClan(siegeClan.getClanId());
            if (defendingClan == null) {
                continue;
            }
            this.writeInt(defendingClan.getId());
            this.writeString((CharSequence)defendingClan.getName());
            this.writeString((CharSequence)defendingClan.getLeaderName());
            this.writeInt(defendingClan.getCrestId());
            this.writeInt(0);
            this.writeInt(SiegeClanType.DEFENDER_PENDING.ordinal());
            this.writeInt(defendingClan.getAllyId());
            this.writeString((CharSequence)defendingClan.getAllyName());
            this.writeString((CharSequence)"");
            this.writeInt(defendingClan.getAllyCrestId());
        }
    }
}
