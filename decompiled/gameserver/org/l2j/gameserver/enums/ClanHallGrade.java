// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum ClanHallGrade
{
    S(50), 
    A(40), 
    B(30), 
    C(20), 
    D(10), 
    NONE(0);
    
    private final int _gradeValue;
    
    private ClanHallGrade(final int gradeValue) {
        this._gradeValue = gradeValue;
    }
    
    public int getGradeValue() {
        return this._gradeValue;
    }
}
