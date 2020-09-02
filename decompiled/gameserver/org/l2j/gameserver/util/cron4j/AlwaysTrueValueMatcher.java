// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

class AlwaysTrueValueMatcher implements ValueMatcher
{
    @Override
    public boolean match(final int value) {
        return true;
    }
}
