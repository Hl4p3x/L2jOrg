// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;
import java.util.Map;

public class ShuntingYard
{
    public static Token[] convertToRPN(final String expression, final Map<String, Function> userFunctions, final Map<String, Operator> userOperators, final Set<String> variableNames, final boolean implicitMultiplication) {
        final Stack<Token> stack = new Stack<Token>();
        final List<Token> output = new ArrayList<Token>();
        final Tokenizer tokenizer = new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication);
        while (tokenizer.hasNext()) {
            final Token token = tokenizer.nextToken();
            switch (token.getType()) {
                case 1:
                case 6: {
                    output.add(token);
                    continue;
                }
                case 3: {
                    stack.add(token);
                    continue;
                }
                case 7: {
                    while (!stack.empty() && stack.peek().getType() != 4) {
                        output.add(stack.pop());
                    }
                    if (stack.empty() || stack.peek().getType() != 4) {
                        throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses");
                    }
                    continue;
                }
                case 2: {
                    while (!stack.empty() && stack.peek().getType() == 2) {
                        final OperatorToken o1 = (OperatorToken)token;
                        final OperatorToken o2 = stack.peek();
                        if (o1.getOperator().getNumOperands() == 1 && o2.getOperator().getNumOperands() == 2) {
                            break;
                        }
                        if ((o1.getOperator().isLeftAssociative() || o1.getOperator().getPrecedence() > o2.getOperator().getPrecedence()) && o1.getOperator().getPrecedence() >= o2.getOperator().getPrecedence()) {
                            break;
                        }
                        output.add(stack.pop());
                    }
                    stack.push(token);
                    continue;
                }
                case 4: {
                    stack.push(token);
                    continue;
                }
                case 5: {
                    while (stack.peek().getType() != 4) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                    if (!stack.isEmpty() && stack.peek().getType() == 3) {
                        output.add(stack.pop());
                        continue;
                    }
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Unknown Token type encountered. This should not happen");
                }
            }
        }
        while (!stack.empty()) {
            final Token t = stack.pop();
            if (t.getType() == 5 || t.getType() == 4) {
                throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression");
            }
            output.add(t);
        }
        return output.toArray(new Token[output.size()]);
    }
}
