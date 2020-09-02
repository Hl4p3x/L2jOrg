// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TowerOfInsolence;

import org.l2j.gameserver.model.ChanceLocation;
import java.util.List;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.Spawn;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.actor.Npc;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class TowerOfInsolence extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private final int LEVEL_MAX_DIFF = 9;
    private final int TIME_UNTIL_MOVE = 1800000;
    private final int ELMOREDEN_LADY = 20977;
    private final int POWER_ANGEL_AMON = 21081;
    private final int ENERGY_OF_INSOLENCE_DROP_RATE = 70;
    private final int ENERGY_OF_INSOLENCE_ITEM_ID = 49685;
    private final int ENERGY_OF_INSOLENCE_DROP_COUNT = 1;
    private final int UNIDENTIFIED_STONE_DROP_RATE = 4;
    private final int UNIDENTIFIED_STONE_ITEM_ID = 49766;
    private final int[] ENERGY_OF_INSOLENCE_NPC_IDS;
    private final int[] ENERGY_OF_INSOLENCE_MINIONS;
    private final int[] UNIDENTIFIED_STONE_NPC_IDS;
    private ScheduledFuture<?> _scheduleTaskElmoreden;
    private ScheduledFuture<?> _scheduleTaskAmon;
    
    private TowerOfInsolence() {
        this.ENERGY_OF_INSOLENCE_NPC_IDS = new int[] { 20977, 21081 };
        this.ENERGY_OF_INSOLENCE_MINIONS = new int[] { 21073, 21078, 21079, 21082, 21083 };
        this.UNIDENTIFIED_STONE_NPC_IDS = new int[] { 20980, 20981, 20982, 20983, 20984, 20985, 21074, 21075, 21076, 21077, 21080, 21980, 21981 };
        this.addSpawnId(this.ENERGY_OF_INSOLENCE_NPC_IDS);
        this.addKillId(this.ENERGY_OF_INSOLENCE_NPC_IDS);
        this.addKillId(this.UNIDENTIFIED_STONE_NPC_IDS);
        this.addKillId(this.ENERGY_OF_INSOLENCE_MINIONS);
    }
    
    private void makeInvul(final Npc npc) {
        npc.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.INVINCIBILITY });
        npc.setIsInvul(true);
    }
    
    private void makeMortal(final Npc npc) {
        npc.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.INVINCIBILITY });
        npc.setIsInvul(false);
    }
    
    private void makeTalk(final Npc npc, final boolean spawning) {
        NpcStringId npcStringId = null;
        switch (npc.getId()) {
            case 20977: {
                npcStringId = (spawning ? NpcStringId.MY_SERVANTS_CAN_KEEP_ME_SAFE_I_HAVE_NOTHING_TO_FEAR : NpcStringId.CAN_T_DIE_IN_A_PLACE_LIKE_THIS);
                break;
            }
            case 21081: {
                npcStringId = (spawning ? NpcStringId.WHO_DARED_TO_ENTER_HERE : NpcStringId.HOW_DARE_YOU_INVADE_OUR_LAND_I_WONT_LEAVE_IT_THAT_EASY);
                break;
            }
        }
        npc.broadcastSay(ChatType.NPC_SHOUT, npcStringId, new String[0]);
    }
    
    public String onSpawn(final Npc npc) {
        if (Util.contains(this.ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            this.makeTalk(npc, true);
            switch (npc.getId()) {
                case 20977: {
                    this.makeInvul(npc);
                    this._scheduleTaskElmoreden = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleAITask(npc, 20977), 1800000L);
                    break;
                }
                case 21081: {
                    this._scheduleTaskAmon = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleAITask(npc, 21081), 1800000L);
                    break;
                }
            }
        }
        return super.onSpawn(npc);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon, final Object payload) {
        if (Util.contains(this.UNIDENTIFIED_STONE_NPC_IDS, npc.getId()) && killer.getLevel() - npc.getLevel() <= 9 && Rnd.get(100) <= 4) {
            npc.dropItem((Creature)killer, 49766, 1L);
        }
        if (Util.contains(this.ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            this.makeTalk(npc, false);
            switch (npc.getId()) {
                case 20977: {
                    this._scheduleTaskElmoreden.cancel(true);
                    this._scheduleTaskElmoreden = null;
                    this._scheduleTaskElmoreden = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleAITask(null, 20977), 1800000L);
                    break;
                }
                case 21081: {
                    this._scheduleTaskAmon.cancel(true);
                    this._scheduleTaskAmon = null;
                    this._scheduleTaskAmon = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleAITask(null, 21081), 1800000L);
                    break;
                }
            }
            if (killer.getLevel() - npc.getLevel() <= 9 && Rnd.get(100) <= 70) {
                npc.dropItem((Creature)killer, 49685, 1L);
            }
        }
        if (Util.contains(this.ENERGY_OF_INSOLENCE_MINIONS, npc.getId()) && payload != null && payload instanceof Monster) {
            final Monster leader = (Monster)payload;
            if (leader.getMinionList().getSpawnedMinions().size() == 0 && !leader.isDead()) {
                this.makeMortal((Npc)leader);
            }
        }
        return super.onKill(npc, killer, isSummon, payload);
    }
    
    public static AbstractNpcAI provider() {
        return new TowerOfInsolence();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TowerOfInsolence.class);
    }
    
    public class ScheduleAITask implements Runnable
    {
        private final Npc _npc;
        private final int _npcId;
        
        public ScheduleAITask(final Npc npc, final int npcId) {
            this._npc = npc;
            this._npcId = npcId;
        }
        
        @Override
        public void run() {
            if (this._npc != null) {
                this._npc.deleteMe();
            }
            try {
                final Spawn spawn = new Spawn(this._npcId);
                final List<NpcSpawnTemplate> spawns = (List<NpcSpawnTemplate>)SpawnsData.getInstance().getNpcSpawns(npcSpawnTemplate -> npcSpawnTemplate.getId() == this._npcId);
                final List<ChanceLocation> locations = (List<ChanceLocation>)spawns.get(0).getLocation();
                final Location location = (Location)locations.get(Rnd.get(0, locations.size() - 1));
                spawn.setLocation(location);
                spawn.doSpawn();
            }
            catch (ClassNotFoundException | NoSuchMethodException ex2) {
                final ReflectiveOperationException ex;
                final ReflectiveOperationException e = ex;
                e.printStackTrace();
            }
        }
    }
}
