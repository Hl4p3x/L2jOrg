// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attendance;

import java.util.Iterator;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExVipAttendanceItemList extends ServerPacket
{
    boolean _available;
    int _index;
    
    public ExVipAttendanceItemList(final Player player) {
        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        this._available = attendanceInfo.isRewardAvailable();
        this._index = attendanceInfo.getRewardIndex();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VIP_ATTENDANCE_ITEM_LIST);
        this.writeByte((byte)(this._available ? (this._index + 1) : this._index));
        this.writeByte((byte)this._index);
        this.writeInt(0);
        this.writeInt(0);
        this.writeByte((byte)1);
        this.writeByte((byte)(byte)(this._available ? 1 : 0));
        this.writeByte((byte)(-6));
        this.writeByte((byte)AttendanceRewardData.getInstance().getRewardsCount());
        int rewardCounter = 0;
        for (final ItemHolder reward : AttendanceRewardData.getInstance().getRewards()) {
            ++rewardCounter;
            this.writeInt(reward.getId());
            this.writeLong(reward.getCount());
            this.writeByte((byte)1);
            this.writeByte((byte)(byte)((rewardCounter % 7 == 0) ? 1 : 0));
        }
        this.writeByte((byte)0);
        this.writeInt(0);
    }
}
