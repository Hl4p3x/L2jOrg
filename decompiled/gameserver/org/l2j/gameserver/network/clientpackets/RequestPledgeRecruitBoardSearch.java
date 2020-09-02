// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitBoardSearch;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeRecruitBoardSearch extends ClientPacket
{
    private int _clanLevel;
    private int _karma;
    private int _type;
    private String _query;
    private int _sort;
    private boolean _descending;
    private int _page;
    private int _applicationType;
    
    public void readImpl() {
        this._clanLevel = this.readInt();
        this._karma = this.readInt();
        this._type = this.readInt();
        this._query = this.readString();
        this._sort = this.readInt();
        this._descending = (this.readInt() == 2);
        this._page = this.readInt();
        this._applicationType = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._query.isEmpty()) {
            if (this._karma < 0 && this._clanLevel < 0) {
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getUnSortedClanList(), this._page));
            }
            else {
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanList(this._clanLevel, this._karma, this._sort, this._descending), this._page));
            }
        }
        else {
            activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanListByName(this._query.toLowerCase(), this._type), this._page));
        }
    }
}
