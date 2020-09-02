// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import org.l2j.gameserver.instancemanager.tasks.PenaltyRemoveTask;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.network.serverpackets.ExCubeGameChangeTeam;
import org.l2j.gameserver.network.serverpackets.ExCubeGameRemovePlayer;
import org.l2j.gameserver.network.serverpackets.ExCubeGameAddPlayer;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.entity.BlockCheckerEngine;
import java.util.Objects;
import org.l2j.gameserver.Config;
import java.util.Set;
import java.util.Map;
import org.l2j.gameserver.model.ArenaParticipantsHolder;

public final class HandysBlockCheckerManager
{
    private static final ArenaParticipantsHolder[] _arenaPlayers;
    private static final Map<Integer, Integer> _arenaVotes;
    private static final Map<Integer, Boolean> _arenaStatus;
    protected static Set<Integer> _registrationPenalty;
    
    private HandysBlockCheckerManager() {
        HandysBlockCheckerManager._arenaStatus.put(0, false);
        HandysBlockCheckerManager._arenaStatus.put(1, false);
        HandysBlockCheckerManager._arenaStatus.put(2, false);
        HandysBlockCheckerManager._arenaStatus.put(3, false);
        HandysBlockCheckerManager._arenaVotes.put(0, 0);
        HandysBlockCheckerManager._arenaVotes.put(1, 0);
        HandysBlockCheckerManager._arenaVotes.put(2, 0);
        HandysBlockCheckerManager._arenaVotes.put(3, 0);
    }
    
    public synchronized int getArenaVotes(final int arenaId) {
        return HandysBlockCheckerManager._arenaVotes.get(arenaId);
    }
    
    public synchronized void increaseArenaVotes(final int arena) {
        final int newVotes = HandysBlockCheckerManager._arenaVotes.get(arena) + 1;
        final ArenaParticipantsHolder holder = HandysBlockCheckerManager._arenaPlayers[arena];
        if (newVotes > holder.getAllPlayers().size() / 2 && !holder.getEvent().isStarted()) {
            this.clearArenaVotes(arena);
            if (holder.getBlueTeamSize() == 0 || holder.getRedTeamSize() == 0) {
                return;
            }
            if (Config.HBCE_FAIR_PLAY) {
                holder.checkAndShuffle();
            }
            final BlockCheckerEngine event = holder.getEvent();
            Objects.requireNonNull(event);
            ThreadPool.execute((Runnable)event.new StartEvent());
        }
        else {
            HandysBlockCheckerManager._arenaVotes.put(arena, newVotes);
        }
    }
    
    public synchronized void clearArenaVotes(final int arena) {
        HandysBlockCheckerManager._arenaVotes.put(arena, 0);
    }
    
    public ArenaParticipantsHolder getHolder(final int arena) {
        return HandysBlockCheckerManager._arenaPlayers[arena];
    }
    
    public void startUpParticipantsQueue() {
        for (int i = 0; i < 4; ++i) {
            HandysBlockCheckerManager._arenaPlayers[i] = new ArenaParticipantsHolder(i);
        }
    }
    
    public boolean addPlayerToArena(final Player player, final int arenaId) {
        final ArenaParticipantsHolder holder = HandysBlockCheckerManager._arenaPlayers[arenaId];
        synchronized (holder) {
            for (int i = 0; i < 4; ++i) {
                if (HandysBlockCheckerManager._arenaPlayers[i].getAllPlayers().contains(player)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH);
                    msg.addString(player.getName());
                    player.sendPacket(msg);
                    return false;
                }
            }
            if (player.isOnEvent() || player.isInOlympiadMode()) {
                player.sendMessage("Couldnt register you due other event participation");
                return false;
            }
            if (OlympiadManager.getInstance().isRegistered(player)) {
                OlympiadManager.getInstance().unRegisterNoble(player);
                player.sendPacket(SystemMessageId.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER);
            }
            if (HandysBlockCheckerManager._registrationPenalty.contains(player.getObjectId())) {
                player.sendPacket(SystemMessageId.YOU_MUST_WAIT_10_SECONDS_BEFORE_ATTEMPTING_TO_REGISTER_AGAIN);
                return false;
            }
            boolean isRed;
            if (holder.getBlueTeamSize() < holder.getRedTeamSize()) {
                holder.addPlayer(player, 1);
                isRed = false;
            }
            else {
                holder.addPlayer(player, 0);
                isRed = true;
            }
            holder.broadCastPacketToTeam(new ExCubeGameAddPlayer(player, isRed));
            return true;
        }
    }
    
    public void removePlayer(final Player player, final int arenaId, final int team) {
        final ArenaParticipantsHolder holder = HandysBlockCheckerManager._arenaPlayers[arenaId];
        synchronized (holder) {
            final boolean isRed = team == 0;
            holder.removePlayer(player, team);
            holder.broadCastPacketToTeam(new ExCubeGameRemovePlayer(player, isRed));
            final int teamSize = isRed ? holder.getRedTeamSize() : holder.getBlueTeamSize();
            if (teamSize == 0) {
                holder.getEvent().endEventAbnormally();
            }
            HandysBlockCheckerManager._registrationPenalty.add(player.getObjectId());
            this.schedulePenaltyRemoval(player.getObjectId());
        }
    }
    
    public void changePlayerToTeam(final Player player, final int arena, final int team) {
        final ArenaParticipantsHolder holder = HandysBlockCheckerManager._arenaPlayers[arena];
        synchronized (holder) {
            final boolean isFromRed = holder.getRedPlayers().contains(player);
            if (isFromRed && holder.getBlueTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            }
            if (!isFromRed && holder.getRedTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            }
            final int futureTeam = isFromRed ? 1 : 0;
            holder.addPlayer(player, futureTeam);
            if (isFromRed) {
                holder.removePlayer(player, 0);
            }
            else {
                holder.removePlayer(player, 1);
            }
            holder.broadCastPacketToTeam(new ExCubeGameChangeTeam(player, isFromRed));
        }
    }
    
    public synchronized void clearPaticipantQueueByArenaId(final int arenaId) {
        HandysBlockCheckerManager._arenaPlayers[arenaId].clearPlayers();
    }
    
    public boolean arenaIsBeingUsed(final int arenaId) {
        return arenaId >= 0 && arenaId <= 3 && HandysBlockCheckerManager._arenaStatus.get(arenaId);
    }
    
    public void setArenaBeingUsed(final int arenaId) {
        HandysBlockCheckerManager._arenaStatus.put(arenaId, true);
    }
    
    public void setArenaFree(final int arenaId) {
        HandysBlockCheckerManager._arenaStatus.put(arenaId, false);
    }
    
    public void onDisconnect(final Player player) {
        final int arena = player.getBlockCheckerArena();
        final int team = this.getHolder(arena).getPlayerTeam(player);
        getInstance().removePlayer(player, arena, team);
        if (player.getTeam() != Team.NONE) {
            player.stopAllEffects();
            player.setTeam(Team.NONE);
            final PlayerInventory inv = player.getInventory();
            if (inv.getItemByItemId(13787) != null) {
                final long count = inv.getInventoryItemCount(13787, 0);
                inv.destroyItemByItemId("Handys Block Checker", 13787, count, player, player);
            }
            if (inv.getItemByItemId(13788) != null) {
                final long count = inv.getInventoryItemCount(13788, 0);
                inv.destroyItemByItemId("Handys Block Checker", 13788, count, player, player);
            }
            player.setInsideZone(ZoneType.PVP, false);
            player.teleToLocation(-57478, -60367, -2370);
        }
    }
    
    public void removePenalty(final int objectId) {
        HandysBlockCheckerManager._registrationPenalty.remove(objectId);
    }
    
    private void schedulePenaltyRemoval(final int objId) {
        ThreadPool.schedule((Runnable)new PenaltyRemoveTask(objId), 10000L);
    }
    
    public static HandysBlockCheckerManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        _arenaPlayers = new ArenaParticipantsHolder[4];
        _arenaVotes = new HashMap<Integer, Integer>();
        _arenaStatus = new HashMap<Integer, Boolean>();
        HandysBlockCheckerManager._registrationPenalty = Collections.synchronizedSet(new HashSet<Integer>());
    }
    
    private static class Singleton
    {
        private static final HandysBlockCheckerManager INSTANCE;
        
        static {
            INSTANCE = new HandysBlockCheckerManager();
        }
    }
}
