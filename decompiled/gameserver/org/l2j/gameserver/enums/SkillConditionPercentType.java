// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum SkillConditionPercentType
{
    MORE {
        @Override
        public boolean test(final int x1, final int x2) {
            return x1 >= x2;
        }
    }, 
    LESS {
        @Override
        public boolean test(final int x1, final int x2) {
            return x1 <= x2;
        }
    };
    
    public abstract boolean test(final int x1, final int x2);
}
