// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public final class NumberToken extends Token
{
    private final double value;
    
    public NumberToken(final double value) {
        super(1);
        this.value = value;
    }
    
    NumberToken(final char[] expression, final int offset, final int len) {
        this(Double.parseDouble(String.valueOf(expression, offset, len)));
    }
    
    public double getValue() {
        return this.value;
    }
}
