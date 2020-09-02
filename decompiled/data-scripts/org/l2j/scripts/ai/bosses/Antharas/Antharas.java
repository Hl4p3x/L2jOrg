// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.Antharas;

import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.Earthquake;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.SpecialCamera;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.world.zone.type.NoRestartZone;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Antharas extends AbstractNpcAI
{
    private static final int ANTHARAS = 29068;
    private static final int BEHEMOTH = 29069;
    private static final int TERASQUE = 29190;
    private static final int BOMBER = 29070;
    private static final SkillHolder ANTH_JUMP;
    private static final SkillHolder ANTH_TAIL;
    private static final SkillHolder ANTH_FEAR;
    private static final SkillHolder ANTH_DEBUFF;
    private static final SkillHolder ANTH_MOUTH;
    private static final SkillHolder ANTH_BREATH;
    private static final SkillHolder ANTH_NORM_ATTACK;
    private static final SkillHolder ANTH_NORM_ATTACK_EX;
    private static final SkillHolder ANTH_REGEN_1;
    private static final SkillHolder ANTH_REGEN_2;
    private static final SkillHolder ANTH_REGEN_3;
    private static final SkillHolder ANTH_REGEN_4;
    private static final SkillHolder DISPEL_BOM;
    private static final SkillHolder ANTH_ANTI_STRIDER;
    private static final SkillHolder ANTH_FEAR_SHORT;
    private static final SkillHolder ANTH_METEOR;
    private static final NoRestartZone zone;
    private static final int ALIVE = 0;
    private static final int WAITING = 1;
    private static final int IN_FIGHT = 2;
    private static final int DEAD = 3;
    private GrandBoss _antharas;
    private static long _lastAttack;
    private static int _minionCount;
    private static int minionMultipler;
    private static int moveChance;
    private static int sandStorm;
    private static Player attacker_1;
    private static Player attacker_2;
    private static Player attacker_3;
    private static int attacker_1_hate;
    private static int attacker_2_hate;
    private static int attacker_3_hate;
    
    private Antharas() {
        this._antharas = null;
        this.addSpawnId(new int[] { 29068 });
        this.addMoveFinishedId(new int[] { 29070 });
        this.addAggroRangeEnterId(new int[] { 29070 });
        this.addSpellFinishedId(new int[] { 29068 });
        this.addAttackId(new int[] { 29068, 29070, 29069, 29190 });
        this.addKillId(new int[] { 29068, 29190, 29069 });
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29068);
        final double curr_hp = info.getDouble("currentHP");
        final double curr_mp = info.getDouble("currentMP");
        final int loc_x = info.getInt("loc_x");
        final int loc_y = info.getInt("loc_y");
        final int loc_z = info.getInt("loc_z");
        final int heading = info.getInt("heading");
        final long respawnTime = info.getLong("respawn_time");
        switch (this.getStatus()) {
            case 0: {
                (this._antharas = (GrandBoss)addSpawn(29068, 125798, 125390, -3952, 0, false, 0L)).setCurrentHpMp(curr_hp, curr_mp);
                this.addBoss(this._antharas);
                break;
            }
            case 1: {
                (this._antharas = (GrandBoss)addSpawn(29068, 125798, 125390, -3952, 0, false, 0L)).setCurrentHpMp(curr_hp, curr_mp);
                this.addBoss(this._antharas);
                this.startQuestTimer("SPAWN_ANTHARAS", (long)(Config.ANTHARAS_WAIT_TIME * 60000), (Npc)null, (Player)null);
                break;
            }
            case 2: {
                (this._antharas = (GrandBoss)addSpawn(29068, loc_x, loc_y, loc_z, heading, false, 0L)).setCurrentHpMp(curr_hp, curr_mp);
                this.addBoss(this._antharas);
                Antharas._lastAttack = System.currentTimeMillis();
                this.startQuestTimer("CHECK_ATTACK", 60000L, (Npc)this._antharas, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 300000L, (Npc)this._antharas, (Player)null);
                break;
            }
            case 3: {
                final long remain = respawnTime - System.currentTimeMillis();
                if (remain > 0L) {
                    this.startQuestTimer("CLEAR_STATUS", remain, (Npc)null, (Player)null);
                    break;
                }
                this.setStatus(0);
                this.addBoss(this._antharas = (GrandBoss)addSpawn(29068, 125798, 125390, -3952, 0, false, 0L));
                break;
            }
        }
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "SPAWN_ANTHARAS": {
                this._antharas.teleToLocation(125798, 125390, -3952, 32542);
                this.setStatus(2);
                Antharas._lastAttack = System.currentTimeMillis();
                Antharas.zone.broadcastPacket((ServerPacket)new PlaySound("BS02_A"));
                this.startQuestTimer("CAMERA_1", 23L, (Npc)this._antharas, (Player)null);
                break;
            }
            case "CAMERA_1": {
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 700, 13, -19, 0, 10000, 20000, 0, 0, 0, 0, 0));
                this.startQuestTimer("CAMERA_2", 3000L, npc, (Player)null);
                break;
            }
            case "CAMERA_2": {
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 700, 13, 0, 6000, 10000, 20000, 0, 0, 0, 0, 0));
                this.startQuestTimer("CAMERA_3", 10000L, npc, (Player)null);
                break;
            }
            case "CAMERA_3": {
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 3700, 0, -3, 0, 10000, 10000, 0, 0, 0, 0, 0));
                Antharas.zone.broadcastPacket((ServerPacket)new SocialAction(npc.getObjectId(), 1));
                this.startQuestTimer("CAMERA_4", 200L, npc, (Player)null);
                this.startQuestTimer("SOCIAL", 5200L, npc, (Player)null);
                break;
            }
            case "CAMERA_4": {
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 1100, 0, -3, 22000, 10000, 30000, 0, 0, 0, 0, 0));
                this.startQuestTimer("CAMERA_5", 10800L, npc, (Player)null);
                break;
            }
            case "CAMERA_5": {
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 1100, 0, -3, 300, 10000, 7000, 0, 0, 0, 0, 0));
                this.startQuestTimer("START_MOVE", 1900L, npc, (Player)null);
                break;
            }
            case "SOCIAL": {
                Antharas.zone.broadcastPacket((ServerPacket)new SocialAction(npc.getObjectId(), 2));
                break;
            }
            case "START_MOVE": {
                final NoRestartZone zone;
                final ExShowScreenMessage exShowScreenMessage;
                World.getInstance().forAnyVisibleObjectInRange((WorldObject)npc, (Class)Player.class, 4000, hero -> {
                    zone = Antharas.zone;
                    new ExShowScreenMessage(NpcStringId.S1_YOU_CANNOT_HOPE_TO_DEFEAT_ME_WITH_YOUR_MEAGER_STRENGTH, 2, 4000, new String[] { hero.getName() });
                    zone.broadcastPacket((ServerPacket)exShowScreenMessage);
                    return;
                }, Player::isHero);
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(179011, 114871, -7704) });
                this.startQuestTimer("CHECK_ATTACK", 60000L, npc, (Player)null);
                this.startQuestTimer("SPAWN_MINION", 300000L, npc, (Player)null);
                break;
            }
            case "SET_REGEN": {
                if (npc != null) {
                    if (npc.getCurrentHp() < npc.getMaxHp() * 0.25) {
                        if (!npc.isAffectedBySkill(Antharas.ANTH_REGEN_4.getSkillId())) {
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, new Object[] { Antharas.ANTH_REGEN_4.getSkill(), npc });
                        }
                    }
                    else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5) {
                        if (!npc.isAffectedBySkill(Antharas.ANTH_REGEN_3.getSkillId())) {
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, new Object[] { Antharas.ANTH_REGEN_3.getSkill(), npc });
                        }
                    }
                    else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                        if (!npc.isAffectedBySkill(Antharas.ANTH_REGEN_2.getSkillId())) {
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, new Object[] { Antharas.ANTH_REGEN_2.getSkill(), npc });
                        }
                    }
                    else if (!npc.isAffectedBySkill(Antharas.ANTH_REGEN_1.getSkillId())) {
                        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, new Object[] { Antharas.ANTH_REGEN_1.getSkill(), npc });
                    }
                    this.startQuestTimer("SET_REGEN", 60000L, npc, (Player)null);
                    break;
                }
                break;
            }
            case "CHECK_ATTACK": {
                if (npc != null && Antharas._lastAttack + 900000L < System.currentTimeMillis()) {
                    this.setStatus(0);
                    this.cancelQuestTimer("CHECK_ATTACK", npc, (Player)null);
                    this.cancelQuestTimer("SPAWN_MINION", npc, (Player)null);
                    break;
                }
                if (npc != null) {
                    if (Antharas.attacker_1_hate > 10) {
                        Antharas.attacker_1_hate -= getRandom(10);
                    }
                    if (Antharas.attacker_2_hate > 10) {
                        Antharas.attacker_2_hate -= getRandom(10);
                    }
                    if (Antharas.attacker_3_hate > 10) {
                        Antharas.attacker_3_hate -= getRandom(10);
                    }
                    this.manageSkills(npc);
                    this.startQuestTimer("CHECK_ATTACK", 60000L, npc, (Player)null);
                    break;
                }
                break;
            }
            case "SPAWN_MINION": {
                if (Antharas.minionMultipler > 1 && Antharas._minionCount < 100 - Antharas.minionMultipler * 2) {
                    for (int i = 0; i < Antharas.minionMultipler; ++i) {
                        addSpawn(29069, (IPositionable)npc, true);
                        addSpawn(29190, (IPositionable)npc, true);
                    }
                    Antharas._minionCount += Antharas.minionMultipler * 2;
                }
                else if (Antharas._minionCount < 98) {
                    addSpawn(29069, (IPositionable)npc, true);
                    addSpawn(29190, (IPositionable)npc, true);
                    Antharas._minionCount += 2;
                }
                else if (Antharas._minionCount < 99) {
                    addSpawn(getRandomBoolean() ? 29069 : 29190, (IPositionable)npc, true);
                    ++Antharas._minionCount;
                }
                if (getRandom(100) > 10 && Antharas.minionMultipler < 4) {
                    ++Antharas.minionMultipler;
                }
                this.startQuestTimer("SPAWN_MINION", 300000L, npc, (Player)null);
                break;
            }
            case "CLEAR_ZONE": {
                Antharas.zone.forEachCreature(creature -> {
                    if (GameUtils.isNpc(creature)) {
                        ((Creature)creature).deleteMe();
                    }
                    else if (GameUtils.isPlayer(creature)) {}
                    return;
                });
                break;
            }
            case "TID_USED_FEAR": {
                if (npc != null && Antharas.sandStorm == 0) {
                    Antharas.sandStorm = 1;
                    npc.disableCoreAI(true);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(177648, 114816, -7735) });
                    this.startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 2000L, npc, (Player)null);
                    this.startQuestTimer("TID_FEAR_COOLTIME", 300000L, npc, (Player)null);
                    break;
                }
                break;
            }
            case "TID_FEAR_COOLTIME": {
                Antharas.sandStorm = 0;
                break;
            }
            case "TID_FEAR_MOVE_TIMEOVER": {
                if (Antharas.sandStorm == 1 && npc.getX() == 177648 && npc.getY() == 114816) {
                    Antharas.sandStorm = 2;
                    Antharas.moveChance = 0;
                    npc.disableCoreAI(false);
                    break;
                }
                if (Antharas.sandStorm != 1) {
                    break;
                }
                if (Antharas.moveChance <= 3) {
                    ++Antharas.moveChance;
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(177648, 114816, -7735) });
                    this.startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 5000L, npc, (Player)null);
                    break;
                }
                this.startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 1000L, npc, (Player)null);
                break;
            }
            case "CLEAR_STATUS": {
                this.addBoss(this._antharas = (GrandBoss)addSpawn(29068, 185708, 114298, -8221, 0, false, 0L));
                Broadcast.toAllOnlinePlayers(new ServerPacket[] { (ServerPacket)new Earthquake(185708, 114298, -8221, 20, 10) });
                this.setStatus(0);
                break;
            }
            case "SKIP_WAITING": {
                if (this.getStatus() == 1) {
                    this.cancelQuestTimer("SPAWN_ANTHARAS", (Npc)null, (Player)null);
                    this.notifyEvent("SPAWN_ANTHARAS", (Npc)null, (Player)null);
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                    break;
                }
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                break;
            }
            case "RESPAWN_ANTHARAS": {
                if (this.getStatus() == 3) {
                    this.setRespawn(0L);
                    this.cancelQuestTimer("CLEAR_STATUS", (Npc)null, (Player)null);
                    this.notifyEvent("CLEAR_STATUS", (Npc)null, (Player)null);
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                    break;
                }
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                break;
            }
            case "DESPAWN_MINIONS": {
                if (this.getStatus() == 2) {
                    Antharas._minionCount = 0;
                    Antharas.zone.forEachCreature(Creature::deleteMe, creature -> GameUtils.isNpc((WorldObject)creature) && (creature.getId() == 29069 || creature.getId() == 29190));
                    if (player != null) {
                        player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                        break;
                    }
                    break;
                }
                else {
                    if (player != null) {
                        player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                        break;
                    }
                    break;
                }
                break;
            }
            case "ABORT_FIGHT": {
                if (this.getStatus() == 2) {
                    this.setStatus(0);
                    this.cancelQuestTimer("CHECK_ATTACK", (Npc)this._antharas, (Player)null);
                    this.cancelQuestTimer("SPAWN_MINION", (Npc)this._antharas, (Player)null);
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                    break;
                }
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                break;
            }
            case "MANAGE_SKILL": {
                this.manageSkills(npc);
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    private void oustCreatures() {
        Antharas.zone.forEachCreature(creature -> {
            if (GameUtils.isNpc(creature)) {
                if (((Creature)creature).getId() != 29068) {
                    ((Creature)creature).deleteMe();
                }
            }
            else if (GameUtils.isPlayer(creature)) {}
        });
    }
    
    public String onAggroRangeEnter(final Npc npc, final Player player, final boolean isSummon) {
        npc.doCast(Antharas.DISPEL_BOM.getSkill());
        npc.doDie((Creature)player);
        return super.onAggroRangeEnter(npc, player, isSummon);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        Antharas._lastAttack = System.currentTimeMillis();
        if (npc.getId() == 29070) {
            if (MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)attacker, 230)) {
                npc.doCast(Antharas.DISPEL_BOM.getSkill());
                npc.doDie((Creature)attacker);
            }
        }
        else if (npc.getId() == 29068) {
            if (!Antharas.zone.isCreatureInZone((Creature)attacker) || this.getStatus() != 2) {
                this.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, attacker.getName()));
            }
            if (attacker.getMountType() == MountType.STRIDER && !attacker.isAffectedBySkill(Antharas.ANTH_ANTI_STRIDER.getSkillId()) && SkillCaster.checkUseConditions((Creature)npc, Antharas.ANTH_ANTI_STRIDER.getSkill())) {
                this.addSkillCastDesire(npc, (WorldObject)attacker, Antharas.ANTH_ANTI_STRIDER.getSkill(), 100);
            }
            if (skill == null) {
                this.refreshAiParams(attacker, damage * 1000);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.25) {
                this.refreshAiParams(attacker, damage / 3 * 100);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5) {
                this.refreshAiParams(attacker, damage * 20);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                this.refreshAiParams(attacker, damage * 10);
            }
            else {
                this.refreshAiParams(attacker, damage / 3 * 20);
            }
            this.manageSkills(npc);
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (Antharas.zone.isCreatureInZone((Creature)killer)) {
            if (npc.getId() == 29068) {
                this._antharas = null;
                this.notifyEvent("DESPAWN_MINIONS", (Npc)null, (Player)null);
                Antharas.zone.broadcastPacket((ServerPacket)new SpecialCamera((Creature)npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
                Antharas.zone.broadcastPacket((ServerPacket)new PlaySound("BS01_D"));
                final long respawnTime = (Config.ANTHARAS_SPAWN_INTERVAL + getRandom(-Config.ANTHARAS_SPAWN_RANDOM, Config.ANTHARAS_SPAWN_RANDOM)) * 3600000;
                this.setRespawn(respawnTime);
                this.startQuestTimer("CLEAR_STATUS", respawnTime, (Npc)null, (Player)null);
                this.cancelQuestTimer("SET_REGEN", npc, (Player)null);
                this.cancelQuestTimer("CHECK_ATTACK", npc, (Player)null);
                this.cancelQuestTimer("SPAWN_MINION", npc, (Player)null);
                this.startQuestTimer("CLEAR_ZONE", 900000L, (Npc)null, (Player)null);
                this.setStatus(3);
            }
            else {
                --Antharas._minionCount;
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public void onMoveFinished(final Npc npc) {
        npc.doCast(Antharas.DISPEL_BOM.getSkill());
        npc.doDie((Creature)null);
    }
    
    public String onSpawn(final Npc npc) {
        if (npc.getId() == 29068) {
            ((Attackable)npc).setCanReturnToSpawnPoint(false);
            npc.setRandomWalking(false);
            this.cancelQuestTimer("SET_REGEN", npc, (Player)null);
            this.startQuestTimer("SET_REGEN", 60000L, npc, (Player)null);
        }
        else {
            for (int i = 1; i <= 6; ++i) {
                final int x = npc.getParameters().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                final int y = npc.getParameters().getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                final Attackable bomber = (Attackable)addSpawn(29070, npc.getX(), npc.getY(), npc.getZ(), 0, true, 15000L, true);
                bomber.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(x, y, npc.getZ()) });
            }
            npc.deleteMe();
        }
        return super.onSpawn(npc);
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        if (skill.getId() == Antharas.ANTH_FEAR.getSkillId() || skill.getId() == Antharas.ANTH_FEAR_SHORT.getSkillId()) {
            this.startQuestTimer("TID_USED_FEAR", 7000L, npc, (Player)null);
        }
        this.startQuestTimer("MANAGE_SKILL", 1000L, npc, (Player)null);
        return super.onSpellFinished(npc, player, skill);
    }
    
    public boolean unload(final boolean removeFromList) {
        if (this._antharas != null) {
            this._antharas.deleteMe();
            this._antharas = null;
        }
        return super.unload(removeFromList);
    }
    
    private int getStatus() {
        return GrandBossManager.getInstance().getBossStatus(29068);
    }
    
    private void addBoss(final GrandBoss grandboss) {
        GrandBossManager.getInstance().addBoss(grandboss);
    }
    
    private void setStatus(final int status) {
        GrandBossManager.getInstance().setBossStatus(29068, status);
    }
    
    private void setRespawn(final long respawnTime) {
        GrandBossManager.getInstance().getStatsSet(29068).set("respawn_time", System.currentTimeMillis() + respawnTime);
    }
    
    private void refreshAiParams(final Player attacker, final int damage) {
        if (Antharas.attacker_1 != null && attacker == Antharas.attacker_1) {
            if (Antharas.attacker_1_hate < damage + 1000) {
                Antharas.attacker_1_hate = damage + getRandom(3000);
            }
        }
        else if (Antharas.attacker_2 != null && attacker == Antharas.attacker_2) {
            if (Antharas.attacker_2_hate < damage + 1000) {
                Antharas.attacker_2_hate = damage + getRandom(3000);
            }
        }
        else if (Antharas.attacker_3 != null && attacker == Antharas.attacker_3) {
            if (Antharas.attacker_3_hate < damage + 1000) {
                Antharas.attacker_3_hate = damage + getRandom(3000);
            }
        }
        else {
            final int i1 = CommonUtil.min(Antharas.attacker_1_hate, Antharas.attacker_2_hate, new int[] { Antharas.attacker_3_hate });
            if (Antharas.attacker_1_hate == i1) {
                Antharas.attacker_1_hate = damage + getRandom(3000);
                Antharas.attacker_1 = attacker;
            }
            else if (Antharas.attacker_2_hate == i1) {
                Antharas.attacker_2_hate = damage + getRandom(3000);
                Antharas.attacker_2 = attacker;
            }
            else if (Antharas.attacker_3_hate == i1) {
                Antharas.attacker_3_hate = damage + getRandom(3000);
                Antharas.attacker_3 = attacker;
            }
        }
    }
    
    private void manageSkills(final Npc npc) {
        if (npc.isCastingNow() || npc.isCoreAIDisabled() || !npc.isInCombat()) {
            return;
        }
        int i1 = 0;
        int i2 = 0;
        Player c2 = null;
        if (Antharas.attacker_1 == null || !MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)Antharas.attacker_1, 9000) || Antharas.attacker_1.isDead()) {
            Antharas.attacker_1_hate = 0;
        }
        if (Antharas.attacker_2 == null || !MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)Antharas.attacker_2, 9000) || Antharas.attacker_2.isDead()) {
            Antharas.attacker_2_hate = 0;
        }
        if (Antharas.attacker_3 == null || !MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)Antharas.attacker_3, 9000) || Antharas.attacker_3.isDead()) {
            Antharas.attacker_3_hate = 0;
        }
        if (Antharas.attacker_1_hate > Antharas.attacker_2_hate) {
            i1 = 2;
            i2 = Antharas.attacker_1_hate;
            c2 = Antharas.attacker_1;
        }
        else if (Antharas.attacker_2_hate > 0) {
            i1 = 3;
            i2 = Antharas.attacker_2_hate;
            c2 = Antharas.attacker_2;
        }
        if (Antharas.attacker_3_hate > i2) {
            i1 = 4;
            i2 = Antharas.attacker_3_hate;
            c2 = Antharas.attacker_3;
        }
        if (i2 > 0) {
            if (getRandom(100) < 70) {
                switch (i1) {
                    case 2: {
                        Antharas.attacker_1_hate = 500;
                        break;
                    }
                    case 3: {
                        Antharas.attacker_2_hate = 500;
                        break;
                    }
                    case 4: {
                        Antharas.attacker_3_hate = 500;
                        break;
                    }
                }
            }
            final double distance_c2 = MathUtil.calculateDistance3D((ILocational)npc, (ILocational)c2);
            final double direction_c2 = npc.calculateDirectionTo((ILocational)c2);
            boolean castOnTarget = false;
            SkillHolder skillToCast;
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.25) {
                if (getRandom(100) < 30) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_MOUTH;
                }
                else if (getRandom(100) < 80 && ((distance_c2 < 1423.0 && direction_c2 < 188.0 && direction_c2 > 172.0) || (distance_c2 < 802.0 && direction_c2 < 194.0 && direction_c2 > 166.0))) {
                    skillToCast = Antharas.ANTH_TAIL;
                }
                else if (getRandom(100) < 40 && ((distance_c2 < 850.0 && direction_c2 < 210.0 && direction_c2 > 150.0) || (distance_c2 < 425.0 && direction_c2 < 270.0 && direction_c2 > 90.0))) {
                    skillToCast = Antharas.ANTH_DEBUFF;
                }
                else if (getRandom(100) < 10 && distance_c2 < 1100.0) {
                    skillToCast = Antharas.ANTH_JUMP;
                }
                else if (getRandom(100) < 10) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_METEOR;
                }
                else if (getRandom(100) < 6) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_BREATH;
                }
                else if (getRandomBoolean()) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK_EX;
                }
                else if (getRandom(100) < 5) {
                    castOnTarget = true;
                    skillToCast = (getRandomBoolean() ? Antharas.ANTH_FEAR : Antharas.ANTH_FEAR_SHORT);
                }
                else {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK;
                }
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5) {
                if (getRandom(100) < 80 && ((distance_c2 < 1423.0 && direction_c2 < 188.0 && direction_c2 > 172.0) || (distance_c2 < 802.0 && direction_c2 < 194.0 && direction_c2 > 166.0))) {
                    skillToCast = Antharas.ANTH_TAIL;
                }
                else if (getRandom(100) < 40 && ((distance_c2 < 850.0 && direction_c2 < 210.0 && direction_c2 > 150.0) || (distance_c2 < 425.0 && direction_c2 < 270.0 && direction_c2 > 90.0))) {
                    skillToCast = Antharas.ANTH_DEBUFF;
                }
                else if (getRandom(100) < 10 && distance_c2 < 1100.0) {
                    skillToCast = Antharas.ANTH_JUMP;
                }
                else if (getRandom(100) < 7) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_METEOR;
                }
                else if (getRandom(100) < 6) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_BREATH;
                }
                else if (getRandomBoolean()) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK_EX;
                }
                else if (getRandom(100) < 5) {
                    castOnTarget = true;
                    skillToCast = (getRandomBoolean() ? Antharas.ANTH_FEAR : Antharas.ANTH_FEAR_SHORT);
                }
                else {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK;
                }
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                if (getRandom(100) < 80 && ((distance_c2 < 1423.0 && direction_c2 < 188.0 && direction_c2 > 172.0) || (distance_c2 < 802.0 && direction_c2 < 194.0 && direction_c2 > 166.0))) {
                    skillToCast = Antharas.ANTH_TAIL;
                }
                else if (getRandom(100) < 10 && distance_c2 < 1100.0) {
                    skillToCast = Antharas.ANTH_JUMP;
                }
                else if (getRandom(100) < 5) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_METEOR;
                }
                else if (getRandom(100) < 6) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_BREATH;
                }
                else if (getRandomBoolean()) {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK_EX;
                }
                else if (getRandom(100) < 5) {
                    castOnTarget = true;
                    skillToCast = (getRandomBoolean() ? Antharas.ANTH_FEAR : Antharas.ANTH_FEAR_SHORT);
                }
                else {
                    castOnTarget = true;
                    skillToCast = Antharas.ANTH_NORM_ATTACK;
                }
            }
            else if (getRandom(100) < 80 && ((distance_c2 < 1423.0 && direction_c2 < 188.0 && direction_c2 > 172.0) || (distance_c2 < 802.0 && direction_c2 < 194.0 && direction_c2 > 166.0))) {
                skillToCast = Antharas.ANTH_TAIL;
            }
            else if (getRandom(100) < 3) {
                castOnTarget = true;
                skillToCast = Antharas.ANTH_METEOR;
            }
            else if (getRandom(100) < 6) {
                castOnTarget = true;
                skillToCast = Antharas.ANTH_BREATH;
            }
            else if (getRandomBoolean()) {
                castOnTarget = true;
                skillToCast = Antharas.ANTH_NORM_ATTACK_EX;
            }
            else if (getRandom(100) < 5) {
                castOnTarget = true;
                skillToCast = (getRandomBoolean() ? Antharas.ANTH_FEAR : Antharas.ANTH_FEAR_SHORT);
            }
            else {
                castOnTarget = true;
                skillToCast = Antharas.ANTH_NORM_ATTACK;
            }
            if (skillToCast != null && SkillCaster.checkUseConditions((Creature)npc, skillToCast.getSkill())) {
                if (castOnTarget) {
                    this.addSkillCastDesire(npc, (WorldObject)c2, skillToCast.getSkill(), 100);
                }
                else {
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, new Object[] { skillToCast.getSkill(), npc });
                }
            }
        }
    }
    
    public static AbstractNpcAI provider() {
        return new Antharas();
    }
    
    static {
        ANTH_JUMP = new SkillHolder(4106, 1);
        ANTH_TAIL = new SkillHolder(4107, 1);
        ANTH_FEAR = new SkillHolder(4108, 1);
        ANTH_DEBUFF = new SkillHolder(4109, 1);
        ANTH_MOUTH = new SkillHolder(4110, 2);
        ANTH_BREATH = new SkillHolder(4111, 1);
        ANTH_NORM_ATTACK = new SkillHolder(4112, 1);
        ANTH_NORM_ATTACK_EX = new SkillHolder(4113, 1);
        ANTH_REGEN_1 = new SkillHolder(4125, 1);
        ANTH_REGEN_2 = new SkillHolder(4239, 1);
        ANTH_REGEN_3 = new SkillHolder(4240, 1);
        ANTH_REGEN_4 = new SkillHolder(4241, 1);
        DISPEL_BOM = new SkillHolder(5042, 1);
        ANTH_ANTI_STRIDER = new SkillHolder(4258, 1);
        ANTH_FEAR_SHORT = new SkillHolder(5092, 1);
        ANTH_METEOR = new SkillHolder(5093, 1);
        zone = (NoRestartZone)ZoneManager.getInstance().getZoneById(70050, (Class)NoRestartZone.class);
        Antharas._lastAttack = 0L;
        Antharas._minionCount = 0;
        Antharas.minionMultipler = 0;
        Antharas.moveChance = 0;
        Antharas.sandStorm = 0;
        Antharas.attacker_1 = null;
        Antharas.attacker_2 = null;
        Antharas.attacker_3 = null;
        Antharas.attacker_1_hate = 0;
        Antharas.attacker_2_hate = 0;
        Antharas.attacker_3_hate = 0;
    }
}
