// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureHpChange implements IBaseEvent
{
    private final Creature _creature;
    private final double _newHp;
    private final double _oldHp;
    
    public OnCreatureHpChange(final Creature creature, final double oldHp, final double newHp) {
        this._creature = creature;
        this._oldHp = oldHp;
        this._newHp = newHp;
    }
    
    public Creature getCreature() {
        return this._creature;
    }
    
    public double getOldHp() {
        return this._oldHp;
    }
    
    public double getNewHp() {
        return this._newHp;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_HP_CHANGE;
    }
}
