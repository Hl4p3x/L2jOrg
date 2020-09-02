// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import java.util.function.Predicate;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestOneDayRewardReceive extends ClientPacket
{
    private int id;
    
    public void readImpl() {
        this.id = this.readShort();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final MissionData missionData = MissionData.getInstance();
        final Collection<MissionDataHolder> missions = missionData.getMissions(this.id);
        if (Util.isNullOrEmpty((Collection)missions)) {
            return;
        }
        missions.stream().filter(o -> o.isDisplayable(player)).forEach(r -> r.requestReward(player));
        player.sendPacket(new ExOneDayReceiveRewardList(player, true));
        player.sendPacket(new ExConnectedTimeAndGettableReward((int)missionData.getStoredMissionData(player).values().stream().filter(MissionPlayerData::isAvailable).count()));
    }
}
