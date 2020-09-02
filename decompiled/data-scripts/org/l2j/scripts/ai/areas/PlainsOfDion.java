// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class PlainsOfDion extends AbstractNpcAI
{
    private static final int[] DELU_LIZARDMEN;
    private static final NpcStringId[] MONSTERS_MSG;
    private static final NpcStringId[] MONSTERS_ASSIST_MSG;
    
    private PlainsOfDion() {
        this.addAttackId(PlainsOfDion.DELU_LIZARDMEN);
    }
    
    public String onAttack(final Npc npc, final Player player, final int damage, final boolean isSummon) {
        if (npc.isScriptValue(0)) {
            final int i = Rnd.get(5);
            if (i < 2) {
                npc.broadcastSay(ChatType.NPC_GENERAL, PlainsOfDion.MONSTERS_MSG[i], new String[] { player.getName() });
            }
            else {
                npc.broadcastSay(ChatType.NPC_GENERAL, PlainsOfDion.MONSTERS_MSG[i], new String[0]);
            }
            World.getInstance().forEachVisibleObjectInRange((WorldObject)npc, (Class)Monster.class, npc.getTemplate().getClanHelpRange(), obj -> {
                if (Util.contains(PlainsOfDion.DELU_LIZARDMEN, ((Monster)obj).getId()) && !((Monster)obj).isAttackingNow() && !((Monster)obj).isDead() && GeoEngine.getInstance().canSeeTarget((WorldObject)npc, obj)) {
                    this.addAttackPlayerDesire((Npc)obj, (Playable)player);
                    ((Monster)obj).broadcastSay(ChatType.NPC_GENERAL, PlainsOfDion.MONSTERS_ASSIST_MSG[Rnd.get(3)], new String[0]);
                }
                return;
            });
            npc.setScriptValue(1);
        }
        return super.onAttack(npc, player, damage, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new PlainsOfDion();
    }
    
    static {
        DELU_LIZARDMEN = new int[] { 21104, 21105, 21107 };
        MONSTERS_MSG = new NpcStringId[] { NpcStringId.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP, NpcStringId.S1_HEY_WE_RE_HAVING_A_DUEL_HERE, NpcStringId.THE_DUEL_IS_OVER_ATTACK, NpcStringId.FOUL_KILL_THE_COWARD, NpcStringId.HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON };
        MONSTERS_ASSIST_MSG = new NpcStringId[] { NpcStringId.DIE_YOU_COWARD, NpcStringId.KILL_THE_COWARD, NpcStringId.WHAT_ARE_YOU_LOOKING_AT };
    }
}
