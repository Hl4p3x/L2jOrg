// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.GameClient;

public final class RequestJoinSiege extends ClientPacket
{
    private int _castleId;
    private int _isAttacker;
    private int _isJoining;
    
    public void readImpl() {
        this._castleId = this.readInt();
        this._isAttacker = this.readInt();
        this._isJoining = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastleById(this._castleId);
        if (castle != null) {
            if (this._isJoining == 1) {
                if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
                    ((GameClient)this.client).sendPacket(SystemMessageId.YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLAN_S_DISSOLUTION);
                    return;
                }
                if (this._isAttacker == 1) {
                    castle.getSiege().registerAttacker(player);
                }
                else {
                    castle.getSiege().registerDefender(player);
                }
            }
            else {
                castle.getSiege().removeSiegeClan(player);
            }
            castle.getSiege().listRegisterClan(player);
        }
    }
}
