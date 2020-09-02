// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcSkillSee implements IBaseEvent
{
    private final Npc _npc;
    private final Player _caster;
    private final Skill _skill;
    private final WorldObject[] _targets;
    private final boolean _isSummon;
    
    public OnNpcSkillSee(final Npc npc, final Player caster, final Skill skill, final boolean isSummon, final WorldObject... targets) {
        this._npc = npc;
        this._caster = caster;
        this._skill = skill;
        this._isSummon = isSummon;
        this._targets = targets;
    }
    
    public Npc getTarget() {
        return this._npc;
    }
    
    public Player getCaster() {
        return this._caster;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public WorldObject[] getTargets() {
        return this._targets;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_SKILL_SEE;
    }
}
