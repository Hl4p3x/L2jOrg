// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Map;

public interface ISkillsHolder
{
    Map<Integer, Skill> getSkills();
    
    Skill addSkill(final Skill skill);
    
    Skill getKnownSkill(final int skillId);
    
    int getSkillLevel(final int skillId);
}
