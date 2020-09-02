// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.bosses.Baium;

import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.variables.NpcVariables;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.Earthquake;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.type.NoRestartZone;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Baium extends AbstractNpcAI
{
    private static final int BAIUM = 29020;
    private static final int BAIUM_STONE = 29025;
    private static final int ANG_VORTEX = 31862;
    private static final int ARCHANGEL = 29021;
    private static final int TELE_CUBE = 31842;
    private static final SkillHolder BAIUM_ATTACK;
    private static final SkillHolder ENERGY_WAVE;
    private static final SkillHolder EARTH_QUAKE;
    private static final SkillHolder THUNDERBOLT;
    private static final SkillHolder GROUP_HOLD;
    private static final SkillHolder SPEAR_ATTACK;
    private static final SkillHolder ANGEL_HEAL;
    private static final SkillHolder HEAL_OF_BAIUM;
    private static final SkillHolder BAIUM_PRESENT;
    private static final SkillHolder ANTI_STRIDER;
    private static final int FABRIC = 4295;
    private static final NoRestartZone zone;
    private static final int ALIVE = 0;
    private static final int WAITING = 1;
    private static final int IN_FIGHT = 2;
    private static final int DEAD = 3;
    private static final Location BAIUM_GIFT_LOC;
    private static final Location BAIUM_LOC;
    private static final Location TELEPORT_CUBIC_LOC;
    private static final Location TELEPORT_IN_LOC;
    private static final Location[] TELEPORT_OUT_LOC;
    private static final Location[] ARCHANGEL_LOC;
    private GrandBoss _baium;
    private static long _lastAttack;
    private static Player _standbyPlayer;
    
    private Baium() {
        this._baium = null;
        this.addFirstTalkId(31862);
        this.addTalkId(new int[] { 31862, 31842, 29025 });
        this.addStartNpc(new int[] { 31862, 31842, 29025 });
        this.addAttackId(new int[] { 29020, 29021 });
        this.addKillId(29020);
        this.addSeeCreatureId(new int[] { 29020 });
        this.addSpellFinishedId(new int[] { 29020 });
        final StatsSet info = GrandBossManager.getInstance().getStatsSet(29020);
        switch (this.getStatus()) {
            case 1: {
                this.setStatus(0);
            }
            case 0: {
                addSpawn(29025, (IPositionable)Baium.BAIUM_LOC, false, 0L);
                break;
            }
            case 2: {
                final double curr_hp = info.getDouble("currentHP");
                final double curr_mp = info.getDouble("currentMP");
                final int loc_x = info.getInt("loc_x");
                final int loc_y = info.getInt("loc_y");
                final int loc_z = info.getInt("loc_z");
                final int heading = info.getInt("heading");
                (this._baium = (GrandBoss)addSpawn(29020, loc_x, loc_y, loc_z, heading, false, 0L)).setCurrentHpMp(curr_hp, curr_mp);
                Baium._lastAttack = System.currentTimeMillis();
                this.addBoss(this._baium);
                for (final Location loc : Baium.ARCHANGEL_LOC) {
                    final Npc archangel = addSpawn(29021, (IPositionable)loc, false, 0L, true);
                    this.startQuestTimer("SELECT_TARGET", 5000L, archangel, (Player)null);
                }
                this.startQuestTimer("CHECK_ATTACK", 60000L, (Npc)this._baium, (Player)null);
                break;
            }
            case 3: {
                final long remain = info.getLong("respawn_time") - System.currentTimeMillis();
                if (remain > 0L) {
                    this.startQuestTimer("CLEAR_STATUS", remain, (Npc)null, (Player)null);
                    break;
                }
                this.notifyEvent("CLEAR_STATUS", (Npc)null, (Player)null);
                break;
            }
        }
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "31862-04.html": {
                return event;
            }
            case "enter": {
                String htmltext = null;
                if (this.getStatus() == 3) {
                    htmltext = "31862-03.html";
                }
                else if (this.getStatus() == 2) {
                    htmltext = "31862-02.html";
                }
                else if (!hasQuestItems(player, 4295)) {
                    htmltext = "31862-01.html";
                }
                else {
                    takeItems(player, 4295, 1L);
                    player.teleToLocation((ILocational)Baium.TELEPORT_IN_LOC);
                }
                return htmltext;
            }
            case "teleportOut": {
                final Location destination = Baium.TELEPORT_OUT_LOC[getRandom(Baium.TELEPORT_OUT_LOC.length)];
                player.teleToLocation(destination.getX() + getRandom(100), destination.getY() + getRandom(100), destination.getZ());
                break;
            }
            case "wakeUp": {
                if (this.getStatus() == 0) {
                    this.setStatus(2);
                    (this._baium = (GrandBoss)addSpawn(29020, (IPositionable)Baium.BAIUM_LOC, false, 0L)).disableCoreAI(true);
                    this.addBoss(this._baium);
                    Baium._lastAttack = System.currentTimeMillis();
                    this.startQuestTimer("WAKEUP_ACTION", 50L, (Npc)this._baium, (Player)null);
                    this.startQuestTimer("MANAGE_EARTHQUAKE", 2000L, (Npc)this._baium, player);
                    this.startQuestTimer("CHECK_ATTACK", 60000L, (Npc)this._baium, (Player)null);
                    npc.deleteMe();
                    break;
                }
                break;
            }
            case "WAKEUP_ACTION": {
                if (npc != null) {
                    Baium.zone.broadcastPacket((ServerPacket)new SocialAction(this._baium.getObjectId(), 2));
                    break;
                }
                break;
            }
            case "MANAGE_EARTHQUAKE": {
                if (npc != null) {
                    Baium.zone.broadcastPacket((ServerPacket)new Earthquake(npc.getX(), npc.getY(), npc.getZ(), 40, 10));
                    Baium.zone.broadcastPacket((ServerPacket)new PlaySound("BS02_A"));
                    this.startQuestTimer("SOCIAL_ACTION", 8000L, npc, player);
                    break;
                }
                break;
            }
            case "SOCIAL_ACTION": {
                if (npc != null) {
                    Baium.zone.broadcastPacket((ServerPacket)new SocialAction(npc.getObjectId(), 3));
                    this.startQuestTimer("PLAYER_PORT", 6000L, npc, player);
                    break;
                }
                break;
            }
            case "PLAYER_PORT": {
                if (npc == null) {
                    break;
                }
                if (player != null && MathUtil.isInsideRadius3D((ILocational)player, (ILocational)npc, 16000)) {
                    player.teleToLocation((ILocational)Baium.BAIUM_GIFT_LOC);
                    this.startQuestTimer("PLAYER_KILL", 3000L, npc, player);
                    break;
                }
                if (Baium._standbyPlayer != null && MathUtil.isInsideRadius3D((ILocational)Baium._standbyPlayer, (ILocational)npc, 16000)) {
                    Baium._standbyPlayer.teleToLocation((ILocational)Baium.BAIUM_GIFT_LOC);
                    this.startQuestTimer("PLAYER_KILL", 3000L, npc, Baium._standbyPlayer);
                    break;
                }
                break;
            }
            case "PLAYER_KILL": {
                if (player != null && MathUtil.isInsideRadius3D((ILocational)player, (ILocational)npc, 16000)) {
                    Baium.zone.broadcastPacket((ServerPacket)new SocialAction(npc.getObjectId(), 1));
                    npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_DARE_YOU_WAKE_ME_NOW_YOU_SHALL_DIE, new String[0]);
                    npc.setTarget((WorldObject)player);
                    npc.doCast(Baium.BAIUM_PRESENT.getSkill());
                }
                final NoRestartZone zone;
                final ExShowScreenMessage exShowScreenMessage;
                Baium.zone.forAnyCreature(creature -> {
                    zone = Baium.zone;
                    new ExShowScreenMessage(NpcStringId.NOT_EVEN_THE_GODS_THEMSELVES_COULD_TOUCH_ME_BUT_YOU_S1_YOU_DARE_CHALLENGE_ME_IGNORANT_MORTAL, 2, 4000, new String[] { creature.getName() });
                    zone.broadcastPacket((ServerPacket)exShowScreenMessage);
                    return;
                }, creature -> GameUtils.isPlayer((WorldObject)creature) && creature.isHero());
                this.startQuestTimer("SPAWN_ARCHANGEL", 8000L, npc, (Player)null);
                break;
            }
            case "SPAWN_ARCHANGEL": {
                this._baium.disableCoreAI(false);
                for (final Location loc : Baium.ARCHANGEL_LOC) {
                    final Npc archangel = addSpawn(29021, (IPositionable)loc, false, 0L, true);
                    this.startQuestTimer("SELECT_TARGET", 5000L, archangel, (Player)null);
                }
                if (player != null && !player.isDead()) {
                    this.addAttackPlayerDesire(npc, (Playable)player);
                    break;
                }
                if (Baium._standbyPlayer != null && !Baium._standbyPlayer.isDead()) {
                    this.addAttackPlayerDesire(npc, (Playable)Baium._standbyPlayer);
                    break;
                }
                World.getInstance().forAnyVisibleObjectInRange((WorldObject)npc, (Class)Player.class, 2000, visiblePlayer -> this.addAttackPlayerDesire(npc, visiblePlayer), visiblePlayer -> Baium.zone.isInsideZone((WorldObject)visiblePlayer) && !visiblePlayer.isDead());
                break;
            }
            case "SELECT_TARGET": {
                if (npc == null) {
                    break;
                }
                final Attackable mob = (Attackable)npc;
                final Creature mostHated = mob.getMostHated();
                if (this._baium == null || this._baium.isDead()) {
                    mob.deleteMe();
                    break;
                }
                if (GameUtils.isPlayer((WorldObject)mostHated) && Baium.zone.isInsideZone((WorldObject)mostHated)) {
                    if (mob.getTarget() != mostHated) {
                        mob.clearAggroList();
                    }
                    this.addAttackPlayerDesire((Npc)mob, (Playable)mostHated);
                }
                else {
                    final Npc npc2;
                    final boolean found = World.getInstance().checkAnyVisibleObjectInRange((WorldObject)mob, (Class)Playable.class, 1000, playable -> {
                        if (Baium.zone.isInsideZone((WorldObject)playable) && !playable.isDead()) {
                            if (!playable.equals((Object)((Attackable)npc2).getTarget())) {
                                ((Attackable)npc2).clearAggroList();
                            }
                            this.addAttackPlayerDesire(npc2, playable);
                            return true;
                        }
                        else {
                            return false;
                        }
                    });
                    if (!found) {
                        if (MathUtil.isInsideRadius3D((ILocational)mob, (ILocational)this._baium, 40)) {
                            if (mob.getTarget() != this._baium) {
                                mob.clearAggroList();
                            }
                            mob.setRunning();
                            mob.addDamageHate((Creature)this._baium, 0, 999);
                            mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { this._baium });
                        }
                        else {
                            mob.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, new Object[] { this._baium });
                        }
                    }
                }
                this.startQuestTimer("SELECT_TARGET", 5000L, npc, (Player)null);
                break;
            }
            case "CHECK_ATTACK": {
                if (npc != null && Baium._lastAttack + 1800000L < System.currentTimeMillis()) {
                    this.notifyEvent("CLEAR_ZONE", (Npc)null, (Player)null);
                    addSpawn(29025, (IPositionable)Baium.BAIUM_LOC, false, 0L);
                    this.setStatus(0);
                    break;
                }
                if (npc != null) {
                    if (Baium._lastAttack + 300000L < System.currentTimeMillis() && npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                        npc.setTarget((WorldObject)npc);
                        npc.doCast(Baium.HEAL_OF_BAIUM.getSkill());
                    }
                    this.startQuestTimer("CHECK_ATTACK", 60000L, npc, (Player)null);
                    break;
                }
                break;
            }
            case "CLEAR_STATUS": {
                this.setStatus(0);
                addSpawn(29025, (IPositionable)Baium.BAIUM_LOC, false, 0L);
                break;
            }
            case "CLEAR_ZONE": {
                Baium.zone.forEachCreature(creature -> {
                    if (GameUtils.isNpc((WorldObject)creature)) {
                        ((Creature)creature).deleteMe();
                    }
                    else if (GameUtils.isPlayer((WorldObject)creature)) {
                        this.notifyEvent("teleportOut", (Npc)null, (Player)creature);
                    }
                    return;
                });
                break;
            }
            case "RESPAWN_BAIUM": {
                if (this.getStatus() == 3) {
                    this.setRespawn(0L);
                    this.cancelQuestTimer("CLEAR_STATUS", (Npc)null, (Player)null);
                    this.notifyEvent("CLEAR_STATUS", (Npc)null, (Player)null);
                    break;
                }
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                break;
            }
            case "ABORT_FIGHT": {
                if (this.getStatus() == 2) {
                    this._baium = null;
                    this.notifyEvent("CLEAR_ZONE", (Npc)null, (Player)null);
                    this.notifyEvent("CLEAR_STATUS", (Npc)null, (Player)null);
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                }
                else {
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                }
                this.cancelQuestTimers("CHECK_ATTACK");
                this.cancelQuestTimers("SELECT_TARGET");
                break;
            }
            case "DESPAWN_MINIONS": {
                if (this.getStatus() == 2) {
                    Baium.zone.forEachCreature(Creature::deleteMe, creature -> GameUtils.isNpc((WorldObject)creature) && creature.getId() == 29021);
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
            case "MANAGE_SKILLS": {
                if (npc != null) {
                    this.manageSkills(npc);
                    break;
                }
                break;
            }
            default: {
                throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event));
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        Baium._lastAttack = System.currentTimeMillis();
        if (npc.getId() == 29020) {
            if (attacker.getMountType() == MountType.STRIDER && !attacker.isAffectedBySkill(Baium.ANTI_STRIDER.getSkillId()) && !npc.isSkillDisabled(Baium.ANTI_STRIDER.getSkill())) {
                npc.setTarget((WorldObject)attacker);
                npc.doCast(Baium.ANTI_STRIDER.getSkill());
            }
            if (skill == null) {
                this.refreshAiParams((Creature)attacker, npc, damage * 1000);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.25) {
                this.refreshAiParams((Creature)attacker, npc, damage / 3 * 100);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5) {
                this.refreshAiParams((Creature)attacker, npc, damage * 20);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                this.refreshAiParams((Creature)attacker, npc, damage * 10);
            }
            else {
                this.refreshAiParams((Creature)attacker, npc, damage / 3 * 20);
            }
            this.manageSkills(npc);
        }
        else {
            final Attackable mob = (Attackable)npc;
            final Creature mostHated = mob.getMostHated();
            if (getRandom(100) < 10 && SkillCaster.checkUseConditions((Creature)mob, Baium.SPEAR_ATTACK.getSkill())) {
                if (mostHated != null && MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)mostHated, 1000) && Baium.zone.isCreatureInZone(mostHated)) {
                    mob.setTarget((WorldObject)mostHated);
                    mob.doCast(Baium.SPEAR_ATTACK.getSkill());
                }
                else if (Baium.zone.isCreatureInZone((Creature)attacker)) {
                    mob.setTarget((WorldObject)attacker);
                    mob.doCast(Baium.SPEAR_ATTACK.getSkill());
                }
            }
            if (getRandom(100) < 5 && npc.getCurrentHp() < npc.getMaxHp() * 0.5 && SkillCaster.checkUseConditions((Creature)mob, Baium.ANGEL_HEAL.getSkill())) {
                npc.setTarget((WorldObject)npc);
                npc.doCast(Baium.ANGEL_HEAL.getSkill());
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (Baium.zone.isCreatureInZone((Creature)killer)) {
            this.setStatus(3);
            addSpawn(31842, (IPositionable)Baium.TELEPORT_CUBIC_LOC, false, 900000L);
            Baium.zone.broadcastPacket((ServerPacket)new PlaySound("BS01_D"));
            final long respawnTime = Config.BAIUM_SPAWN_INTERVAL * 3600000;
            this.setRespawn(respawnTime);
            this.startQuestTimer("CLEAR_STATUS", respawnTime, (Npc)null, (Player)null);
            this.startQuestTimer("CLEAR_ZONE", 900000L, (Npc)null, (Player)null);
            this.cancelQuestTimer("CHECK_ATTACK", npc, (Player)null);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onSeeCreature(final Npc npc, final Creature creature, final boolean isSummon) {
        if (!Baium.zone.isInsideZone((WorldObject)creature) || (GameUtils.isNpc((WorldObject)creature) && creature.getId() == 29025)) {
            return super.onSeeCreature(npc, creature, isSummon);
        }
        if (GameUtils.isPlayer((WorldObject)creature) && !creature.isDead() && Baium._standbyPlayer == null) {
            Baium._standbyPlayer = (Player)creature;
        }
        if (creature.isInCategory(CategoryType.CLERIC_GROUP)) {
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.25) {
                this.refreshAiParams(creature, npc, 10000);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5) {
                this.refreshAiParams(creature, npc, 10000, 6000);
            }
            else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75) {
                this.refreshAiParams(creature, npc, 10000, 3000);
            }
            else {
                this.refreshAiParams(creature, npc, 10000, 2000);
            }
        }
        else {
            this.refreshAiParams(creature, npc, 10000, 1000);
        }
        this.manageSkills(npc);
        return super.onSeeCreature(npc, creature, isSummon);
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        this.startQuestTimer("MANAGE_SKILLS", 1000L, npc, (Player)null);
        if (!Baium.zone.isCreatureInZone((Creature)npc) && this._baium != null) {
            this._baium.teleToLocation((ILocational)Baium.BAIUM_LOC);
        }
        return super.onSpellFinished(npc, player, skill);
    }
    
    public boolean unload(final boolean removeFromList) {
        if (this._baium != null) {
            this._baium.deleteMe();
        }
        return super.unload(removeFromList);
    }
    
    private void refreshAiParams(final Creature attacker, final Npc npc, final int damage) {
        this.refreshAiParams(attacker, npc, damage, damage);
    }
    
    private void refreshAiParams(final Creature attacker, final Npc npc, final int damage, final int aggro) {
        final int newAggroVal = damage + getRandom(3000);
        final int aggroVal = aggro + 1000;
        final NpcVariables vars = npc.getVariables();
        for (int i = 0; i < 3; ++i) {
            if (attacker == vars.getObject(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), (Class)Creature.class)) {
                if (vars.getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i)) < aggroVal) {
                    vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), newAggroVal);
                }
                return;
            }
        }
        final int index = CommonUtil.getIndexOfMinValue(new int[] { vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2") });
        vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), newAggroVal);
        vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), (Object)attacker);
    }
    
    private int getStatus() {
        return GrandBossManager.getInstance().getBossStatus(29020);
    }
    
    private void addBoss(final GrandBoss grandboss) {
        GrandBossManager.getInstance().addBoss(grandboss);
    }
    
    private void setStatus(final int status) {
        GrandBossManager.getInstance().setBossStatus(29020, status);
    }
    
    private void setRespawn(final long respawnTime) {
        GrandBossManager.getInstance().getStatsSet(29020).set("respawn_time", System.currentTimeMillis() + respawnTime);
    }
    
    private void manageSkills(final Npc npc) {
        if (npc.isCastingNow(SkillCaster::isAnyNormalType) || npc.isCoreAIDisabled() || !npc.isInCombat()) {
            return;
        }
        final NpcVariables vars = npc.getVariables();
        for (int i = 0; i < 3; ++i) {
            final Creature attacker = (Creature)vars.getObject(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), (Class)Creature.class);
            if (attacker == null || !MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)attacker, 9000) || attacker.isDead()) {
                vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), 0);
            }
        }
        final int index = CommonUtil.getIndexOfMaxValue(new int[] { vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2") });
        final Creature player = (Creature)vars.getObject(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), (Class)Creature.class);
        final int i2 = vars.getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index));
        if (i2 > 0 && getRandom(100) < 70) {
            vars.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), 500);
        }
        SkillHolder skillToCast = null;
        if (player != null && !player.isDead()) {
            if (npc.getCurrentHp() > npc.getMaxHp() * 0.75) {
                if (getRandom(100) < 10) {
                    skillToCast = Baium.ENERGY_WAVE;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.EARTH_QUAKE;
                }
                else {
                    skillToCast = Baium.BAIUM_ATTACK;
                }
            }
            else if (npc.getCurrentHp() > npc.getMaxHp() * 0.5) {
                if (getRandom(100) < 10) {
                    skillToCast = Baium.GROUP_HOLD;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.ENERGY_WAVE;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.EARTH_QUAKE;
                }
                else {
                    skillToCast = Baium.BAIUM_ATTACK;
                }
            }
            else if (npc.getCurrentHp() > npc.getMaxHp() * 0.25) {
                if (getRandom(100) < 10) {
                    skillToCast = Baium.THUNDERBOLT;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.GROUP_HOLD;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.ENERGY_WAVE;
                }
                else if (getRandom(100) < 10) {
                    skillToCast = Baium.EARTH_QUAKE;
                }
                else {
                    skillToCast = Baium.BAIUM_ATTACK;
                }
            }
            else if (getRandom(100) < 10) {
                skillToCast = Baium.THUNDERBOLT;
            }
            else if (getRandom(100) < 10) {
                skillToCast = Baium.GROUP_HOLD;
            }
            else if (getRandom(100) < 10) {
                skillToCast = Baium.ENERGY_WAVE;
            }
            else if (getRandom(100) < 10) {
                skillToCast = Baium.EARTH_QUAKE;
            }
            else {
                skillToCast = Baium.BAIUM_ATTACK;
            }
        }
        if (skillToCast != null && SkillCaster.checkUseConditions((Creature)npc, skillToCast.getSkill())) {
            npc.setTarget((WorldObject)player);
            npc.doCast(skillToCast.getSkill());
        }
    }
    
    public static AbstractNpcAI provider() {
        return new Baium();
    }
    
    static {
        BAIUM_ATTACK = new SkillHolder(4127, 1);
        ENERGY_WAVE = new SkillHolder(4128, 1);
        EARTH_QUAKE = new SkillHolder(4129, 1);
        THUNDERBOLT = new SkillHolder(4130, 1);
        GROUP_HOLD = new SkillHolder(4131, 1);
        SPEAR_ATTACK = new SkillHolder(4132, 1);
        ANGEL_HEAL = new SkillHolder(4133, 1);
        HEAL_OF_BAIUM = new SkillHolder(4135, 1);
        BAIUM_PRESENT = new SkillHolder(4136, 1);
        ANTI_STRIDER = new SkillHolder(4258, 1);
        zone = (NoRestartZone)ZoneManager.getInstance().getZoneById(70051, (Class)NoRestartZone.class);
        BAIUM_GIFT_LOC = new Location(115910, 17337, 10105);
        BAIUM_LOC = new Location(116033, 17447, 10107, -25348);
        TELEPORT_CUBIC_LOC = new Location(115017, 15549, 10090);
        TELEPORT_IN_LOC = new Location(114077, 15882, 10078);
        TELEPORT_OUT_LOC = new Location[] { new Location(108784, 16000, -4928), new Location(113824, 10448, -5164), new Location(115488, 22096, -5168) };
        ARCHANGEL_LOC = new Location[] { new Location(115792, 16608, 10136, 0), new Location(115168, 17200, 10136, 0), new Location(115780, 15564, 10136, 13620), new Location(114880, 16236, 10136, 5400), new Location(114239, 17168, 10136, -1992) };
        Baium._lastAttack = 0L;
        Baium._standbyPlayer = null;
    }
}
