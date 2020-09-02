// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.List;
import org.slf4j.Logger;

public class OlympiadGameManager implements Runnable
{
    private static final Logger LOGGER;
    private static final int STADIUM_COUNT = 80;
    private final List<OlympiadStadium> _tasks;
    private volatile boolean _battleStarted;
    private int _delay;
    
    private OlympiadGameManager() {
        this._battleStarted = false;
        this._delay = 0;
        final Collection<OlympiadStadiumZone> zones = ZoneManager.getInstance().getAllZones(OlympiadStadiumZone.class);
        if (zones == null || zones.isEmpty()) {
            throw new Error("No olympiad stadium zones defined !");
        }
        final OlympiadStadiumZone[] array = zones.toArray(new OlympiadStadiumZone[zones.size()]);
        this._tasks = new ArrayList<OlympiadStadium>(80);
        final int zonesCount = array.length;
        for (int i = 0; i < 80; ++i) {
            final OlympiadStadium stadium = new OlympiadStadium(array[i % zonesCount], i);
            stadium.registerTask(new OlympiadGameTask(stadium));
            this._tasks.add(stadium);
        }
        OlympiadGameManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._tasks.size()));
    }
    
    public final boolean isBattleStarted() {
        return this._battleStarted;
    }
    
    protected final void startBattle() {
        this._battleStarted = true;
    }
    
    @Override
    public final void run() {
        if (Olympiad.getInstance().isMatchesInProgress()) {
            List<Set<Integer>> readyClassed = OlympiadManager.getInstance().hasEnoughRegisteredClassed();
            boolean readyNonClassed = OlympiadManager.getInstance().hasEnoughRegisteredNonClassed();
            if (readyClassed != null || readyNonClassed) {
                this._delay = 0;
                for (int i = 0; i < this._tasks.size(); ++i) {
                    final OlympiadGameTask task = this._tasks.get(i).getTask();
                    synchronized (task) {
                        if (!task.isRunning()) {
                            if (readyClassed != null) {
                                final AbstractOlympiadGame newGame = OlympiadGameClassed.createGame(i, readyClassed);
                                if (newGame != null) {
                                    task.attachGame(newGame);
                                    continue;
                                }
                                readyClassed = null;
                            }
                            if (readyNonClassed) {
                                final AbstractOlympiadGame newGame = OlympiadGameNonClassed.createGame(i, OlympiadManager.getInstance().getRegisteredNonClassBased());
                                if (newGame != null) {
                                    task.attachGame(newGame);
                                    continue;
                                }
                                readyNonClassed = false;
                            }
                        }
                    }
                    if (readyClassed == null && !readyNonClassed) {
                        break;
                    }
                }
            }
            else {
                ++this._delay;
                if (this._delay >= 10) {
                    for (final Integer id : OlympiadManager.getInstance().getRegisteredNonClassBased()) {
                        if (id == null) {
                            continue;
                        }
                        final Player noble = World.getInstance().findPlayer(id);
                        if (noble == null) {
                            continue;
                        }
                        noble.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAMES_MAY_BE_DELAYED_DUE_TO_AN_INSUFFICIENT_NUMBER_OF_PLAYERS_WAITING));
                    }
                    for (final Set<Integer> list : OlympiadManager.getInstance().getRegisteredClassBased().values()) {
                        for (final Integer id2 : list) {
                            if (id2 == null) {
                                continue;
                            }
                            final Player noble2 = World.getInstance().findPlayer(id2);
                            if (noble2 == null) {
                                continue;
                            }
                            noble2.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAMES_MAY_BE_DELAYED_DUE_TO_AN_INSUFFICIENT_NUMBER_OF_PLAYERS_WAITING));
                        }
                    }
                    this._delay = 0;
                }
            }
        }
        else if (this.isAllTasksFinished()) {
            OlympiadManager.getInstance().clearRegistered();
            this._battleStarted = false;
            OlympiadGameManager.LOGGER.info("Olympiad System: All current games finished.");
        }
    }
    
    public final boolean isAllTasksFinished() {
        for (final OlympiadStadium stadium : this._tasks) {
            final OlympiadGameTask task = stadium.getTask();
            if (task.isRunning()) {
                return false;
            }
        }
        return true;
    }
    
    public final OlympiadGameTask getOlympiadTask(final int id) {
        if (id < 0 || id >= this._tasks.size()) {
            return null;
        }
        return this._tasks.get(id).getTask();
    }
    
    public final int getNumberOfStadiums() {
        return this._tasks.size();
    }
    
    public final void notifyCompetitorDamage(final Player attacker, final int damage) {
        if (attacker == null) {
            return;
        }
        final int id = attacker.getOlympiadGameId();
        if (id < 0 || id >= this._tasks.size()) {
            return;
        }
        final AbstractOlympiadGame game = this._tasks.get(id).getTask().getGame();
        if (game != null) {
            game.addDamage(attacker, damage);
        }
    }
    
    public List<OlympiadStadium> getTasks() {
        return this._tasks;
    }
    
    public static OlympiadGameManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)OlympiadGameManager.class);
    }
    
    private static class Singleton
    {
        private static final OlympiadGameManager INSTANCE;
        
        static {
            INSTANCE = new OlympiadGameManager();
        }
    }
}
