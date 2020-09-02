// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingList;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeWaitingList extends ClientPacket
{
    private int _clanId;
    
    public void readImpl() {
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClanId() != this._clanId) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExPledgeWaitingList(this._clanId));
    }
}
