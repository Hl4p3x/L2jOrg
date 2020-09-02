// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Trap;

public class TrapTriggerTask implements Runnable
{
    private final Trap _trap;
    
    public TrapTriggerTask(final Trap trap) {
        this._trap = trap;
    }
    
    @Override
    public void run() {
        try {
            this._trap.doCast(this._trap.getSkill());
            ThreadPool.schedule((Runnable)new TrapUnsummonTask(this._trap), (long)(this._trap.getSkill().getHitTime() + 300));
        }
        catch (Exception e) {
            this._trap.unSummon();
        }
    }
}
