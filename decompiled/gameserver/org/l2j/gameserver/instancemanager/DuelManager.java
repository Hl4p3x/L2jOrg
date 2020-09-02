// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.entity.Duel;
import java.util.Map;

public final class DuelManager
{
    private static final int[] ARENAS;
    private final Map<Integer, Duel> _duels;
    private final AtomicInteger _currentDuelId;
    
    private DuelManager() {
        this._duels = new ConcurrentHashMap<Integer, Duel>();
        this._currentDuelId = new AtomicInteger();
    }
    
    public Duel getDuel(final int duelId) {
        return this._duels.get(duelId);
    }
    
    public void addDuel(final Player playerA, final Player playerB, final int partyDuel) {
        if (playerA == null || playerB == null) {
            return;
        }
        final String engagedInPvP = "The duel was canceled because a duelist engaged in PvP combat.";
        if (partyDuel == 1) {
            boolean playerInPvP = false;
            for (final Player temp : playerA.getParty().getMembers()) {
                if (temp.getPvpFlag() != 0) {
                    playerInPvP = true;
                    break;
                }
            }
            if (!playerInPvP) {
                for (final Player temp : playerB.getParty().getMembers()) {
                    if (temp.getPvpFlag() != 0) {
                        playerInPvP = true;
                        break;
                    }
                }
            }
            if (playerInPvP) {
                for (final Player temp : playerA.getParty().getMembers()) {
                    temp.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
                }
                for (final Player temp : playerB.getParty().getMembers()) {
                    temp.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
                }
                return;
            }
        }
        else if (playerA.getPvpFlag() != 0 || playerB.getPvpFlag() != 0) {
            playerA.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
            playerB.sendMessage("The duel was canceled because a duelist engaged in PvP combat.");
            return;
        }
        final int duelId = this._currentDuelId.incrementAndGet();
        this._duels.put(duelId, new Duel(playerA, playerB, partyDuel, duelId));
    }
    
    public void removeDuel(final Duel duel) {
        this._duels.remove(duel.getId());
    }
    
    public void doSurrender(final Player player) {
        if (player == null || !player.isInDuel()) {
            return;
        }
        final Duel duel = this.getDuel(player.getDuelId());
        duel.doSurrender(player);
    }
    
    public void onPlayerDefeat(final Player player) {
        if (player == null || !player.isInDuel()) {
            return;
        }
        final Duel duel = this.getDuel(player.getDuelId());
        if (duel != null) {
            duel.onPlayerDefeat(player);
        }
    }
    
    public void onBuff(final Player player, final Skill buff) {
        if (player == null || !player.isInDuel() || buff == null) {
            return;
        }
        final Duel duel = this.getDuel(player.getDuelId());
        if (duel != null) {
            duel.onBuff(player, buff);
        }
    }
    
    public void onRemoveFromParty(final Player player) {
        if (player == null || !player.isInDuel()) {
            return;
        }
        final Duel duel = this.getDuel(player.getDuelId());
        if (duel != null) {
            duel.onRemoveFromParty(player);
        }
    }
    
    public void broadcastToOppositTeam(final Player player, final ServerPacket packet) {
        if (player == null || !player.isInDuel()) {
            return;
        }
        final Duel duel = this.getDuel(player.getDuelId());
        if (duel == null) {
            return;
        }
        if (duel.getPlayerA() == null || duel.getPlayerB() == null) {
            return;
        }
        if (duel.getPlayerA() == player) {
            duel.broadcastToTeam2(packet);
        }
        else if (duel.getPlayerB() == player) {
            duel.broadcastToTeam1(packet);
        }
        else if (duel.isPartyDuel()) {
            if (duel.getPlayerA().getParty() != null && duel.getPlayerA().getParty().getMembers().contains(player)) {
                duel.broadcastToTeam2(packet);
            }
            else if (duel.getPlayerB().getParty() != null && duel.getPlayerB().getParty().getMembers().contains(player)) {
                duel.broadcastToTeam1(packet);
            }
        }
    }
    
    public int getDuelArena() {
        return DuelManager.ARENAS[Rnd.get(DuelManager.ARENAS.length)];
    }
    
    public static DuelManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        ARENAS = new int[] { 147, 148, 149, 150 };
    }
    
    private static class Singleton
    {
        private static final DuelManager INSTANCE;
        
        static {
            INSTANCE = new DuelManager();
        }
    }
}
