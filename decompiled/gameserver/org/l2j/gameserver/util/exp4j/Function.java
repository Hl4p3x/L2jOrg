// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.exp4j;

public abstract class Function
{
    protected final String name;
    protected final int numArguments;
    
    public Function(final String name, final int numArguments) {
        if (numArguments < 0) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
        if (!isValidFunctionName(name)) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
        this.name = name;
        this.numArguments = numArguments;
    }
    
    public Function(final String name) {
        this(name, 1);
    }
    
    @Deprecated
    public static char[] getAllowedFunctionCharacters() {
        final char[] chars = new char[53];
        int count = 0;
        for (int i = 65; i < 91; ++i) {
            chars[count++] = (char)i;
        }
        for (int i = 97; i < 123; ++i) {
            chars[count++] = (char)i;
        }
        chars[count] = '_';
        return chars;
    }
    
    public static boolean isValidFunctionName(final String name) {
        if (name == null) {
            return false;
        }
        final int size = name.length();
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final char c = name.charAt(i);
            if (!Character.isLetter(c)) {
                if (c != '_') {
                    if (!Character.isDigit(c) || i <= 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getNumArguments() {
        return this.numArguments;
    }
    
    public abstract double apply(final double... args);
}
