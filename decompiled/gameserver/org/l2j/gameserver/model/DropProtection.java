// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.Creature;

public class DropProtection implements Runnable
{
    private static final long PROTECTED_MILLIS_TIME = 15000L;
    private volatile boolean _isProtected;
    private Creature _owner;
    private ScheduledFuture<?> _task;
    
    public DropProtection() {
        this._isProtected = false;
        this._owner = null;
        this._task = null;
    }
    
    @Override
    public synchronized void run() {
        this._isProtected = false;
        this._owner = null;
        this._task = null;
    }
    
    public boolean isProtected() {
        return this._isProtected;
    }
    
    public Creature getOwner() {
        return this._owner;
    }
    
    public synchronized boolean tryPickUp(final Player actor) {
        return !this._isProtected || this._owner == actor || (this._owner.getParty() != null && this._owner.getParty() == actor.getParty());
    }
    
    public boolean tryPickUp(final Pet pet) {
        return this.tryPickUp(pet.getOwner());
    }
    
    public synchronized void unprotect() {
        if (this._task != null) {
            this._task.cancel(false);
        }
        this._isProtected = false;
        this._owner = null;
        this._task = null;
    }
    
    public synchronized void protect(final Creature character) {
        this.unprotect();
        this._isProtected = true;
        this._owner = character;
        if (this._owner == null) {
            throw new NullPointerException("Trying to protect dropped item to null owner");
        }
        this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)this, 15000L);
    }
}
