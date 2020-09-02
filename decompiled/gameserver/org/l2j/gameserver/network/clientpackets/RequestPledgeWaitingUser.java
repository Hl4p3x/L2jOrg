// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingUser;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingList;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeWaitingUser extends ClientPacket
{
    private int _clanId;
    private int _playerId;
    
    public void readImpl() {
        this._clanId = this.readInt();
        this._playerId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClanId() != this._clanId) {
            return;
        }
        final PledgeApplicantInfo infos = ClanEntryManager.getInstance().getPlayerApplication(this._clanId, this._playerId);
        if (infos == null) {
            ((GameClient)this.client).sendPacket(new ExPledgeWaitingList(this._clanId));
        }
        else {
            ((GameClient)this.client).sendPacket(new ExPledgeWaitingUser(infos));
        }
    }
}
