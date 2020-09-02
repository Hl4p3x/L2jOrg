// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExConfirmVipAttendanceCheck extends ServerPacket
{
    boolean _available;
    int _index;
    
    public ExConfirmVipAttendanceCheck(final boolean rewardAvailable, final int rewardIndex) {
        this._available = rewardAvailable;
        this._index = rewardIndex;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CONFIRM_VIP_ATTENDANCE_CHECK);
        this.writeByte((byte)(byte)(this._available ? 1 : 0));
        this.writeByte((byte)this._index);
        this.writeInt(0);
        this.writeInt(0);
    }
}
