// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingUser;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingList;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeWaitingUser extends ClientPacket
{
    private int _clanId;
    private int _playerId;
    
    public void readImpl() {
        this._clanId = this.readInt();
        this._playerId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClanId() != this._clanId) {
            return;
        }
        final PledgeApplicantData infos = ClanEntryManager.getInstance().getPlayerApplication(this._clanId, this._playerId);
        if (infos == null) {
            ((GameClient)this.client).sendPacket(new ExPledgeWaitingList(this._clanId));
        }
        else {
            ((GameClient)this.client).sendPacket(new ExPledgeWaitingUser(infos));
        }
    }
}
