// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public class OperatorToken extends Token
{
    private final Operator operator;
    
    public OperatorToken(final Operator op) {
        super(2);
        if (op == null) {
            throw new IllegalArgumentException("Operator is unknown for token.");
        }
        this.operator = op;
    }
    
    public Operator getOperator() {
        return this.operator;
    }
}
