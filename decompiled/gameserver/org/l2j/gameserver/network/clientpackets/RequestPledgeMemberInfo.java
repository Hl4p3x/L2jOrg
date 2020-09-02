// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveMemberInfo;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeMemberInfo extends ClientPacket
{
    private int _unk1;
    private String _player;
    
    public void readImpl() {
        this._unk1 = this.readInt();
        this._player = this.readString();
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
        final ClanMember member = clan.getClanMember(this._player);
        if (member == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new PledgeReceiveMemberInfo(member));
    }
}
