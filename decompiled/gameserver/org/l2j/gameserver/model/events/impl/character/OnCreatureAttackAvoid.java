// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureAttackAvoid implements IBaseEvent
{
    private final Creature _attacker;
    private final Creature _target;
    private final boolean _damageOverTime;
    
    public OnCreatureAttackAvoid(final Creature attacker, final Creature target, final boolean isDot) {
        this._attacker = attacker;
        this._target = target;
        this._damageOverTime = isDot;
    }
    
    public final Creature getAttacker() {
        return this._attacker;
    }
    
    public final Creature getTarget() {
        return this._target;
    }
    
    public boolean isDamageOverTime() {
        return this._damageOverTime;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_ATTACK_AVOID;
    }
}
