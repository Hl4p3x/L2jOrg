// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.LastImperialTomb;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.commons.util.Rnd;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.MagicSkillCanceld;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.Location;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IPositionable;
import java.util.HashMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.model.actor.instance.Monster;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.SpecialCamera;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.Earthquake;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;
import java.util.Map;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.slf4j.Logger;
import org.l2j.scripts.instances.AbstractInstance;

public class LastImperialTomb extends AbstractInstance
{
    private static Logger LOGGER;
    private static final int GUIDE = 32011;
    private static final int CUBE = 29061;
    private static final int HALL_ALARM = 18328;
    private static final int HALL_KEEPER_SUICIDAL_SOLDIER = 18333;
    private static final int DUMMY = 29052;
    private static final int DUMMY2 = 29053;
    private static final int[] PORTRAITS;
    private static final int[] DEMONS;
    private static final int FRINTEZZA = 9020;
    private static final int SCARLET1 = 29046;
    private static final int SCARLET2 = 29047;
    private static final int[] ON_KILL_MONSTERS;
    private static final int FIRST_SCARLET_WEAPON = 8204;
    private static final int SECOND_SCARLET_WEAPON = 7903;
    private static final int[] FIRST_ROOM_DOORS;
    private static final int[] SECOND_ROOM_DOORS;
    private static final int[] FIRST_ROUTE_DOORS;
    private static final int[] SECOND_ROUTE_DOORS;
    private static final int DEWDROP_OF_DESTRUCTION_SKILL_ID = 2276;
    private static final SkillHolder INTRO_SKILL;
    private static final SkillHolder FIRST_MORPH_SKILL;
    private static final Map<Integer, NpcStringId> SKILL_MSG;
    static final int[][] PORTRAIT_SPAWNS;
    private static final int TEMPLATE_ID = 205;
    private static final int FRINTEZZA_WAIT_TIME = 1;
    private static final int RANDOM_SONG_INTERVAL = 90;
    private static final int TIME_BETWEEN_DEMON_SPAWNS = 20;
    private static final int MAX_DEMONS = 24;
    
    public LastImperialTomb() {
        super(new int[] { 205 });
        this.addTalkId(new int[] { 32011, 29061 });
        this.addAttackId(29046);
        this.addKillId(LastImperialTomb.ON_KILL_MONSTERS);
        this.addKillId(new int[] { 18328, 29047 });
        this.addKillId(LastImperialTomb.PORTRAITS);
        this.addKillId(LastImperialTomb.DEMONS);
        this.addSpawnId(new int[] { 18328, 29052, 29053 });
        this.addSpellFinishedId(new int[] { 18333 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "FRINTEZZA_INTRO_START": {
                final Instance world = player.getInstanceWorld();
                this.startQuestTimer("FRINTEZZA_INTRO_1", 17000L, (Npc)null, player, false);
                this.startQuestTimer("FRINTEZZA_INTRO_2", 20000L, (Npc)null, player, false);
                this.broadCastPacket(world, (ServerPacket)new Earthquake(-87784, -155083, -9087, 45, 27));
                break;
            }
            case "FRINTEZZA_INTRO_1": {
                final Instance world = player.getInstanceWorld();
                for (final int doorId : LastImperialTomb.FIRST_ROOM_DOORS) {
                    world.openCloseDoor(doorId, false);
                }
                for (final int doorId : LastImperialTomb.FIRST_ROUTE_DOORS) {
                    world.openCloseDoor(doorId, false);
                }
                for (final int doorId : LastImperialTomb.SECOND_ROOM_DOORS) {
                    world.openCloseDoor(doorId, false);
                }
                for (final int doorId : LastImperialTomb.SECOND_ROUTE_DOORS) {
                    world.openCloseDoor(doorId, false);
                }
                addSpawn(29061, -87904, -141296, -9168, 0, false, 0L, false, world.getId());
                break;
            }
            case "FRINTEZZA_INTRO_2": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezzaDummy = addSpawn(29052, -87784, -155083, -9087, 16048, false, 0L, false, world.getId());
                world.setParameter("frintezzaDummy", (Object)frintezzaDummy);
                final Npc overheadDummy = addSpawn(29052, -87784, -153298, -9175, 16384, false, 0L, false, world.getId());
                overheadDummy.setCollisionHeight(600.0);
                this.broadCastPacket(world, (ServerPacket)new NpcInfo(overheadDummy));
                world.setParameter("overheadDummy", (Object)overheadDummy);
                final Npc portraitDummy1 = addSpawn(29052, -89566, -153168, -9165, 16048, false, 0L, false, world.getId());
                world.setParameter("portraitDummy1", (Object)portraitDummy1);
                final Npc portraitDummy2 = addSpawn(29052, -86004, -153168, -9165, 16048, false, 0L, false, world.getId());
                world.setParameter("portraitDummy3", (Object)portraitDummy2);
                final Npc scarletDummy = addSpawn(29053, -87784, -153298, -9175, 16384, false, 0L, false, world.getId());
                world.setParameter("scarletDummy", (Object)scarletDummy);
                this.disablePlayers(world);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)overheadDummy, 300, 90, -10, 6500, 7000, 0, 0, 1, 0, 0));
                final Npc frintezza = addSpawn(9020, -87780, -155086, -9080, 16384, false, 0L, false, world.getId());
                frintezza.setIsImmobilized(true);
                frintezza.setIsInvul(true);
                frintezza.disableAllSkills();
                world.setParameter("frintezza", (Object)frintezza);
                final List<Npc> demons = new ArrayList<Npc>();
                for (final int[] element : LastImperialTomb.PORTRAIT_SPAWNS) {
                    final Monster demon = (Monster)addSpawn(element[0] + 2, element[5], element[6], element[7], element[8], false, 0L, false, world.getId());
                    demon.setIsImmobilized(true);
                    demon.disableAllSkills();
                    demons.add((Npc)demon);
                }
                world.setParameter("demons", (Object)demons);
                this.startQuestTimer("FRINTEZZA_INTRO_3", 6500L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_3": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezzaDummy = (Npc)world.getParameters().getObject("frintezzaDummy", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezzaDummy, 1800, 90, 8, 6500, 7000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_4", 900L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_4": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezzaDummy = (Npc)world.getParameters().getObject("frintezzaDummy", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezzaDummy, 140, 90, 10, 2500, 4500, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_5", 4000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_5": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 40, 75, -10, 0, 1000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 40, 75, -10, 0, 12000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_6", 1350L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_6": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SocialAction(frintezza2.getObjectId(), 2));
                final Npc frintezzaDummy2 = (Npc)world.getParameters().getObject("frintezzaDummy", (Class)Npc.class);
                frintezzaDummy2.deleteMe();
                this.startQuestTimer("FRINTEZZA_INTRO_7", 8000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_7": {
                final Instance world = player.getInstanceWorld();
                final List<Npc> demons2 = (List<Npc>)world.getParameters().getList("demons", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SocialAction(demons2.get(1).getObjectId(), 1));
                this.broadCastPacket(world, (ServerPacket)new SocialAction(demons2.get(2).getObjectId(), 1));
                this.startQuestTimer("FRINTEZZA_INTRO_8", 400L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_8": {
                final Instance world = player.getInstanceWorld();
                final List<Npc> demons2 = (List<Npc>)world.getParameters().getList("demons", (Class)Npc.class);
                final Npc portraitDummy3 = (Npc)world.getParameters().getObject("portraitDummy1", (Class)Npc.class);
                final Npc portraitDummy4 = (Npc)world.getParameters().getObject("portraitDummy3", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SocialAction(demons2.get(0).getObjectId(), 1));
                this.broadCastPacket(world, (ServerPacket)new SocialAction(demons2.get(3).getObjectId(), 1));
                this.sendPacketX(world, (ServerPacket)new SpecialCamera((Creature)portraitDummy3, 1000, 118, 0, 0, 1000, 0, 0, 1, 0, 0), (ServerPacket)new SpecialCamera((Creature)portraitDummy4, 1000, 62, 0, 0, 1000, 0, 0, 1, 0, 0), -87784);
                this.sendPacketX(world, (ServerPacket)new SpecialCamera((Creature)portraitDummy3, 1000, 118, 0, 0, 10000, 0, 0, 1, 0, 0), (ServerPacket)new SpecialCamera((Creature)portraitDummy4, 1000, 62, 0, 0, 10000, 0, 0, 1, 0, 0), -87784);
                this.startQuestTimer("FRINTEZZA_INTRO_9", 2000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_9": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                final Npc portraitDummy3 = (Npc)world.getParameters().getObject("portraitDummy1", (Class)Npc.class);
                final Npc portraitDummy4 = (Npc)world.getParameters().getObject("portraitDummy3", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 240, 90, 0, 0, 1000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 240, 90, 25, 5500, 10000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SocialAction(frintezza2.getObjectId(), 3));
                portraitDummy3.deleteMe();
                portraitDummy4.deleteMe();
                this.startQuestTimer("FRINTEZZA_INTRO_10", 4500L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_10": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_11", 700L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_11": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_12", 1300L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_12": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new ExShowScreenMessage(NpcStringId.MOURNFUL_CHORALE_PRELUDE, 2, 5000, new String[0]));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 120, 180, 45, 1500, 10000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new MagicSkillUse((Creature)frintezza2, (WorldObject)frintezza2, 5006, 1, 34000, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_13", 1500L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_13": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 520, 135, 45, 8000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_14", 7500L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_14": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 1500, 110, 25, 10000, 13000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_15", 9500L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_15": {
                final Instance world = player.getInstanceWorld();
                final Npc overheadDummy2 = (Npc)world.getParameters().getObject("overheadDummy", (Class)Npc.class);
                final Npc scarletDummy2 = (Npc)world.getParameters().getObject("scarletDummy", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)overheadDummy2, 930, 160, -20, 0, 1000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)overheadDummy2, 600, 180, -25, 0, 10000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new MagicSkillUse((Creature)scarletDummy2, (WorldObject)overheadDummy2, 5004, 1, 5800, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_16", 5000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_16": {
                final Instance world = player.getInstanceWorld();
                final Npc scarletDummy3 = (Npc)world.getParameters().getObject("scarletDummy", (Class)Npc.class);
                final Npc activeScarlet = addSpawn(29046, -87789, -153295, -9176, 16384, false, 0L, false, world.getId());
                world.setParameter("activeScarlet", (Object)activeScarlet);
                activeScarlet.setRHandId(8204);
                activeScarlet.setIsInvul(true);
                activeScarlet.setIsImmobilized(true);
                activeScarlet.disableAllSkills();
                this.broadCastPacket(world, (ServerPacket)new SocialAction(activeScarlet.getObjectId(), 3));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)scarletDummy3, 800, 180, 10, 1000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_17", 2100L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_17": {
                final Instance world = player.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 300, 60, 8, 0, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FRINTEZZA_INTRO_18", 2000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_18": {
                final Instance world = player.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 500, 90, 10, 3000, 5000, 0, 0, 1, 0, 0));
                world.setParameter("isPlayingSong", (Object)false);
                this.playRandomSong(world);
                this.startQuestTimer("FRINTEZZA_INTRO_19", 3000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_19": {
                final Instance world = player.getInstanceWorld();
                final Map<Npc, Integer> portraits = new HashMap<Npc, Integer>();
                for (int i = 0; i < LastImperialTomb.PORTRAIT_SPAWNS.length; ++i) {
                    final Npc portrait = addSpawn(LastImperialTomb.PORTRAIT_SPAWNS[i][0], LastImperialTomb.PORTRAIT_SPAWNS[i][1], LastImperialTomb.PORTRAIT_SPAWNS[i][2], LastImperialTomb.PORTRAIT_SPAWNS[i][3], LastImperialTomb.PORTRAIT_SPAWNS[i][4], false, 0L, false, world.getId());
                    portraits.put(portrait, i);
                }
                world.setParameter("portraits", (Object)portraits);
                final Npc overheadDummy = (Npc)world.getParameters().getObject("overheadDummy", (Class)Npc.class);
                final Npc scarletDummy4 = (Npc)world.getParameters().getObject("scarletDummy", (Class)Npc.class);
                overheadDummy.deleteMe();
                scarletDummy4.deleteMe();
                this.startQuestTimer("FRINTEZZA_INTRO_20", 2000L, (Npc)null, player, false);
                break;
            }
            case "FRINTEZZA_INTRO_20": {
                final Instance world = player.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                final Npc activeScarlet = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                final List<Npc> demons3 = (List<Npc>)world.getParameters().getList("demons", (Class)Npc.class);
                for (final Npc demon2 : demons3) {
                    demon2.setIsImmobilized(false);
                    demon2.enableAllSkills();
                }
                activeScarlet.setIsInvul(false);
                activeScarlet.setIsImmobilized(false);
                activeScarlet.enableAllSkills();
                activeScarlet.setRunning();
                activeScarlet.doCast(LastImperialTomb.INTRO_SKILL.getSkill());
                frintezza2.enableAllSkills();
                frintezza2.disableCoreAI(true);
                frintezza2.setIsInvul(true);
                this.enablePlayers(world);
                this.startQuestTimer("PLAY_RANDOM_SONG", 90000L, frintezza2, (Player)null, false);
                this.startQuestTimer("SPAWN_DEMONS", 20000L, (Npc)null, player, false);
                break;
            }
            case "SPAWN_DEMONS": {
                final Instance world = player.getInstanceWorld();
                if (world != null) {
                    final Map<Npc, Integer> portraits = (Map<Npc, Integer>)world.getParameters().getMap("portraits", (Class)Npc.class, (Class)Integer.class);
                    if (portraits != null && !portraits.isEmpty()) {
                        final List<Npc> demons4 = (List<Npc>)world.getParameters().getList("demons", (Class)Npc.class);
                        for (final int j : portraits.values()) {
                            if (demons4.size() > 24) {
                                break;
                            }
                            final Npc demon2 = addSpawn(LastImperialTomb.PORTRAIT_SPAWNS[j][0] + 2, LastImperialTomb.PORTRAIT_SPAWNS[j][5], LastImperialTomb.PORTRAIT_SPAWNS[j][6], LastImperialTomb.PORTRAIT_SPAWNS[j][7], LastImperialTomb.PORTRAIT_SPAWNS[j][8], false, 0L, false, world.getId());
                            demons4.add(demon2);
                        }
                        world.setParameter("demons", (Object)demons4);
                        this.startQuestTimer("SPAWN_DEMONS", 20000L, (Npc)null, player, false);
                    }
                    break;
                }
                break;
            }
            case "PLAY_RANDOM_SONG": {
                if (npc != null) {
                    final Instance world = npc.getInstanceWorld();
                    this.playRandomSong(world);
                    this.startQuestTimer("PLAY_RANDOM_SONG", 90000L, (Npc)null, player, false);
                    break;
                }
                break;
            }
            case "SCARLET_FIRST_MORPH": {
                final Instance world = npc.getInstanceWorld();
                npc.doCast(LastImperialTomb.FIRST_MORPH_SKILL.getSkill());
                this.playRandomSong(world);
                break;
            }
            case "SCARLET_SECOND_MORPH": {
                final Instance world = npc.getInstanceWorld();
                this.disablePlayers(world);
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                activeScarlet2.abortAttack();
                activeScarlet2.abortCast();
                activeScarlet2.setIsInvul(true);
                activeScarlet2.setIsImmobilized(true);
                activeScarlet2.disableAllSkills();
                this.playRandomSong(world);
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_1", 2000L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_1": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SocialAction(frintezza2.getObjectId(), 4));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 250, 120, 15, 0, 1000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 250, 120, 15, 0, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_2", 7000L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_2": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new MagicSkillUse((Creature)frintezza2, (WorldObject)frintezza2, 5006, 1, 34000, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 500, 70, 15, 3000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_3", 3000L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_3": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 2500, 90, 12, 6000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_4", 3000L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_4": {
                final Instance world = npc.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                final Location scarletLocation = activeScarlet2.getLocation();
                int newHeading = 0;
                if (scarletLocation.getHeading() < 32768) {
                    newHeading = Math.abs(180 - (int)(scarletLocation.getHeading() / 182.044444444));
                }
                else {
                    newHeading = Math.abs(540 - (int)(scarletLocation.getHeading() / 182.044444444));
                }
                world.setParameter("scarletLocation", (Object)scarletLocation);
                world.setParameter("newHeading", (Object)newHeading);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 250, newHeading, 12, 0, 1000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 250, newHeading, 12, 0, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_5", 500L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_5": {
                final Instance world = npc.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                final int newHeading2 = world.getParameters().getInt("newHeading");
                activeScarlet2.doDie((Creature)activeScarlet2);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 450, newHeading2, 14, 8000, 8000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_6", 6250L, npc, (Player)null, false);
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_7", 7200L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_6": {
                final Instance world = npc.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                activeScarlet2.deleteMe();
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_7": {
                final Instance world = npc.getInstanceWorld();
                final int newHeading3 = world.getParameters().getInt("newHeading");
                final Location scarletLocation = world.getParameters().getLocation("scarletLocation");
                final Npc activeScarlet3 = addSpawn(29047, (IPositionable)scarletLocation, false, 0L, false, world.getId());
                world.setParameter("activeScarlet", (Object)activeScarlet3);
                activeScarlet3.setRHandId(7903);
                activeScarlet3.setIsInvul(true);
                activeScarlet3.setIsImmobilized(true);
                activeScarlet3.disableAllSkills();
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet3, 450, newHeading3, 12, 500, 14000, 0, 0, 1, 0, 0));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_8", 8100L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_8": {
                final Instance world = npc.getInstanceWorld();
                this.broadCastPacket(world, (ServerPacket)new SocialAction(npc.getObjectId(), 2));
                this.startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_9", 9000L, npc, (Player)null, false);
                break;
            }
            case "SCARLET_SECOND_MORPH_CAMERA_9": {
                final Instance world = npc.getInstanceWorld();
                npc.setIsInvul(false);
                npc.setIsImmobilized(false);
                npc.enableAllSkills();
                this.enablePlayers(world);
                break;
            }
            case "FINISH_CAMERA_1": {
                final Instance world = npc.getInstanceWorld();
                final Npc activeScarlet2 = (Npc)world.getParameters().getObject("activeScarlet", (Class)Npc.class);
                final int newHeading2 = world.getParameters().getInt("newHeading");
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 300, newHeading2 - 180, 5, 0, 7000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)activeScarlet2, 200, newHeading2, 85, 4000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FINISH_CAMERA_2", 7400L, npc, (Player)null, false);
                this.startQuestTimer("FINISH_CAMERA_3", 7500L, npc, (Player)null, false);
                break;
            }
            case "FINISH_CAMERA_2": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                assert frintezza2 != null;
                frintezza2.doDie((Creature)player);
                break;
            }
            case "FINISH_CAMERA_3": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 100, 120, 5, 0, 7000, 0, 0, 1, 0, 0));
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 100, 90, 5, 5000, 15000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FINISH_CAMERA_4", 7000L, npc, (Player)null, false);
                break;
            }
            case "FINISH_CAMERA_4": {
                final Instance world = npc.getInstanceWorld();
                final Npc frintezza2 = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
                this.broadCastPacket(world, (ServerPacket)new SpecialCamera((Creature)frintezza2, 900, 90, 25, 7000, 10000, 0, 0, 1, 0, 0));
                this.startQuestTimer("FINISH_CAMERA_5", 9000L, npc, (Player)null, false);
                break;
            }
            case "FINISH_CAMERA_5": {
                final Instance world = npc.getInstanceWorld();
                for (final int doorId : LastImperialTomb.FIRST_ROOM_DOORS) {
                    world.openCloseDoor(doorId, true);
                }
                for (final int doorId : LastImperialTomb.FIRST_ROUTE_DOORS) {
                    world.openCloseDoor(doorId, true);
                }
                for (final int doorId : LastImperialTomb.SECOND_ROOM_DOORS) {
                    world.openCloseDoor(doorId, true);
                }
                for (final int doorId : LastImperialTomb.SECOND_ROUTE_DOORS) {
                    world.openCloseDoor(doorId, true);
                }
                this.enablePlayers(world);
                break;
            }
        }
        return null;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        if (player.isGM()) {
            this.enterInstance(player, npc, 205);
            player.sendMessage("SYS: You have entered as GM/Admin to Frintezza Instance");
        }
        if (npc.getId() == 32011) {
            this.enterInstance(player, npc, 205);
        }
        else {
            final Instance world = this.getPlayerInstance(player);
            if (world != null) {
                this.teleportPlayerOut(player, world);
            }
        }
        return null;
    }
    
    public String onSpawn(final Npc npc) {
        npc.setRandomWalking(false);
        npc.setIsImmobilized(true);
        if (npc.getId() == 18328) {
            npc.disableCoreAI(true);
        }
        else {
            npc.setIsInvul(true);
        }
        return super.onSpawn(npc);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        if (npc.getId() == 29046) {
            if (npc.isScriptValue(0) && npc.getCurrentHp() < npc.getMaxHp() * 0.8) {
                npc.setScriptValue(1);
                this.startQuestTimer("SCARLET_FIRST_MORPH", 1000L, npc, (Player)null, false);
            }
            if (npc.isScriptValue(1) && npc.getCurrentHp() < npc.getMaxHp() * 0.2) {
                npc.setScriptValue(2);
                this.startQuestTimer("SCARLET_SECOND_MORPH", 1000L, npc, (Player)null, false);
            }
        }
        if (skill != null && Util.contains(LastImperialTomb.PORTRAITS, npc.getId()) && skill.getId() == 2276) {
            npc.doDie((Creature)attacker);
        }
        return null;
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        if (skill.isSuicideAttack()) {
            return this.onKill(npc, null, false);
        }
        return super.onSpellFinished(npc, player, skill);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final Instance world = killer.getInstanceWorld();
        if (npc.getId() == 18328 && world.getStatus() == 0) {
            world.setStatus(1);
            world.spawnGroup("room1");
            final Set<Npc> monsters = (Set<Npc>)world.getAliveNpcs();
            world.setParameter("monstersCount", (Object)(monsters.size() - 1));
            for (final int doorId : LastImperialTomb.FIRST_ROOM_DOORS) {
                world.openCloseDoor(doorId, true);
            }
            for (final Npc monster : monsters) {
                monster.setRunning();
                monster.reduceCurrentHp(1.0, (Creature)killer, (Skill)null, DamageInfo.DamageType.ATTACK);
            }
        }
        else if (npc.getId() == 29047) {
            final Npc frintezza = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
            this.broadCastPacket(world, (ServerPacket)new MagicSkillCanceld(frintezza.getObjectId()));
            this.startQuestTimer("FINISH_CAMERA_1", 500L, npc, (Player)null, false);
        }
        else if (Util.contains(LastImperialTomb.DEMONS, npc.getId())) {
            final List<Npc> demons = (List<Npc>)world.getParameters().getList("demons", (Class)Npc.class);
            if (demons != null) {
                demons.remove(npc);
                world.setParameter("demons", (Object)demons);
            }
        }
        else if (Util.contains(LastImperialTomb.PORTRAITS, npc.getId())) {
            final Map<Npc, Integer> portraits = (Map<Npc, Integer>)world.getParameters().getMap("portraits", (Class)Npc.class, (Class)Integer.class);
            if (portraits != null) {
                portraits.remove(npc);
                world.setParameter("portraits", (Object)portraits);
            }
        }
        else {
            final int killCount = world.getParameters().getInt("monstersCount");
            world.setParameter("monstersCount", (Object)(killCount - 1));
            if (killCount <= 0) {
                switch (world.getStatus()) {
                    case 1: {
                        world.setStatus(2);
                        world.spawnGroup("room2_part1");
                        final Set<Npc> monsters2 = (Set<Npc>)world.getAliveNpcs();
                        world.setParameter("monstersCount", (Object)(monsters2.size() - 1));
                        for (final int doorId2 : LastImperialTomb.FIRST_ROUTE_DOORS) {
                            world.openCloseDoor(doorId2, true);
                        }
                        break;
                    }
                    case 2: {
                        world.setStatus(3);
                        world.spawnGroup("room2_part2");
                        final Set<Npc> monsters2 = (Set<Npc>)world.getAliveNpcs();
                        world.setParameter("monstersCount", (Object)(monsters2.size() - 1));
                        for (final int doorId2 : LastImperialTomb.SECOND_ROOM_DOORS) {
                            world.openCloseDoor(doorId2, true);
                        }
                        for (final Npc monster2 : monsters2) {
                            monster2.setRunning();
                            monster2.reduceCurrentHp(1.0, (Creature)killer, (Skill)null, DamageInfo.DamageType.ATTACK);
                        }
                        break;
                    }
                    case 3: {
                        world.setStatus(4);
                        for (final int doorId : LastImperialTomb.SECOND_ROUTE_DOORS) {
                            world.openCloseDoor(doorId, true);
                        }
                        this.startQuestTimer("FRINTEZZA_INTRO_START", 60000L, (Npc)null, killer, false);
                        break;
                    }
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    private void playRandomSong(final Instance world) {
        final Npc frintezza = (Npc)world.getParameters().getObject("frintezza", (Class)Npc.class);
        final boolean isPlayingSong = world.getParameters().getBoolean("isPlayingSong");
        if (isPlayingSong) {
            return;
        }
        world.setParameter("isPlayingSong", (Object)true);
        final int random = Rnd.get(1, 5);
        final SkillHolder skill = new SkillHolder(5007, random);
        final SkillHolder skillEffect = new SkillHolder(5008, random);
        this.broadCastPacket(world, (ServerPacket)new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 4000, false, (String)null, (NpcStringId)LastImperialTomb.SKILL_MSG.get(random), (String)null));
        this.broadCastPacket(world, (ServerPacket)new MagicSkillUse((Creature)frintezza, (WorldObject)frintezza, skill.getSkillId(), skill.getLevel(), skill.getSkill().getHitTime(), 0));
        for (final Player player : world.getPlayers()) {
            if (player != null && player.isOnline()) {
                frintezza.setTarget((WorldObject)player);
                frintezza.doCast(skillEffect.getSkill());
            }
        }
        world.setParameter("isPlayingSong", (Object)false);
    }
    
    private void disablePlayers(final Instance world) {
        for (final Player player : world.getPlayers()) {
            if (player != null && player.isOnline()) {
                player.abortAttack();
                player.abortCast();
                player.disableAllSkills();
                player.setTarget((WorldObject)null);
                player.stopMove((Location)null);
                player.setIsImmobilized(true);
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }
        }
    }
    
    private void enablePlayers(final Instance world) {
        for (final Player player : world.getPlayers()) {
            if (player != null && player.isOnline()) {
                player.enableAllSkills();
                player.setIsImmobilized(false);
            }
        }
    }
    
    void broadCastPacket(final Instance world, final ServerPacket packet) {
        for (final Player player : world.getPlayers()) {
            if (player != null && player.isOnline()) {
                player.sendPacket(new ServerPacket[] { packet });
            }
        }
    }
    
    private void sendPacketX(final Instance world, final ServerPacket packet1, final ServerPacket packet2, final int x) {
        for (final Player player : world.getPlayers()) {
            if (player != null && player.isOnline()) {
                if (player.getX() < x) {
                    player.sendPacket(new ServerPacket[] { packet1 });
                }
                else {
                    player.sendPacket(new ServerPacket[] { packet2 });
                }
            }
        }
    }
    
    public static LastImperialTomb provider() {
        return new LastImperialTomb();
    }
    
    static {
        LastImperialTomb.LOGGER = LoggerFactory.getLogger((Class)LastImperialTomb.class);
        PORTRAITS = new int[] { 29048, 29049 };
        DEMONS = new int[] { 29050, 29051 };
        ON_KILL_MONSTERS = new int[] { 18328, 18333, 18329, 18330, 18331, 18334, 18335, 18336, 18337, 18338, 18339 };
        FIRST_ROOM_DOORS = new int[] { 17130051, 17130052, 17130053, 17130054, 17130055, 17130056, 17130057, 17130058 };
        SECOND_ROOM_DOORS = new int[] { 17130061, 17130062, 17130063, 17130064, 17130065, 17130066, 17130067, 17130068, 17130069, 17130070 };
        FIRST_ROUTE_DOORS = new int[] { 17130042, 17130043 };
        SECOND_ROUTE_DOORS = new int[] { 17130045, 17130046 };
        INTRO_SKILL = new SkillHolder(5004, 1);
        FIRST_MORPH_SKILL = new SkillHolder(5017, 1);
        (SKILL_MSG = new HashMap<Integer, NpcStringId>()).put(1, NpcStringId.REQUIEM_OF_HATRED);
        LastImperialTomb.SKILL_MSG.put(2, NpcStringId.RONDO_OF_SOLITUDE);
        LastImperialTomb.SKILL_MSG.put(3, NpcStringId.FRENETIC_TOCCATA);
        LastImperialTomb.SKILL_MSG.put(4, NpcStringId.FUGUE_OF_JUBILATION);
        LastImperialTomb.SKILL_MSG.put(5, NpcStringId.HYPNOTIC_MAZURKA);
        PORTRAIT_SPAWNS = new int[][] { { 29048, -89381, -153981, -9168, 3368, -89378, -153968, -9168, 3368 }, { 29048, -86234, -152467, -9168, 37656, -86261, -152492, -9168, 37656 }, { 29049, -89342, -152479, -9168, -5152, -89311, -152491, -9168, -5152 }, { 29049, -86189, -153968, -9168, 29456, -86217, -153956, -9168, 29456 } };
    }
}
