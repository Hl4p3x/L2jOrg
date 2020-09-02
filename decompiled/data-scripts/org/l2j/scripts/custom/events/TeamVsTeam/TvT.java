// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.events.TeamVsTeam;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.quest.QuestTimer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.ExPVPMatchCCRecord;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.enums.Team;
import java.util.Collections;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.Event;

public class TvT extends Event
{
    private static final int MANAGER = 70010;
    private static final SkillHolder[] FIGHTER_BUFFS;
    private static final SkillHolder[] MAGE_BUFFS;
    private static final SkillHolder GHOST_WALKING;
    private static final int INSTANCE_ID = 3049;
    private static final int BLUE_DOOR_ID = 24190002;
    private static final int RED_DOOR_ID = 24190003;
    private static final Location MANAGER_SPAWN_LOC;
    private static final Location BLUE_BUFFER_SPAWN_LOC;
    private static final Location RED_BUFFER_SPAWN_LOC;
    private static final Location BLUE_SPAWN_LOC;
    private static final Location RED_SPAWN_LOC;
    private static final Zone BLUE_PEACE_ZONE;
    private static final Zone RED_PEACE_ZONE;
    private static final int REGISTRATION_TIME = 10;
    private static final int WAIT_TIME = 1;
    private static final int FIGHT_TIME = 20;
    private static final int INACTIVITY_TIME = 2;
    private static final int MINIMUM_PARTICIPANT_LEVEL = 76;
    private static final int MAXIMUM_PARTICIPANT_LEVEL = 200;
    private static final int MINIMUM_PARTICIPANT_COUNT = 4;
    private static final int MAXIMUM_PARTICIPANT_COUNT = 24;
    private static final int PARTY_MEMBER_COUNT = 7;
    private static final ItemHolder REWARD;
    private static final Map<Player, Integer> PLAYER_SCORES;
    private static final List<Player> PLAYER_LIST;
    private static final List<Player> BLUE_TEAM;
    private static final List<Player> RED_TEAM;
    private static volatile int BLUE_SCORE;
    private static volatile int RED_SCORE;
    private static Instance PVP_WORLD;
    private static Npc MANAGER_NPC_INSTANCE;
    private static boolean EVENT_ACTIVE;
    
    private TvT() {
        this.addTalkId(70010);
        this.addFirstTalkId(70010);
        this.addExitZoneId(new int[] { TvT.BLUE_PEACE_ZONE.getId(), TvT.RED_PEACE_ZONE.getId() });
        this.addEnterZoneId(new int[] { TvT.BLUE_PEACE_ZONE.getId(), TvT.RED_PEACE_ZONE.getId() });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (!TvT.EVENT_ACTIVE) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "Participate": {
                if (this.canRegister(player)) {
                    TvT.PLAYER_LIST.add(player);
                    TvT.PLAYER_SCORES.put(player, 0);
                    player.setOnCustomEvent(true);
                    this.addLogoutListener(player);
                    htmltext = "registration-success.html";
                    break;
                }
                htmltext = "registration-failed.html";
                break;
            }
            case "CancelParticipation": {
                TvT.PLAYER_LIST.remove(player);
                TvT.PLAYER_SCORES.remove(player);
                this.removeListeners(player);
                player.setOnCustomEvent(false);
                htmltext = "registration-canceled.html";
                break;
            }
            case "BuffHeal": {
                if (!player.isOnCustomEvent() && !player.isGM()) {
                    break;
                }
                if (player.isInCombat()) {
                    htmltext = "manager-combat.html";
                    break;
                }
                if (player.isMageClass()) {
                    for (final SkillHolder skill : TvT.MAGE_BUFFS) {
                        SkillCaster.triggerCast((Creature)npc, (Creature)player, skill.getSkill());
                    }
                }
                else {
                    for (final SkillHolder skill : TvT.FIGHTER_BUFFS) {
                        SkillCaster.triggerCast((Creature)npc, (Creature)player, skill.getSkill());
                    }
                }
                player.setCurrentHp((double)player.getMaxHp());
                player.setCurrentMp((double)player.getMaxMp());
                player.setCurrentCp((double)player.getMaxCp());
                break;
            }
            case "TeleportToArena": {
                for (final Player participant : TvT.PLAYER_LIST) {
                    if (participant == null || participant.isOnlineInt() != 1) {
                        TvT.PLAYER_LIST.remove(participant);
                        TvT.PLAYER_SCORES.remove(participant);
                    }
                }
                if (TvT.PLAYER_LIST.size() < 4) {
                    Broadcast.toAllOnlinePlayers("TvT Event: Event was canceled, not enough participants.");
                    for (final Player participant : TvT.PLAYER_LIST) {
                        this.removeListeners(participant);
                        participant.setOnCustomEvent(false);
                    }
                    TvT.EVENT_ACTIVE = false;
                    return null;
                }
                final InstanceManager manager = InstanceManager.getInstance();
                final InstanceTemplate template = manager.getInstanceTemplate(3049);
                TvT.PVP_WORLD = manager.createInstance(template, (Player)null);
                Collections.shuffle(TvT.PLAYER_LIST);
                boolean team = getRandomBoolean();
                for (final Player participant2 : TvT.PLAYER_LIST) {
                    if (team) {
                        TvT.BLUE_TEAM.add(participant2);
                        TvT.PVP_WORLD.addAllowed(participant2);
                        participant2.leaveParty();
                        participant2.setTeam(Team.BLUE);
                        participant2.teleToLocation((ILocational)TvT.BLUE_SPAWN_LOC, TvT.PVP_WORLD);
                        team = false;
                    }
                    else {
                        TvT.RED_TEAM.add(participant2);
                        TvT.PVP_WORLD.addAllowed(participant2);
                        participant2.leaveParty();
                        participant2.setTeam(Team.RED);
                        participant2.teleToLocation((ILocational)TvT.RED_SPAWN_LOC, TvT.PVP_WORLD);
                        team = true;
                    }
                    this.addDeathListener(participant2);
                }
                if (TvT.BLUE_TEAM.size() > 1) {
                    CommandChannel blueCC = null;
                    Party lastBlueParty = null;
                    int blueParticipantCounter = 0;
                    for (final Player participant3 : TvT.BLUE_TEAM) {
                        if (++blueParticipantCounter == 1) {
                            lastBlueParty = new Party(participant3, PartyDistributionType.FINDERS_KEEPERS);
                            participant3.joinParty(lastBlueParty);
                            if (TvT.BLUE_TEAM.size() > 7) {
                                if (blueCC == null) {
                                    blueCC = new CommandChannel(participant3);
                                }
                                else {
                                    blueCC.addParty(lastBlueParty);
                                }
                            }
                        }
                        else {
                            participant3.joinParty(lastBlueParty);
                        }
                        if (blueParticipantCounter == 7) {
                            blueParticipantCounter = 0;
                        }
                    }
                }
                if (TvT.RED_TEAM.size() > 1) {
                    CommandChannel redCC = null;
                    Party lastRedParty = null;
                    int redParticipantCounter = 0;
                    for (final Player participant3 : TvT.RED_TEAM) {
                        if (++redParticipantCounter == 1) {
                            lastRedParty = new Party(participant3, PartyDistributionType.FINDERS_KEEPERS);
                            participant3.joinParty(lastRedParty);
                            if (TvT.RED_TEAM.size() > 7) {
                                if (redCC == null) {
                                    redCC = new CommandChannel(participant3);
                                }
                                else {
                                    redCC.addParty(lastRedParty);
                                }
                            }
                        }
                        else {
                            participant3.joinParty(lastRedParty);
                        }
                        if (redParticipantCounter == 7) {
                            redParticipantCounter = 0;
                        }
                    }
                }
                addSpawn(70010, (IPositionable)TvT.BLUE_BUFFER_SPAWN_LOC, false, 1260000L, false, TvT.PVP_WORLD.getId());
                addSpawn(70010, (IPositionable)TvT.RED_BUFFER_SPAWN_LOC, false, 1260000L, false, TvT.PVP_WORLD.getId());
                TvT.BLUE_SCORE = 0;
                TvT.RED_SCORE = 0;
                TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExPVPMatchCCRecord(0, GameUtils.sortByValue((Map)TvT.PLAYER_SCORES)) });
                this.startQuestTimer("5", 55000L, (Npc)null, (Player)null);
                this.startQuestTimer("4", 56000L, (Npc)null, (Player)null);
                this.startQuestTimer("3", 57000L, (Npc)null, (Player)null);
                this.startQuestTimer("2", 58000L, (Npc)null, (Player)null);
                this.startQuestTimer("1", 59000L, (Npc)null, (Player)null);
                this.startQuestTimer("StartFight", 60000L, (Npc)null, (Player)null);
                break;
            }
            case "StartFight": {
                this.openDoor(24190002, TvT.PVP_WORLD.getId());
                this.openDoor(24190003, TvT.PVP_WORLD.getId());
                this.broadcastScreenMessageWithEffect("The fight has began!", 5);
                this.startQuestTimer("10", 1190000L, (Npc)null, (Player)null);
                this.startQuestTimer("9", 1191000L, (Npc)null, (Player)null);
                this.startQuestTimer("8", 1192000L, (Npc)null, (Player)null);
                this.startQuestTimer("7", 1193000L, (Npc)null, (Player)null);
                this.startQuestTimer("6", 1194000L, (Npc)null, (Player)null);
                this.startQuestTimer("5", 1195000L, (Npc)null, (Player)null);
                this.startQuestTimer("4", 1196000L, (Npc)null, (Player)null);
                this.startQuestTimer("3", 1197000L, (Npc)null, (Player)null);
                this.startQuestTimer("2", 1198000L, (Npc)null, (Player)null);
                this.startQuestTimer("1", 1199000L, (Npc)null, (Player)null);
                this.startQuestTimer("EndFight", 1200000L, (Npc)null, (Player)null);
                break;
            }
            case "EndFight": {
                this.closeDoor(24190002, TvT.PVP_WORLD.getId());
                this.closeDoor(24190003, TvT.PVP_WORLD.getId());
                for (final Player participant : TvT.PLAYER_LIST) {
                    participant.setIsInvul(true);
                    participant.setIsImmobilized(true);
                    participant.disableAllSkills();
                    for (final Summon summon : participant.getServitors().values()) {
                        summon.setIsInvul(true);
                        summon.setIsImmobilized(true);
                        summon.disableAllSkills();
                    }
                }
                for (final Player participant : TvT.PLAYER_LIST) {
                    if (participant.isDead()) {
                        participant.doRevive();
                    }
                }
                if (TvT.BLUE_SCORE > TvT.RED_SCORE) {
                    final Skill skill2 = CommonSkill.FIREWORK.getSkill();
                    this.broadcastScreenMessageWithEffect("Team Blue won the event!", 7);
                    for (final Player participant4 : TvT.BLUE_TEAM) {
                        if (participant4 != null && participant4.getInstanceWorld() == TvT.PVP_WORLD) {
                            participant4.broadcastPacket((ServerPacket)new MagicSkillUse((Creature)participant4, (WorldObject)participant4, skill2.getId(), skill2.getLevel(), skill2.getHitTime(), skill2.getReuseDelay()));
                            participant4.broadcastSocialAction(3);
                            giveItems(participant4, TvT.REWARD);
                        }
                    }
                }
                else if (TvT.RED_SCORE > TvT.BLUE_SCORE) {
                    final Skill skill2 = CommonSkill.FIREWORK.getSkill();
                    this.broadcastScreenMessageWithEffect("Team Red won the event!", 7);
                    for (final Player participant4 : TvT.RED_TEAM) {
                        if (participant4 != null && participant4.getInstanceWorld() == TvT.PVP_WORLD) {
                            participant4.broadcastPacket((ServerPacket)new MagicSkillUse((Creature)participant4, (WorldObject)participant4, skill2.getId(), skill2.getLevel(), skill2.getHitTime(), skill2.getReuseDelay()));
                            participant4.broadcastSocialAction(3);
                            giveItems(participant4, TvT.REWARD);
                        }
                    }
                }
                else {
                    this.broadcastScreenMessageWithEffect("The event ended with a tie!", 7);
                    for (final Player participant : TvT.PLAYER_LIST) {
                        participant.broadcastSocialAction(13);
                    }
                }
                this.startQuestTimer("ScoreBoard", 3500L, (Npc)null, (Player)null);
                this.startQuestTimer("TeleportOut", 7000L, (Npc)null, (Player)null);
                break;
            }
            case "ScoreBoard": {
                TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExPVPMatchCCRecord(2, GameUtils.sortByValue((Map)TvT.PLAYER_SCORES)) });
                break;
            }
            case "TeleportOut": {
                for (final Player participant : TvT.PLAYER_LIST) {
                    this.removeListeners(participant);
                    participant.setTeam(Team.NONE);
                    participant.setOnCustomEvent(false);
                    participant.leaveParty();
                }
                if (TvT.PVP_WORLD != null) {
                    TvT.PVP_WORLD.destroy();
                    TvT.PVP_WORLD = null;
                }
                for (final Player participant : TvT.PLAYER_LIST) {
                    participant.setIsInvul(false);
                    participant.setIsImmobilized(false);
                    participant.enableAllSkills();
                    for (final Summon summon : participant.getServitors().values()) {
                        summon.setIsInvul(true);
                        summon.setIsImmobilized(true);
                        summon.disableAllSkills();
                    }
                }
                TvT.EVENT_ACTIVE = false;
                break;
            }
            case "ResurrectPlayer": {
                if (!player.isDead() || !player.isOnCustomEvent()) {
                    break;
                }
                if (TvT.BLUE_TEAM.contains(player)) {
                    player.setIsPendingRevive(true);
                    player.teleToLocation((ILocational)TvT.BLUE_SPAWN_LOC, false, TvT.PVP_WORLD);
                    TvT.GHOST_WALKING.getSkill().applyEffects((Creature)player, (Creature)player);
                    this.resetActivityTimers(player);
                    break;
                }
                if (TvT.RED_TEAM.contains(player)) {
                    player.setIsPendingRevive(true);
                    player.teleToLocation((ILocational)TvT.RED_SPAWN_LOC, false, TvT.PVP_WORLD);
                    TvT.GHOST_WALKING.getSkill().applyEffects((Creature)player, (Creature)player);
                    this.resetActivityTimers(player);
                    break;
                }
                break;
            }
            case "10":
            case "9":
            case "8":
            case "7":
            case "6":
            case "5":
            case "4":
            case "3":
            case "2":
            case "1": {
                this.broadcastScreenMessage(event, 4);
                break;
            }
        }
        if (event.startsWith("KickPlayer") && player != null && player.getInstanceWorld() == TvT.PVP_WORLD) {
            if (event.contains("Warning")) {
                this.sendScreenMessage(player, "You have been marked as inactive!", 10);
            }
            else {
                player.setTeam(Team.NONE);
                TvT.PVP_WORLD.ejectPlayer(player);
                TvT.PLAYER_LIST.remove(player);
                TvT.PLAYER_SCORES.remove(player);
                TvT.BLUE_TEAM.remove(player);
                TvT.RED_TEAM.remove(player);
                player.setOnCustomEvent(false);
                this.removeListeners(player);
                player.sendMessage("You have been kicked for been inactive.");
                if (TvT.PVP_WORLD != null) {
                    if ((TvT.BLUE_TEAM.isEmpty() && !TvT.RED_TEAM.isEmpty()) || (TvT.RED_TEAM.isEmpty() && !TvT.BLUE_TEAM.isEmpty())) {
                        this.manageForfeit();
                    }
                    else {
                        this.broadcastScreenMessageWithEffect(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()), 7);
                    }
                }
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        if (!TvT.EVENT_ACTIVE) {
            return null;
        }
        if (!TvT.PLAYER_LIST.contains(player)) {
            return "manager-register.html";
        }
        if (npc.getInstanceWorld() != null) {
            return "manager-buffheal.html";
        }
        return "manager-cancel.html";
    }
    
    public String onEnterZone(final Creature character, final Zone zone) {
        if (GameUtils.isPlayable((WorldObject)character) && character.getActingPlayer().isOnCustomEvent()) {
            if (zone == TvT.BLUE_PEACE_ZONE && character.getTeam() == Team.RED) {
                character.teleToLocation((ILocational)TvT.RED_SPAWN_LOC, TvT.PVP_WORLD);
                this.sendScreenMessage(character.getActingPlayer(), "Entering the enemy headquarters is prohibited!", 10);
            }
            if (zone == TvT.RED_PEACE_ZONE && character.getTeam() == Team.BLUE) {
                character.teleToLocation((ILocational)TvT.BLUE_SPAWN_LOC, TvT.PVP_WORLD);
                this.sendScreenMessage(character.getActingPlayer(), "Entering the enemy headquarters is prohibited!", 10);
            }
            if (GameUtils.isPlayer((WorldObject)character) && ((zone == TvT.BLUE_PEACE_ZONE && character.getTeam() == Team.BLUE) || (zone == TvT.RED_PEACE_ZONE && character.getTeam() == Team.RED))) {
                this.resetActivityTimers(character.getActingPlayer());
            }
        }
        return null;
    }
    
    public String onExitZone(final Creature character, final Zone zone) {
        if (GameUtils.isPlayer((WorldObject)character) && character.getActingPlayer().isOnCustomEvent()) {
            final Player player = character.getActingPlayer();
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, character.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, character.getObjectId()), (Npc)null, player);
            if (player.isAffectedBySkill(TvT.GHOST_WALKING)) {
                player.getEffectList().stopSkillEffects(true, TvT.GHOST_WALKING.getSkill());
            }
        }
        return super.onExitZone(character, zone);
    }
    
    private boolean canRegister(final Player player) {
        if (TvT.PLAYER_LIST.contains(player)) {
            player.sendMessage("You are already registered on this event.");
            return false;
        }
        if (player.getLevel() < 76) {
            player.sendMessage("Your level is too low to participate.");
            return false;
        }
        if (player.getLevel() > 200) {
            player.sendMessage("Your level is too high to participate.");
            return false;
        }
        if (player.isOnEvent() || player.getBlockCheckerArena() > -1) {
            player.sendMessage("You are already registered on an event.");
            return false;
        }
        if (TvT.PLAYER_LIST.size() >= 24) {
            player.sendMessage("There are too many players registered on the event.");
            return false;
        }
        if (player.isFlyingMounted()) {
            player.sendMessage("You cannot register on the event while flying.");
            return false;
        }
        if (player.isTransformed()) {
            player.sendMessage("You cannot register on the event while on a transformed state.");
            return false;
        }
        if (!player.isInventoryUnder80(false)) {
            player.sendMessage("There are too many items in your inventory.");
            player.sendMessage("Try removing some items.");
            return false;
        }
        if (player.getWeightPenalty() != 0) {
            player.sendMessage("Your invetory weight has exceeded the normal limit.");
            player.sendMessage("Try removing some items.");
            return false;
        }
        if (player.getReputation() < 0) {
            player.sendMessage("People with bad reputation can't register.");
            return false;
        }
        if (player.isInDuel()) {
            player.sendMessage("You cannot register while on a duel.");
            return false;
        }
        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendMessage("You cannot participate while registered on the Olympiad.");
            return false;
        }
        if (player.isInInstance()) {
            player.sendMessage("You cannot register while in an instance.");
            return false;
        }
        if (player.isInSiege() || player.isInsideZone(ZoneType.SIEGE)) {
            player.sendMessage("You cannot register while on a siege.");
            return false;
        }
        if (player.isFishing()) {
            player.sendMessage("You cannot register while fishing.");
            return false;
        }
        return true;
    }
    
    private void sendScreenMessage(final Player player, final String message, final int duration) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(message, 2, duration * 1000, 0, true, false) });
    }
    
    private void broadcastScreenMessage(final String message, final int duration) {
        TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(message, 2, duration * 1000, 0, true, false) });
    }
    
    private void broadcastScreenMessageWithEffect(final String message, final int duration) {
        TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(message, 2, duration * 1000, 0, true, true) });
    }
    
    private void broadcastScoreMessage() {
        TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, TvT.BLUE_SCORE, TvT.RED_SCORE), 8, 15000, 0, true, false) });
    }
    
    private void addLogoutListener(final Player player) {
        player.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)player, EventType.ON_PLAYER_LOGOUT, event -> this.OnPlayerLogout(event), (Object)this));
    }
    
    private void addDeathListener(final Player player) {
        player.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)player, EventType.ON_CREATURE_DEATH, event -> this.onPlayerDeath(event), (Object)this));
    }
    
    private void removeListeners(final Player player) {
        for (final AbstractEventListener listener : player.getListeners(EventType.ON_PLAYER_LOGOUT)) {
            if (listener.getOwner() == this) {
                listener.unregisterMe();
            }
        }
        for (final AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH)) {
            if (listener.getOwner() == this) {
                listener.unregisterMe();
            }
        }
    }
    
    private void resetActivityTimers(final Player player) {
        this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, player.getObjectId()), (Npc)null, player);
        this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, player.getObjectId()), (Npc)null, player);
        this.startQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, player.getObjectId()), TvT.PVP_WORLD.getDoor(24190002).isOpen() ? 120000L : 180000L, (Npc)null, player);
        this.startQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, player.getObjectId()), TvT.PVP_WORLD.getDoor(24190002).isOpen() ? 60000L : 120000L, (Npc)null, player);
    }
    
    private void manageForfeit() {
        this.cancelQuestTimer("10", (Npc)null, (Player)null);
        this.cancelQuestTimer("9", (Npc)null, (Player)null);
        this.cancelQuestTimer("8", (Npc)null, (Player)null);
        this.cancelQuestTimer("7", (Npc)null, (Player)null);
        this.cancelQuestTimer("6", (Npc)null, (Player)null);
        this.cancelQuestTimer("5", (Npc)null, (Player)null);
        this.cancelQuestTimer("4", (Npc)null, (Player)null);
        this.cancelQuestTimer("3", (Npc)null, (Player)null);
        this.cancelQuestTimer("2", (Npc)null, (Player)null);
        this.cancelQuestTimer("1", (Npc)null, (Player)null);
        this.cancelQuestTimer("EndFight", (Npc)null, (Player)null);
        this.startQuestTimer("EndFight", 10000L, (Npc)null, (Player)null);
        this.broadcastScreenMessageWithEffect("Enemy team forfeit!", 7);
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    private void OnPlayerLogout(final OnPlayerLogout event) {
        final Player player = event.getActiveChar();
        TvT.PLAYER_LIST.remove(player);
        TvT.PLAYER_SCORES.remove(player);
        TvT.BLUE_TEAM.remove(player);
        TvT.RED_TEAM.remove(player);
        if ((TvT.BLUE_TEAM.isEmpty() && !TvT.RED_TEAM.isEmpty()) || (TvT.RED_TEAM.isEmpty() && !TvT.BLUE_TEAM.isEmpty())) {
            this.manageForfeit();
        }
    }
    
    @RegisterEvent(EventType.ON_CREATURE_DEATH)
    public void onPlayerDeath(final OnCreatureDeath event) {
        if (GameUtils.isPlayer((WorldObject)event.getTarget())) {
            final Player killedPlayer = event.getTarget().getActingPlayer();
            final Player killer = event.getAttacker().getActingPlayer();
            if (killer.getTeam() == Team.BLUE && killedPlayer.getTeam() == Team.RED) {
                TvT.PLAYER_SCORES.put(killer, TvT.PLAYER_SCORES.get(killer) + 1);
                ++TvT.BLUE_SCORE;
                this.broadcastScoreMessage();
                TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExPVPMatchCCRecord(1, GameUtils.sortByValue((Map)TvT.PLAYER_SCORES)) });
            }
            if (killer.getTeam() == Team.RED && killedPlayer.getTeam() == Team.BLUE) {
                TvT.PLAYER_SCORES.put(killer, TvT.PLAYER_SCORES.get(killer) + 1);
                ++TvT.RED_SCORE;
                this.broadcastScoreMessage();
                TvT.PVP_WORLD.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExPVPMatchCCRecord(1, GameUtils.sortByValue((Map)TvT.PLAYER_SCORES)) });
            }
            this.startQuestTimer("ResurrectPlayer", 10000L, (Npc)null, killedPlayer);
        }
    }
    
    public boolean eventStart(final Player eventMaker) {
        if (TvT.EVENT_ACTIVE) {
            return false;
        }
        TvT.EVENT_ACTIVE = true;
        for (final List<QuestTimer> timers : this.getQuestTimers().values()) {
            for (final QuestTimer timer : timers) {
                timer.cancel();
            }
        }
        TvT.PLAYER_LIST.clear();
        TvT.PLAYER_SCORES.clear();
        TvT.BLUE_TEAM.clear();
        TvT.RED_TEAM.clear();
        TvT.MANAGER_NPC_INSTANCE = addSpawn(70010, (IPositionable)TvT.MANAGER_SPAWN_LOC, false, 600000L);
        this.startQuestTimer("TeleportToArena", 600000L, (Npc)null, (Player)null);
        Broadcast.toAllOnlinePlayers("TvT Event: Registration opened for 10 minutes.");
        Broadcast.toAllOnlinePlayers("TvT Event: You can register at Giran TvT Event Manager.");
        return true;
    }
    
    public boolean eventStop() {
        if (!TvT.EVENT_ACTIVE) {
            return false;
        }
        TvT.EVENT_ACTIVE = false;
        TvT.MANAGER_NPC_INSTANCE.deleteMe();
        for (final List<QuestTimer> timers : this.getQuestTimers().values()) {
            for (final QuestTimer timer : timers) {
                timer.cancel();
            }
        }
        for (final Player participant : TvT.PLAYER_LIST) {
            this.removeListeners(participant);
            participant.setTeam(Team.NONE);
            participant.setOnCustomEvent(false);
        }
        if (TvT.PVP_WORLD != null) {
            TvT.PVP_WORLD.destroy();
            TvT.PVP_WORLD = null;
        }
        Broadcast.toAllOnlinePlayers("TvT Event: Event was canceled.");
        return true;
    }
    
    public boolean eventBypass(final Player activeChar, final String bypass) {
        return false;
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new TvT();
    }
    
    static {
        FIGHTER_BUFFS = new SkillHolder[] { new SkillHolder(4322, 1), new SkillHolder(4323, 1), new SkillHolder(5637, 1), new SkillHolder(4324, 1), new SkillHolder(4325, 1), new SkillHolder(4326, 1), new SkillHolder(5632, 1) };
        MAGE_BUFFS = new SkillHolder[] { new SkillHolder(4322, 1), new SkillHolder(4323, 1), new SkillHolder(5637, 1), new SkillHolder(4328, 1), new SkillHolder(4329, 1), new SkillHolder(4330, 1), new SkillHolder(4331, 1) };
        GHOST_WALKING = new SkillHolder(100000, 1);
        MANAGER_SPAWN_LOC = new Location(83425, 148585, -3406, 32938);
        BLUE_BUFFER_SPAWN_LOC = new Location(147450, 46913, -3400, 49000);
        RED_BUFFER_SPAWN_LOC = new Location(151545, 46528, -3400, 16000);
        BLUE_SPAWN_LOC = new Location(147447, 46722, -3416);
        RED_SPAWN_LOC = new Location(151536, 46722, -3416);
        BLUE_PEACE_ZONE = ZoneManager.getInstance().getZoneByName("colosseum_peace1");
        RED_PEACE_ZONE = ZoneManager.getInstance().getZoneByName("colosseum_peace2");
        REWARD = new ItemHolder(57, 100000L);
        PLAYER_SCORES = new ConcurrentHashMap<Player, Integer>();
        PLAYER_LIST = new ArrayList<Player>();
        BLUE_TEAM = new ArrayList<Player>();
        RED_TEAM = new ArrayList<Player>();
        TvT.PVP_WORLD = null;
        TvT.MANAGER_NPC_INSTANCE = null;
        TvT.EVENT_ACTIVE = false;
    }
}
