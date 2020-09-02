// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;

public class SummonRequestHolder
{
    private final Player _target;
    private final Skill _skill;
    
    public SummonRequestHolder(final Player destination, final Skill skill) {
        this._target = destination;
        this._skill = skill;
    }
    
    public Player getTarget() {
        return this._target;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
}
