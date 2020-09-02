// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestOustPledgeMember extends ClientPacket
{
    private static final Logger LOGGER;
    private String _target;
    
    public void readImpl() {
        this._target = this.readString();
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
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_DISMISS)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (activeChar.getName().equalsIgnoreCase(this._target)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_DISMISS_YOURSELF);
            return;
        }
        final Clan clan = activeChar.getClan();
        final ClanMember member = clan.getClanMember(this._target);
        if (member == null) {
            RequestOustPledgeMember.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._target));
            return;
        }
        if (member.isOnline() && member.getPlayerInstance().isInCombat()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
            return;
        }
        clan.removeClanMember(member.getObjectId(), System.currentTimeMillis() + Config.ALT_CLAN_JOIN_DAYS * 86400000);
        clan.setCharPenaltyExpiryTime(System.currentTimeMillis() + Config.ALT_CLAN_JOIN_DAYS * 86400000);
        clan.updateClanInDB();
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED);
        sm.addString(member.getName());
        clan.broadcastToOnlineMembers(sm);
        ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN_MEMBER);
        ((GameClient)this.client).sendPacket(SystemMessageId.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
        clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(this._target));
        clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
        if (member.isOnline()) {
            final Player player = member.getPlayerInstance();
            player.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestOustPledgeMember.class);
    }
}
