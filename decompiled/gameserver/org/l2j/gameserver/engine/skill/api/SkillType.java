// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import java.util.function.Consumer;

public enum SkillType
{
    PHYSIC, 
    MAGIC, 
    STATIC, 
    DANCE;
    
    private static final SkillType[] CACHE;
    
    public static void forEach(final Consumer<SkillType> action) {
        for (final SkillType type : SkillType.CACHE) {
            action.accept(type);
        }
    }
    
    static {
        CACHE = values();
    }
}
