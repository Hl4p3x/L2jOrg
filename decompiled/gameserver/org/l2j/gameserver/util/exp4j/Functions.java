// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public class Functions
{
    private static final int INDEX_SIN = 0;
    private static final int INDEX_COS = 1;
    private static final int INDEX_TAN = 2;
    private static final int INDEX_COT = 3;
    private static final int INDEX_LOG = 4;
    private static final int INDEX_LOG1P = 5;
    private static final int INDEX_ABS = 6;
    private static final int INDEX_ACOS = 7;
    private static final int INDEX_ASIN = 8;
    private static final int INDEX_ATAN = 9;
    private static final int INDEX_CBRT = 10;
    private static final int INDEX_CEIL = 11;
    private static final int INDEX_FLOOR = 12;
    private static final int INDEX_SINH = 13;
    private static final int INDEX_SQRT = 14;
    private static final int INDEX_TANH = 15;
    private static final int INDEX_COSH = 16;
    private static final int INDEX_POW = 17;
    private static final int INDEX_EXP = 18;
    private static final int INDEX_EXPM1 = 19;
    private static final int INDEX_LOG10 = 20;
    private static final int INDEX_LOG2 = 21;
    private static final int INDEX_SGN = 22;
    private static final Function[] builtinFunctions;
    
    public static Function getBuiltinFunction(final String name) {
        if (name.equals("sin")) {
            return Functions.builtinFunctions[0];
        }
        if (name.equals("cos")) {
            return Functions.builtinFunctions[1];
        }
        if (name.equals("tan")) {
            return Functions.builtinFunctions[2];
        }
        if (name.equals("cot")) {
            return Functions.builtinFunctions[3];
        }
        if (name.equals("asin")) {
            return Functions.builtinFunctions[8];
        }
        if (name.equals("acos")) {
            return Functions.builtinFunctions[7];
        }
        if (name.equals("atan")) {
            return Functions.builtinFunctions[9];
        }
        if (name.equals("sinh")) {
            return Functions.builtinFunctions[13];
        }
        if (name.equals("cosh")) {
            return Functions.builtinFunctions[16];
        }
        if (name.equals("tanh")) {
            return Functions.builtinFunctions[15];
        }
        if (name.equals("abs")) {
            return Functions.builtinFunctions[6];
        }
        if (name.equals("log")) {
            return Functions.builtinFunctions[4];
        }
        if (name.equals("log10")) {
            return Functions.builtinFunctions[20];
        }
        if (name.equals("log2")) {
            return Functions.builtinFunctions[21];
        }
        if (name.equals("log1p")) {
            return Functions.builtinFunctions[5];
        }
        if (name.equals("ceil")) {
            return Functions.builtinFunctions[11];
        }
        if (name.equals("floor")) {
            return Functions.builtinFunctions[12];
        }
        if (name.equals("sqrt")) {
            return Functions.builtinFunctions[14];
        }
        if (name.equals("cbrt")) {
            return Functions.builtinFunctions[10];
        }
        if (name.equals("pow")) {
            return Functions.builtinFunctions[17];
        }
        if (name.equals("exp")) {
            return Functions.builtinFunctions[18];
        }
        if (name.equals("expm1")) {
            return Functions.builtinFunctions[19];
        }
        if (name.equals("signum")) {
            return Functions.builtinFunctions[22];
        }
        return null;
    }
    
    static {
        (builtinFunctions = new Function[23])[0] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.sin(args[0]);
            }
        };
        Functions.builtinFunctions[1] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.cos(args[0]);
            }
        };
        Functions.builtinFunctions[2] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.tan(args[0]);
            }
        };
        Functions.builtinFunctions[3] = new Function() {
            @Override
            public double apply(final double... args) {
                final double tan = Math.tan(args[0]);
                if (tan == 0.0) {
                    throw new ArithmeticException("Division by zero in cotangent!");
                }
                return 1.0 / Math.tan(args[0]);
            }
        };
        Functions.builtinFunctions[4] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.log(args[0]);
            }
        };
        Functions.builtinFunctions[21] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.log(args[0]) / Math.log(2.0);
            }
        };
        Functions.builtinFunctions[20] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.log10(args[0]);
            }
        };
        Functions.builtinFunctions[5] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.log1p(args[0]);
            }
        };
        Functions.builtinFunctions[6] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.abs(args[0]);
            }
        };
        Functions.builtinFunctions[7] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.acos(args[0]);
            }
        };
        Functions.builtinFunctions[8] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.asin(args[0]);
            }
        };
        Functions.builtinFunctions[9] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.atan(args[0]);
            }
        };
        Functions.builtinFunctions[10] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.cbrt(args[0]);
            }
        };
        Functions.builtinFunctions[12] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.floor(args[0]);
            }
        };
        Functions.builtinFunctions[13] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.sinh(args[0]);
            }
        };
        Functions.builtinFunctions[14] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.sqrt(args[0]);
            }
        };
        Functions.builtinFunctions[15] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.tanh(args[0]);
            }
        };
        Functions.builtinFunctions[16] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.cosh(args[0]);
            }
        };
        Functions.builtinFunctions[11] = new Function() {
            @Override
            public double apply(final double... args) {
                return Math.ceil(args[0]);
            }
        };
        Functions.builtinFunctions[17] = new Function(2) {
            @Override
            public double apply(final double... args) {
                return Math.pow(args[0], args[1]);
            }
        };
        Functions.builtinFunctions[18] = new Function(1) {
            @Override
            public double apply(final double... args) {
                return Math.exp(args[0]);
            }
        };
        Functions.builtinFunctions[19] = new Function(1) {
            @Override
            public double apply(final double... args) {
                return Math.expm1(args[0]);
            }
        };
        Functions.builtinFunctions[22] = new Function(1) {
            @Override
            public double apply(final double... args) {
                if (args[0] > 0.0) {
                    return 1.0;
                }
                if (args[0] < 0.0) {
                    return -1.0;
                }
                return 0.0;
            }
        };
    }
}
