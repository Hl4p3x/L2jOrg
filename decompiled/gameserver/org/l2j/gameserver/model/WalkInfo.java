// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.instancemanager.WalkingManager;
import java.util.concurrent.ScheduledFuture;

public class WalkInfo
{
    private final String _routeName;
    private ScheduledFuture<?> _walkCheckTask;
    private boolean _blocked;
    private boolean _suspended;
    private boolean _stoppedByAttack;
    private int _currentNode;
    private boolean _forward;
    private long _lastActionTime;
    
    public WalkInfo(final String routeName) {
        this._blocked = false;
        this._suspended = false;
        this._stoppedByAttack = false;
        this._currentNode = 0;
        this._forward = true;
        this._routeName = routeName;
    }
    
    public WalkRoute getRoute() {
        return WalkingManager.getInstance().getRoute(this._routeName);
    }
    
    public NpcWalkerNode getCurrentNode() {
        return this.getRoute().getNodeList().get(Math.min(Math.max(0, this._currentNode), this.getRoute().getNodeList().size() - 1));
    }
    
    public synchronized void calculateNextNode(final Npc npc) {
        if (this.getRoute().getRepeatType() == 3) {
            int newNode;
            for (newNode = this._currentNode; newNode == this._currentNode; newNode = Rnd.get(this.getRoute().getNodesCount())) {}
            this._currentNode = newNode;
        }
        else {
            if (this._forward) {
                ++this._currentNode;
            }
            else {
                --this._currentNode;
            }
            if (this._currentNode == this.getRoute().getNodesCount()) {
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcMoveRouteFinished(npc), npc);
                if (!this.getRoute().repeatWalk()) {
                    WalkingManager.getInstance().cancelMoving(npc);
                    return;
                }
                switch (this.getRoute().getRepeatType()) {
                    case 0: {
                        this._forward = false;
                        this._currentNode -= 2;
                        break;
                    }
                    case 1: {
                        this._currentNode = 0;
                        break;
                    }
                    case 2: {
                        npc.teleToLocation(npc.getSpawn().getLocation());
                        this._currentNode = 0;
                        break;
                    }
                }
            }
            else if (this._currentNode == -1) {
                this._currentNode = 1;
                this._forward = true;
            }
        }
    }
    
    public boolean isBlocked() {
        return this._blocked;
    }
    
    public void setBlocked(final boolean val) {
        this._blocked = val;
    }
    
    public boolean isSuspended() {
        return this._suspended;
    }
    
    public void setSuspended(final boolean val) {
        this._suspended = val;
    }
    
    public boolean isStoppedByAttack() {
        return this._stoppedByAttack;
    }
    
    public void setStoppedByAttack(final boolean val) {
        this._stoppedByAttack = val;
    }
    
    public int getCurrentNodeId() {
        return this._currentNode;
    }
    
    public long getLastAction() {
        return this._lastActionTime;
    }
    
    public void setLastAction(final long val) {
        this._lastActionTime = val;
    }
    
    public ScheduledFuture<?> getWalkCheckTask() {
        return this._walkCheckTask;
    }
    
    public void setWalkCheckTask(final ScheduledFuture<?> val) {
        this._walkCheckTask = val;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/util/concurrent/ScheduledFuture;ZZZIZJ)Ljava/lang/String;, this._routeName, this._walkCheckTask, this._blocked, this._suspended, this._stoppedByAttack, this._currentNode, this._forward, this._lastActionTime);
    }
}
