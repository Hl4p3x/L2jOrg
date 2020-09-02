// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class ExpressionBuilder
{
    private final String expression;
    private final Map<String, Function> userFunctions;
    private final Map<String, Operator> userOperators;
    private final Set<String> variableNames;
    private boolean implicitMultiplication;
    
    public ExpressionBuilder(final String expression) {
        this.implicitMultiplication = true;
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression can not be empty");
        }
        this.expression = expression;
        this.userOperators = new HashMap<String, Operator>(4);
        this.userFunctions = new HashMap<String, Function>(4);
        this.variableNames = new HashSet<String>(4);
    }
    
    public ExpressionBuilder function(final Function function) {
        this.userFunctions.put(function.getName(), function);
        return this;
    }
    
    public ExpressionBuilder functions(final Function... functions) {
        for (final Function f : functions) {
            this.userFunctions.put(f.getName(), f);
        }
        return this;
    }
    
    public ExpressionBuilder functions(final List<Function> functions) {
        for (final Function f : functions) {
            this.userFunctions.put(f.getName(), f);
        }
        return this;
    }
    
    public ExpressionBuilder variables(final Set<String> variableNames) {
        this.variableNames.addAll(variableNames);
        return this;
    }
    
    public ExpressionBuilder variables(final String... variableNames) {
        Collections.addAll(this.variableNames, variableNames);
        return this;
    }
    
    public ExpressionBuilder variable(final String variableName) {
        this.variableNames.add(variableName);
        return this;
    }
    
    public ExpressionBuilder implicitMultiplication(final boolean enabled) {
        this.implicitMultiplication = enabled;
        return this;
    }
    
    public ExpressionBuilder operator(final Operator operator) {
        this.checkOperatorSymbol(operator);
        this.userOperators.put(operator.getSymbol(), operator);
        return this;
    }
    
    private void checkOperatorSymbol(final Operator op) {
        final String name = op.getSymbol();
        for (final char ch : name.toCharArray()) {
            if (!Operator.isAllowedOperatorChar(ch)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
            }
        }
    }
    
    public ExpressionBuilder operator(final Operator... operators) {
        for (final Operator o : operators) {
            this.operator(o);
        }
        return this;
    }
    
    public ExpressionBuilder operator(final List<Operator> operators) {
        for (final Operator o : operators) {
            this.operator(o);
        }
        return this;
    }
    
    public Expression build() {
        if (this.expression.isEmpty()) {
            throw new IllegalArgumentException("The expression can not be empty");
        }
        this.variableNames.add("pi");
        this.variableNames.add("\u03c0");
        this.variableNames.add("e");
        this.variableNames.add("\u03c6");
        for (final String var : this.variableNames) {
            if (Functions.getBuiltinFunction(var) != null || this.userFunctions.containsKey(var)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, var));
            }
        }
        return new Expression(ShuntingYard.convertToRPN(this.expression, this.userFunctions, this.userOperators, this.variableNames, this.implicitMultiplication), this.userFunctions.keySet());
    }
}
