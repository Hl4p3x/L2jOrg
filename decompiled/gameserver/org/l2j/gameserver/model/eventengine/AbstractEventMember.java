// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEventMember<T extends AbstractEvent>
{
    private final T _event;
    private final AtomicInteger _score;
    private final Player player;
    
    public AbstractEventMember(final Player player, final T event) {
        this._score = new AtomicInteger();
        this.player = player;
        this._event = event;
    }
    
    public final int getObjectId() {
        return this.player.getObjectId();
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void sendPacket(final ServerPacket... packets) {
        final Player player = this.getPlayer();
        if (player != null) {
            for (final ServerPacket packet : packets) {
                player.sendPacket(packet);
            }
        }
    }
    
    public int getClassId() {
        final Player player = this.getPlayer();
        if (player != null) {
            return player.getClassId().getId();
        }
        return 0;
    }
    
    public int getScore() {
        return this._score.get();
    }
    
    public void setScore(final int score) {
        this._score.set(score);
    }
    
    public int incrementScore() {
        return this._score.incrementAndGet();
    }
    
    public int decrementScore() {
        return this._score.decrementAndGet();
    }
    
    public int addScore(final int score) {
        return this._score.addAndGet(score);
    }
    
    public final T getEvent() {
        return this._event;
    }
}
