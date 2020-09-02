// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.Zone;

public class HeavenlyRift
{
    private static Zone _zone;
    
    public static Zone getZone() {
        if (HeavenlyRift._zone == null) {
            HeavenlyRift._zone = ZoneManager.getInstance().getZoneByName("[heavenly_rift]");
        }
        return HeavenlyRift._zone;
    }
    
    public static int getAliveNpcCount(final int npcId) {
        final AtomicInteger res = new AtomicInteger();
        getZone().forEachCreature(creature -> res.getAndIncrement(), npc -> npc.getId() == npcId && !npc.isDead());
        return res.get();
    }
    
    public static void startEvent20Bomb(final Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.SET_OFF_BOMBS_AND_GET_TREASURES, 2, 5000, new String[0]));
        spawnMonster(18003, 113352, 12936, 10976, 1800000L);
        spawnMonster(18003, 113592, 13272, 10976, 1800000L);
        spawnMonster(18003, 113816, 13592, 10976, 1800000L);
        spawnMonster(18003, 113080, 13192, 10976, 1800000L);
        spawnMonster(18003, 113336, 13528, 10976, 1800000L);
        spawnMonster(18003, 113560, 13832, 10976, 1800000L);
        spawnMonster(18003, 112776, 13512, 10976, 1800000L);
        spawnMonster(18003, 113064, 13784, 10976, 1800000L);
        spawnMonster(18003, 112440, 13848, 10976, 1800000L);
        spawnMonster(18003, 112728, 14104, 10976, 1800000L);
        spawnMonster(18003, 112760, 14600, 10976, 1800000L);
        spawnMonster(18003, 112392, 14456, 10976, 1800000L);
        spawnMonster(18003, 112104, 14184, 10976, 1800000L);
        spawnMonster(18003, 111816, 14488, 10976, 1800000L);
        spawnMonster(18003, 112104, 14760, 10976, 1800000L);
        spawnMonster(18003, 112392, 15032, 10976, 1800000L);
        spawnMonster(18003, 112120, 15288, 10976, 1800000L);
        spawnMonster(18003, 111784, 15064, 10976, 1800000L);
        spawnMonster(18003, 111480, 14824, 10976, 1800000L);
        spawnMonster(18003, 113144, 14216, 10976, 1800000L);
    }
    
    public static void startEventTower(final Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS, 2, 5000, new String[0]));
        spawnMonster(18004, 112648, 14072, 10976, 1800000L);
        int i;
        ThreadPool.schedule(() -> {
            for (i = 0; i < 20; ++i) {
                spawnMonster(20139, 112696, 13960, 10958, 1800000L);
            }
        }, 10000L);
    }
    
    public static void startEvent40Angels(final Player player) {
        getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.DESTROY_WEAKENED_DIVINE_ANGELS, 2, 5000, new String[0]));
        for (int i = 0; i < 40; ++i) {
            spawnMonster(20139, 112696, 13960, 10958, 1800000L);
        }
    }
    
    private static void spawnMonster(final int npcId, final int x, final int y, final int z, final long despawnTime) {
        try {
            final Spawn spawn = new Spawn(npcId);
            final Location location = new Location(x, y, z);
            spawn.setLocation(location);
            final Npc npc = spawn.doSpawn();
            npc.scheduleDespawn(despawnTime);
        }
        catch (NoSuchMethodException ex) {}
        catch (ClassNotFoundException ex2) {}
    }
    
    static {
        HeavenlyRift._zone = null;
    }
    
    public static class ClearZoneTask implements Runnable
    {
        private Npc _npc;
        
        public ClearZoneTask(final Npc npc) {
            this._npc = npc;
        }
        
        @Override
        public void run() {
            HeavenlyRift.getZone().forEachCreature(creature -> {
                if (GameUtils.isPlayer(creature)) {
                    creature.teleToLocation(114264, 13352, -5104);
                }
                else if (GameUtils.isNpc(creature) && creature.getId() != 30401) {
                    creature.decayMe();
                }
                return;
            });
            this._npc.setBusy(false);
        }
    }
}
