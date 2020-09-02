// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeSetMemberPowerGrade extends ClientPacket
{
    private String _member;
    private int _powerGrade;
    
    public void readImpl() {
        this._member = this.readString();
        this._powerGrade = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_MANAGE_RANKS)) {
            return;
        }
        final ClanMember member = clan.getClanMember(this._member);
        if (member == null) {
            return;
        }
        if (member.getObjectId() == clan.getLeaderId()) {
            return;
        }
        if (member.getPledgeType() == -1) {
            activeChar.sendPacket(SystemMessageId.THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER);
            return;
        }
        member.setPowerGrade(this._powerGrade);
        clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(member));
        clan.broadcastToOnlineMembers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_C1_S_PRIVILEGE_LEVEL_HAS_BEEN_CHANGED_TO_S2).addString(member.getName())).addInt(this._powerGrade));
    }
}
