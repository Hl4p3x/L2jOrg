// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Clan;

public class GMViewPledgeInfo extends ServerPacket
{
    private final Clan _clan;
    private final Player _activeChar;
    
    public GMViewPledgeInfo(final Clan clan, final Player activeChar) {
        this._clan = clan;
        this._activeChar = activeChar;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_PLEDGE_INFO);
        this.writeInt(0);
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._clan.getId());
        this.writeInt(0);
        this.writeString((CharSequence)this._clan.getName());
        this.writeString((CharSequence)this._clan.getLeaderName());
        this.writeInt(this._clan.getCrestId());
        this.writeInt(this._clan.getLevel());
        this.writeInt(this._clan.getCastleId());
        this.writeInt(this._clan.getHideoutId());
        this.writeInt(0);
        this.writeInt(this._clan.getRank());
        this.writeInt(this._clan.getReputationScore());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(this._clan.getAllyId());
        this.writeString((CharSequence)this._clan.getAllyName());
        this.writeInt(this._clan.getAllyCrestId());
        this.writeInt((int)(this._clan.isAtWar() ? 1 : 0));
        this.writeInt(0);
        this.writeInt(this._clan.getMembers().size());
        for (final ClanMember member : this._clan.getMembers()) {
            if (member != null) {
                this.writeString((CharSequence)member.getName());
                this.writeInt(member.getLevel());
                this.writeInt(member.getClassId());
                this.writeInt((int)(member.getSex() ? 1 : 0));
                this.writeInt(member.getRaceOrdinal());
                this.writeInt(member.isOnline() ? member.getObjectId() : 0);
                this.writeInt((int)((member.getSponsor() != 0) ? 1 : 0));
            }
        }
    }
}
