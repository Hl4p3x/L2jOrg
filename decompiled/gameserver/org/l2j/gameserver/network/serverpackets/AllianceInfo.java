// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.ClanInfo;

public class AllianceInfo extends ServerPacket
{
    private final String _name;
    private final int _total;
    private final int _online;
    private final String _leaderC;
    private final String _leaderP;
    private final ClanInfo[] _allies;
    
    public AllianceInfo(final int allianceId) {
        final Clan leader = ClanTable.getInstance().getClan(allianceId);
        this._name = leader.getAllyName();
        this._leaderC = leader.getName();
        this._leaderP = leader.getLeaderName();
        final Collection<Clan> allies = ClanTable.getInstance().getClanAllies(allianceId);
        this._allies = new ClanInfo[allies.size()];
        int idx = 0;
        int total = 0;
        int online = 0;
        for (final Clan clan : allies) {
            final ClanInfo ci = new ClanInfo(clan);
            this._allies[idx++] = ci;
            total += ci.getTotal();
            online += ci.getOnline();
        }
        this._total = total;
        this._online = online;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ALLIANCE_INFO);
        this.writeString((CharSequence)this._name);
        this.writeInt(this._total);
        this.writeInt(this._online);
        this.writeString((CharSequence)this._leaderC);
        this.writeString((CharSequence)this._leaderP);
        this.writeInt(this._allies.length);
        for (final ClanInfo aci : this._allies) {
            this.writeString((CharSequence)aci.getClan().getName());
            this.writeInt(0);
            this.writeInt(aci.getClan().getLevel());
            this.writeString((CharSequence)aci.getClan().getLeaderName());
            this.writeInt(aci.getTotal());
            this.writeInt(aci.getOnline());
        }
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getTotal() {
        return this._total;
    }
    
    public int getOnline() {
        return this._online;
    }
    
    public String getLeaderC() {
        return this._leaderC;
    }
    
    public String getLeaderP() {
        return this._leaderP;
    }
    
    public ClanInfo[] getAllies() {
        return this._allies;
    }
}
