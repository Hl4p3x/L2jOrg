// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attendance;

import java.util.Iterator;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExVipAttendanceItemList extends ServerPacket
{
    boolean available;
    byte index;
    
    public ExVipAttendanceItemList(final Player player) {
        this.available = player.canReceiveAttendance();
        this.index = player.lastAttendanceReward();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VIP_ATTENDANCE_ITEM_LIST);
        this.writeByte(this.available ? (this.index + 1) : this.index);
        this.writeByte(this.index);
        this.writeInt(0);
        this.writeInt(0);
        this.writeByte(1);
        this.writeByte(this.available);
        this.writeByte(250);
        this.writeByte(AttendanceRewardData.getInstance().getRewardsCount());
        int rewardCounter = 0;
        for (final ItemHolder reward : AttendanceRewardData.getInstance().getRewards()) {
            ++rewardCounter;
            this.writeInt(reward.getId());
            this.writeLong(reward.getCount());
            this.writeByte(1);
            this.writeByte(rewardCounter % 7 == 0);
        }
        this.writeByte(0);
        this.writeInt(0);
    }
}
