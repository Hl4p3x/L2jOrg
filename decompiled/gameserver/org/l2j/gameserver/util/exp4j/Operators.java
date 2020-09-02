// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public abstract class Operators
{
    private static final int INDEX_ADDITION = 0;
    private static final int INDEX_SUBTRACTION = 1;
    private static final int INDEX_MUTLIPLICATION = 2;
    private static final int INDEX_DIVISION = 3;
    private static final int INDEX_POWER = 4;
    private static final int INDEX_MODULO = 5;
    private static final int INDEX_UNARYMINUS = 6;
    private static final int INDEX_UNARYPLUS = 7;
    private static final Operator[] builtinOperators;
    
    public static Operator getBuiltinOperator(final char symbol, final int numArguments) {
        switch (symbol) {
            case '+': {
                if (numArguments != 1) {
                    return Operators.builtinOperators[0];
                }
                return Operators.builtinOperators[7];
            }
            case '-': {
                if (numArguments != 1) {
                    return Operators.builtinOperators[1];
                }
                return Operators.builtinOperators[6];
            }
            case '*': {
                return Operators.builtinOperators[2];
            }
            case '/': {
                return Operators.builtinOperators[3];
            }
            case '^': {
                return Operators.builtinOperators[4];
            }
            case '%': {
                return Operators.builtinOperators[5];
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        (builtinOperators = new Operator[8])[0] = new Operator(2, true, 500) {
            @Override
            public double apply(final double... args) {
                return args[0] + args[1];
            }
        };
        Operators.builtinOperators[1] = new Operator(2, true, 500) {
            @Override
            public double apply(final double... args) {
                return args[0] - args[1];
            }
        };
        Operators.builtinOperators[6] = new Operator(1, false, 5000) {
            @Override
            public double apply(final double... args) {
                return -args[0];
            }
        };
        Operators.builtinOperators[7] = new Operator(1, false, 5000) {
            @Override
            public double apply(final double... args) {
                return args[0];
            }
        };
        Operators.builtinOperators[2] = new Operator(2, true, 1000) {
            @Override
            public double apply(final double... args) {
                return args[0] * args[1];
            }
        };
        Operators.builtinOperators[3] = new Operator(2, true, 1000) {
            @Override
            public double apply(final double... args) {
                if (args[1] == 0.0) {
                    throw new ArithmeticException("Division by zero!");
                }
                return args[0] / args[1];
            }
        };
        Operators.builtinOperators[4] = new Operator(2, false, 10000) {
            @Override
            public double apply(final double... args) {
                return Math.pow(args[0], args[1]);
            }
        };
        Operators.builtinOperators[5] = new Operator(2, true, 1000) {
            @Override
            public double apply(final double... args) {
                if (args[1] == 0.0) {
                    throw new ArithmeticException("Division by zero!");
                }
                return args[0] % args[1];
            }
        };
    }
}
