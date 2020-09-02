// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.GiantCave;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Iterator;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Scout extends AbstractNpcAI
{
    private static Logger LOGGER;
    private static final int SPAWN_DELAY = 10000;
    private static final int GAMLIN = 20651;
    private static final int LEOGUL = 20652;
    
    private Scout() {
        this.addAttackId(new int[] { 20651, 20652 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("GC_SCOUT_EVENT_AI")) {
            final Playable pAttacker = (Playable)((player.getServitors().size() > 0) ? ((Playable)player.getServitors().values().stream().findFirst().orElse((Playable)player.getPet())) : player);
            final Monster monster = (Monster)npc;
            if (monster != null && !monster.isDead() && !monster.isTeleporting() && !monster.hasMinions()) {
                for (final MinionHolder is : npc.getParameters().getMinionList("Privates")) {
                    monster.getMinionList().spawnMinions(monster.getParameters().getMinionList("Privates"));
                    monster.getMinionList().getSpawnedMinions().forEach(minion -> this.addAttackPlayerDesire(minion, pAttacker));
                }
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        if (GameUtils.isMonster((WorldObject)npc)) {
            final Monster monster = (Monster)npc;
            if (!monster.isTeleporting() && !monster.hasMinions() && this.getQuestTimer("GC_SCOUT_EVENT_AI", npc, attacker) == null) {
                this.startQuestTimer("GC_SCOUT_EVENT_AI", 10000L, npc, attacker);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Scout();
    }
    
    static {
        Scout.LOGGER = LoggerFactory.getLogger((Class)Scout.class);
    }
}
