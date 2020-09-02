// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeWaitingList extends ClientPacket
{
    private int _clanId;
    
    public void readImpl() {
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClanId() != this._clanId) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExPledgeWaitingList(this._clanId));
    }
}
