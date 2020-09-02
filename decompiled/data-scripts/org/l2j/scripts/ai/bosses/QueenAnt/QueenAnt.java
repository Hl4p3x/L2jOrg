// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.QueenAnt;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Attackable;
import java.util.Iterator;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class QueenAnt extends AbstractNpcAI
{
    private static final int QUEEN = 29001;
    private static final int LARVA = 29002;
    private static final int NURSE = 29003;
    private static final int GUARD = 29004;
    private static final int ROYAL = 29005;
    private static final int[] MOBS;
    private static final Location OUST_LOC_1;
    private static final Location OUST_LOC_2;
    private static final Location OUST_LOC_3;
    private static final int QUEEN_X = -21610;
    private static final int QUEEN_Y = 181594;
    private static final int QUEEN_Z = -5734;
    private static final byte ALIVE = 0;
    private static final byte DEAD = 1;
    private static Zone _zone;
    private static SkillHolder HEAL1;
    private static SkillHolder HEAL2;
    Monster _queen;
    private Monster _larva;
    private final Set<Monster> _nurses;
    
    private QueenAnt() {
        this._queen = null;
        this._larva = null;
        this._nurses = (Set<Monster>)ConcurrentHashMap.newKeySet();
        this.addSpawnId(QueenAnt.MOBS);
        this.addKillId(QueenAnt.MOBS);
        this.addAggroRangeEnterId(QueenAnt.MOBS);
        this.addFactionCallId(new int[] { 29003 });
        QueenAnt._zone = ZoneManager.getInstance().getZoneById(12012);
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29001);
        final int status = GrandBossManager.getInstance().getBossStatus(29001);
        if (status == 1) {
            final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0L) {
                this.startQuestTimer("queen_unlock", temp, (Npc)null, (Player)null);
            }
            else {
                final GrandBoss queen = (GrandBoss)addSpawn(29001, -21610, 181594, -5734, 0, false, 0L);
                GrandBossManager.getInstance().setBossStatus(29001, 0);
                this.spawnBoss(queen);
            }
        }
        else {
            int loc_x = info.getInt("loc_x");
            int loc_y = info.getInt("loc_y");
            int loc_z = info.getInt("loc_z");
            final int heading = info.getInt("heading");
            final double hp = info.getDouble("currentHP");
            final double mp = info.getDouble("currentMP");
            if (!QueenAnt._zone.isInsideZone(loc_x, loc_y, loc_z)) {
                loc_x = -21610;
                loc_y = 181594;
                loc_z = -5734;
            }
            final GrandBoss queen2 = (GrandBoss)addSpawn(29001, loc_x, loc_y, loc_z, heading, false, 0L);
            queen2.setCurrentHpMp(hp, mp);
            this.spawnBoss(queen2);
        }
    }
    
    private void spawnBoss(final GrandBoss npc) {
        GrandBossManager.getInstance().addBoss(npc);
        if (Rnd.get(100) < 33) {
            QueenAnt._zone.movePlayersTo(QueenAnt.OUST_LOC_1);
        }
        else if (Rnd.get(100) < 50) {
            QueenAnt._zone.movePlayersTo(QueenAnt.OUST_LOC_2);
        }
        else {
            QueenAnt._zone.movePlayersTo(QueenAnt.OUST_LOC_3);
        }
        GrandBossManager.getInstance().addBoss(npc);
        this.startQuestTimer("action", 10000L, (Npc)npc, (Player)null, true);
        this.startQuestTimer("heal", 1000L, (Npc)null, (Player)null, true);
        npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        this._queen = (Monster)npc;
        this._larva = (Monster)addSpawn(29002, -21600, 179482, -5846, Rnd.get(360), false, 0L);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "heal": {
                final boolean larvaNeedHeal = this._larva != null && this._larva.getCurrentHp() < this._larva.getMaxHp();
                final boolean queenNeedHeal = this._queen != null && this._queen.getCurrentHp() < this._queen.getMaxHp();
                for (final Monster nurse : this._nurses) {
                    if (nurse != null && !nurse.isDead()) {
                        if (nurse.isCastingNow(SkillCaster::isAnyNormalType)) {
                            continue;
                        }
                        final boolean notCasting = nurse.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST;
                        if (larvaNeedHeal) {
                            if (nurse.getTarget() == this._larva && !notCasting) {
                                continue;
                            }
                            nurse.setTarget((WorldObject)this._larva);
                            nurse.useMagic(Rnd.nextBoolean() ? QueenAnt.HEAL1.getSkill() : QueenAnt.HEAL2.getSkill());
                        }
                        else if (queenNeedHeal) {
                            if (nurse.getLeader() == this._larva) {
                                continue;
                            }
                            if (nurse.getTarget() == this._queen && !notCasting) {
                                continue;
                            }
                            nurse.setTarget((WorldObject)this._queen);
                            nurse.useMagic(QueenAnt.HEAL1.getSkill());
                        }
                        else {
                            if (!notCasting || nurse.getTarget() == null) {
                                continue;
                            }
                            nurse.setTarget((WorldObject)null);
                        }
                    }
                }
                break;
            }
            case "action": {
                if (npc == null || Rnd.get(3) != 0) {
                    break;
                }
                if (Rnd.get(2) == 0) {
                    npc.broadcastSocialAction(3);
                    break;
                }
                npc.broadcastSocialAction(4);
                break;
            }
            case "queen_unlock": {
                final GrandBoss queen = (GrandBoss)addSpawn(29001, -21610, 181594, -5734, 0, false, 0L);
                GrandBossManager.getInstance().setBossStatus(29001, 0);
                this.spawnBoss(queen);
                break;
            }
            case "ANT_QUEEN_TASK": {
                if (this._queen == null || this._queen.isDead()) {
                    this.cancelQuestTimers("ANT_QUEEN_TASK");
                    break;
                }
                if (!MathUtil.isInsideRadius2D((ILocational)this._queen, -21610, 181594, 2000)) {
                    this._queen.clearAggroList();
                    this._queen.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(-21610, 181594, -5734, 0) });
                    break;
                }
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSpawn(final Npc npc) {
        final Monster mob = (Monster)npc;
        switch (npc.getId()) {
            case 29002: {
                mob.setIsImmobilized(true);
                mob.setUndying(true);
                mob.setIsRaidMinion(true);
                break;
            }
            case 29003: {
                mob.disableCoreAI(true);
                mob.setIsRaidMinion(true);
                this._nurses.add(mob);
                break;
            }
            case 29004:
            case 29005: {
                mob.setIsRaidMinion(true);
                break;
            }
            case 29001: {
                if (mob.getMinionList().getSpawnedMinions().isEmpty()) {
                    ((Monster)npc).getMinionList().spawnMinions(npc.getParameters().getMinionList("Privates"));
                }
                this.cancelQuestTimer("ANT_QUEEN_TASK", npc, (Player)null);
                this.startQuestTimer("ANT_QUEEN_TASK", 5000L, npc, (Player)null, true);
                break;
            }
        }
        return super.onSpawn(npc);
    }
    
    public String onFactionCall(final Npc npc, final Npc caller, final Player attacker, final boolean isSummon) {
        if (caller == null || npc == null) {
            return super.onFactionCall(npc, caller, attacker, isSummon);
        }
        if (!npc.isCastingNow(SkillCaster::isAnyNormalType) && npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST && caller.getCurrentHp() < caller.getMaxHp()) {
            npc.setTarget((WorldObject)caller);
            ((Attackable)npc).useMagic(QueenAnt.HEAL1.getSkill());
        }
        return null;
    }
    
    public String onAggroRangeEnter(final Npc npc, final Player player, final boolean isSummon) {
        if (npc == null || (player.isGM() && player.isInvisible())) {
            return null;
        }
        boolean isMage;
        Playable character;
        if (isSummon) {
            isMage = false;
            character = player.getServitors().values().stream().findFirst().orElse((Playable)player.getPet());
        }
        else {
            isMage = player.isMageClass();
            character = (Playable)player;
        }
        if (character == null) {
            return null;
        }
        if (!Config.RAID_DISABLE_CURSE && character.getLevel() - npc.getLevel() > 8) {
            Skill curse = null;
            if (isMage) {
                if (!character.hasAbnormalType(CommonSkill.RAID_CURSE.getSkill().getAbnormalType()) && Rnd.get(4) == 0) {
                    curse = CommonSkill.RAID_CURSE.getSkill();
                }
            }
            else if (!character.hasAbnormalType(CommonSkill.RAID_CURSE2.getSkill().getAbnormalType()) && Rnd.get(4) == 0) {
                curse = CommonSkill.RAID_CURSE2.getSkill();
            }
            if (curse != null) {
                npc.broadcastPacket((ServerPacket)new MagicSkillUse((Creature)npc, (WorldObject)character, curse.getId(), curse.getLevel(), 300, 0));
                curse.applyEffects((Creature)npc, (Creature)character);
            }
            ((Attackable)npc).stopHating((Creature)character);
            return null;
        }
        return super.onAggroRangeEnter(npc, player, isSummon);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final int npcId = npc.getId();
        if (npcId == 29001) {
            npc.broadcastPacket((ServerPacket)new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
            GrandBossManager.getInstance().setBossStatus(29001, 1);
            final long respawnTime = (Config.QUEEN_ANT_SPAWN_INTERVAL + Rnd.get(-Config.QUEEN_ANT_SPAWN_RANDOM, Config.QUEEN_ANT_SPAWN_RANDOM)) * 3600000;
            this.startQuestTimer("queen_unlock", respawnTime, (Npc)null, (Player)null);
            this.cancelQuestTimer("action", npc, (Player)null);
            this.cancelQuestTimer("heal", (Npc)null, (Player)null);
            final StatsSet info = GrandBossManager.getInstance().getStatsSet(29001);
            info.set("respawn_time", System.currentTimeMillis() + respawnTime);
            GrandBossManager.getInstance().setStatsSet(29001, info);
            this._nurses.clear();
            this._larva.deleteMe();
            this._larva = null;
            this._queen = null;
            this.cancelQuestTimers("ANT_QUEEN_TASK");
        }
        else if (this._queen != null && !this._queen.isAlikeDead()) {
            if (npcId == 29005) {
                final Monster mob = (Monster)npc;
                if (mob.getLeader() != null) {
                    mob.getLeader().getMinionList().onMinionDie(mob, (280 + Rnd.get(40)) * 1000);
                }
            }
            else if (npcId == 29003) {
                final Monster mob = (Monster)npc;
                this._nurses.remove(mob);
                if (mob.getLeader() != null) {
                    mob.getLeader().getMinionList().onMinionDie(mob, 10000);
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new QueenAnt();
    }
    
    static {
        MOBS = new int[] { 29001, 29002, 29003, 29004, 29005 };
        OUST_LOC_1 = new Location(-19480, 187344, -5600);
        OUST_LOC_2 = new Location(-17928, 180912, -5520);
        OUST_LOC_3 = new Location(-23808, 182368, -5600);
        QueenAnt.HEAL1 = new SkillHolder(4020, 1);
        QueenAnt.HEAL2 = new SkillHolder(4024, 1);
    }
}
