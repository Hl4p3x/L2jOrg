// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import java.util.Iterator;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class TimakOrcTroopLeader extends AbstractNpcAI
{
    private static final int TIMAK_ORC_TROOP_LEADER = 20767;
    private static final NpcStringId[] ON_ATTACK_MSG;
    
    private TimakOrcTroopLeader() {
        this.addAttackId(20767);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        if (GameUtils.isMonster((WorldObject)npc)) {
            final Monster monster = (Monster)npc;
            if (!monster.isTeleporting() && !monster.hasMinions() && getRandom(1, 100) <= npc.getParameters().getInt("SummonPrivateRate", 0)) {
                for (final MinionHolder is : npc.getParameters().getMinionList("Privates")) {
                    this.addMinion((Monster)npc, is.getId());
                }
                npc.broadcastSay(ChatType.NPC_GENERAL, (NpcStringId)getRandomEntry((Object[])TimakOrcTroopLeader.ON_ATTACK_MSG), new String[0]);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new TimakOrcTroopLeader();
    }
    
    static {
        ON_ATTACK_MSG = new NpcStringId[] { NpcStringId.COME_OUT_YOU_CHILDREN_OF_DARKNESS, NpcStringId.SHOW_YOURSELVES, NpcStringId.DESTROY_THE_ENEMY_MY_BROTHERS, NpcStringId.FORCES_OF_DARKNESS_FOLLOW_ME };
    }
}
