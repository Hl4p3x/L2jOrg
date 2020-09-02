// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.eventengine.AbstractEvent;

public abstract class OlympiadMatch extends AbstractEvent implements Runnable
{
    protected MatchState state;
    
    OlympiadMatch() {
        this.state = MatchState.CREATED;
    }
    
    @Override
    public void run() {
        switch (this.state) {
            case CREATED: {
                this.start();
                break;
            }
            case STARTED: {
                this.teleportToArena();
                break;
            }
        }
    }
    
    private void teleportToArena() {
        this.state = MatchState.WARM_UP;
    }
    
    private void start() {
        this.state = MatchState.STARTED;
        this.sendMessage(SystemMessageId.AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA);
        ThreadPool.schedule((Runnable)this, 1L, TimeUnit.MINUTES);
    }
    
    public abstract void addParticipant(final Player player);
    
    static OlympiadMatch of(final OlympiadRuleType type) {
        return new OlympiadClassLessMatch();
    }
}
