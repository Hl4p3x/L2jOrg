// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureSkillUse implements IBaseEvent
{
    private final Creature _caster;
    private final Skill _skill;
    private final boolean _simultaneously;
    
    public OnCreatureSkillUse(final Creature caster, final Skill skill, final boolean simultaneously) {
        this._caster = caster;
        this._skill = skill;
        this._simultaneously = simultaneously;
    }
    
    public final Creature getCaster() {
        return this._caster;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public boolean isSimultaneously() {
        return this._simultaneously;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_SKILL_USE;
    }
}
