// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.gameserver.model.entity.BlockCheckerEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public final class ArenaParticipantsHolder
{
    private final int _arena;
    private final List<Player> _redPlayers;
    private final List<Player> _bluePlayers;
    private final BlockCheckerEngine _engine;
    
    public ArenaParticipantsHolder(final int arena) {
        this._arena = arena;
        this._redPlayers = new ArrayList<Player>(6);
        this._bluePlayers = new ArrayList<Player>(6);
        this._engine = new BlockCheckerEngine(this, this._arena);
    }
    
    public List<Player> getRedPlayers() {
        return this._redPlayers;
    }
    
    public List<Player> getBluePlayers() {
        return this._bluePlayers;
    }
    
    public List<Player> getAllPlayers() {
        final List<Player> all = new ArrayList<Player>(12);
        all.addAll(this._redPlayers);
        all.addAll(this._bluePlayers);
        return all;
    }
    
    public void addPlayer(final Player player, final int team) {
        if (team == 0) {
            this._redPlayers.add(player);
        }
        else {
            this._bluePlayers.add(player);
        }
    }
    
    public void removePlayer(final Player player, final int team) {
        if (team == 0) {
            this._redPlayers.remove(player);
        }
        else {
            this._bluePlayers.remove(player);
        }
    }
    
    public int getPlayerTeam(final Player player) {
        if (this._redPlayers.contains(player)) {
            return 0;
        }
        if (this._bluePlayers.contains(player)) {
            return 1;
        }
        return -1;
    }
    
    public int getRedTeamSize() {
        return this._redPlayers.size();
    }
    
    public int getBlueTeamSize() {
        return this._bluePlayers.size();
    }
    
    public void broadCastPacketToTeam(final ServerPacket packet) {
        for (final Player p : this._redPlayers) {
            p.sendPacket(packet);
        }
        for (final Player p : this._bluePlayers) {
            p.sendPacket(packet);
        }
    }
    
    public void clearPlayers() {
        this._redPlayers.clear();
        this._bluePlayers.clear();
    }
    
    public BlockCheckerEngine getEvent() {
        return this._engine;
    }
    
    public void updateEvent() {
        this._engine.updatePlayersOnStart(this);
    }
    
    public void checkAndShuffle() {
        final int redSize = this._redPlayers.size();
        final int blueSize = this._bluePlayers.size();
        if (redSize > blueSize + 1) {
            this.broadCastPacketToTeam(SystemMessage.getSystemMessage(SystemMessageId.TEAM_MEMBERS_WERE_MODIFIED_BECAUSE_THE_TEAMS_WERE_UNBALANCED));
            for (int needed = redSize - (blueSize + 1), i = 0; i < needed + 1; ++i) {
                final Player plr = this._redPlayers.get(i);
                if (plr != null) {
                    HandysBlockCheckerManager.getInstance().changePlayerToTeam(plr, this._arena, 1);
                }
            }
        }
        else if (blueSize > redSize + 1) {
            this.broadCastPacketToTeam(SystemMessage.getSystemMessage(SystemMessageId.TEAM_MEMBERS_WERE_MODIFIED_BECAUSE_THE_TEAMS_WERE_UNBALANCED));
            for (int needed = blueSize - (redSize + 1), i = 0; i < needed + 1; ++i) {
                final Player plr = this._bluePlayers.get(i);
                if (plr != null) {
                    HandysBlockCheckerManager.getInstance().changePlayerToTeam(plr, this._arena, 0);
                }
            }
        }
    }
}
