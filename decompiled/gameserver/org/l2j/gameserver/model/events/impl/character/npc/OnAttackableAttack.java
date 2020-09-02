// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnAttackableAttack implements IBaseEvent
{
    private final Player _attacker;
    private final Attackable _target;
    private final int _damage;
    private final Skill _skill;
    private final boolean _isSummon;
    
    public OnAttackableAttack(final Player attacker, final Attackable target, final int damage, final Skill skill, final boolean isSummon) {
        this._attacker = attacker;
        this._target = target;
        this._damage = damage;
        this._skill = skill;
        this._isSummon = isSummon;
    }
    
    public final Player getAttacker() {
        return this._attacker;
    }
    
    public final Attackable getTarget() {
        return this._target;
    }
    
    public int getDamage() {
        return this._damage;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_ATTACK;
    }
}
