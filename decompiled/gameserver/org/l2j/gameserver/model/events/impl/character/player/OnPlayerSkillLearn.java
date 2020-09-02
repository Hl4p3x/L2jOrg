// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerSkillLearn implements IBaseEvent
{
    private final Npc _trainer;
    private final Player _activeChar;
    private final Skill _skill;
    private final AcquireSkillType _type;
    
    public OnPlayerSkillLearn(final Npc trainer, final Player activeChar, final Skill skill, final AcquireSkillType type) {
        this._trainer = trainer;
        this._activeChar = activeChar;
        this._skill = skill;
        this._type = type;
    }
    
    public Npc getTrainer() {
        return this._trainer;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public AcquireSkillType getAcquireType() {
        return this._type;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SKILL_LEARN;
    }
}
