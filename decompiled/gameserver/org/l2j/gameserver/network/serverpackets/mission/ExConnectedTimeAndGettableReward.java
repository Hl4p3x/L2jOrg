// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mission;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExConnectedTimeAndGettableReward extends ServerPacket
{
    private final int oneDayRewardAvailableCount;
    
    public ExConnectedTimeAndGettableReward(final Player player) {
        this.oneDayRewardAvailableCount = MissionData.getInstance().getAvailableMissionCount(player);
    }
    
    public ExConnectedTimeAndGettableReward(final int availableCount) {
        this.oneDayRewardAvailableCount = availableCount;
    }
    
    public void writeImpl(final GameClient client) {
        if (!MissionData.getInstance().isAvailable()) {
            return;
        }
        this.writeId(ServerExPacketId.EX_ONE_DAY_REWARD_INFO);
        this.writeInt(0);
        this.writeInt(this.oneDayRewardAvailableCount);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
