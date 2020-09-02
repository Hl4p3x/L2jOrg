// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.events.Elpies;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.EventMonster;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Iterator;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.quest.Event;

public final class Elpies extends Event
{
    private static final int ELPY = 900100;
    private static final int ELPY_AMOUNT = 100;
    private static final int EVENT_DURATION_MINUTES = 2;
    private static final int[][] DROPLIST_CONSUMABLES;
    private static final int[][] DROPLIST_CRYSTALS;
    private static boolean EVENT_ACTIVE;
    private static int CURRENT_ELPY_COUNT;
    private ScheduledFuture<?> _eventTask;
    
    private Elpies() {
        this._eventTask = null;
        this.addSpawnId(new int[] { 900100 });
        this.addKillId(900100);
    }
    
    public boolean eventBypass(final Player activeChar, final String bypass) {
        return false;
    }
    
    public boolean eventStart(final Player eventMaker) {
        if (Elpies.EVENT_ACTIVE) {
            return false;
        }
        if (!Config.CUSTOM_NPC_DATA) {
            eventMaker.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName()));
            return false;
        }
        Elpies.EVENT_ACTIVE = true;
        final EventLocation randomLoc = (EventLocation)Rnd.get((Object[])EventLocation.values());
        Elpies.CURRENT_ELPY_COUNT = 0;
        final long despawnDelay = 120000L;
        for (int i = 0; i < 100; ++i) {
            addSpawn(900100, randomLoc.getRandomX(), randomLoc.getRandomY(), randomLoc.getZ(), 0, true, 120000L);
            ++Elpies.CURRENT_ELPY_COUNT;
        }
        Broadcast.toAllOnlinePlayers("*Squeak Squeak*");
        Broadcast.toAllOnlinePlayers(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, randomLoc.getName()));
        Broadcast.toAllOnlinePlayers("Help us exterminate them!");
        Broadcast.toAllOnlinePlayers("You have 2 minutes!");
        this._eventTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> {
            Broadcast.toAllOnlinePlayers("Time is up!");
            this.eventStop();
            return;
        }, 120000L);
        return true;
    }
    
    public boolean eventStop() {
        if (!Elpies.EVENT_ACTIVE) {
            return false;
        }
        Elpies.EVENT_ACTIVE = false;
        if (this._eventTask != null) {
            this._eventTask.cancel(true);
            this._eventTask = null;
        }
        for (final Spawn spawn : SpawnTable.getInstance().getSpawns(900100)) {
            final Npc npc = spawn.getLastSpawn();
            if (npc != null) {
                npc.deleteMe();
            }
        }
        Broadcast.toAllOnlinePlayers("*Squeak Squeak*");
        Broadcast.toAllOnlinePlayers("Elpy Event finished!");
        return true;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (Elpies.EVENT_ACTIVE) {
            dropItem(npc, killer, Elpies.DROPLIST_CONSUMABLES);
            dropItem(npc, killer, Elpies.DROPLIST_CRYSTALS);
            --Elpies.CURRENT_ELPY_COUNT;
            if (Elpies.CURRENT_ELPY_COUNT <= 0) {
                Broadcast.toAllOnlinePlayers("All elpies have been killed!");
                this.eventStop();
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onSpawn(final Npc npc) {
        ((EventMonster)npc).eventSetDropOnGround(true);
        ((EventMonster)npc).eventSetBlockOffensiveSkills(true);
        return super.onSpawn(npc);
    }
    
    private static void dropItem(final Npc mob, final Player player, final int[][] droplist) {
        final int chance = Rnd.get(100);
        for (final int[] drop : droplist) {
            if (chance >= drop[1]) {
                mob.dropItem((Creature)player, drop[0], (long)Rnd.get(drop[2], drop[3]));
                break;
            }
        }
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new Elpies();
    }
    
    static {
        DROPLIST_CONSUMABLES = new int[][] { { 1540, 80, 10, 15 }, { 1538, 60, 5, 10 }, { 3936, 40, 5, 10 }, { 6387, 25, 5, 10 }, { 22025, 15, 5, 10 }, { 6622, 10, 1, 1 }, { 20034, 5, 1, 1 }, { 20004, 1, 1, 1 }, { 20004, 0, 1, 1 } };
        DROPLIST_CRYSTALS = new int[][] { { 1458, 80, 50, 100 }, { 1459, 60, 40, 80 }, { 1460, 40, 30, 60 }, { 1461, 20, 20, 30 }, { 1462, 0, 10, 20 } };
        Elpies.EVENT_ACTIVE = false;
        Elpies.CURRENT_ELPY_COUNT = 0;
    }
    
    private enum EventLocation
    {
        ADEN("Aden", 146558, 148341, 26622, 28560, -2200), 
        DION("Dion", 18564, 19200, 144377, 145782, -3081), 
        GLUDIN("Gludin", -84040, -81420, 150257, 151175, -3125), 
        HV("Hunters Village", 116094, 117141, 75776, 77072, -2700), 
        OREN("Oren", 82048, 82940, 53240, 54126, -1490);
        
        private final String _name;
        private final int _minX;
        private final int _maxX;
        private final int _minY;
        private final int _maxY;
        private final int _z;
        
        private EventLocation(final String name, final int minX, final int maxX, final int minY, final int maxY, final int z) {
            this._name = name;
            this._minX = minX;
            this._maxX = maxX;
            this._minY = minY;
            this._maxY = maxY;
            this._z = z;
        }
        
        public String getName() {
            return this._name;
        }
        
        public int getRandomX() {
            return Rnd.get(this._minX, this._maxX);
        }
        
        public int getRandomY() {
            return Rnd.get(this._minY, this._maxY);
        }
        
        public int getZ() {
            return this._z;
        }
    }
}
