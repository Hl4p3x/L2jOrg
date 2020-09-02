// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.AbstractInventoryUpdate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMode;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public abstract class AbstractOlympiadGame
{
    protected static final Logger LOGGER;
    protected static final Logger LOGGER_OLYMPIAD;
    protected static final String POINTS = "olympiad_points";
    protected static final String COMP_DONE = "competitions_done";
    protected static final String COMP_WON = "competitions_won";
    protected static final String COMP_LOST = "competitions_lost";
    protected static final String COMP_DRAWN = "competitions_drawn";
    protected static final String COMP_DONE_WEEK = "competitions_done_week";
    protected static final String COMP_DONE_WEEK_CLASSED = "competitions_done_week_classed";
    protected static final String COMP_DONE_WEEK_NON_CLASSED = "competitions_done_week_non_classed";
    protected static final String COMP_DONE_WEEK_TEAM = "competitions_done_week_team";
    protected long _startTime;
    protected boolean _aborted;
    protected final int _stadiumId;
    
    protected AbstractOlympiadGame(final int id) {
        this._startTime = 0L;
        this._aborted = false;
        this._stadiumId = id;
    }
    
    protected static SystemMessage checkDefaulted(final Player player) {
        if (player == null || !player.isOnline()) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (player.getClient() == null) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (player.inObserverMode()) {
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (player.isDead()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (player.isSubClassActive()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGED_YOUR_CLASS_TO_SUBCLASS);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        if (!player.isInventoryUnder90(true)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_FOR_OLYMPIAD_AS_THE_INVENTORY_WEIGHT_SLOT_EXCEEDS_80);
            sm.addPcName(player);
            player.sendPacket(sm);
            return SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED);
        }
        return null;
    }
    
    protected static boolean portPlayerToArena(final Participant par, final Location loc, final int id, final Instance instance) {
        final Player player = par.getPlayer();
        if (player == null || !player.isOnline()) {
            return false;
        }
        try {
            player.setLastLocation();
            if (player.isSitting()) {
                player.standUp();
            }
            player.setTarget(null);
            player.setOlympiadGameId(id);
            player.setIsInOlympiadMode(true);
            player.setIsOlympiadStart(false);
            player.setOlympiadSide(par.getSide());
            player.teleToLocation(loc, instance);
            player.sendPacket(new ExOlympiadMode(2));
        }
        catch (Exception e) {
            AbstractOlympiadGame.LOGGER.warn(e.getMessage(), (Throwable)e);
            return false;
        }
        return true;
    }
    
    protected static void removals(final Player player, final boolean removeParty) {
        try {
            if (player == null) {
                return;
            }
            player.stopAllEffectsExceptThoseThatLastThroughDeath();
            player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            if (player.getClan() != null) {
                player.getClan().removeSkillEffects(player);
                if (player.getClan().getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
                }
            }
            player.abortAttack();
            player.abortCast();
            player.setInvisible(false);
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            if (player.hasSummon()) {
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.unSummon(player);
                }
                player.getServitors().values().forEach(s -> {
                    s.stopAllEffectsExceptThoseThatLastThroughDeath();
                    s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
                    s.abortAttack();
                    s.abortCast();
                    return;
                });
            }
            player.stopCubicsByOthers();
            if (removeParty) {
                final Party party = player.getParty();
                if (party != null) {
                    party.removePartyMember(player, Party.MessageType.EXPELLED);
                }
            }
            if (player.getAgathionId() > 0) {
                player.setAgathionId(0);
                player.broadcastUserInfo();
            }
            player.checkItemRestriction();
            player.disableAutoShots();
            player.unchargeAllShots();
            for (final Skill skill : player.getAllSkills()) {
                if (skill.getReuseDelay() <= 900000) {
                    player.enableSkill(skill);
                }
            }
            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        }
        catch (Exception e) {
            AbstractOlympiadGame.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    protected static void cleanEffects(final Player player) {
        try {
            player.setIsOlympiadStart(false);
            player.setTarget(null);
            player.abortAttack();
            player.abortCast();
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            if (player.isDead()) {
                player.setIsDead(false);
            }
            player.stopAllEffectsExceptThoseThatLastThroughDeath();
            player.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            player.clearSouls();
            player.clearCharges();
            if (player.getAgathionId() > 0) {
                player.setAgathionId(0);
            }
            final Summon pet = player.getPet();
            if (pet != null && !pet.isDead()) {
                pet.setTarget(null);
                pet.abortAttack();
                pet.abortCast();
                pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                pet.stopAllEffectsExceptThoseThatLastThroughDeath();
                pet.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
            }
            player.getServitors().values().stream().filter(s -> !s.isDead()).forEach(s -> {
                s.setTarget(null);
                s.abortAttack();
                s.abortCast();
                s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                s.stopAllEffectsExceptThoseThatLastThroughDeath();
                s.getEffectList().stopEffects(info -> info.getSkill().isBlockedInOlympiad(), true, true);
                return;
            });
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.getStatus().startHpMpRegeneration();
        }
        catch (Exception e) {
            AbstractOlympiadGame.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    protected static void playerStatusBack(final Player player) {
        try {
            if (player.isTransformed()) {
                player.untransform();
            }
            player.setIsInOlympiadMode(false);
            player.setIsOlympiadStart(false);
            player.setOlympiadSide(-1);
            player.setOlympiadGameId(-1);
            player.sendPacket(new ExOlympiadMode(0));
            if (player.getClan() != null) {
                player.getClan().addSkillEffects(player);
                if (player.getClan().getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(player.getClan()).giveResidentialSkills(player);
                }
                player.sendSkillList();
            }
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.getStatus().startHpMpRegeneration();
            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(1, player);
            }
        }
        catch (Exception e) {
            AbstractOlympiadGame.LOGGER.warn("playerStatusBack()", (Throwable)e);
        }
    }
    
    protected static void portPlayerBack(final Player player) {
        if (player == null) {
            return;
        }
        final Location loc = player.getLastLocation();
        if (loc != null) {
            player.setIsPendingRevive(false);
            player.teleToLocation(loc, null);
            player.unsetLastLocation();
        }
    }
    
    public static void rewardParticipant(final Player player, final List<ItemHolder> list) {
        if (player == null || !player.isOnline() || list == null) {
            return;
        }
        try {
            final InventoryUpdate iu = new InventoryUpdate();
            final Item item;
            final AbstractInventoryUpdate abstractInventoryUpdate;
            SystemMessage sm;
            list.forEach(holder -> {
                item = player.getInventory().addItem("Olympiad", holder.getId(), holder.getCount(), player, null);
                if (item == null) {
                    return;
                }
                else {
                    abstractInventoryUpdate.addModifiedItem(item);
                    sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                    sm.addItemName(item);
                    sm.addLong(holder.getCount());
                    player.sendPacket(sm);
                    return;
                }
            });
            player.sendInventoryUpdate(iu);
        }
        catch (Exception e) {
            AbstractOlympiadGame.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public final boolean isAborted() {
        return this._aborted;
    }
    
    public final int getStadiumId() {
        return this._stadiumId;
    }
    
    protected boolean makeCompetitionStart() {
        this._startTime = System.currentTimeMillis();
        return !this._aborted;
    }
    
    protected final void addPointsToParticipant(final Participant par, final int points) {
        par.updateStat("olympiad_points", points);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_EARNED_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
        sm.addString(par.getName());
        sm.addInt(points);
        this.broadcastPacket(sm);
    }
    
    protected final void removePointsFromParticipant(final Participant par, final int points) {
        par.updateStat("olympiad_points", -points);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
        sm.addString(par.getName());
        sm.addInt(points);
        this.broadcastPacket(sm);
    }
    
    public abstract CompetitionType getType();
    
    public abstract String[] getPlayerNames();
    
    public abstract boolean containsParticipant(final int playerId);
    
    public abstract void sendOlympiadInfo(final Creature player);
    
    public abstract void broadcastOlympiadInfo(final OlympiadStadium _stadium);
    
    protected abstract void broadcastPacket(final ServerPacket packet);
    
    protected abstract boolean needBuffers();
    
    protected abstract boolean checkDefaulted();
    
    protected abstract void removals();
    
    protected abstract boolean portPlayersToArena(final List<Location> spawns, final Instance instance);
    
    protected abstract void cleanEffects();
    
    protected abstract void portPlayersBack();
    
    protected abstract void playersStatusBack();
    
    protected abstract void clearPlayers();
    
    protected abstract void handleDisconnect(final Player player);
    
    protected abstract void resetDamage();
    
    protected abstract void addDamage(final Player player, final int damage);
    
    protected abstract boolean checkBattleStatus();
    
    protected abstract boolean haveWinner();
    
    protected abstract void validateWinner(final OlympiadStadium stadium);
    
    protected abstract int getDivider();
    
    protected abstract void healPlayers();
    
    protected abstract void untransformPlayers();
    
    protected abstract void makePlayersInvul();
    
    protected abstract void removePlayersInvul();
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractOlympiadGame.class);
        LOGGER_OLYMPIAD = LoggerFactory.getLogger("olympiad");
    }
}
