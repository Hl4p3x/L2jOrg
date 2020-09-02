// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPool;
import org.slf4j.Logger;

public final class OlympiadGameTask implements Runnable
{
    protected static final Logger LOGGER;
    private static final int[] TELEPORT_TO_ARENA_TIMES;
    private static final int[] BATTLE_START_TIME_FIRST;
    private static final int[] BATTLE_START_TIME_SECOND;
    private static final int[] BATTLE_END_TIME_SECOND;
    private static final int[] TELEPORT_TO_TOWN_TIMES;
    private final OlympiadStadium _stadium;
    private AbstractOlympiadGame _game;
    private GameState _state;
    private boolean _needAnnounce;
    private int _countDown;
    
    public OlympiadGameTask(final OlympiadStadium stadium) {
        this._state = GameState.IDLE;
        this._needAnnounce = false;
        this._countDown = 0;
        (this._stadium = stadium).registerTask(this);
    }
    
    public final boolean isRunning() {
        return this._state != GameState.IDLE;
    }
    
    public final boolean isGameStarted() {
        return this._state.ordinal() >= GameState.GAME_STARTED.ordinal() && this._state.ordinal() <= GameState.CLEANUP.ordinal();
    }
    
    public final boolean isBattleStarted() {
        return this._state == GameState.BATTLE_IN_PROGRESS;
    }
    
    public final boolean isBattleFinished() {
        return this._state == GameState.TELEPORT_TO_TOWN;
    }
    
    public final boolean needAnnounce() {
        if (this._needAnnounce) {
            this._needAnnounce = false;
            return true;
        }
        return false;
    }
    
    public final OlympiadStadium getStadium() {
        return this._stadium;
    }
    
    public final AbstractOlympiadGame getGame() {
        return this._game;
    }
    
    public final void attachGame(final AbstractOlympiadGame game) {
        if (game != null && this._state != GameState.IDLE) {
            OlympiadGameTask.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/olympiad/OlympiadGameTask$GameState;)Ljava/lang/String;, this._state));
            return;
        }
        this._game = game;
        this._state = GameState.BEGIN;
        this._needAnnounce = false;
        ThreadPool.execute((Runnable)this);
    }
    
    @Override
    public final void run() {
        try {
            int delay = 1;
            switch (this._state) {
                case BEGIN: {
                    this._state = GameState.TELEPORT_TO_ARENA;
                    this._countDown = Config.ALT_OLY_WAIT_TIME;
                    break;
                }
                case TELEPORT_TO_ARENA: {
                    if (this._countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S);
                        sm.addInt(this._countDown);
                        this._game.broadcastPacket(sm);
                    }
                    if (this._countDown == 1) {
                        this._game.untransformPlayers();
                    }
                    delay = this.getDelay(OlympiadGameTask.TELEPORT_TO_ARENA_TIMES);
                    if (this._countDown <= 0) {
                        this._state = GameState.GAME_STARTED;
                        break;
                    }
                    break;
                }
                case GAME_STARTED: {
                    if (!this.startGame()) {
                        this._state = GameState.GAME_STOPPED;
                        break;
                    }
                    this._state = GameState.BATTLE_COUNTDOWN_FIRST;
                    this._countDown = OlympiadGameTask.BATTLE_START_TIME_FIRST[0];
                    this._stadium.updateZoneInfoForObservers();
                    delay = 5;
                    break;
                }
                case BATTLE_COUNTDOWN_FIRST: {
                    if (this._countDown > 0) {
                        if (this._countDown == 55) {
                            this._game.healPlayers();
                        }
                        else {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_WILL_START_IN_S1_SECOND_S);
                            sm.addInt(this._countDown);
                            this._stadium.broadcastPacket(sm);
                        }
                    }
                    delay = this.getDelay(OlympiadGameTask.BATTLE_START_TIME_FIRST);
                    if (this._countDown <= 0) {
                        this._game.resetDamage();
                        this._stadium.openDoors();
                        this._state = GameState.BATTLE_COUNTDOWN_SECOND;
                        this._countDown = OlympiadGameTask.BATTLE_START_TIME_SECOND[0];
                        delay = this.getDelay(OlympiadGameTask.BATTLE_START_TIME_SECOND);
                        break;
                    }
                    break;
                }
                case BATTLE_COUNTDOWN_SECOND: {
                    if (this._countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_WILL_START_IN_S1_SECOND_S);
                        sm.addInt(this._countDown);
                        this._stadium.broadcastPacket(sm);
                    }
                    delay = this.getDelay(OlympiadGameTask.BATTLE_START_TIME_SECOND);
                    if (this._countDown <= 0) {
                        this._state = GameState.BATTLE_STARTED;
                        break;
                    }
                    break;
                }
                case BATTLE_STARTED: {
                    this._countDown = 0;
                    this._state = GameState.BATTLE_IN_PROGRESS;
                    if (!this.startBattle()) {
                        this._state = GameState.GAME_STOPPED;
                        break;
                    }
                    break;
                }
                case BATTLE_IN_PROGRESS: {
                    this._countDown += 1000;
                    final int remaining = (int)((Config.ALT_OLY_BATTLE - this._countDown) / 1000L);
                    for (final int announceTime : OlympiadGameTask.BATTLE_END_TIME_SECOND) {
                        if (announceTime == remaining) {
                            final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_WILL_END_IN_S1_SECOND_S);
                            sm2.addInt(announceTime);
                            this._stadium.broadcastPacket(sm2);
                            break;
                        }
                    }
                    if (this.checkBattle() || this._countDown > Config.ALT_OLY_BATTLE) {
                        this._state = GameState.GAME_STOPPED;
                        break;
                    }
                    break;
                }
                case GAME_STOPPED: {
                    this._state = GameState.TELEPORT_TO_TOWN;
                    this._countDown = OlympiadGameTask.TELEPORT_TO_TOWN_TIMES[0];
                    this.stopGame();
                    delay = this.getDelay(OlympiadGameTask.TELEPORT_TO_TOWN_TIMES);
                    break;
                }
                case TELEPORT_TO_TOWN: {
                    if (this._countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECOND_S);
                        sm.addInt(this._countDown);
                        this._game.broadcastPacket(sm);
                    }
                    delay = this.getDelay(OlympiadGameTask.TELEPORT_TO_TOWN_TIMES);
                    if (this._countDown <= 0) {
                        this._state = GameState.CLEANUP;
                        break;
                    }
                    break;
                }
                case CLEANUP: {
                    this.cleanupGame();
                    this._state = GameState.IDLE;
                    this._game = null;
                    return;
                }
            }
            ThreadPool.schedule((Runnable)this, (long)(delay * 1000));
        }
        catch (Exception e) {
            switch (this._state) {
                case GAME_STOPPED:
                case TELEPORT_TO_TOWN:
                case CLEANUP:
                case IDLE: {
                    OlympiadGameTask.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                    this._state = GameState.IDLE;
                    this._game = null;
                }
                default: {
                    OlympiadGameTask.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/olympiad/OlympiadGameTask$GameState;Ljava/lang/String;)Ljava/lang/String;, this._state, e.getMessage()), (Throwable)e);
                    this._state = GameState.GAME_STOPPED;
                    ThreadPool.schedule((Runnable)this, 1000L);
                    break;
                }
            }
        }
    }
    
    private int getDelay(final int[] times) {
        for (int i = 0; i < times.length - 1; ++i) {
            final int time = times[i];
            if (time < this._countDown) {
                final int delay = this._countDown - time;
                this._countDown = time;
                return delay;
            }
        }
        this._countDown = -1;
        return 1;
    }
    
    private boolean startGame() {
        try {
            if (this._game.checkDefaulted()) {
                return false;
            }
            this._stadium.closeDoors();
            if (this._game.needBuffers()) {
                this._stadium.spawnBuffers();
            }
            if (!this._game.portPlayersToArena(this._stadium.getZone().getSpawns(), this._stadium.getInstance())) {
                return false;
            }
            this._game.removals();
            this._needAnnounce = true;
            OlympiadGameManager.getInstance().startBattle();
            return true;
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
            return false;
        }
    }
    
    private boolean startBattle() {
        try {
            if (this._game.needBuffers()) {
                this._stadium.deleteBuffers();
            }
            if (this._game.checkBattleStatus() && this._game.makeCompetitionStart()) {
                this._game.broadcastOlympiadInfo(this._stadium);
                this._stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_HAS_STARTED_FIGHT));
                this._stadium.updateZoneStatusForCharactersInside();
                return true;
            }
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        return false;
    }
    
    private boolean checkBattle() {
        try {
            return this._game.haveWinner();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
            return true;
        }
    }
    
    private void stopGame() {
        try {
            this._game.validateWinner(this._stadium);
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._game.cleanEffects();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._game.makePlayersInvul();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._stadium.updateZoneStatusForCharactersInside();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    private void cleanupGame() {
        try {
            this._game.removePlayersInvul();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._game.playersStatusBack();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._game.portPlayersBack();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._game.clearPlayers();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        try {
            this._stadium.closeDoors();
        }
        catch (Exception e) {
            OlympiadGameTask.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)OlympiadGameTask.class);
        TELEPORT_TO_ARENA_TIMES = new int[] { 120, 60, 30, 15, 10, 5, 4, 3, 2, 1, 0 };
        BATTLE_START_TIME_FIRST = new int[] { 60, 55, 50, 40, 30, 20, 10, 0 };
        BATTLE_START_TIME_SECOND = new int[] { 10, 5, 4, 3, 2, 1, 0 };
        BATTLE_END_TIME_SECOND = new int[] { 120, 60, 30, 10, 5 };
        TELEPORT_TO_TOWN_TIMES = new int[] { 40, 30, 20, 10, 5, 4, 3, 2, 1, 0 };
    }
    
    private enum GameState
    {
        BEGIN, 
        TELEPORT_TO_ARENA, 
        GAME_STARTED, 
        BATTLE_COUNTDOWN_FIRST, 
        BATTLE_COUNTDOWN_SECOND, 
        BATTLE_STARTED, 
        BATTLE_IN_PROGRESS, 
        GAME_STOPPED, 
        TELEPORT_TO_TOWN, 
        CLEANUP, 
        IDLE;
    }
}
