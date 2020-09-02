// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public class VariableToken extends Token
{
    private final String name;
    
    public VariableToken(final String name) {
        super(6);
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
