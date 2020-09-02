// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class AllyLeave extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.getClan() == null) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE);
            return;
        }
        final Clan clan = player.getClan();
        if (clan.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (clan.getId() == clan.getAllyId()) {
            player.sendPacket(SystemMessageId.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
            return;
        }
        final long currentTime = System.currentTimeMillis();
        clan.setAllyId(0);
        clan.setAllyName(null);
        clan.changeAllyCrest(0, true);
        clan.setAllyPenaltyExpiryTime(currentTime + Config.ALT_ALLY_JOIN_DAYS_WHEN_LEAVED * 86400000, 1);
        clan.updateClanInDB();
        player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
    }
}
