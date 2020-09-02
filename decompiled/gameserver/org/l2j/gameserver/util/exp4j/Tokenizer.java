// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.Set;
import java.util.Map;

public class Tokenizer
{
    private final char[] expression;
    private final int expressionLength;
    private final Map<String, Function> userFunctions;
    private final Map<String, Operator> userOperators;
    private final Set<String> variableNames;
    private final boolean implicitMultiplication;
    private int pos;
    private Token lastToken;
    
    public Tokenizer(final String expression, final Map<String, Function> userFunctions, final Map<String, Operator> userOperators, final Set<String> variableNames, final boolean implicitMultiplication) {
        this.pos = 0;
        this.expression = expression.trim().toCharArray();
        this.expressionLength = this.expression.length;
        this.userFunctions = userFunctions;
        this.userOperators = userOperators;
        this.variableNames = variableNames;
        this.implicitMultiplication = implicitMultiplication;
    }
    
    public Tokenizer(final String expression, final Map<String, Function> userFunctions, final Map<String, Operator> userOperators, final Set<String> variableNames) {
        this.pos = 0;
        this.expression = expression.trim().toCharArray();
        this.expressionLength = this.expression.length;
        this.userFunctions = userFunctions;
        this.userOperators = userOperators;
        this.variableNames = variableNames;
        this.implicitMultiplication = true;
    }
    
    private static boolean isNumeric(final char ch, final boolean lastCharE) {
        return Character.isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || (lastCharE && (ch == '-' || ch == '+'));
    }
    
    private static boolean isAlphabetic(final int codePoint) {
        return Character.isLetter(codePoint);
    }
    
    private static boolean isVariableOrFunctionCharacter(final int codePoint) {
        return isAlphabetic(codePoint) || Character.isDigit(codePoint) || codePoint == 95 || codePoint == 46;
    }
    
    public boolean hasNext() {
        return this.expression.length > this.pos;
    }
    
    public Token nextToken() {
        char ch;
        for (ch = this.expression[this.pos]; Character.isWhitespace(ch); ch = this.expression[++this.pos]) {}
        if (Character.isDigit(ch) || ch == '.') {
            if (this.lastToken != null) {
                if (this.lastToken.getType() == 1) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(CII)Ljava/lang/String;, ch, ch, this.pos));
                }
                if (this.implicitMultiplication && this.lastToken.getType() != 2 && this.lastToken.getType() != 4 && this.lastToken.getType() != 3 && this.lastToken.getType() != 7) {
                    return this.lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2));
                }
            }
            return this.parseNumberToken(ch);
        }
        if (this.isArgumentSeparator(ch)) {
            return this.parseArgumentSeparatorToken(ch);
        }
        if (this.isOpenParentheses(ch)) {
            if (this.lastToken != null && this.implicitMultiplication && this.lastToken.getType() != 2 && this.lastToken.getType() != 4 && this.lastToken.getType() != 3 && this.lastToken.getType() != 7) {
                return this.lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2));
            }
            return this.parseParentheses(true);
        }
        else {
            if (this.isCloseParentheses(ch)) {
                return this.parseParentheses(false);
            }
            if (Operator.isAllowedOperatorChar(ch)) {
                return this.parseOperatorToken(ch);
            }
            if (!isAlphabetic(ch) && ch != '_') {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(CII)Ljava/lang/String;, ch, ch, this.pos));
            }
            if (this.lastToken != null && this.implicitMultiplication && this.lastToken.getType() != 2 && this.lastToken.getType() != 4 && this.lastToken.getType() != 3 && this.lastToken.getType() != 7) {
                return this.lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2));
            }
            return this.parseFunctionOrVariable();
        }
    }
    
    private Token parseArgumentSeparatorToken(final char ch) {
        ++this.pos;
        return this.lastToken = new ArgumentSeparatorToken();
    }
    
    private boolean isArgumentSeparator(final char ch) {
        return ch == ',';
    }
    
    private Token parseParentheses(final boolean open) {
        if (open) {
            this.lastToken = new OpenParenthesesToken();
        }
        else {
            this.lastToken = new CloseParenthesesToken();
        }
        ++this.pos;
        return this.lastToken;
    }
    
    private boolean isOpenParentheses(final char ch) {
        return ch == '(' || ch == '{' || ch == '[';
    }
    
    private boolean isCloseParentheses(final char ch) {
        return ch == ')' || ch == '}' || ch == ']';
    }
    
    private Token parseFunctionOrVariable() {
        final int offset = this.pos;
        int lastValidLen = 1;
        Token lastValidToken = null;
        int len = 1;
        if (this.isEndOfExpression(offset)) {
            ++this.pos;
        }
        for (int testPos = offset + len - 1; !this.isEndOfExpression(testPos) && isVariableOrFunctionCharacter(this.expression[testPos]); testPos = offset + len - 1) {
            final String name = new String(this.expression, offset, len);
            if (this.variableNames != null && this.variableNames.contains(name)) {
                lastValidLen = len;
                lastValidToken = new VariableToken(name);
            }
            else {
                final Function f = this.getFunction(name);
                if (f != null) {
                    lastValidLen = len;
                    lastValidToken = new FunctionToken(f);
                }
            }
            ++len;
        }
        if (lastValidToken == null) {
            throw new UnknownFunctionOrVariableException(new String(this.expression), this.pos, len);
        }
        this.pos += lastValidLen;
        return this.lastToken = lastValidToken;
    }
    
    private Function getFunction(final String name) {
        Function f = null;
        if (this.userFunctions != null) {
            f = this.userFunctions.get(name);
        }
        if (f == null) {
            f = Functions.getBuiltinFunction(name);
        }
        return f;
    }
    
    private Token parseOperatorToken(final char firstChar) {
        final int offset = this.pos;
        int len = 1;
        final StringBuilder symbol = new StringBuilder();
        Operator lastValid = null;
        symbol.append(firstChar);
        while (!this.isEndOfExpression(offset + len) && Operator.isAllowedOperatorChar(this.expression[offset + len])) {
            symbol.append(this.expression[offset + len++]);
        }
        while (symbol.length() > 0) {
            final Operator op = this.getOperator(symbol.toString());
            if (op != null) {
                lastValid = op;
                break;
            }
            symbol.setLength(symbol.length() - 1);
        }
        this.pos += symbol.length();
        return this.lastToken = new OperatorToken(lastValid);
    }
    
    private Operator getOperator(final String symbol) {
        Operator op = null;
        if (this.userOperators != null) {
            op = this.userOperators.get(symbol);
        }
        if (op == null && symbol.length() == 1) {
            int argc = 2;
            if (this.lastToken == null) {
                argc = 1;
            }
            else {
                final int lastTokenType = this.lastToken.getType();
                if (lastTokenType == 4 || lastTokenType == 7) {
                    argc = 1;
                }
                else if (lastTokenType == 2) {
                    final Operator lastOp = ((OperatorToken)this.lastToken).getOperator();
                    if (lastOp.getNumOperands() == 2 || (lastOp.getNumOperands() == 1 && !lastOp.isLeftAssociative())) {
                        argc = 1;
                    }
                }
            }
            op = Operators.getBuiltinOperator(symbol.charAt(0), argc);
        }
        return op;
    }
    
    private Token parseNumberToken(final char firstChar) {
        final int offset = this.pos;
        int len = 1;
        ++this.pos;
        if (this.isEndOfExpression(offset + len)) {
            return this.lastToken = new NumberToken(Double.parseDouble(String.valueOf(firstChar)));
        }
        while (!this.isEndOfExpression(offset + len) && isNumeric(this.expression[offset + len], this.expression[offset + len - 1] == 'e' || this.expression[offset + len - 1] == 'E')) {
            ++len;
            ++this.pos;
        }
        if (this.expression[offset + len - 1] == 'e' || this.expression[offset + len - 1] == 'E') {
            --len;
            --this.pos;
        }
        return this.lastToken = new NumberToken(this.expression, offset, len);
    }
    
    private boolean isEndOfExpression(final int offset) {
        return this.expressionLength <= offset;
    }
}
