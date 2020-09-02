// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.Location;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class RandomWalkingGuards extends AbstractNpcAI
{
    private static final int[] GUARDS;
    private static final int MIN_WALK_DELAY = 15000;
    private static final int MAX_WALK_DELAY = 45000;
    
    private RandomWalkingGuards() {
        this.addSpawnId(RandomWalkingGuards.GUARDS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("RANDOM_WALK") && npc != null) {
            if (!npc.isInCombat() && npc.getWorldRegion().isActive()) {
                final Location randomLoc = GameUtils.getRandomPosition((ILocational)npc.getSpawn().getLocation(), 0, Config.MAX_DRIFT_RANGE);
                this.addMoveToDesire(npc, GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
            }
            this.startQuestTimer("RANDOM_WALK", (long)Rnd.get(15000, 45000), npc, (Player)null);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSpawn(final Npc npc) {
        this.startQuestTimer("RANDOM_WALK", (long)Rnd.get(15000, 45000), npc, (Player)null);
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new RandomWalkingGuards();
    }
    
    static {
        GUARDS = new int[] { 31032, 31033, 31034, 31036, 31035 };
    }
}
