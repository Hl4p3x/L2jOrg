// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

public enum SkillOperateType
{
    A1, 
    A2, 
    A3, 
    A4, 
    A5, 
    A6, 
    CA1, 
    CA2, 
    CA5, 
    DA1, 
    DA2, 
    DA3, 
    DA4, 
    DA5, 
    P, 
    T, 
    TG, 
    AU;
    
    public boolean isActive() {
        switch (this) {
            case A1:
            case A2:
            case A3:
            case A4:
            case A5:
            case A6:
            case CA1:
            case CA5:
            case DA1:
            case DA2:
            case DA4:
            case DA5: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isContinuous() {
        boolean b = false;
        switch (this) {
            case A2:
            case A3:
            case A4:
            case A5:
            case A6:
            case DA2:
            case DA4:
            case DA5: {
                b = true;
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public boolean isSelfContinuous() {
        return this == SkillOperateType.A3;
    }
    
    public boolean isPassive() {
        return this == SkillOperateType.P;
    }
    
    public boolean isToggle() {
        return this == SkillOperateType.T || this == SkillOperateType.TG || this == SkillOperateType.AU;
    }
    
    public boolean isAura() {
        return this == SkillOperateType.A5 || this == SkillOperateType.A6 || this == SkillOperateType.AU;
    }
    
    public boolean isHidingMessages() {
        return this == SkillOperateType.A5 || this == SkillOperateType.A6 || this == SkillOperateType.TG || this == SkillOperateType.P;
    }
    
    public boolean isNotBroadcastable() {
        return this == SkillOperateType.AU || this == SkillOperateType.A5 || this == SkillOperateType.A6 || this == SkillOperateType.TG || this == SkillOperateType.T;
    }
    
    public boolean isChanneling() {
        switch (this) {
            case CA1:
            case CA5:
            case CA2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isSynergy() {
        return this == SkillOperateType.A6;
    }
    
    public boolean isFlyType() {
        switch (this) {
            case DA1:
            case DA2:
            case DA4:
            case DA5:
            case DA3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
