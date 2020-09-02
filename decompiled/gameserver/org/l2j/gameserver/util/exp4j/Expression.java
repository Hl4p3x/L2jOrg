// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;

public class Expression
{
    private final Token[] tokens;
    private final Map<String, Double> variables;
    private final Set<String> userFunctionNames;
    
    public Expression(final Expression existing) {
        this.tokens = Arrays.copyOf(existing.tokens, existing.tokens.length);
        (this.variables = new HashMap<String, Double>()).putAll(existing.variables);
        this.userFunctionNames = new HashSet<String>(existing.userFunctionNames);
    }
    
    Expression(final Token[] tokens) {
        this.tokens = tokens;
        this.variables = createDefaultVariables();
        this.userFunctionNames = Collections.emptySet();
    }
    
    Expression(final Token[] tokens, final Set<String> userFunctionNames) {
        this.tokens = tokens;
        this.variables = createDefaultVariables();
        this.userFunctionNames = userFunctionNames;
    }
    
    private static Map<String, Double> createDefaultVariables() {
        final Map<String, Double> vars = new HashMap<String, Double>(4);
        vars.put("pi", 3.141592653589793);
        vars.put("\u03c0", 3.141592653589793);
        vars.put("\u03c6", 1.61803398874);
        vars.put("e", 2.718281828459045);
        return vars;
    }
    
    public Expression setVariable(final String name, final double value) {
        this.checkVariableName(name);
        this.variables.put(name, value);
        return this;
    }
    
    private void checkVariableName(final String name) {
        if (this.userFunctionNames.contains(name) || Functions.getBuiltinFunction(name) != null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
    }
    
    public Expression setVariables(final Map<String, Double> variables) {
        for (final Map.Entry<String, Double> v : variables.entrySet()) {
            this.setVariable(v.getKey(), v.getValue());
        }
        return this;
    }
    
    public Set<String> getVariableNames() {
        final Set<String> variables = new HashSet<String>();
        for (final Token t : this.tokens) {
            if (t.getType() == 6) {
                variables.add(((VariableToken)t).getName());
            }
        }
        return variables;
    }
    
    public ValidationResult validate(final boolean checkVariablesSet) {
        final List<String> errors = new ArrayList<String>(0);
        if (checkVariablesSet) {
            for (final Token t : this.tokens) {
                if (t.getType() == 6) {
                    final String var = ((VariableToken)t).getName();
                    if (!this.variables.containsKey(var)) {
                        errors.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, var));
                    }
                }
            }
        }
        int count = 0;
        for (final Token tok : this.tokens) {
            switch (tok.getType()) {
                case 1:
                case 6: {
                    ++count;
                    break;
                }
                case 3: {
                    final Function func = ((FunctionToken)tok).getFunction();
                    final int argsNum = func.getNumArguments();
                    if (argsNum > count) {
                        errors.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, func.getName()));
                    }
                    if (argsNum > 1) {
                        count -= argsNum - 1;
                        break;
                    }
                    if (argsNum == 0) {
                        ++count;
                        break;
                    }
                    break;
                }
                case 2: {
                    final Operator op = ((OperatorToken)tok).getOperator();
                    if (op.getNumOperands() == 2) {
                        --count;
                        break;
                    }
                    break;
                }
            }
            if (count < 1) {
                errors.add("Too many operators");
                return new ValidationResult(false, errors);
            }
        }
        if (count > 1) {
            errors.add("Too many operands");
        }
        return (errors.size() == 0) ? ValidationResult.SUCCESS : new ValidationResult(false, errors);
    }
    
    public ValidationResult validate() {
        return this.validate(true);
    }
    
    public Future<Double> evaluateAsync(final ExecutorService executor) {
        return executor.submit(this::evaluate);
    }
    
    public double evaluate() {
        final ArrayStack output = new ArrayStack();
        for (final Token t : this.tokens) {
            if (t.getType() == 1) {
                output.push(((NumberToken)t).getValue());
            }
            else if (t.getType() == 6) {
                final String name = ((VariableToken)t).getName();
                final Double value = this.variables.get(name);
                if (value == null) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
                }
                output.push(value);
            }
            else if (t.getType() == 2) {
                final OperatorToken op = (OperatorToken)t;
                if (output.size() < op.getOperator().getNumOperands()) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, op.getOperator().getSymbol()));
                }
                if (op.getOperator().getNumOperands() == 2) {
                    final double rightArg = output.pop();
                    final double leftArg = output.pop();
                    output.push(op.getOperator().apply(leftArg, rightArg));
                }
                else if (op.getOperator().getNumOperands() == 1) {
                    final double arg = output.pop();
                    output.push(op.getOperator().apply(arg));
                }
            }
            else if (t.getType() == 3) {
                final FunctionToken func = (FunctionToken)t;
                final int numArguments = func.getFunction().getNumArguments();
                if (output.size() < numArguments) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, func.getFunction().getName()));
                }
                final double[] args = new double[numArguments];
                for (int j = numArguments - 1; j >= 0; --j) {
                    args[j] = output.pop();
                }
                output.push(func.getFunction().apply(args));
            }
        }
        if (output.size() > 1) {
            throw new IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.");
        }
        return output.pop();
    }
}
