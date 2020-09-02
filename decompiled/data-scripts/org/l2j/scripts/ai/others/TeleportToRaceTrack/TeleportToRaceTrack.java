// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.TeleportToRaceTrack;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class TeleportToRaceTrack extends AbstractNpcAI
{
    private static final int RACE_MANAGER = 30995;
    private static final Location RACE_TRACK_TELEPORT;
    private static final IntMap<Location> TELEPORTER_LOCATIONS;
    private static final String MONSTER_RETURN = "MONSTER_RETURN";
    
    private TeleportToRaceTrack() {
        final int[] teleporters = TeleportToRaceTrack.TELEPORTER_LOCATIONS.keySet().toArray();
        this.addStartNpc(30995);
        this.addStartNpc(teleporters);
        this.addTalkId(30995);
        this.addTalkId(teleporters);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        if (npc.getId() == 30995) {
            final int returnId = player.getMonsterReturn();
            if (returnId > 30000) {
                player.teleToLocation((ILocational)TeleportToRaceTrack.TELEPORTER_LOCATIONS.get(returnId));
                player.setMonsterReturn(-1);
            }
            else {
                player.teleToLocation((ILocational)TeleportToRaceTrack.TELEPORTER_LOCATIONS.get(30059));
            }
        }
        else {
            player.teleToLocation((ILocational)TeleportToRaceTrack.RACE_TRACK_TELEPORT);
            player.setMonsterReturn(npc.getId());
        }
        return super.onTalk(npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new TeleportToRaceTrack();
    }
    
    static {
        RACE_TRACK_TELEPORT = new Location(12661, 181687, -3540);
        (TELEPORTER_LOCATIONS = (IntMap)new HashIntMap()).put(30320, (Object)new Location(-80826, 149775, -3043));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30256, (Object)new Location(-12672, 122776, -3116));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30059, (Object)new Location(15670, 142983, -2705));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30080, (Object)new Location(83400, 147943, -3404));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30899, (Object)new Location(111409, 219364, -3545));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30177, (Object)new Location(82956, 53162, -1495));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30848, (Object)new Location(146331, 25762, -2018));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(30233, (Object)new Location(116819, 76994, -2714));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(31275, (Object)new Location(147930, -55281, -2728));
        TeleportToRaceTrack.TELEPORTER_LOCATIONS.put(31210, (Object)new Location(12882, 181053, -3560));
    }
}
