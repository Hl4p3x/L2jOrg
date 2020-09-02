// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.Spawn;
import java.util.Set;
import org.l2j.gameserver.model.actor.Tower;

public class ControlTower extends Tower
{
    private volatile Set<Spawn> _guards;
    
    public ControlTower(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2ControlTowerInstance);
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (this.getCastle().getSiege().isInProgress()) {
            this.getCastle().getSiege().killedControlTower(this);
            if (this._guards != null && !this._guards.isEmpty()) {
                for (final Spawn spawn : this._guards) {
                    if (spawn == null) {
                        continue;
                    }
                    try {
                        spawn.stopRespawn();
                    }
                    catch (Exception e) {
                        ControlTower.LOGGER.warn("Error at ControlTower", (Throwable)e);
                    }
                }
                this._guards.clear();
            }
        }
        return super.doDie(killer);
    }
    
    public void registerGuard(final Spawn guard) {
        this.getGuards().add(guard);
    }
    
    private Set<Spawn> getGuards() {
        if (this._guards == null) {
            synchronized (this) {
                if (this._guards == null) {
                    this._guards = (Set<Spawn>)ConcurrentHashMap.newKeySet();
                }
            }
        }
        return this._guards;
    }
}
