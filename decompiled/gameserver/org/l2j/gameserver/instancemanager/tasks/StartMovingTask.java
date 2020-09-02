// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.Npc;

public final class StartMovingTask implements Runnable
{
    final Npc _npc;
    final String _routeName;
    
    public StartMovingTask(final Npc npc, final String routeName) {
        this._npc = npc;
        this._routeName = routeName;
    }
    
    @Override
    public void run() {
        if (this._npc != null) {
            WalkingManager.getInstance().startMoving(this._npc, this._routeName);
        }
    }
}
