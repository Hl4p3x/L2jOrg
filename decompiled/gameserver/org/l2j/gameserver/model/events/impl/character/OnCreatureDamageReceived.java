// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureDamageReceived implements IBaseEvent
{
    private final Creature _attacker;
    private final Creature _target;
    private final double _damage;
    private final Skill _skill;
    private final boolean _crit;
    private final boolean _damageOverTime;
    private final boolean _reflect;
    
    public OnCreatureDamageReceived(final Creature attacker, final Creature target, final double damage, final Skill skill, final boolean crit, final boolean damageOverTime, final boolean reflect) {
        this._attacker = attacker;
        this._target = target;
        this._damage = damage;
        this._skill = skill;
        this._crit = crit;
        this._damageOverTime = damageOverTime;
        this._reflect = reflect;
    }
    
    public final Creature getAttacker() {
        return this._attacker;
    }
    
    public final Creature getTarget() {
        return this._target;
    }
    
    public double getDamage() {
        return this._damage;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public boolean isCritical() {
        return this._crit;
    }
    
    public boolean isDamageOverTime() {
        return this._damageOverTime;
    }
    
    public boolean isReflect() {
        return this._reflect;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_DAMAGE_RECEIVED;
    }
}
