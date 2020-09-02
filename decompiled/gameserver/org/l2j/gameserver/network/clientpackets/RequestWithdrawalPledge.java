// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestWithdrawalPledge extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getClan() == null) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        if (activeChar.isClanLeader()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            return;
        }
        if (activeChar.isInCombat()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT);
            return;
        }
        final Clan clan = activeChar.getClan();
        clan.removeClanMember(activeChar.getObjectId(), System.currentTimeMillis() + Config.ALT_CLAN_JOIN_DAYS * 86400000);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WITHDRAWN_FROM_THE_CLAN);
        sm.addString(activeChar.getName());
        clan.broadcastToOnlineMembers(sm);
        clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(activeChar.getName()));
        clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
        ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_CLAN);
        ((GameClient)this.client).sendPacket(SystemMessageId.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
    }
}
