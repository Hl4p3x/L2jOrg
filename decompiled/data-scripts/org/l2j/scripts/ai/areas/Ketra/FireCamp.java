// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.Ketra;

import org.slf4j.LoggerFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import java.lang.reflect.InvocationTargetException;
import org.l2j.gameserver.world.zone.type.ScriptZone;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class FireCamp extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private static final long THREAD_LOOP_DELAY = 120000L;
    private static final long RESPAWN_TIME_MIN = 10000L;
    private static final long RESPAWN_TIME_MAX = 60000L;
    private static final int FIRE_CAMP_LIGHTING_CHANCE = 20;
    private static final int SPAWN_CHANCE = 85;
    private static final int SPAWN_COUNT_MULT_CHANCE = 20;
    private static final int FIRE_CAMP_OFF = 18927;
    private static final int FIRE_CAMP_ON = 18928;
    private static final int KETRA_WOLF_HOUND = 21844;
    private static final int KETRA_ORC_RAIDER = 21846;
    private final Map<Integer, Npc> zoneIdNPC;
    
    private FireCamp() {
        this.zoneIdNPC = new ConcurrentHashMap<Integer, Npc>();
        this.startQuestTimer("START_KETRA_FIRE_CAMP_AI", 30000L, (Npc)null, (Player)null);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("START_KETRA_FIRE_CAMP_AI")) {
            final AtomicInteger index = new AtomicInteger();
            final AtomicInteger atomicInteger;
            int zoneId;
            final ReflectiveOperationException ex;
            ReflectiveOperationException e;
            this.getFireCampInstances().forEach(fireCampNpc -> {
                try {
                    zoneId = ZoneManager.getInstance().addCylinderZone((Class)ScriptZone.class, invokedynamic(makeConcatWithConstants:(Ljava/util/concurrent/atomic/AtomicInteger;)Ljava/lang/String;, atomicInteger), fireCampNpc.getLocation(), 70);
                    this.addEnterZoneId(zoneId);
                    this.zoneIdNPC.put(zoneId, fireCampNpc);
                }
                catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex2) {
                    e = ex;
                    e.printStackTrace();
                }
                atomicInteger.getAndIncrement();
                return;
            });
            this.startQuestTimer("KETRA_FIRE_CAMP_AI_THREAD", 10000L, (Npc)null, (Player)null);
        }
        else if (event.equals("KETRA_FIRE_CAMP_AI_THREAD")) {
            for (final Map.Entry<Integer, Npc> zone : this.zoneIdNPC.entrySet()) {
                if (Rnd.get(100) < 20) {
                    this.fireCampSwitch(zone.getKey(), zone.getValue());
                }
            }
            this.startQuestTimer("KETRA_FIRE_CAMP_AI_THREAD", 120000L, (Npc)null, (Player)null);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    private List<Npc> getFireCampInstances() {
        final List<Npc> npcs = new ArrayList<Npc>();
        final List<Npc> list;
        World.getInstance().forEachCreature(creature -> {
            if (GameUtils.isNpc((WorldObject)creature) && (this.getAsNpc(creature).getId() == 18927 || this.getAsNpc(creature).getId() == 18928)) {
                list.add(this.getAsNpc(creature));
            }
            return;
        });
        return npcs;
    }
    
    public Npc getAsNpc(final Creature creature) {
        return (Npc)creature;
    }
    
    public String onEnterZone(final Creature character, final Zone zone) {
        final Npc fireCamp = this.zoneIdNPC.get(zone.getId());
        if (GameUtils.isPlayer((WorldObject)character) && fireCamp != null && fireCamp.getId() == 18928 && Rnd.get(100) < 85) {
            this.fireCampKill(fireCamp, (Player)character, zone.getId());
        }
        return super.onEnterZone(character, zone);
    }
    
    private void fireCampSwitch(final int zoneID, final Npc npc) {
        final Location location = npc.getLocation();
        this.zoneIdNPC.remove(zoneID);
        npc.scheduleDespawn(0L);
        switch (npc.getId()) {
            case 18927: {
                this.zoneIdNPC.put(zoneID, addSpawn(18928, (IPositionable)location));
                break;
            }
            case 18928: {
                this.zoneIdNPC.put(zoneID, addSpawn(18927, (IPositionable)location));
                break;
            }
        }
    }
    
    private void fireCampKill(final Npc npc, final Player killer, final int zoneID) {
        final Location location = npc.getLocation();
        switch (npc.getId()) {
            case 18928: {
                final long respawnDelay = Rnd.get(10000L, 60000L);
                this.zoneIdNPC.remove(zoneID);
                npc.scheduleDespawn(0L);
                this.addAttackPlayerDesire(addSpawn(21846, (IPositionable)location), (Playable)killer);
                this.addAttackPlayerDesire(addSpawn(21844, (IPositionable)location), (Playable)killer);
                if (Rnd.get(100) < 20) {
                    this.addAttackPlayerDesire(addSpawn(21846, (IPositionable)location), (Playable)killer);
                    this.addAttackPlayerDesire(addSpawn(21844, (IPositionable)location), (Playable)killer);
                }
                ThreadPool.schedule(() -> this.zoneIdNPC.put(zoneID, addSpawn(18927, (IPositionable)location)), respawnDelay);
                break;
            }
        }
    }
    
    public static AbstractNpcAI provider() {
        return new FireCamp();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FireCamp.class);
    }
}
