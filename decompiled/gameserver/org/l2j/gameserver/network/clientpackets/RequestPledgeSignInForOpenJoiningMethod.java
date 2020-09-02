// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.JoinPledge;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeSignInForOpenJoiningMethod extends ClientPacket
{
    private int _clanId;
    
    public void readImpl() {
        this._clanId = this.readInt();
        this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(this._clanId);
        if (pledgeRecruitInfo != null) {
            final Clan clan = pledgeRecruitInfo.getClan();
            if (clan != null && activeChar.getClan() == null) {
                activeChar.sendPacket(new JoinPledge(clan.getId()));
                activeChar.setPowerGrade(5);
                clan.addClanMember(activeChar);
                activeChar.setClanPrivileges(activeChar.getClan().getRankPrivs(activeChar.getPowerGrade()));
                activeChar.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm.addString(activeChar.getName());
                clan.broadcastToOnlineMembers(sm);
                if (clan.getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
                }
                activeChar.sendSkillList();
                clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
                PledgeShowMemberListAll.sendAllTo(activeChar);
                activeChar.setClanJoinExpiryTime(0L);
                activeChar.broadcastUserInfo();
                ClanEntryManager.getInstance().removePlayerApplication(this._clanId, activeChar.getObjectId());
            }
        }
    }
}
