// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeSetAcademyMaster extends ClientPacket
{
    private String _currPlayerName;
    private int _set;
    private String _targetPlayerName;
    
    public void readImpl() {
        this._set = this.readInt();
        this._currPlayerName = this.readString();
        this._targetPlayerName = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_APPRENTICE)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE);
            return;
        }
        final ClanMember currentMember = clan.getClanMember(this._currPlayerName);
        final ClanMember targetMember = clan.getClanMember(this._targetPlayerName);
        if (currentMember == null || targetMember == null) {
            return;
        }
        ClanMember apprenticeMember;
        ClanMember sponsorMember;
        if (currentMember.getPledgeType() == -1) {
            apprenticeMember = currentMember;
            sponsorMember = targetMember;
        }
        else {
            apprenticeMember = targetMember;
            sponsorMember = currentMember;
        }
        final Player apprentice = apprenticeMember.getPlayerInstance();
        final Player sponsor = sponsorMember.getPlayerInstance();
        SystemMessage sm = null;
        if (this._set == 0) {
            if (apprentice != null) {
                apprentice.setSponsor(0);
            }
            else {
                apprenticeMember.setApprenticeAndSponsor(0, 0);
            }
            if (sponsor != null) {
                sponsor.setApprentice(0);
            }
            else {
                sponsorMember.setApprenticeAndSponsor(0, 0);
            }
            apprenticeMember.saveApprenticeAndSponsor(0, 0);
            sponsorMember.saveApprenticeAndSponsor(0, 0);
            sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CLAN_MEMBER_C1_S_APPRENTICE_HAS_BEEN_REMOVED);
        }
        else {
            if (apprenticeMember.getSponsor() != 0 || sponsorMember.getApprentice() != 0 || apprenticeMember.getApprentice() != 0 || sponsorMember.getSponsor() != 0) {
                activeChar.sendMessage("Remove previous connections first.");
                return;
            }
            if (apprentice != null) {
                apprentice.setSponsor(sponsorMember.getObjectId());
            }
            else {
                apprenticeMember.setApprenticeAndSponsor(0, sponsorMember.getObjectId());
            }
            if (sponsor != null) {
                sponsor.setApprentice(apprenticeMember.getObjectId());
            }
            else {
                sponsorMember.setApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);
            }
            apprenticeMember.saveApprenticeAndSponsor(0, sponsorMember.getObjectId());
            sponsorMember.saveApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);
            sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1);
        }
        sm.addString(sponsorMember.getName());
        sm.addString(apprenticeMember.getName());
        if (sponsor != activeChar && sponsor != apprentice) {
            activeChar.sendPacket(sm);
        }
        if (sponsor != null) {
            sponsor.sendPacket(sm);
        }
        if (apprentice != null) {
            apprentice.sendPacket(sm);
        }
    }
}
