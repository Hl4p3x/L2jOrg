// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.JoinPledge;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestAnswerJoinPledge extends ClientPacket
{
    private int _answer;
    
    public void readImpl() {
        this._answer = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getRequest().getPartner();
        if (requestor == null) {
            return;
        }
        if (this._answer == 0) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED);
            sm.addString(requestor.getName());
            activeChar.sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(activeChar.getName());
            requestor.sendPacket(sm);
        }
        else {
            if (!(requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge) && !(requestor.getRequest().getRequestPacket() instanceof RequestClanAskJoinByName)) {
                return;
            }
            int pledgeType;
            if (requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge) {
                pledgeType = ((RequestJoinPledge)requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            else {
                pledgeType = ((RequestClanAskJoinByName)requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            final Clan clan = requestor.getClan();
            if (clan.checkClanJoinCondition(requestor, activeChar, pledgeType)) {
                if (activeChar.getClan() != null) {
                    return;
                }
                activeChar.sendPacket(new JoinPledge(requestor.getClanId()));
                activeChar.setPledgeType(pledgeType);
                if (pledgeType == -1) {
                    activeChar.setPowerGrade(9);
                    activeChar.setLvlJoinedAcademy(activeChar.getLevel());
                }
                else {
                    activeChar.setPowerGrade(5);
                }
                clan.addClanMember(activeChar);
                activeChar.setClanPrivileges(activeChar.getClan().getRankPrivs(activeChar.getPowerGrade()));
                activeChar.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm2.addString(activeChar.getName());
                clan.broadcastToOnlineMembers(sm2);
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
            }
        }
        activeChar.getRequest().onRequestResponse();
    }
}
