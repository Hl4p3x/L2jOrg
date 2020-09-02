// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureAttack implements IBaseEvent
{
    private final Creature _attacker;
    private final Creature _target;
    private final Skill _skill;
    
    public OnCreatureAttack(final Creature attacker, final Creature target, final Skill skill) {
        this._attacker = attacker;
        this._target = target;
        this._skill = skill;
    }
    
    public final Creature getAttacker() {
        return this._attacker;
    }
    
    public final Creature getTarget() {
        return this._target;
    }
    
    public final Skill getSkill() {
        return this._skill;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_ATTACK;
    }
}
