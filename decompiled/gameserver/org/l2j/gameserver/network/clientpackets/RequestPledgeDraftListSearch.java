// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPledgeDraftListSearch;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.util.CommonUtil;

public class RequestPledgeDraftListSearch extends ClientPacket
{
    private int _levelMin;
    private int _levelMax;
    private int _classId;
    private String _query;
    private int _sortBy;
    private boolean _descending;
    
    public void readImpl() {
        this._levelMin = CommonUtil.constrain(this.readInt(), 0, 107);
        this._levelMax = CommonUtil.constrain(this.readInt(), 0, 107);
        this._classId = this.readInt();
        this._query = this.readString();
        this._sortBy = this.readInt();
        this._descending = (this.readInt() == 2);
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._query.isEmpty()) {
            ((GameClient)this.client).sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().getSortedWaitingList(this._levelMin, this._levelMax, this._classId, this._sortBy, this._descending)));
        }
        else {
            ((GameClient)this.client).sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().queryWaitingListByName(this._query.toLowerCase())));
        }
    }
}
