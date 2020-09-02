// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.ImperialTomb.FourSepulchers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.gameserver.util.GameXmlReader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.type.EffectZone;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.scripts.quests.Q00620_FourGoblets.Q00620_FourGoblets;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import org.l2j.gameserver.model.Location;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class FourSepulchers extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private static final int CONQUEROR_MANAGER = 31921;
    private static final int EMPEROR_MANAGER = 31922;
    private static final int GREAT_SAGES_MANAGER = 31923;
    private static final int JUDGE_MANAGER = 31924;
    private static final int MYSTERIOUS_CHEST = 31468;
    private static final int KEY_CHEST = 31467;
    private static final int ROOM_3_VICTIM = 18150;
    private static final int ROOM_3_CHEST_REWARDER = 18158;
    private static final int ROOM_4_CHARM_1 = 18196;
    private static final int ROOM_4_CHARM_2 = 18197;
    private static final int ROOM_4_CHARM_3 = 18198;
    private static final int ROOM_4_CHARM_4 = 18199;
    private static final int ROOM_5_STATUE_GUARD = 18232;
    private static final int ROOM_6_REWARD_CHEST = 18256;
    private static final int CONQUEROR_BOSS = 25346;
    private static final int EMPEROR_BOSS = 25342;
    private static final int GREAT_SAGES_BOSS = 25339;
    private static final int JUDGE_BOSS = 25349;
    private static final int TELEPORTER = 31452;
    private static final int[] FIRST_TALK_NPCS;
    private static final int[] CHEST_REWARD_MONSTERS;
    private static final int ENTRANCE_PASS = 91406;
    private static final int USED_PASS = 7261;
    private static final int CHAPEL_KEY = 7260;
    private static final int ANTIQUE_BROOCH = 7262;
    private static final Map<Integer, Location> START_HALL_SPAWNS;
    private static final int CONQUEROR_ZONE = 200221;
    private static final int EMPEROR_ZONE = 200222;
    private static final int GREAT_SAGES_ZONE = 200224;
    private static final int JUDGE_ZONE = 200223;
    private static final Map<Integer, Integer> MANAGER_ZONES;
    private static List<int[]> ROOM_SPAWN_DATA;
    private static final Map<Integer, List<Npc>> STORED_MONSTER_SPAWNS;
    private static final int[][] CHEST_SPAWN_LOCATIONS;
    private static final int[][] DOORS;
    private static final SkillHolder PETRIFY;
    private static final Map<Integer, Integer> CHARM_SKILLS;
    private static final Map<Integer, NpcStringId> CHARM_MSG;
    private static final NpcStringId[] VICTIM_MSG;
    private static final Map<Integer, Integer> STORED_PROGRESS;
    private static final int PARTY_MEMBER_COUNT = 0;
    private static final int ENTRY_DELAY = 3;
    private static final int TIME_ATTACK = 60;
    
    private FourSepulchers() {
        new DataLoader().load();
        this.addFirstTalkId(new int[] { 31921, 31922, 31923, 31924, 31468, 31467 });
        this.addTalkId(new int[] { 31921, 31922, 31923, 31924, 31468, 31467 });
        this.addFirstTalkId(FourSepulchers.FIRST_TALK_NPCS);
        this.addTalkId(FourSepulchers.FIRST_TALK_NPCS);
        this.addKillId(FourSepulchers.CHEST_REWARD_MONSTERS);
        this.addKillId(new int[] { 18150, 18196, 18197, 18198, 18199, 18256, 25346, 25342, 25339, 25349 });
        this.addSpawnId(new int[] { 18150, 18196, 18197, 18198, 18199, 18232, 18256, 25346, 25342, 25339, 25349 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = event;
        switch (event) {
            case "Enter": {
                final QuestState qs = player.getQuestState(Q00620_FourGoblets.class.getSimpleName());
                if (qs == null) {
                    return getNoQuestMsg(player);
                }
                if (qs.isStarted()) {
                    this.tryEnter(npc, player);
                    return null;
                }
                break;
            }
            case "OpenGate": {
                final QuestState qs = player.getQuestState(Q00620_FourGoblets.class.getSimpleName());
                if (qs == null) {
                    return getNoQuestMsg(player);
                }
                if (qs.isStarted() && npc.getScriptValue() == 0) {
                    if (hasQuestItems(player, 7260)) {
                        npc.setScriptValue(1);
                        takeItems(player, 7260, -1L);
                        final int sepulcherId = this.getSepulcherId(player);
                        final int currentWave = FourSepulchers.STORED_PROGRESS.get(sepulcherId) + 1;
                        FourSepulchers.STORED_PROGRESS.put(sepulcherId, currentWave);
                        for (final int[] doorInfo : FourSepulchers.DOORS) {
                            if (doorInfo[0] == sepulcherId && doorInfo[1] == currentWave) {
                                this.openDoor(doorInfo[2], 0);
                                ThreadPool.schedule(() -> this.closeDoor(doorInfo[2], 0), 15000L);
                                break;
                            }
                        }
                        if (currentWave < 7) {
                            this.spawnMysteriousChest(player);
                        }
                        else {
                            this.spawnNextWave(player);
                        }
                        npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_MONSTERS_HAVE_SPAWNED, new String[0]);
                    }
                    else {
                        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
                        html.setFile(player, "data/scripts/ai/areas/ImperialTomb/FourSepulchers/Gatekeeper-no.html");
                        html.replace("%npcname%", npc.getName());
                        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                    }
                    return null;
                }
                htmltext = getNoQuestMsg(player);
                break;
            }
            case "SPAWN_MYSTERIOUS_CHEST": {
                this.spawnMysteriousChest(player);
                return null;
            }
            case "VICTIM_FLEE": {
                if (npc != null && !npc.isDead()) {
                    final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), npc.getSpawn().getLocation().getX() + Rnd.get(-400, 400), npc.getSpawn().getLocation().getY() + Rnd.get(-400, 400), npc.getZ(), npc.getInstanceWorld());
                    if (MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)npc.getSpawn().getLocation(), 600)) {
                        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { destination });
                    }
                    npc.broadcastSay(ChatType.NPC_GENERAL, FourSepulchers.VICTIM_MSG[Rnd.get(FourSepulchers.VICTIM_MSG.length)], new String[0]);
                    this.startQuestTimer("VICTIM_FLEE", 3000L, npc, (Player)null, false);
                }
                return null;
            }
            case "REMOVE_PETRIFY": {
                npc.stopSkillEffects(FourSepulchers.PETRIFY.getSkill());
                npc.setTargetable(true);
                npc.setIsInvul(false);
                return null;
            }
            case "WAVE_DEFEATED_CHECK": {
                final int sepulcherId2 = this.getSepulcherId(player);
                final int currentWave2 = FourSepulchers.STORED_PROGRESS.get(sepulcherId2);
                Location lastLocation = null;
                for (final Npc spawn : FourSepulchers.STORED_MONSTER_SPAWNS.get(sepulcherId2)) {
                    lastLocation = spawn.getLocation();
                    if (spawn.isDead()) {
                        FourSepulchers.STORED_MONSTER_SPAWNS.get(sepulcherId2).remove(spawn);
                    }
                }
                if (FourSepulchers.STORED_MONSTER_SPAWNS.get(sepulcherId2).isEmpty()) {
                    if (currentWave2 == 2) {
                        if (Rnd.nextBoolean()) {
                            this.spawnNextWave(player);
                        }
                        else {
                            this.spawnKeyChest(player, lastLocation);
                        }
                    }
                    else if (currentWave2 == 5) {
                        FourSepulchers.STORED_PROGRESS.put(sepulcherId2, currentWave2 + 1);
                        this.spawnNextWave(player);
                    }
                }
                else if (sepulcherId2 > 0) {
                    this.startQuestTimer("WAVE_DEFEATED_CHECK", 5000L, (Npc)null, player, false);
                }
                return null;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        if (npc == null) {
            return null;
        }
        if (npc.getId() == 31468) {
            if (npc.getScriptValue() == 0) {
                npc.setScriptValue(1);
                npc.deleteMe();
                this.spawnNextWave(player);
            }
            return null;
        }
        if (npc.getId() == 31467) {
            if (npc.getScriptValue() == 0) {
                npc.setScriptValue(1);
                npc.deleteMe();
                giveItems(player, 7260, 1L);
            }
            return null;
        }
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public String onSpawn(final Npc npc) {
        npc.setRandomWalking(false);
        if (npc.getId() == 18150) {
            npc.disableCoreAI(true);
            npc.setRunning();
            this.startQuestTimer("VICTIM_FLEE", 1000L, npc, (Player)null, false);
        }
        if (npc.getId() == 18232) {
            npc.setTarget((WorldObject)npc);
            npc.doCast(FourSepulchers.PETRIFY.getSkill());
            ((Attackable)npc).setCanReturnToSpawnPoint(false);
            npc.setTargetable(false);
            npc.setIsInvul(true);
            this.cancelQuestTimer("REMOVE_PETRIFY", npc, (Player)null);
            this.startQuestTimer("REMOVE_PETRIFY", 300000L, npc, (Player)null, false);
        }
        return super.onSpawn(npc);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        switch (npc.getId()) {
            case 18150: {
                addSpawn(18158, (IPositionable)npc);
                break;
            }
            case 18196:
            case 18197:
            case 18198:
            case 18199: {
                for (final Zone zone : ZoneManager.getInstance().getZones((ILocational)killer)) {
                    if (zone instanceof EffectZone && ((EffectZone)zone).getSkillLevel((int)FourSepulchers.CHARM_SKILLS.get(npc.getId())) > 0) {
                        zone.setEnabled(false);
                        break;
                    }
                }
                npc.broadcastSay(ChatType.NPC_GENERAL, (NpcStringId)FourSepulchers.CHARM_MSG.get(npc.getId()), new String[0]);
                break;
            }
            case 25339:
            case 25342:
            case 25346:
            case 25349: {
                final int sepulcherId = this.getSepulcherId(killer);
                final int currentWave = FourSepulchers.STORED_PROGRESS.get(sepulcherId);
                FourSepulchers.STORED_PROGRESS.put(sepulcherId, currentWave + 1);
                if (killer.getParty() != null && sepulcherId > 0) {
                    for (final Player mem : killer.getParty().getMembers()) {
                        if (MathUtil.isInsideRadius3D((ILocational)killer, (ILocational)mem, 1500)) {
                            final QuestState qs = killer.getQuestState(Q00620_FourGoblets.class.getSimpleName());
                            if (qs == null || !qs.isStarted()) {
                                continue;
                            }
                            giveItems(mem, 7255 + sepulcherId, 1L);
                        }
                    }
                }
                this.spawnNextWave(killer);
                addSpawn(31452, (IPositionable)npc, true, 0L, false);
                break;
            }
            case 18256: {
                npc.dropItem((Creature)killer, 57, (long)Rnd.get(300, 1300));
                break;
            }
            default: {
                this.spawnKeyChest(killer, npc.getLocation());
                break;
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    private void tryEnter(final Npc npc, final Player player) {
        final int npcId = npc.getId();
        if (ZoneManager.getInstance().getZoneById((int)FourSepulchers.MANAGER_ZONES.get(npcId)).getPlayersInsideCount() > 0L) {
            this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, (Player)null);
            return;
        }
        if (!player.isInParty() || player.getParty().getMemberCount() < 0) {
            this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, (Player)null);
            return;
        }
        if (!player.getParty().isLeader(player)) {
            this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, (Player)null);
            return;
        }
        for (final Player mem : player.getParty().getMembers()) {
            final QuestState qs = mem.getQuestState(Q00620_FourGoblets.class.getSimpleName());
            if (qs == null || (!qs.isStarted() && !qs.isCompleted())) {
                this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, mem);
                return;
            }
            if (!hasQuestItems(mem, 91406)) {
                this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, mem);
                return;
            }
            if (player.getWeightPenalty() >= 3) {
                mem.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            }
        }
        final GlobalVariablesManager vars = GlobalVariablesManager.getInstance();
        final long var = vars.getLong(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), 0L) + 3600000L;
        if (var > System.currentTimeMillis()) {
            this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, (Player)null);
            return;
        }
        final int sepulcherId = this.getSepulcherId(player);
        ZoneManager.getInstance().getZoneById((int)FourSepulchers.MANAGER_ZONES.get(npcId)).forEachCreature(creature -> {
            if (GameUtils.isMonster((WorldObject)creature) || ((Creature)creature).isRaid() || (GameUtils.isNpc((WorldObject)creature) && (creature.getId() == 31468 || creature.getId() == 31467 || creature.getId() == 31452))) {
                ((Creature)creature).deleteMe();
            }
            return;
        });
        for (final int[] spawnInfo : FourSepulchers.CHEST_SPAWN_LOCATIONS) {
            if (spawnInfo[0] == sepulcherId && spawnInfo[1] == 4) {
                for (final Zone zone : ZoneManager.getInstance().getZones(spawnInfo[2], spawnInfo[3], spawnInfo[4])) {
                    if (zone instanceof EffectZone) {
                        zone.setEnabled(false);
                    }
                }
                break;
            }
        }
        for (final int[] doorInfo : FourSepulchers.DOORS) {
            if (doorInfo[0] == sepulcherId) {
                this.closeDoor(doorInfo[2], 0);
            }
        }
        final List<Player> members = new ArrayList<Player>();
        for (final Player mem2 : player.getParty().getMembers()) {
            if (MathUtil.isInsideRadius3D((ILocational)player, (ILocational)mem2, 700)) {
                members.add(mem2);
            }
        }
        for (final Player mem2 : members) {
            mem2.teleToLocation((ILocational)FourSepulchers.START_HALL_SPAWNS.get(npcId), 80);
            takeItems(mem2, 91406, 1L);
            takeItems(mem2, 7260, -1L);
            if (!hasQuestItems(mem2, 7262)) {
                giveItems(mem2, 7261, 1L);
            }
        }
        this.showHtmlFile(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), npc, (Player)null);
        ThreadPool.schedule(() -> ZoneManager.getInstance().getZoneById((int)FourSepulchers.MANAGER_ZONES.get(npcId)).oustAllPlayers(), 3600000L);
        vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId), System.currentTimeMillis());
        FourSepulchers.STORED_PROGRESS.put(sepulcherId, 1);
        this.startQuestTimer("SPAWN_MYSTERIOUS_CHEST", 180000L, npc, player, false);
    }
    
    private void spawnNextWave(final Player player) {
        final int sepulcherId = this.getSepulcherId(player);
        final int currentWave = FourSepulchers.STORED_PROGRESS.get(sepulcherId);
        for (final int[] spawnInfo : FourSepulchers.ROOM_SPAWN_DATA) {
            if (spawnInfo[0] == sepulcherId && spawnInfo[1] == currentWave) {
                FourSepulchers.STORED_MONSTER_SPAWNS.get(sepulcherId).add(addSpawn(spawnInfo[2], spawnInfo[3], spawnInfo[4], spawnInfo[5], spawnInfo[6], false, 0L));
            }
        }
        if (currentWave == 4) {
            for (final Zone zone : ZoneManager.getInstance().getZones((ILocational)player)) {
                if (zone instanceof EffectZone) {
                    zone.setEnabled(true);
                }
            }
        }
        if (currentWave == 2 || currentWave == 5) {
            this.startQuestTimer("WAVE_DEFEATED_CHECK", 5000L, (Npc)null, player, false);
        }
        else {
            FourSepulchers.STORED_MONSTER_SPAWNS.get(sepulcherId).clear();
        }
    }
    
    private void spawnMysteriousChest(final Player player) {
        final int sepulcherId = this.getSepulcherId(player);
        final int currentWave = FourSepulchers.STORED_PROGRESS.get(sepulcherId);
        for (final int[] spawnInfo : FourSepulchers.CHEST_SPAWN_LOCATIONS) {
            if (spawnInfo[0] == sepulcherId && spawnInfo[1] == currentWave) {
                addSpawn(31468, spawnInfo[2], spawnInfo[3], spawnInfo[4], spawnInfo[5], false, 0L);
                break;
            }
        }
    }
    
    private void spawnKeyChest(final Player player, final Location loc) {
        addSpawn(31467, (IPositionable)((loc != null) ? loc : player));
    }
    
    private int getSepulcherId(final Player player) {
        if (ZoneManager.getInstance().getZoneById(200221).isCreatureInZone((Creature)player)) {
            return 1;
        }
        if (ZoneManager.getInstance().getZoneById(200222).isCreatureInZone((Creature)player)) {
            return 2;
        }
        if (ZoneManager.getInstance().getZoneById(200224).isCreatureInZone((Creature)player)) {
            return 3;
        }
        if (ZoneManager.getInstance().getZoneById(200223).isCreatureInZone((Creature)player)) {
            return 4;
        }
        return 0;
    }
    
    private void showHtmlFile(final Player player, final String file, final Npc npc, final Player member) {
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        html.setFile(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file));
        if (member != null) {
            html.replace("%member%", member.getName());
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public static AbstractNpcAI provider() {
        return new FourSepulchers();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FourSepulchers.class);
        FIRST_TALK_NPCS = new int[] { 31452, 31453, 31454, 31919, 31920, 31925, 31926, 31927, 31928, 31929, 31930, 31931, 31932, 31933, 31934, 31935, 31936, 31937, 31938, 31939, 31940, 31941, 31942, 31943, 31944 };
        CHEST_REWARD_MONSTERS = new int[] { 18120, 18158, 18177, 18212 };
        (START_HALL_SPAWNS = new HashMap<Integer, Location>()).put(31921, new Location(181632, -85587, -7218));
        FourSepulchers.START_HALL_SPAWNS.put(31922, new Location(179963, -88978, -7218));
        FourSepulchers.START_HALL_SPAWNS.put(31923, new Location(173217, -86132, -7218));
        FourSepulchers.START_HALL_SPAWNS.put(31924, new Location(175608, -82296, -7218));
        (MANAGER_ZONES = new HashMap<Integer, Integer>()).put(31921, 200221);
        FourSepulchers.MANAGER_ZONES.put(31922, 200222);
        FourSepulchers.MANAGER_ZONES.put(31923, 200224);
        FourSepulchers.MANAGER_ZONES.put(31924, 200223);
        FourSepulchers.ROOM_SPAWN_DATA = new ArrayList<int[]>();
        (STORED_MONSTER_SPAWNS = new HashMap<Integer, List<Npc>>()).put(1, new CopyOnWriteArrayList<Npc>());
        FourSepulchers.STORED_MONSTER_SPAWNS.put(2, new CopyOnWriteArrayList<Npc>());
        FourSepulchers.STORED_MONSTER_SPAWNS.put(3, new CopyOnWriteArrayList<Npc>());
        FourSepulchers.STORED_MONSTER_SPAWNS.put(4, new CopyOnWriteArrayList<Npc>());
        CHEST_SPAWN_LOCATIONS = new int[][] { { 1, 1, 182074, -85579, -7216, 32768 }, { 1, 2, 183868, -85577, -7216, 32768 }, { 1, 3, 185681, -85573, -7216, 32768 }, { 1, 4, 187498, -85566, -7216, 32768 }, { 1, 5, 189306, -85571, -7216, 32768 }, { 2, 1, 180375, -88968, -7216, 32768 }, { 2, 2, 182151, -88962, -7216, 32768 }, { 2, 3, 183960, -88964, -7216, 32768 }, { 2, 4, 185792, -88966, -7216, 32768 }, { 2, 5, 187625, -88953, -7216, 32768 }, { 3, 1, 173218, -85703, -7216, 49152 }, { 3, 2, 173206, -83929, -7216, 49152 }, { 3, 3, 173208, -82085, -7216, 49152 }, { 3, 4, 173191, -80290, -7216, 49152 }, { 3, 5, 173198, -78465, -7216, 49152 }, { 4, 1, 175601, -81905, -7216, 49152 }, { 4, 2, 175619, -80094, -7216, 49152 }, { 4, 3, 175608, -78268, -7216, 49152 }, { 4, 4, 175588, -76472, -7216, 49152 }, { 4, 5, 175594, -74655, -7216, 49152 } };
        DOORS = new int[][] { { 1, 2, 25150012 }, { 1, 3, 25150013 }, { 1, 4, 25150014 }, { 1, 5, 25150015 }, { 1, 7, 25150016 }, { 2, 2, 25150002 }, { 2, 3, 25150003 }, { 2, 4, 25150004 }, { 2, 5, 25150005 }, { 2, 7, 25150006 }, { 3, 2, 25150032 }, { 3, 3, 25150033 }, { 3, 4, 25150034 }, { 3, 5, 25150035 }, { 3, 7, 25150036 }, { 4, 2, 25150022 }, { 4, 3, 25150023 }, { 4, 4, 25150024 }, { 4, 5, 25150025 }, { 4, 7, 25150026 } };
        PETRIFY = new SkillHolder(4616, 1);
        (CHARM_SKILLS = new HashMap<Integer, Integer>()).put(18196, 4146);
        FourSepulchers.CHARM_SKILLS.put(18197, 4145);
        FourSepulchers.CHARM_SKILLS.put(18198, 4148);
        FourSepulchers.CHARM_SKILLS.put(18199, 4624);
        (CHARM_MSG = new HashMap<Integer, NpcStringId>()).put(18196, NpcStringId.THE_P_ATK_REDUCTION_DEVICE_HAS_NOW_BEEN_DESTROYED);
        FourSepulchers.CHARM_MSG.put(18197, NpcStringId.THE_P_ATK_REDUCTION_DEVICE_HAS_NOW_BEEN_DESTROYED);
        FourSepulchers.CHARM_MSG.put(18198, NpcStringId.THE_POISON_DEVICE_HAS_NOW_BEEN_DESTROYED);
        FourSepulchers.CHARM_MSG.put(18199, NpcStringId.THE_POISON_DEVICE_HAS_NOW_BEEN_DESTROYED);
        VICTIM_MSG = new NpcStringId[] { NpcStringId.HELP_ME, NpcStringId.DON_T_MISS, NpcStringId.KEEP_PUSHING };
        (STORED_PROGRESS = new HashMap<Integer, Integer>()).put(1, 1);
        FourSepulchers.STORED_PROGRESS.put(2, 1);
        FourSepulchers.STORED_PROGRESS.put(3, 1);
        FourSepulchers.STORED_PROGRESS.put(4, 1);
    }
    
    private class DataLoader extends GameXmlReader
    {
        public void load() {
            FourSepulchers.ROOM_SPAWN_DATA.clear();
            this.parseDatapackFile("data/FourSepulchers.xml");
            FourSepulchers.LOGGER.info("Loaded {} spawn zones data.", (Object)FourSepulchers.ROOM_SPAWN_DATA.size());
        }
        
        protected Path getSchemaFilePath() {
            return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/FourSepulchers.xsd");
        }
        
        public void parseDocument(final Document doc, final File f) {
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling()) {
                        if ("spawn".equalsIgnoreCase(b.getNodeName())) {
                            final NamedNodeMap attrs = b.getAttributes();
                            final int[] info = { this.parseInt(attrs, "sepulcherId"), this.parseInt(attrs, "wave"), this.parseInt(attrs, "npcId"), this.parseInt(attrs, "x"), this.parseInt(attrs, "y"), this.parseInt(attrs, "z"), this.parseInt(attrs, "heading") };
                            FourSepulchers.ROOM_SPAWN_DATA.add(info);
                        }
                    }
                }
            }
        }
    }
}
