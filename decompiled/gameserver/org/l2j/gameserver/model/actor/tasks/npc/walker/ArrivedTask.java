// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.npc.walker;

import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WalkInfo;

public class ArrivedTask implements Runnable
{
    private final WalkInfo _walk;
    private final Npc _npc;
    
    public ArrivedTask(final Npc npc, final WalkInfo walk) {
        this._npc = npc;
        this._walk = walk;
    }
    
    @Override
    public void run() {
        this._npc.broadcastInfo();
        this._walk.setBlocked(false);
        WalkingManager.getInstance().startMoving(this._npc, this._walk.getRoute().getName());
    }
}
