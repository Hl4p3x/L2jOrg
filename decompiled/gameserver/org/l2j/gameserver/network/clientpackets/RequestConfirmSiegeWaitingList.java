// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SiegeDefenderList;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestConfirmSiegeWaitingList extends ClientPacket
{
    private int _approved;
    private int _castleId;
    private int _clanId;
    
    public void readImpl() {
        this._castleId = this.readInt();
        this._clanId = this.readInt();
        this._approved = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getClan() == null) {
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastleById(this._castleId);
        if (castle == null) {
            return;
        }
        if (castle.getOwnerId() != activeChar.getClanId() || !activeChar.isClanLeader()) {
            return;
        }
        final Clan clan = ClanTable.getInstance().getClan(this._clanId);
        if (clan == null) {
            return;
        }
        if (!castle.getSiege().getIsRegistrationOver()) {
            if (this._approved == 1) {
                if (!castle.getSiege().checkIsDefenderWaiting(clan)) {
                    return;
                }
                castle.getSiege().approveSiegeDefenderClan(this._clanId);
            }
            else if (castle.getSiege().checkIsDefenderWaiting(clan) || castle.getSiege().checkIsDefender(clan)) {
                castle.getSiege().removeSiegeClan(this._clanId);
            }
        }
        ((GameClient)this.client).sendPacket(new SiegeDefenderList(castle));
    }
}
