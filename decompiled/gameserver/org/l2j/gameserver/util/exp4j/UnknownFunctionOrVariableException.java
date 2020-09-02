// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public class UnknownFunctionOrVariableException extends IllegalArgumentException
{
    private final String message;
    private final String expression;
    private final String token;
    private final int position;
    
    public UnknownFunctionOrVariableException(final String expression, final int position, final int length) {
        this.expression = expression;
        this.token = token(expression, position, length);
        this.position = position;
        this.message = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.token, position, expression);
    }
    
    private static String token(final String expression, final int position, final int length) {
        final int len = expression.length();
        int end = position + length - 1;
        if (len < end) {
            end = len;
        }
        return expression.substring(position, end);
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public int getPosition() {
        return this.position;
    }
}
