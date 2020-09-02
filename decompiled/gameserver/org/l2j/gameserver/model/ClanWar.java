// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.network.serverpackets.SurrenderPledgeWar;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarStart;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.data.ClanWarData;
import java.util.concurrent.Future;

public final class ClanWar
{
    public static final long TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR;
    public static final long TIME_TO_DELETION_AFTER_CANCELLATION;
    public static final long TIME_TO_DELETION_AFTER_DEFEAT;
    private Future<?> _cancelTask;
    private final ClanWarData data;
    
    public ClanWar(final Clan attacker, final Clan attacked) {
        this.data = ClanWarData.of(attacker, attacked);
        this._cancelTask = (Future<?>)ThreadPool.schedule(this::clanWarTimeout, this.data.getStartTime() + ClanWar.TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR - System.currentTimeMillis());
        attacker.addWar(attacked.getId(), this);
        attacked.addWar(attacker.getId(), this);
        EventDispatcher.getInstance().notifyEventAsync(new OnClanWarStart(attacker, attacked), new ListenersContainer[0]);
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DECLARED_A_CLAN_WAR_WITH_S1);
        sm.addString(attacked.getName());
        attacker.broadcastToOnlineMembers(sm);
        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_DECLARED_A_CLAN_WAR_THE_WAR_WILL_AUTOMATICALLY_START_IF_YOU_KILL_S1_CLAN_MEMBERS_5_TIMES_WITHIN_A_WEEK);
        sm.addString(attacker.getName());
        attacked.broadcastToOnlineMembers(sm);
    }
    
    public ClanWar(final ClanWarData warData) {
        this.data = warData;
        if (warData.getStartTime() + ClanWar.TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR > System.currentTimeMillis()) {
            this._cancelTask = (Future<?>)ThreadPool.schedule(this::clanWarTimeout, warData.getStartTime() + ClanWar.TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR - System.currentTimeMillis());
        }
        if (warData.getEndTime() > 0L) {
            long endTimePeriod = warData.getEndTime() + ((this.data.getState() == ClanWarState.TIE) ? ClanWar.TIME_TO_DELETION_AFTER_CANCELLATION : ClanWar.TIME_TO_DELETION_AFTER_DEFEAT);
            if (endTimePeriod > System.currentTimeMillis()) {
                endTimePeriod = 10000L;
            }
            ThreadPool.schedule(() -> ClanTable.getInstance().deleteClanWars(warData.getAttacker(), warData.getAttacked()), endTimePeriod);
        }
    }
    
    public void onKill(final Player killer, final Player victim) {
        final Clan victimClan = victim.getClan();
        final Clan killerClan = killer.getClan();
        if (victim.getLevel() > 4 && this.data.getState() == ClanWarState.MUTUAL) {
            if (victimClan.getReputationScore() > 0) {
                victimClan.takeReputationScore(Config.REPUTATION_SCORE_PER_KILL, false);
                killerClan.addReputationScore(Config.REPUTATION_SCORE_PER_KILL, false);
            }
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_REPUTATION_DECREASED_BY_1);
            sm.addPcName(victim);
            sm.addString(killerClan.getName());
            victimClan.broadcastToOtherOnlineMembers(sm, victim);
            sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_REPUTATION_INCREASED_BY_1);
            sm.addString(victimClan.getName());
            sm.addPcName(killer);
            killerClan.broadcastToOtherOnlineMembers(sm, killer);
            if (killerClan.getId() == this.data.getAttacker()) {
                this.data.incrementAttackerKill();
            }
            else {
                this.data.incrementAttackedKill();
            }
        }
        else if (this.data.getState() == ClanWarState.BLOOD_DECLARATION && victimClan.getId() == this.data.getAttacker() && victim.getReputation() >= 0) {
            final int killCount = this.data.incrementAttackedKill();
            if (killCount >= 5) {
                this.data.setState(ClanWarState.MUTUAL);
                SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
                sm2.addString(victimClan.getName());
                killerClan.broadcastToOnlineMembers(sm2);
                sm2 = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
                sm2.addString(killerClan.getName());
                victimClan.broadcastToOnlineMembers(sm2);
                if (this._cancelTask != null) {
                    this._cancelTask.cancel(true);
                    this._cancelTask = null;
                }
            }
            else {
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_YOUR_CLAN_MEMBER_IF_YOUR_CLAN_KILLS_S2_MEMBERS_OF_CLAN_S1_A_CLAN_WAR_WITH_CLAN_S1_WILL_START);
                sm2.addString(victimClan.getName());
                sm2.addInt(5 - killCount);
                killerClan.broadcastToOnlineMembers(sm2);
            }
        }
    }
    
    public void cancel(final Player player, final Clan cancelor) {
        final Clan winnerClan = (cancelor.getId() == this.data.getAttacker()) ? ClanTable.getInstance().getClan(this.data.getAttacked()) : ClanTable.getInstance().getClan(this.data.getAttacker());
        cancelor.takeReputationScore(500, true);
        player.sendPacket(new SurrenderPledgeWar(cancelor.getName(), player.getName()));
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_WAR_ENDED_BY_YOUR_DEFEAT_DECLARATION_WITH_THE_S1_CLAN);
        sm.addString(winnerClan.getName());
        cancelor.broadcastToOnlineMembers(sm);
        sm = SystemMessage.getSystemMessage(SystemMessageId.THE_WAR_ENDED_BY_THE_S1_CLAN_S_DEFEAT_DECLARATION_YOU_HAVE_WON_THE_CLAN_WAR_OVER_THE_S1_CLAN);
        sm.addString(cancelor.getName());
        winnerClan.broadcastToOnlineMembers(sm);
        this.data.setWinnerClan(winnerClan.getId());
        this.data.setEndTime(System.currentTimeMillis());
        ThreadPool.schedule(() -> ClanTable.getInstance().deleteClanWars(cancelor.getId(), winnerClan.getId()), this.data.getEndTime() + ClanWar.TIME_TO_DELETION_AFTER_DEFEAT - System.currentTimeMillis());
    }
    
    public void clanWarTimeout() {
        final Clan attackerClan = ClanTable.getInstance().getClan(this.data.getAttacker());
        final Clan attackedClan = ClanTable.getInstance().getClan(this.data.getAttacked());
        if (attackerClan != null && attackedClan != null) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_DECLARED_BY_CLAN_S1_WAS_CANCELLED);
            sm.addString(attackerClan.getName());
            attackedClan.broadcastToOnlineMembers(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_CLAN_S1_DID_NOT_FIGHT_BACK_FOR_1_WEEK_THE_CLAN_WAR_WAS_CANCELLED);
            sm.addString(attackedClan.getName());
            attackerClan.broadcastToOnlineMembers(sm);
            this.data.setState(ClanWarState.TIE);
            this.data.setEndTime(System.currentTimeMillis());
            ThreadPool.schedule(() -> ClanTable.getInstance().deleteClanWars(attackerClan.getId(), attackedClan.getId()), this.data.getEndTime() + ClanWar.TIME_TO_DELETION_AFTER_CANCELLATION - System.currentTimeMillis());
        }
    }
    
    public void mutualClanWarAccepted(final Clan attacker, final Clan attacked) {
        this.data.setState(ClanWarState.MUTUAL);
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
        sm.addString(attacker.getName());
        attacked.broadcastToOnlineMembers(sm);
        sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
        sm.addString(attacked.getName());
        attacker.broadcastToOnlineMembers(sm);
        if (this._cancelTask != null) {
            this._cancelTask.cancel(true);
            this._cancelTask = null;
        }
    }
    
    public int getKillDifference(final Clan clan) {
        return (this.data.getAttacker() == clan.getId()) ? (this.data.getAttackerKills() - this.data.getAttackedKills()) : (this.data.getAttackedKills() - this.data.getAttackerKills());
    }
    
    public ClanWarState getClanWarState(final Clan clan) {
        if (this.data.getWinnerClan() > 0) {
            return (this.data.getWinnerClan() == clan.getId()) ? ClanWarState.WIN : ClanWarState.LOSS;
        }
        return this.data.getState();
    }
    
    public int getAttackerClanId() {
        return this.data.getAttacker();
    }
    
    public int getAttackedClanId() {
        return this.data.getAttacked();
    }
    
    public int getAttackerKillCount() {
        return this.data.getAttackerKills();
    }
    
    public int getAttackedKillCount() {
        return this.data.getAttackedKills();
    }
    
    public int getWinnerClanId() {
        return this.data.getWinnerClan();
    }
    
    public long getStartTime() {
        return this.data.getStartTime();
    }
    
    public long getEndTime() {
        return this.data.getEndTime();
    }
    
    public ClanWarState getState() {
        return this.data.getState();
    }
    
    public int getKillToStart() {
        return (this.data.getState() == ClanWarState.BLOOD_DECLARATION) ? (5 - this.data.getAttackedKills()) : 0;
    }
    
    public int getRemainingTime() {
        return (int)TimeUnit.SECONDS.convert(this.data.getStartTime() + ClanWar.TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR, TimeUnit.MILLISECONDS);
    }
    
    public Clan getOpposingClan(final Clan clan) {
        return (this.data.getAttacker() == clan.getId()) ? ClanTable.getInstance().getClan(this.data.getAttacked()) : ClanTable.getInstance().getClan(this.data.getAttacker());
    }
    
    public void save() {
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).save(this.data);
    }
    
    static {
        TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR = TimeUnit.DAYS.toMillis(7L);
        TIME_TO_DELETION_AFTER_CANCELLATION = TimeUnit.DAYS.toMillis(5L);
        TIME_TO_DELETION_AFTER_DEFEAT = TimeUnit.DAYS.toMillis(21L);
    }
}
