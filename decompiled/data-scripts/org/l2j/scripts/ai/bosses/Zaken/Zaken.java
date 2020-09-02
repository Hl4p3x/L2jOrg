// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.Zaken;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Zaken extends AbstractNpcAI
{
    private static final int ZAKEN = 29022;
    private static final int ZAKEN_X = 52207;
    private static final int ZAKEN_Y = 217230;
    private static final int ZAKEN_Z = -3341;
    private static final byte ALIVE = 0;
    private static final byte DEAD = 1;
    
    private Zaken() {
        this.addKillId(29022);
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29022);
        final int status = GrandBossManager.getInstance().getBossStatus(29022);
        if (status == 1) {
            final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0L) {
                this.startQuestTimer("zaken_unlock", temp, (Npc)null, (Player)null);
            }
            else {
                this.spawnBoss();
            }
        }
        else {
            this.spawnBoss();
        }
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("zaken_unlock")) {
            this.spawnBoss();
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    private void spawnBoss() {
        final GrandBoss zaken = (GrandBoss)addSpawn(29022, 52207, 217230, -3341, 0, false, 0L);
        GrandBossManager.getInstance().setBossStatus(29022, 0);
        GrandBossManager.getInstance().addBoss(zaken);
        zaken.broadcastPacket((ServerPacket)new PlaySound(1, "BS01_A", 1, zaken.getObjectId(), zaken.getX(), zaken.getY(), zaken.getZ()));
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        GrandBossManager.getInstance().setBossStatus(29022, 1);
        final long respawnTime = (Config.ZAKEN_SPAWN_INTERVAL + Rnd.get(-Config.ZAKEN_SPAWN_RANDOM, Config.ZAKEN_SPAWN_RANDOM)) * 3600000;
        this.startQuestTimer("zaken_unlock", respawnTime, (Npc)null, (Player)null);
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29022);
        info.set("respawn_time", System.currentTimeMillis() + respawnTime);
        GrandBossManager.getInstance().setStatsSet(29022, info);
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Zaken();
    }
}
