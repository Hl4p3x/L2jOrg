// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PledgeShowMemberListUpdate extends ServerPacket
{
    private final int _pledgeType;
    private final String _name;
    private final int _level;
    private final int _classId;
    private final int _objectId;
    private final int _onlineStatus;
    private final int _race;
    private final int _sex;
    private int _hasSponsor;
    
    public PledgeShowMemberListUpdate(final Player player) {
        this(player.getClan().getClanMember(player.getObjectId()));
    }
    
    public PledgeShowMemberListUpdate(final ClanMember member) {
        this._name = member.getName();
        this._level = member.getLevel();
        this._classId = member.getClassId();
        this._objectId = member.getObjectId();
        this._pledgeType = member.getPledgeType();
        this._race = member.getRaceOrdinal();
        this._sex = (member.getSex() ? 1 : 0);
        this._onlineStatus = member.getOnlineStatus();
        if (this._pledgeType == -1) {
            this._hasSponsor = ((member.getSponsor() != 0) ? 1 : 0);
        }
        else {
            this._hasSponsor = 0;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_UPDATE);
        this.writeString((CharSequence)this._name);
        this.writeInt(this._level);
        this.writeInt(this._classId);
        this.writeInt(this._sex);
        this.writeInt(this._race);
        if (this._onlineStatus > 0) {
            this.writeInt(this._objectId);
            this.writeInt(this._pledgeType);
        }
        else {
            this.writeInt(0);
            this.writeInt(0);
        }
        this.writeInt(this._hasSponsor);
        this.writeByte((byte)this._onlineStatus);
    }
}
