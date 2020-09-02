// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.GiantCave;

import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class EntranceRoom extends AbstractNpcAI
{
    private final int MONSTER_SPAWN_CHANCE_SOLO = 10;
    private final int MONSTER_SPAWN_CHANCE_PARTY = 30;
    private final int MONSTER_DESPAWN_DELAY_SOLO = 300000;
    private final int MONSTER_DESPAWN_DELAY_PARTY = 300000;
    private final int[] MONSTER_NPC_IDS;
    private static final int SUMMON_MONSTER_NPC_ID_SOLO = 24017;
    private static final int SUMMON_MONSTER_NPC_ID_PARTY = 24023;
    
    private EntranceRoom() {
        this.addKillId(this.MONSTER_NPC_IDS = new int[] { 20646, 20647, 20648, 20649, 20650 });
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (killer.isInParty() && killer.getParty().getMemberCount() >= 5) {
            if (Rnd.get(100) <= 30) {
                addSpawn(24023, (IPositionable)npc, false, 300000L);
            }
        }
        else if (Rnd.get(100) <= 10) {
            addSpawn(24017, (IPositionable)npc, false, 300000L);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new EntranceRoom();
    }
}
