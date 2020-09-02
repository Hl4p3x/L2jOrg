// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.ai.AttackableAI;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class DivineAngel extends AbstractNpcAI
{
    public DivineAngel() {
        this.addSpawnId(new int[] { 20139 });
        this.addKillId(20139);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0) > 1 && HeavenlyRift.getAliveNpcCount(npc.getId()) == 0) {
            GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
            GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
            GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 1);
            HeavenlyRift.getZone().forEachCreature(riftNpc -> riftNpc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.DIVINE_ANGELS_ARE_NOWHERE_TO_BE_SEEN_I_WANT_TO_TALK_TO_THE_PARTY_LEADER, new String[0]), riftNpc -> GameUtils.isNpc(riftNpc) && ((Creature)riftNpc).getId() == 18004);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onSpawn(final Npc npc) {
        if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0) == 2) {
            ((AttackableAI)npc.getAI()).setGlobalAggro(0);
        }
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new DivineAngel();
    }
}
