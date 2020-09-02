// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

public enum SkillCastingType
{
    SIMULTANEOUS(-1), 
    NORMAL(0), 
    NORMAL_SECOND(1), 
    BLUE(2), 
    GREEN(3), 
    RED(4);
    
    private final int _clientBarId;
    
    private SkillCastingType(final int clientBarId) {
        this._clientBarId = clientBarId;
    }
    
    public int getClientBarId() {
        return this._clientBarId;
    }
}
