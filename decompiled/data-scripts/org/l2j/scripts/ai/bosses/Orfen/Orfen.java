// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.Orfen;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Iterator;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.actor.Attackable;
import java.util.Set;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Orfen extends AbstractNpcAI
{
    private static final Location[] POS;
    private static final NpcStringId[] TEXT;
    private static final int ORFEN = 29014;
    private static final int RAIKEL_LEOS = 29016;
    private static final int RIBA_IREN = 29018;
    private static boolean _IsTeleported;
    private static Set<Attackable> _minions;
    private static Zone ZONE;
    private static final byte ALIVE = 0;
    private static final byte DEAD = 1;
    private static final SkillHolder PARALYSIS;
    private static final SkillHolder BLOW;
    private static final SkillHolder ORFEN_HEAL;
    
    private Orfen() {
        final int[] mobs = { 29014, 29016, 29018 };
        this.registerMobs(mobs);
        Orfen._IsTeleported = false;
        Orfen.ZONE = ZoneManager.getInstance().getZoneById(12013);
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29014);
        final int status = GrandBossManager.getInstance().getBossStatus(29014);
        if (status == 1) {
            final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0L) {
                this.startQuestTimer("orfen_unlock", temp, (Npc)null, (Player)null);
            }
            else {
                final int i = getRandom(10);
                Location loc;
                if (i < 4) {
                    loc = Orfen.POS[1];
                }
                else if (i < 7) {
                    loc = Orfen.POS[2];
                }
                else {
                    loc = Orfen.POS[3];
                }
                final GrandBoss orfen = (GrandBoss)addSpawn(29014, (IPositionable)loc, false, 0L);
                GrandBossManager.getInstance().setBossStatus(29014, 0);
                this.spawnBoss(orfen);
            }
        }
        else {
            final int loc_x = info.getInt("loc_x");
            final int loc_y = info.getInt("loc_y");
            final int loc_z = info.getInt("loc_z");
            final int heading = info.getInt("heading");
            final double hp = info.getDouble("currentHP");
            final double mp = info.getDouble("currentMP");
            final GrandBoss orfen2 = (GrandBoss)addSpawn(29014, loc_x, loc_y, loc_z, heading, false, 0L);
            orfen2.setCurrentHpMp(hp, mp);
            this.spawnBoss(orfen2);
        }
    }
    
    public void setSpawnPoint(final Npc npc, final int index) {
        ((Attackable)npc).clearAggroList();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[] { null, null });
        final Spawn spawn = npc.getSpawn();
        spawn.setLocation(Orfen.POS[index]);
        npc.teleToLocation((ILocational)Orfen.POS[index], false);
    }
    
    public void spawnBoss(final GrandBoss npc) {
        GrandBossManager.getInstance().addBoss(npc);
        npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        this.startQuestTimer("check_orfen_pos", 10000L, (Npc)npc, (Player)null, true);
        final int x = npc.getX();
        final int y = npc.getY();
        Attackable mob = (Attackable)addSpawn(29016, x + 100, y + 100, npc.getZ(), 0, false, 0L);
        mob.setIsRaidMinion(true);
        Orfen._minions.add(mob);
        mob = (Attackable)addSpawn(29016, x + 100, y - 100, npc.getZ(), 0, false, 0L);
        mob.setIsRaidMinion(true);
        Orfen._minions.add(mob);
        mob = (Attackable)addSpawn(29016, x - 100, y + 100, npc.getZ(), 0, false, 0L);
        mob.setIsRaidMinion(true);
        Orfen._minions.add(mob);
        mob = (Attackable)addSpawn(29016, x - 100, y - 100, npc.getZ(), 0, false, 0L);
        mob.setIsRaidMinion(true);
        Orfen._minions.add(mob);
        this.startQuestTimer("check_minion_loc", 10000L, (Npc)npc, (Player)null, true);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equalsIgnoreCase("orfen_unlock")) {
            final int i = getRandom(10);
            Location loc;
            if (i < 4) {
                loc = Orfen.POS[1];
            }
            else if (i < 7) {
                loc = Orfen.POS[2];
            }
            else {
                loc = Orfen.POS[3];
            }
            final GrandBoss orfen = (GrandBoss)addSpawn(29014, (IPositionable)loc, false, 0L);
            GrandBossManager.getInstance().setBossStatus(29014, 0);
            this.spawnBoss(orfen);
        }
        else if (event.equalsIgnoreCase("check_orfen_pos")) {
            if ((Orfen._IsTeleported && npc.getCurrentHp() > npc.getMaxHp() * 0.95) || (!Orfen.ZONE.isInsideZone((WorldObject)npc) && !Orfen._IsTeleported)) {
                this.setSpawnPoint(npc, getRandom(3) + 1);
                Orfen._IsTeleported = false;
            }
            else if (Orfen._IsTeleported && !Orfen.ZONE.isInsideZone((WorldObject)npc)) {
                this.setSpawnPoint(npc, 0);
            }
        }
        else if (event.equalsIgnoreCase("check_minion_loc")) {
            for (final Attackable mob : Orfen._minions) {
                if (!MathUtil.isInsideRadius2D((ILocational)npc, (ILocational)mob, 3000)) {
                    mob.teleToLocation((ILocational)npc.getLocation());
                    ((Attackable)npc).clearAggroList();
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[] { null, null });
                }
            }
        }
        else if (event.equalsIgnoreCase("despawn_minions")) {
            for (final Attackable mob : Orfen._minions) {
                mob.decayMe();
            }
            Orfen._minions.clear();
        }
        else if (event.equalsIgnoreCase("spawn_minion")) {
            final Attackable mob2 = (Attackable)addSpawn(29016, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0L);
            mob2.setIsRaidMinion(true);
            Orfen._minions.add(mob2);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSkillSee(final Npc npc, final Player caster, final Skill skill, final WorldObject[] targets, final boolean isSummon) {
        if (npc.getId() == 29014) {
            final Creature originalCaster = (Creature)(isSummon ? ((Creature)caster.getServitors().values().stream().findFirst().orElse((Creature)caster.getPet())) : caster);
            if (skill.getEffectPoint() > 0 && getRandom(5) == 0 && MathUtil.isInsideRadius2D((ILocational)npc, (ILocational)originalCaster, 1000)) {
                npc.broadcastSay(ChatType.NPC_GENERAL, Orfen.TEXT[getRandom(4)], new String[] { caster.getName() });
                originalCaster.teleToLocation((ILocational)npc.getLocation());
                npc.setTarget((WorldObject)originalCaster);
                npc.doCast(Orfen.PARALYSIS.getSkill());
            }
        }
        return super.onSkillSee(npc, caster, skill, targets, isSummon);
    }
    
    public String onFactionCall(final Npc npc, final Npc caller, final Player attacker, final boolean isSummon) {
        if (caller == null || npc == null || npc.isCastingNow(SkillCaster::isAnyNormalType)) {
            return super.onFactionCall(npc, caller, attacker, isSummon);
        }
        final int npcId = npc.getId();
        final int callerId = caller.getId();
        if (npcId == 29016 && getRandom(20) == 0) {
            npc.setTarget((WorldObject)attacker);
            npc.doCast(Orfen.BLOW.getSkill());
        }
        else if (npcId == 29018) {
            int chance = 1;
            if (callerId == 29014) {
                chance = 9;
            }
            if (callerId != 29018 && caller.getCurrentHp() < caller.getMaxHp() / 2.0 && getRandom(10) < chance) {
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, new Object[] { null, null });
                npc.setTarget((WorldObject)caller);
                npc.doCast(Orfen.ORFEN_HEAL.getSkill());
            }
        }
        return super.onFactionCall(npc, caller, attacker, isSummon);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        final int npcId = npc.getId();
        if (npcId == 29014) {
            if (!Orfen._IsTeleported && npc.getCurrentHp() - damage < npc.getMaxHp() / 2.0) {
                Orfen._IsTeleported = true;
                this.setSpawnPoint(npc, 0);
            }
            else if (MathUtil.isInsideRadius2D((ILocational)npc, (ILocational)attacker, 1000) && !MathUtil.isInsideRadius2D((ILocational)npc, (ILocational)attacker, 300) && getRandom(10) == 0) {
                npc.broadcastSay(ChatType.NPC_GENERAL, Orfen.TEXT[getRandom(3)], new String[] { attacker.getName() });
                attacker.teleToLocation((ILocational)npc.getLocation());
                npc.setTarget((WorldObject)attacker);
                npc.doCast(Orfen.PARALYSIS.getSkill());
            }
        }
        else if (npcId == 29018 && !npc.isCastingNow(SkillCaster::isAnyNormalType) && npc.getCurrentHp() - damage < npc.getMaxHp() / 2.0) {
            npc.setTarget((WorldObject)attacker);
            npc.doCast(Orfen.ORFEN_HEAL.getSkill());
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (npc.getId() == 29014) {
            npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
            GrandBossManager.getInstance().setBossStatus(29014, 1);
            long respawnTime = Config.ORFEN_SPAWN_INTERVAL + getRandom(-Config.ORFEN_SPAWN_RANDOM, Config.ORFEN_SPAWN_RANDOM);
            respawnTime *= 3600000L;
            this.startQuestTimer("orfen_unlock", respawnTime, (Npc)null, (Player)null);
            final StatsSet info = GrandBossManager.getInstance().getStatsSet(29014);
            info.set("respawn_time", System.currentTimeMillis() + respawnTime);
            GrandBossManager.getInstance().setStatsSet(29014, info);
            this.cancelQuestTimer("check_minion_loc", npc, (Player)null);
            this.cancelQuestTimer("check_orfen_pos", npc, (Player)null);
            this.startQuestTimer("despawn_minions", 20000L, (Npc)null, (Player)null);
            this.cancelQuestTimers("spawn_minion");
        }
        else if (GrandBossManager.getInstance().getBossStatus(29014) == 0 && npc.getId() == 29016) {
            Orfen._minions.remove(npc);
            this.startQuestTimer("spawn_minion", 360000L, npc, (Player)null);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new Orfen();
    }
    
    static {
        POS = new Location[] { new Location(43728, 17220, -4342), new Location(55024, 17368, -5412), new Location(53504, 21248, -5486), new Location(53248, 24576, -5262) };
        TEXT = new NpcStringId[] { NpcStringId.S1_STOP_KIDDING_YOURSELF_ABOUT_YOUR_OWN_POWERLESSNESS, NpcStringId.S1_I_LL_MAKE_YOU_FEEL_WHAT_TRUE_FEAR_IS, NpcStringId.YOU_RE_REALLY_STUPID_TO_HAVE_CHALLENGED_ME_S1_GET_READY, NpcStringId.S1_DO_YOU_THINK_THAT_S_GOING_TO_WORK };
        Orfen._minions = (Set<Attackable>)ConcurrentHashMap.newKeySet();
        PARALYSIS = new SkillHolder(4064, 1);
        BLOW = new SkillHolder(4067, 4);
        ORFEN_HEAL = new SkillHolder(4516, 1);
    }
}
