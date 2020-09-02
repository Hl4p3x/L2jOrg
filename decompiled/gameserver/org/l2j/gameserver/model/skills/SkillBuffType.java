// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

public enum SkillBuffType
{
    NONE, 
    BUFF, 
    DEBUFF, 
    DANCE, 
    TOGGLE, 
    TRIGGER;
    
    public boolean isNone() {
        return this == SkillBuffType.NONE;
    }
    
    public boolean isBuff() {
        return this == SkillBuffType.BUFF;
    }
    
    public boolean isDebuff() {
        return this == SkillBuffType.DEBUFF;
    }
    
    public boolean isDance() {
        return this == SkillBuffType.DANCE;
    }
    
    public boolean isToggle() {
        return this == SkillBuffType.TOGGLE;
    }
    
    public boolean isTrigger() {
        return this == SkillBuffType.TRIGGER;
    }
}
