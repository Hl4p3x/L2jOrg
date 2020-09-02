// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.model.actor.instance.Trap;

public class TrapUnsummonTask implements Runnable
{
    private final Trap _trap;
    
    public TrapUnsummonTask(final Trap trap) {
        this._trap = trap;
    }
    
    @Override
    public void run() {
        this._trap.unSummon();
    }
}
