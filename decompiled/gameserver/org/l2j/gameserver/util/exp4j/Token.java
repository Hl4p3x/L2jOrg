// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public abstract class Token
{
    public static final short TOKEN_NUMBER = 1;
    public static final short TOKEN_OPERATOR = 2;
    public static final short TOKEN_FUNCTION = 3;
    public static final short TOKEN_PARENTHESES_OPEN = 4;
    public static final short TOKEN_PARENTHESES_CLOSE = 5;
    public static final short TOKEN_VARIABLE = 6;
    public static final short TOKEN_SEPARATOR = 7;
    private final int type;
    
    Token(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
}
