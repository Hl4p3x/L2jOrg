// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.Core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.actor.Attackable;
import java.util.Collection;
import org.l2j.gameserver.model.Location;
import java.util.Map;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Core extends AbstractNpcAI
{
    private static final int CORE = 29006;
    private static final int DEATH_KNIGHT = 29007;
    private static final int DOOM_WRAITH = 29008;
    private static final int SUSCEPTOR = 29011;
    private static final Map<Integer, Location> MINNION_SPAWNS;
    private static final byte ALIVE = 0;
    private static final byte DEAD = 1;
    private static boolean _firstAttacked;
    private static final Collection<Attackable> _minions;
    
    private Core() {
        Core.MINNION_SPAWNS.put(29007, new Location(17191, 109298, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(17564, 109548, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(17855, 109552, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(18280, 109202, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(18784, 109253, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(18059, 108314, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(17300, 108444, -6488));
        Core.MINNION_SPAWNS.put(29007, new Location(17148, 110071, -6648));
        Core.MINNION_SPAWNS.put(29007, new Location(18318, 110077, -6648));
        Core.MINNION_SPAWNS.put(29007, new Location(17726, 110391, -6648));
        Core.MINNION_SPAWNS.put(29008, new Location(17113, 110970, -6648));
        Core.MINNION_SPAWNS.put(29008, new Location(17496, 110880, -6648));
        Core.MINNION_SPAWNS.put(29008, new Location(18061, 110990, -6648));
        Core.MINNION_SPAWNS.put(29008, new Location(18384, 110698, -6648));
        Core.MINNION_SPAWNS.put(29008, new Location(17993, 111458, -6584));
        Core.MINNION_SPAWNS.put(29011, new Location(17297, 111470, -6584));
        Core.MINNION_SPAWNS.put(29011, new Location(17893, 110198, -6648));
        Core.MINNION_SPAWNS.put(29011, new Location(17706, 109423, -6488));
        Core.MINNION_SPAWNS.put(29011, new Location(17849, 109388, -6480));
        this.registerMobs(29006, 29007, 29008, 29011);
        Core._firstAttacked = false;
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29006);
        if (GrandBossManager.getInstance().getBossStatus(29006) == 1) {
            final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0L) {
                this.startQuestTimer("core_unlock", temp, (Npc)null, (Player)null);
            }
            else {
                final GrandBoss core = (GrandBoss)addSpawn(29006, 17726, 108915, -6480, 0, false, 0L);
                GrandBossManager.getInstance().setBossStatus(29006, 0);
                this.spawnBoss(core);
            }
        }
        else {
            final boolean test = GlobalVariablesManager.getInstance().getBoolean("Core_Attacked", false);
            if (test) {
                Core._firstAttacked = true;
            }
            final int loc_x = info.getInt("loc_x");
            final int loc_y = info.getInt("loc_y");
            final int loc_z = info.getInt("loc_z");
            final int heading = info.getInt("heading");
            final double hp = info.getDouble("currentHP");
            final double mp = info.getDouble("currentMP");
            final GrandBoss core2 = (GrandBoss)addSpawn(29006, loc_x, loc_y, loc_z, heading, false, 0L);
            core2.setCurrentHpMp(hp, mp);
            this.spawnBoss(core2);
        }
    }
    
    public void onSave() {
        GlobalVariablesManager.getInstance().set("Core_Attacked", Core._firstAttacked);
    }
    
    public void spawnBoss(final GrandBoss npc) {
        GrandBossManager.getInstance().addBoss(npc);
        npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        for (final Map.Entry<Integer, Location> spawn : Core.MINNION_SPAWNS.entrySet()) {
            final Location spawnLocation = spawn.getValue();
            final Attackable mob = (Attackable)addSpawn((int)spawn.getKey(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), Rnd.get(61794), false, 0L);
            mob.setIsRaidMinion(true);
            Core._minions.add(mob);
        }
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equalsIgnoreCase("core_unlock")) {
            final GrandBoss core = (GrandBoss)addSpawn(29006, 17726, 108915, -6480, 0, false, 0L);
            GrandBossManager.getInstance().setBossStatus(29006, 0);
            this.spawnBoss(core);
        }
        else if (event.equalsIgnoreCase("spawn_minion")) {
            final Attackable mob = (Attackable)addSpawn(npc.getId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0L);
            mob.setIsRaidMinion(true);
            Core._minions.add(mob);
        }
        else if (event.equalsIgnoreCase("despawn_minions")) {
            Core._minions.forEach(Creature::decayMe);
            Core._minions.clear();
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        if (npc.getId() == 29006) {
            if (Core._firstAttacked) {
                if (Rnd.get(100) == 0) {
                    npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.REMOVING_INTRUDERS, new String[0]);
                }
            }
            else {
                Core._firstAttacked = true;
                npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_NON_PERMITTED_TARGET_HAS_BEEN_DISCOVERED, new String[0]);
                npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.INTRUDER_REMOVAL_SYSTEM_INITIATED, new String[0]);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (npc.getId() == 29006) {
            npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
            npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_FATAL_ERROR_HAS_OCCURRED, new String[0]);
            npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SYSTEM_IS_BEING_SHUT_DOWN, new String[0]);
            npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.EMPTY, new String[0]);
            Core._firstAttacked = false;
            GrandBossManager.getInstance().setBossStatus(29006, 1);
            final long respawnTime = (Config.CORE_SPAWN_INTERVAL + Rnd.get(-Config.CORE_SPAWN_RANDOM, Config.CORE_SPAWN_RANDOM)) * 3600000;
            this.startQuestTimer("core_unlock", respawnTime, (Npc)null, (Player)null);
            final StatsSet info = GrandBossManager.getInstance().getStatsSet(29006);
            info.set("respawn_time", System.currentTimeMillis() + respawnTime);
            GrandBossManager.getInstance().setStatsSet(29006, info);
            this.startQuestTimer("despawn_minions", 20000L, (Npc)null, (Player)null);
            this.cancelQuestTimers("spawn_minion");
        }
        else if (GrandBossManager.getInstance().getBossStatus(29006) == 0 && Core._minions.contains(npc)) {
            Core._minions.remove(npc);
            this.startQuestTimer("spawn_minion", 60000L, npc, (Player)null);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onSpawn(final Npc npc) {
        if (npc.getId() == 29006) {
            npc.setIsImmobilized(true);
        }
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new Core();
    }
    
    static {
        MINNION_SPAWNS = new HashMap<Integer, Location>();
        _minions = ConcurrentHashMap.newKeySet();
    }
}
