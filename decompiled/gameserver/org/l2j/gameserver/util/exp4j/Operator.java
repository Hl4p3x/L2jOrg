// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public abstract class Operator
{
    public static final int PRECEDENCE_ADDITION = 500;
    public static final int PRECEDENCE_SUBTRACTION = 500;
    public static final int PRECEDENCE_MULTIPLICATION = 1000;
    public static final int PRECEDENCE_DIVISION = 1000;
    public static final int PRECEDENCE_MODULO = 1000;
    public static final int PRECEDENCE_POWER = 10000;
    public static final int PRECEDENCE_UNARY_MINUS = 5000;
    public static final int PRECEDENCE_UNARY_PLUS = 5000;
    public static final char[] ALLOWED_OPERATOR_CHARS;
    protected final int numOperands;
    protected final boolean leftAssociative;
    protected final String symbol;
    protected final int precedence;
    
    public Operator(final String symbol, final int numberOfOperands, final boolean leftAssociative, final int precedence) {
        this.numOperands = numberOfOperands;
        this.leftAssociative = leftAssociative;
        this.symbol = symbol;
        this.precedence = precedence;
    }
    
    public static boolean isAllowedOperatorChar(final char ch) {
        for (final char allowed : Operator.ALLOWED_OPERATOR_CHARS) {
            if (ch == allowed) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLeftAssociative() {
        return this.leftAssociative;
    }
    
    public int getPrecedence() {
        return this.precedence;
    }
    
    public abstract double apply(final double... args);
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public int getNumOperands() {
        return this.numOperands;
    }
    
    static {
        ALLOWED_OPERATOR_CHARS = new char[] { '+', '-', '*', '/', '%', '^', '!', '#', '$', '&', ';', ':', '~', '<', '>', '|', '=' };
    }
}
