// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcSkillFinished implements IBaseEvent
{
    private final Npc _caster;
    private final Player _target;
    private final Skill _skill;
    
    public OnNpcSkillFinished(final Npc caster, final Player target, final Skill skill) {
        this._caster = caster;
        this._target = target;
        this._skill = skill;
    }
    
    public Player getTarget() {
        return this._target;
    }
    
    public Npc getCaster() {
        return this._caster;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_SKILL_FINISHED;
    }
}
