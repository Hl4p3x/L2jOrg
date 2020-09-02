// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.GiantCave;

import org.l2j.gameserver.model.ChanceLocation;
import java.util.List;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Batur extends AbstractNpcAI
{
    private final int TIME_TO_LIVE = 60000;
    private final int BATUR_ID = 24020;
    private final long RESPAWN_DELAY = 900000L;
    private static Npc BATUR;
    
    private Batur() {
        this.addAttackId(24020);
        this.addKillId(24020);
        this.startQuestTimer("BATUR_SPAWN_THREAD", 30000L, (Npc)null, (Player)null);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("BATUR_SPAWN_THREAD")) {
            final List<NpcSpawnTemplate> spawns = (List<NpcSpawnTemplate>)SpawnsData.getInstance().getNpcSpawns(npcSpawnTemplate -> npcSpawnTemplate.getId() == 24020);
            final List<ChanceLocation> locations = (List<ChanceLocation>)spawns.get(0).getLocation();
            final Location location = (Location)locations.get(Rnd.get(0, locations.size() - 1));
            Batur.BATUR = addSpawn(24020, (IPositionable)location);
        }
        else if (event.equals("BATUR_DESPAWN_THREAD")) {
            Batur.BATUR.scheduleDespawn(0L);
            this.startQuestTimer("BATUR_SPAWN_THREAD", 900000L, (Npc)null, (Player)null);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        if (npc.getId() == 24020 && this.getQuestTimer("BATUR_DESPAWN_THREAD", (Npc)null, (Player)null) == null) {
            this.startQuestTimer("BATUR_DESPAWN_THREAD", 60000L, (Npc)null, (Player)null);
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (npc.getId() == 24020) {
            this.startQuestTimer("BATUR_SPAWN_THREAD", 900000L, (Npc)null, (Player)null);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Batur();
    }
}
