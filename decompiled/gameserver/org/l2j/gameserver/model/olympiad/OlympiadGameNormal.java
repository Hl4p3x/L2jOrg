// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchResult;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadUserInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Rnd;
import java.util.Set;

public abstract class OlympiadGameNormal extends AbstractOlympiadGame
{
    protected int _damageP1;
    protected int _damageP2;
    protected Participant _playerOne;
    protected Participant _playerTwo;
    
    protected OlympiadGameNormal(final int id, final Participant[] opponents) {
        super(id);
        this._damageP1 = 0;
        this._damageP2 = 0;
        this._playerOne = opponents[0];
        this._playerTwo = opponents[1];
        this._playerOne.getPlayer().setOlympiadGameId(id);
        this._playerTwo.getPlayer().setOlympiadGameId(id);
    }
    
    protected static Participant[] createListOfParticipants(final Set<Integer> set) {
        if (set == null || set.isEmpty() || set.size() < 2) {
            return null;
        }
        int playerOneObjectId = 0;
        int playerTwoObjectId = 0;
        Player playerOne = null;
        Player playerTwo = null;
        while (set.size() > 1) {
            int random = Rnd.get(set.size());
            Iterator<Integer> iter = set.iterator();
            while (iter.hasNext()) {
                playerOneObjectId = iter.next();
                if (--random < 0) {
                    iter.remove();
                    break;
                }
            }
            playerOne = World.getInstance().findPlayer(playerOneObjectId);
            if (playerOne != null) {
                if (!playerOne.isOnline()) {
                    continue;
                }
                random = Rnd.get(set.size());
                iter = set.iterator();
                while (iter.hasNext()) {
                    playerTwoObjectId = iter.next();
                    if (--random < 0) {
                        iter.remove();
                        break;
                    }
                }
                playerTwo = World.getInstance().findPlayer(playerTwoObjectId);
                if (playerTwo != null && playerTwo.isOnline()) {
                    final Participant[] result = { new Participant(playerOne, 1), new Participant(playerTwo, 2) };
                    return result;
                }
                set.add(playerOneObjectId);
            }
        }
        return null;
    }
    
    protected static void saveResults(final Participant one, final Participant two, final int winner, final long startTime, final long fightTime, final CompetitionType type) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO olympiad_fights (charOneId, charTwoId, charOneClass, charTwoClass, winner, start, time, classed) values(?,?,?,?,?,?,?,?)");
                try {
                    statement.setInt(1, one.getObjectId());
                    statement.setInt(2, two.getObjectId());
                    statement.setInt(3, one.getBaseClass());
                    statement.setInt(4, two.getBaseClass());
                    statement.setInt(5, winner);
                    statement.setLong(6, startTime);
                    statement.setLong(7, fightTime);
                    statement.setInt(8, (type == CompetitionType.CLASSED) ? 1 : 0);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            OlympiadGameNormal.LOGGER.error("SQL exception while saving olympiad fight.", (Throwable)e);
        }
    }
    
    @Override
    public final boolean containsParticipant(final int playerId) {
        return (this._playerOne != null && this._playerOne.getObjectId() == playerId) || (this._playerTwo != null && this._playerTwo.getObjectId() == playerId);
    }
    
    @Override
    public final void sendOlympiadInfo(final Creature player) {
        player.sendPacket(new ExOlympiadUserInfo(this._playerOne));
        player.sendPacket(new ExOlympiadUserInfo(this._playerTwo));
    }
    
    @Override
    public final void broadcastOlympiadInfo(final OlympiadStadium stadium) {
        stadium.broadcastPacket(new ExOlympiadUserInfo(this._playerOne));
        stadium.broadcastPacket(new ExOlympiadUserInfo(this._playerTwo));
    }
    
    @Override
    protected final void broadcastPacket(final ServerPacket packet) {
        if (this._playerOne.updatePlayer()) {
            this._playerOne.getPlayer().sendPacket(packet);
        }
        if (this._playerTwo.updatePlayer()) {
            this._playerTwo.getPlayer().sendPacket(packet);
        }
    }
    
    @Override
    protected final boolean portPlayersToArena(final List<Location> spawns, final Instance instance) {
        boolean result = true;
        try {
            result &= AbstractOlympiadGame.portPlayerToArena(this._playerOne, spawns.get(0), this._stadiumId, instance);
            result &= AbstractOlympiadGame.portPlayerToArena(this._playerTwo, spawns.get(spawns.size() / 2), this._stadiumId, instance);
        }
        catch (Exception e) {
            OlympiadGameNormal.LOGGER.warn("", (Throwable)e);
            return false;
        }
        return result;
    }
    
    @Override
    protected boolean needBuffers() {
        return true;
    }
    
    @Override
    protected final void removals() {
        if (this._aborted) {
            return;
        }
        AbstractOlympiadGame.removals(this._playerOne.getPlayer(), true);
        AbstractOlympiadGame.removals(this._playerTwo.getPlayer(), true);
    }
    
    @Override
    protected final boolean makeCompetitionStart() {
        if (!super.makeCompetitionStart()) {
            return false;
        }
        if (this._playerOne.getPlayer() == null || this._playerTwo.getPlayer() == null) {
            return false;
        }
        this._playerOne.getPlayer().setIsOlympiadStart(true);
        this._playerOne.getPlayer().updateEffectIcons();
        this._playerTwo.getPlayer().setIsOlympiadStart(true);
        this._playerTwo.getPlayer().updateEffectIcons();
        return true;
    }
    
    @Override
    protected final void cleanEffects() {
        if (this._playerOne.getPlayer() != null && !this._playerOne.isDefaulted() && !this._playerOne.isDisconnected() && this._playerOne.getPlayer().getOlympiadGameId() == this._stadiumId) {
            AbstractOlympiadGame.cleanEffects(this._playerOne.getPlayer());
        }
        if (this._playerTwo.getPlayer() != null && !this._playerTwo.isDefaulted() && !this._playerTwo.isDisconnected() && this._playerTwo.getPlayer().getOlympiadGameId() == this._stadiumId) {
            AbstractOlympiadGame.cleanEffects(this._playerTwo.getPlayer());
        }
    }
    
    @Override
    protected final void portPlayersBack() {
        if (this._playerOne.getPlayer() != null && !this._playerOne.isDefaulted() && !this._playerOne.isDisconnected()) {
            AbstractOlympiadGame.portPlayerBack(this._playerOne.getPlayer());
        }
        if (this._playerTwo.getPlayer() != null && !this._playerTwo.isDefaulted() && !this._playerTwo.isDisconnected()) {
            AbstractOlympiadGame.portPlayerBack(this._playerTwo.getPlayer());
        }
    }
    
    @Override
    protected final void playersStatusBack() {
        if (this._playerOne.getPlayer() != null && !this._playerOne.isDefaulted() && !this._playerOne.isDisconnected() && this._playerOne.getPlayer().getOlympiadGameId() == this._stadiumId) {
            AbstractOlympiadGame.playerStatusBack(this._playerOne.getPlayer());
        }
        if (this._playerTwo.getPlayer() != null && !this._playerTwo.isDefaulted() && !this._playerTwo.isDisconnected() && this._playerTwo.getPlayer().getOlympiadGameId() == this._stadiumId) {
            AbstractOlympiadGame.playerStatusBack(this._playerTwo.getPlayer());
        }
    }
    
    @Override
    protected final void clearPlayers() {
        this._playerOne.setPlayer(null);
        this._playerOne = null;
        this._playerTwo.setPlayer(null);
        this._playerTwo = null;
    }
    
    @Override
    protected final void handleDisconnect(final Player player) {
        if (player.getObjectId() == this._playerOne.getObjectId()) {
            this._playerOne.setDisconnected(true);
        }
        else if (player.getObjectId() == this._playerTwo.getObjectId()) {
            this._playerTwo.setDisconnected(true);
        }
    }
    
    @Override
    protected final boolean checkBattleStatus() {
        return !this._aborted && this._playerOne.getPlayer() != null && !this._playerOne.isDisconnected() && this._playerTwo.getPlayer() != null && !this._playerTwo.isDisconnected();
    }
    
    @Override
    protected final boolean haveWinner() {
        if (!this.checkBattleStatus()) {
            return true;
        }
        boolean playerOneLost = true;
        try {
            if (this._playerOne.getPlayer().getOlympiadGameId() == this._stadiumId) {
                playerOneLost = this._playerOne.getPlayer().isDead();
            }
        }
        catch (Exception e) {
            playerOneLost = true;
        }
        boolean playerTwoLost = true;
        try {
            if (this._playerTwo.getPlayer().getOlympiadGameId() == this._stadiumId) {
                playerTwoLost = this._playerTwo.getPlayer().isDead();
            }
        }
        catch (Exception e2) {
            playerTwoLost = true;
        }
        return playerOneLost || playerTwoLost;
    }
    
    @Override
    protected void validateWinner(final OlympiadStadium stadium) {
        if (this._aborted) {
            return;
        }
        ExOlympiadMatchResult result = null;
        boolean tie = false;
        int winside = 0;
        final List<OlympiadInfo> list1 = new ArrayList<OlympiadInfo>(1);
        final List<OlympiadInfo> list2 = new ArrayList<OlympiadInfo>(1);
        final boolean _pOneCrash = this._playerOne.getPlayer() == null || this._playerOne.isDisconnected();
        final boolean _pTwoCrash = this._playerTwo.getPlayer() == null || this._playerTwo.isDisconnected();
        final int playerOnePoints = this._playerOne.getStats().getInt("olympiad_points");
        final int playerTwoPoints = this._playerTwo.getStats().getInt("olympiad_points");
        int pointDiff = Math.min(playerOnePoints, playerTwoPoints) / this.getDivider();
        if (pointDiff <= 0) {
            pointDiff = 1;
        }
        else if (pointDiff > Config.ALT_OLY_MAX_POINTS) {
            pointDiff = Config.ALT_OLY_MAX_POINTS;
        }
        Label_0566: {
            if (!this._playerOne.isDefaulted()) {
                if (!this._playerTwo.isDefaulted()) {
                    break Label_0566;
                }
            }
            try {
                if (this._playerOne.isDefaulted()) {
                    try {
                        final int points = Math.min(playerOnePoints / 3, Config.ALT_OLY_MAX_POINTS);
                        this.removePointsFromParticipant(this._playerOne, points);
                        list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints - points, -points));
                        winside = 2;
                        if (Config.ALT_OLY_LOG_FIGHTS) {
                            OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;ILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, this._playerOne.getName(), this._playerOne, this._playerTwo, points, this.getType()));
                        }
                    }
                    catch (Exception e) {
                        OlympiadGameNormal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
                    }
                }
                if (this._playerTwo.isDefaulted()) {
                    try {
                        final int points = Math.min(playerTwoPoints / 3, Config.ALT_OLY_MAX_POINTS);
                        this.removePointsFromParticipant(this._playerTwo, points);
                        list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints - points, -points));
                        if (winside == 2) {
                            tie = true;
                        }
                        else {
                            winside = 1;
                        }
                        if (Config.ALT_OLY_LOG_FIGHTS) {
                            OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;ILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, this._playerTwo.getName(), this._playerOne, this._playerTwo, points, this.getType()));
                        }
                    }
                    catch (Exception e) {
                        OlympiadGameNormal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
                    }
                }
                if (winside == 1) {
                    result = new ExOlympiadMatchResult(tie, winside, list1, list2);
                }
                else {
                    result = new ExOlympiadMatchResult(tie, winside, list2, list1);
                }
                stadium.broadcastPacket(result);
                return;
            }
            catch (Exception e) {
                OlympiadGameNormal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
                return;
            }
        }
        Label_1396: {
            if (!_pOneCrash) {
                if (!_pTwoCrash) {
                    break Label_1396;
                }
            }
            try {
                if (_pTwoCrash && !_pOneCrash) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
                    sm.addString(this._playerOne.getName());
                    stadium.broadcastPacket(sm);
                    this._playerOne.updateStat("competitions_won", 1);
                    this.addPointsToParticipant(this._playerOne, pointDiff);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints + pointDiff, pointDiff));
                    this._playerTwo.updateStat("competitions_lost", 1);
                    this.removePointsFromParticipant(this._playerTwo, pointDiff);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints - pointDiff, -pointDiff));
                    winside = 1;
                    AbstractOlympiadGame.rewardParticipant(this._playerOne.getPlayer(), Config.ALT_OLY_WINNER_REWARD);
                    if (Config.ALT_OLY_LOG_FIGHTS) {
                        OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;ILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, this._playerTwo.getName(), this._playerOne, this._playerTwo, pointDiff, this.getType()));
                    }
                }
                else if (_pOneCrash && !_pTwoCrash) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
                    sm.addString(this._playerTwo.getName());
                    stadium.broadcastPacket(sm);
                    this._playerTwo.updateStat("competitions_won", 1);
                    this.addPointsToParticipant(this._playerTwo, pointDiff);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints + pointDiff, pointDiff));
                    this._playerOne.updateStat("competitions_lost", 1);
                    this.removePointsFromParticipant(this._playerOne, pointDiff);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints - pointDiff, -pointDiff));
                    winside = 2;
                    AbstractOlympiadGame.rewardParticipant(this._playerTwo.getPlayer(), Config.ALT_OLY_WINNER_REWARD);
                    if (Config.ALT_OLY_LOG_FIGHTS) {
                        OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;ILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, this._playerOne.getName(), this._playerOne, this._playerTwo, pointDiff, this.getType()));
                    }
                }
                else if (_pOneCrash && _pTwoCrash) {
                    stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE));
                    this._playerOne.updateStat("competitions_lost", 1);
                    this.removePointsFromParticipant(this._playerOne, pointDiff);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints - pointDiff, -pointDiff));
                    this._playerTwo.updateStat("competitions_lost", 1);
                    this.removePointsFromParticipant(this._playerTwo, pointDiff);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints - pointDiff, -pointDiff));
                    tie = true;
                    if (Config.ALT_OLY_LOG_FIGHTS) {
                        OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;ILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, this._playerOne.getName(), this._playerOne, this._playerTwo, pointDiff, this.getType()));
                    }
                }
                this._playerOne.updateStat("competitions_done", 1);
                this._playerTwo.updateStat("competitions_done", 1);
                this._playerOne.updateStat("competitions_done_week", 1);
                this._playerTwo.updateStat("competitions_done_week", 1);
                if (winside == 1) {
                    result = new ExOlympiadMatchResult(tie, winside, list1, list2);
                }
                else {
                    result = new ExOlympiadMatchResult(tie, winside, list2, list1);
                }
                stadium.broadcastPacket(result);
                return;
            }
            catch (Exception e) {
                OlympiadGameNormal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
                return;
            }
            try {
                String winner = "draw";
                final long _fightTime = System.currentTimeMillis() - this._startTime;
                double playerOneHp = 0.0;
                if (this._playerOne.getPlayer() != null && !this._playerOne.getPlayer().isDead()) {
                    playerOneHp = this._playerOne.getPlayer().getCurrentHp() + this._playerOne.getPlayer().getCurrentCp();
                    if (playerOneHp < 0.5) {
                        playerOneHp = 0.0;
                    }
                }
                double playerTwoHp = 0.0;
                if (this._playerTwo.getPlayer() != null && !this._playerTwo.getPlayer().isDead()) {
                    playerTwoHp = this._playerTwo.getPlayer().getCurrentHp() + this._playerTwo.getPlayer().getCurrentCp();
                    if (playerTwoHp < 0.5) {
                        playerTwoHp = 0.0;
                    }
                }
                this._playerOne.updatePlayer();
                this._playerTwo.updatePlayer();
                if ((this._playerOne.getPlayer() == null || !this._playerOne.getPlayer().isOnline()) && (this._playerTwo.getPlayer() == null || !this._playerTwo.getPlayer().isOnline())) {
                    this._playerOne.updateStat("competitions_drawn", 1);
                    this._playerTwo.updateStat("competitions_drawn", 1);
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);
                    stadium.broadcastPacket(sm);
                }
                else if (this._playerTwo.getPlayer() == null || !this._playerTwo.getPlayer().isOnline() || (playerTwoHp == 0.0 && playerOneHp != 0.0) || (this._damageP1 > this._damageP2 && playerTwoHp != 0.0 && playerOneHp != 0.0)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
                    sm.addString(this._playerOne.getName());
                    stadium.broadcastPacket(sm);
                    this._playerOne.updateStat("competitions_won", 1);
                    this._playerTwo.updateStat("competitions_lost", 1);
                    this.addPointsToParticipant(this._playerOne, pointDiff);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints + pointDiff, pointDiff));
                    this.removePointsFromParticipant(this._playerTwo, pointDiff);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints - pointDiff, -pointDiff));
                    winner = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._playerOne.getName());
                    winside = 1;
                    saveResults(this._playerOne, this._playerTwo, 1, this._startTime, _fightTime, this.getType());
                    AbstractOlympiadGame.rewardParticipant(this._playerOne.getPlayer(), Config.ALT_OLY_WINNER_REWARD);
                    AbstractOlympiadGame.rewardParticipant(this._playerTwo.getPlayer(), Config.ALT_OLY_LOSER_REWARD);
                }
                else if (this._playerOne.getPlayer() == null || !this._playerOne.getPlayer().isOnline() || (playerOneHp == 0.0 && playerTwoHp != 0.0) || (this._damageP2 > this._damageP1 && playerOneHp != 0.0 && playerTwoHp != 0.0)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
                    sm.addString(this._playerTwo.getName());
                    stadium.broadcastPacket(sm);
                    this._playerTwo.updateStat("competitions_won", 1);
                    this._playerOne.updateStat("competitions_lost", 1);
                    this.addPointsToParticipant(this._playerTwo, pointDiff);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints + pointDiff, pointDiff));
                    this.removePointsFromParticipant(this._playerOne, pointDiff);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints - pointDiff, -pointDiff));
                    winner = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._playerTwo.getName());
                    winside = 2;
                    saveResults(this._playerOne, this._playerTwo, 2, this._startTime, _fightTime, this.getType());
                    AbstractOlympiadGame.rewardParticipant(this._playerTwo.getPlayer(), Config.ALT_OLY_WINNER_REWARD);
                    AbstractOlympiadGame.rewardParticipant(this._playerOne.getPlayer(), Config.ALT_OLY_LOSER_REWARD);
                }
                else {
                    saveResults(this._playerOne, this._playerTwo, 0, this._startTime, _fightTime, this.getType());
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);
                    stadium.broadcastPacket(sm);
                    int value = Math.min(playerOnePoints / this.getDivider(), Config.ALT_OLY_MAX_POINTS);
                    this.removePointsFromParticipant(this._playerOne, value);
                    list1.add(new OlympiadInfo(this._playerOne.getName(), this._playerOne.getClanName(), this._playerOne.getClanId(), this._playerOne.getBaseClass(), this._damageP1, playerOnePoints - value, -value));
                    value = Math.min(playerTwoPoints / this.getDivider(), Config.ALT_OLY_MAX_POINTS);
                    this.removePointsFromParticipant(this._playerTwo, value);
                    list2.add(new OlympiadInfo(this._playerTwo.getName(), this._playerTwo.getClanName(), this._playerTwo.getClanId(), this._playerTwo.getBaseClass(), this._damageP2, playerTwoPoints - value, -value));
                    tie = true;
                }
                this._playerOne.updateStat("competitions_done", 1);
                this._playerTwo.updateStat("competitions_done", 1);
                this._playerOne.updateStat("competitions_done_week", 1);
                this._playerTwo.updateStat("competitions_done_week", 1);
                if (winside == 1) {
                    result = new ExOlympiadMatchResult(tie, winside, list1, list2);
                }
                else {
                    result = new ExOlympiadMatchResult(tie, winside, list2, list1);
                }
                stadium.broadcastPacket(result);
                if (Config.ALT_OLY_LOG_FIGHTS) {
                    OlympiadGameNormal.LOGGER_OLYMPIAD.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lorg/l2j/gameserver/model/olympiad/Participant;Lorg/l2j/gameserver/model/olympiad/Participant;DDIIILorg/l2j/gameserver/model/olympiad/CompetitionType;)Ljava/lang/String;, winner, this._playerOne.getName(), this._playerOne, this._playerTwo, playerOneHp, playerTwoHp, this._damageP1, this._damageP2, pointDiff, this.getType()));
                }
            }
            catch (Exception e) {
                OlympiadGameNormal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
        }
    }
    
    @Override
    protected final void addDamage(final Player player, final int damage) {
        if (this._playerOne.getPlayer() == null || this._playerTwo.getPlayer() == null) {
            return;
        }
        if (player == this._playerOne.getPlayer()) {
            this._damageP1 += damage;
        }
        else if (player == this._playerTwo.getPlayer()) {
            this._damageP2 += damage;
        }
    }
    
    @Override
    public final String[] getPlayerNames() {
        return new String[] { this._playerOne.getName(), this._playerTwo.getName() };
    }
    
    public boolean checkDefaulted() {
        this._playerOne.updatePlayer();
        this._playerTwo.updatePlayer();
        SystemMessage reason = AbstractOlympiadGame.checkDefaulted(this._playerOne.getPlayer());
        if (reason != null) {
            this._playerOne.setDefaulted(true);
            if (this._playerTwo.getPlayer() != null) {
                this._playerTwo.getPlayer().sendPacket(reason);
            }
        }
        reason = AbstractOlympiadGame.checkDefaulted(this._playerTwo.getPlayer());
        if (reason != null) {
            this._playerTwo.setDefaulted(true);
            if (this._playerOne.getPlayer() != null) {
                this._playerOne.getPlayer().sendPacket(reason);
            }
        }
        return this._playerOne.isDefaulted() || this._playerTwo.isDefaulted();
    }
    
    public final void resetDamage() {
        this._damageP1 = 0;
        this._damageP2 = 0;
    }
    
    @Override
    protected void healPlayers() {
        final Player player1 = this._playerOne.getPlayer();
        if (player1 != null) {
            player1.setCurrentCp(player1.getMaxCp());
            player1.setCurrentHp(player1.getMaxHp());
            player1.setCurrentMp(player1.getMaxMp());
        }
        final Player player2 = this._playerTwo.getPlayer();
        if (player2 != null) {
            player2.setCurrentCp(player2.getMaxCp());
            player2.setCurrentHp(player2.getMaxHp());
            player2.setCurrentMp(player2.getMaxMp());
        }
    }
    
    @Override
    protected void untransformPlayers() {
        final Player player1 = this._playerOne.getPlayer();
        if (player1 != null && player1.isTransformed()) {
            player1.stopTransformation(true);
        }
        final Player player2 = this._playerTwo.getPlayer();
        if (player2 != null && player2.isTransformed()) {
            player2.stopTransformation(true);
        }
    }
    
    public final void makePlayersInvul() {
        if (this._playerOne.getPlayer() != null) {
            this._playerOne.getPlayer().setIsInvul(true);
        }
        if (this._playerTwo.getPlayer() != null) {
            this._playerTwo.getPlayer().setIsInvul(true);
        }
    }
    
    public final void removePlayersInvul() {
        if (this._playerOne.getPlayer() != null) {
            this._playerOne.getPlayer().setIsInvul(false);
        }
        if (this._playerTwo.getPlayer() != null) {
            this._playerTwo.getPlayer().setIsInvul(false);
        }
    }
}
