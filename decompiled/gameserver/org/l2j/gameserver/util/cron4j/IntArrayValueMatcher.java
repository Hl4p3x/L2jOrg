// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.ArrayList;

class IntArrayValueMatcher implements ValueMatcher
{
    private final int[] values;
    
    public IntArrayValueMatcher(final ArrayList<?> integers) {
        final int size = integers.size();
        this.values = new int[size];
        for (int i = 0; i < size; ++i) {
            try {
                this.values[i] = (int)integers.get(i);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
    
    @Override
    public boolean match(final int value) {
        for (final int value2 : this.values) {
            if (value2 == value) {
                return true;
            }
        }
        return false;
    }
}
