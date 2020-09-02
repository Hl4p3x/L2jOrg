// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ClanWar;
import java.util.Collection;
import org.l2j.gameserver.model.Clan;

public class PledgeReceiveWarList extends ServerPacket
{
    private final Clan _clan;
    private final int _tab;
    private final Collection<ClanWar> _clanList;
    
    public PledgeReceiveWarList(final Clan clan, final int tab) {
        this._clan = clan;
        this._tab = tab;
        this._clanList = (Collection<ClanWar>)clan.getWarList().values();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VIEW_PLEDGE_WARLIST);
        this.writeInt(this._tab);
        this.writeInt(this._clanList.size());
        for (final ClanWar clanWar : this._clanList) {
            final Clan clan = clanWar.getOpposingClan(this._clan);
            if (clan == null) {
                continue;
            }
            this.writeString((CharSequence)clan.getName());
            this.writeInt(clanWar.getState().ordinal());
            this.writeInt(clanWar.getRemainingTime());
            this.writeInt(clanWar.getKillDifference(this._clan));
            this.writeInt(0);
            this.writeInt(clanWar.getKillToStart());
        }
    }
}
