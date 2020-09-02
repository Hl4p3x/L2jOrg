// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import java.util.Arrays;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.network.NpcStringId;
import java.util.List;
import io.github.joealisson.primitive.IntMap;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class PolymorphingOnAttack extends AbstractNpcAI
{
    private static final IntMap<List<Integer>> MOBSPAWNS;
    protected static final NpcStringId[][] MOBTEXTS;
    
    private PolymorphingOnAttack() {
        this.addAttackId((IntCollection)PolymorphingOnAttack.MOBSPAWNS.keySet());
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        if (npc.isSpawned() && !npc.isDead()) {
            final List<Integer> tmp = (List<Integer>)PolymorphingOnAttack.MOBSPAWNS.get(npc.getId());
            if (tmp != null && npc.getCurrentHp() <= npc.getMaxHp() * tmp.get(1) / 100.0 && getRandom(100) < tmp.get(2)) {
                if (tmp.get(3) >= 0) {
                    final NpcStringId npcString = PolymorphingOnAttack.MOBTEXTS[tmp.get(3)][getRandom(PolymorphingOnAttack.MOBTEXTS[tmp.get(3)].length)];
                    npc.broadcastPacket((ServerPacket)new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), npcString));
                }
                npc.deleteMe();
                final Attackable newNpc = (Attackable)addSpawn((int)tmp.get(0), npc.getX(), npc.getY(), npc.getZ() + 10, npc.getHeading(), false, 0L, true);
                final Creature originalAttacker = (Creature)(isSummon ? ((Creature)attacker.getServitors().values().stream().findFirst().orElse((Creature)attacker.getPet())) : attacker);
                newNpc.setRunning();
                newNpc.addDamageHate(originalAttacker, 0, 500);
                newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { originalAttacker });
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new PolymorphingOnAttack();
    }
    
    static {
        (MOBSPAWNS = (IntMap)new HashIntMap()).put(21258, (Object)Arrays.asList(21259, 100, 100, -1));
        PolymorphingOnAttack.MOBSPAWNS.put(21261, (Object)Arrays.asList(21262, 100, 20, 0));
        PolymorphingOnAttack.MOBSPAWNS.put(21262, (Object)Arrays.asList(21263, 100, 10, 1));
        PolymorphingOnAttack.MOBSPAWNS.put(21263, (Object)Arrays.asList(21264, 100, 5, 2));
        PolymorphingOnAttack.MOBSPAWNS.put(21265, (Object)Arrays.asList(21271, 100, 33, 0));
        PolymorphingOnAttack.MOBSPAWNS.put(21266, (Object)Arrays.asList(21269, 100, 100, -1));
        PolymorphingOnAttack.MOBSPAWNS.put(21267, (Object)Arrays.asList(21270, 100, 100, -1));
        PolymorphingOnAttack.MOBSPAWNS.put(21271, (Object)Arrays.asList(21272, 66, 10, 1));
        PolymorphingOnAttack.MOBSPAWNS.put(21272, (Object)Arrays.asList(21273, 33, 5, 2));
        MOBTEXTS = new NpcStringId[][] { { NpcStringId.ENOUGH_FOOLING_AROUND_GET_READY_TO_DIE, NpcStringId.YOU_IDIOT_I_VE_JUST_BEEN_TOYING_WITH_YOU, NpcStringId.NOW_THE_FUN_STARTS }, { NpcStringId.I_MUST_ADMIT_NO_ONE_MAKES_MY_BLOOD_BOIL_QUITE_LIKE_YOU_DO, NpcStringId.NOW_THE_BATTLE_BEGINS, NpcStringId.WITNESS_MY_TRUE_POWER }, { NpcStringId.PREPARE_TO_DIE, NpcStringId.I_LL_DOUBLE_MY_STRENGTH, NpcStringId.IT_S_STRONGER_THAN_EXPECTED_2 } };
    }
}
