// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

import org.l2j.gameserver.model.holders.SkillHolder;

public class OptionsSkillHolder extends SkillHolder
{
    private final OptionsSkillType type;
    private final double chance;
    
    public OptionsSkillHolder(final SkillHolder skill, final double chance, final OptionsSkillType type) {
        super(skill.getSkillId(), skill.getLevel());
        this.chance = chance;
        this.type = type;
    }
    
    public OptionsSkillType getSkillType() {
        return this.type;
    }
    
    public double getChance() {
        return this.chance;
    }
}
