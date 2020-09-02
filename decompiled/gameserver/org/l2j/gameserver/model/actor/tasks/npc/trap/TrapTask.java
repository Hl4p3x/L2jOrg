// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.slf4j.Logger;

public class TrapTask implements Runnable
{
    private static final Logger LOGGER;
    private static final int TICK = 1000;
    private final Trap _trap;
    
    public TrapTask(final Trap trap) {
        this._trap = trap;
    }
    
    @Override
    public void run() {
        try {
            if (!this._trap.isTriggered()) {
                if (this._trap.hasLifeTime()) {
                    this._trap.setRemainingTime(this._trap.getRemainingTime() - 1000);
                    if (this._trap.getRemainingTime() < this._trap.getLifeTime() - 15000) {
                        this._trap.broadcastPacket(new SocialAction(this._trap.getObjectId(), 2));
                    }
                    if (this._trap.getRemainingTime() <= 0) {
                        this._trap.triggerTrap(this._trap);
                        return;
                    }
                }
                if (!this._trap.getSkill().getTargetsAffected(this._trap, this._trap).isEmpty()) {
                    this._trap.triggerTrap(this._trap);
                }
            }
        }
        catch (Exception e) {
            TrapTask.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, TrapTask.class.getSimpleName(), e.getMessage()));
            this._trap.unSummon();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TrapTask.class);
    }
}
