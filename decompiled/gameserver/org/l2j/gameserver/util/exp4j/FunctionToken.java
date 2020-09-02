// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public class FunctionToken extends Token
{
    private final Function function;
    
    public FunctionToken(final Function function) {
        super(3);
        this.function = function;
    }
    
    public Function getFunction() {
        return this.function;
    }
}
