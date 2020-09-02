// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.base;

public enum AcquireSkillType
{
    CLASS(0), 
    DUMMY(1), 
    PLEDGE(2), 
    SUBPLEDGE(3), 
    TRANSFORM(4), 
    DUMMY2(8), 
    DUMMY3(9), 
    FISHING(10);
    
    private final int _id;
    
    private AcquireSkillType(final int id) {
        this._id = id;
    }
    
    public static AcquireSkillType getAcquireSkillType(final int id) {
        for (final AcquireSkillType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
    
    public int getId() {
        return this._id;
    }
}
