// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.Iterator;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Map;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;

public class PlayerSkillHolder implements ISkillsHolder
{
    private final Map<Integer, Skill> _skills;
    
    public PlayerSkillHolder(final Player player) {
        this._skills = new HashMap<Integer, Skill>();
        for (final Skill skill : player.getSkills().values()) {
            if (SkillTreesData.getInstance().isSkillAllowed(player, skill)) {
                this.addSkill(skill);
            }
        }
    }
    
    @Override
    public Map<Integer, Skill> getSkills() {
        return this._skills;
    }
    
    @Override
    public Skill addSkill(final Skill skill) {
        return this._skills.put(skill.getId(), skill);
    }
    
    @Override
    public int getSkillLevel(final int skillId) {
        final Skill skill = this.getKnownSkill(skillId);
        return (skill == null) ? 0 : skill.getLevel();
    }
    
    @Override
    public Skill getKnownSkill(final int skillId) {
        return this._skills.get(skillId);
    }
    
    public Skill removeSkill(final Skill skill) {
        return this._skills.remove(skill.getId());
    }
}
