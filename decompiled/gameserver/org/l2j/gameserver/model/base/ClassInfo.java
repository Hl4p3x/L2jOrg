// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.base;

import java.util.regex.Matcher;

public final class ClassInfo
{
    private final ClassId _classId;
    private final String _className;
    private final ClassId _parentClassId;
    
    public ClassInfo(final ClassId classId, final String className, final ClassId parentClassId) {
        this._classId = classId;
        this._className = className;
        this._parentClassId = parentClassId;
    }
    
    public ClassId getClassId() {
        return this._classId;
    }
    
    public String getClassName() {
        return this._className;
    }
    
    private int getClassClientId() {
        int classClientId = this._classId.getId();
        if (classClientId >= 0 && classClientId <= 57) {
            classClientId += 247;
        }
        else if (classClientId >= 88 && classClientId <= 118) {
            classClientId += 1071;
        }
        else if (classClientId >= 123 && classClientId <= 136) {
            classClientId += 1438;
        }
        else if (classClientId >= 139 && classClientId <= 146) {
            classClientId += 2338;
        }
        else if (classClientId >= 148 && classClientId <= 181) {
            classClientId += 2884;
        }
        else if (classClientId >= 182 && classClientId <= 189) {
            classClientId += 3121;
        }
        return classClientId;
    }
    
    public String getClientCode() {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getClassClientId());
    }
    
    public String getEscapedClientCode() {
        return Matcher.quoteReplacement(this.getClientCode());
    }
    
    public ClassId getParentClassId() {
        return this._parentClassId;
    }
}
