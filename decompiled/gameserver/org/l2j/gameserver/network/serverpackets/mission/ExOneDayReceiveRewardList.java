// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mission;

import org.l2j.gameserver.util.cron4j.Predictor;
import java.util.Iterator;
import java.util.Calendar;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collections;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Function;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOneDayReceiveRewardList extends ServerPacket
{
    private static final Function<String, Long> remainTime;
    private final Player player;
    private final Collection<MissionDataHolder> missions;
    private final long dayRemainTime;
    private final long weekRemainTime;
    private final long monthRemainTime;
    
    public ExOneDayReceiveRewardList(final Player player, final boolean sendRewards) {
        this.player = player;
        this.missions = (Collection<MissionDataHolder>)(sendRewards ? MissionData.getInstance().getMissions(player) : Collections.emptyList());
        this.dayRemainTime = ExOneDayReceiveRewardList.remainTime.apply("30 6 * * *");
        this.weekRemainTime = ExOneDayReceiveRewardList.remainTime.apply("30 6 * * 1");
        this.monthRemainTime = ExOneDayReceiveRewardList.remainTime.apply("30 6 1 * *");
    }
    
    public void writeImpl(final GameClient client) {
        if (!MissionData.getInstance().isAvailable()) {
            return;
        }
        this.writeId(ServerExPacketId.EX_ONE_DAY_REWARD_LIST);
        this.writeInt((int)this.dayRemainTime);
        this.writeInt((int)this.weekRemainTime);
        this.writeInt((int)this.monthRemainTime);
        this.writeByte(23);
        this.writeInt(this.player.getClassId().getId());
        this.writeInt(Calendar.getInstance().get(7));
        this.writeInt(this.missions.size());
        for (final MissionDataHolder mission : this.missions) {
            this.writeShort(mission.getId());
            this.writeByte(mission.getStatus(this.player));
            this.writeByte(mission.getRequiredCompletions() > 1);
            this.writeInt(mission.getProgress(this.player));
            this.writeInt(mission.getRequiredCompletions());
        }
    }
    
    static {
        remainTime = (pattern -> (new Predictor(pattern).nextMatchingTime() - System.currentTimeMillis()) / 1000L);
    }
}
