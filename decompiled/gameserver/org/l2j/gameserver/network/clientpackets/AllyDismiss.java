// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class AllyDismiss extends ClientPacket
{
    private String _clanName;
    
    public void readImpl() {
        this._clanName = this.readString();
    }
    
    public void runImpl() {
        if (this._clanName == null) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.getClan() == null) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        final Clan leaderClan = player.getClan();
        if (leaderClan.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isClanLeader() || leaderClan.getId() != leaderClan.getAllyId()) {
            player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }
        final Clan clan = ClanTable.getInstance().getClanByName(this._clanName);
        if (clan == null) {
            player.sendPacket(SystemMessageId.THAT_CLAN_DOES_NOT_EXIST);
            return;
        }
        if (clan.getId() == leaderClan.getId()) {
            player.sendPacket(SystemMessageId.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
            return;
        }
        if (clan.getAllyId() != leaderClan.getAllyId()) {
            player.sendPacket(SystemMessageId.DIFFERENT_ALLIANCE);
            return;
        }
        final long currentTime = System.currentTimeMillis();
        leaderClan.setAllyPenaltyExpiryTime(currentTime + Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000, 3);
        leaderClan.updateClanInDB();
        clan.setAllyId(0);
        clan.setAllyName(null);
        clan.changeAllyCrest(0, true);
        clan.setAllyPenaltyExpiryTime(currentTime + Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000, 2);
        clan.updateClanInDB();
        player.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN);
    }
}
