// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PledgeShowMemberListAdd extends ServerPacket
{
    private final String _name;
    private final int _lvl;
    private final int _classId;
    private final int _isOnline;
    private final int _pledgeType;
    
    public PledgeShowMemberListAdd(final Player player) {
        this._name = player.getName();
        this._lvl = player.getLevel();
        this._classId = player.getClassId().getId();
        this._isOnline = (player.isOnline() ? player.getObjectId() : 0);
        this._pledgeType = player.getPledgeType();
    }
    
    public PledgeShowMemberListAdd(final ClanMember cm) {
        this._name = cm.getName();
        this._lvl = cm.getLevel();
        this._classId = cm.getClassId();
        this._isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
        this._pledgeType = cm.getPledgeType();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ADD);
        this.writeString((CharSequence)this._name);
        this.writeInt(this._lvl);
        this.writeInt(this._classId);
        this.writeInt(0);
        this.writeInt(1);
        this.writeInt(this._isOnline);
        this.writeInt(this._pledgeType);
    }
}
