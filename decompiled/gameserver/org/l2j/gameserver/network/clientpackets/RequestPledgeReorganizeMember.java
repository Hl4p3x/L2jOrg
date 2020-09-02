// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeReorganizeMember extends ClientPacket
{
    private int _isMemberSelected;
    private String _memberName;
    private int _newPledgeType;
    private String _selectedMember;
    
    public void readImpl() {
        this._isMemberSelected = this.readInt();
        this._memberName = this.readString();
        this._newPledgeType = this.readInt();
        this._selectedMember = this.readString();
    }
    
    public void runImpl() {
        if (this._isMemberSelected == 0) {
            return;
        }
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
        final ClanMember member1 = clan.getClanMember(this._memberName);
        if (member1 == null || member1.getObjectId() == clan.getLeaderId()) {
            return;
        }
        final ClanMember member2 = clan.getClanMember(this._selectedMember);
        if (member2 == null || member2.getObjectId() == clan.getLeaderId()) {
            return;
        }
        final int oldPledgeType = member1.getPledgeType();
        if (oldPledgeType == this._newPledgeType) {
            return;
        }
        member1.setPledgeType(this._newPledgeType);
        member2.setPledgeType(oldPledgeType);
        clan.broadcastClanStatus();
    }
}
